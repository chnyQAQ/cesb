package com.dah.desb.infrastructure.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.AssertionErrors;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public abstract class Worker {

	private static final Logger logger = LoggerFactory.getLogger(Worker.class);

	protected String key;
	protected String name;

	private Map<Object, Object> context = new HashMap<>();

	private WorkerEngine engine;
	private RedisDistributedLock lock;
	private int order;

	private String mode = WorkerEngine.MODE_AUTO;
	private boolean holded = false;
	private boolean busy = false;

	private long duration = 0;

	private boolean executing = false;
	private Long executionCompletedTime = null;

	private Long publishStatusTime = null;
	
	@Autowired
	private TaskScheduler scheduler;
	private ScheduledFuture<?> synchronizeAutomaticallyFuture;
	private ScheduledFuture<?> publishStatusAutomaticallyFuture;
	private ScheduledFuture<?> watchForIdleFuture;

	public Worker() {
		super();
	}

	final String getKey() {
		return key;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	final int getOrder() {
		return order;
	}

	final String getMode() {
		return mode;
	}

	void publishStatus() {
		engine.getRepository().sendStatusToTopic(this);
		publishStatusTime = System.currentTimeMillis();
	}

	final void switchMode() {
		if (WorkerEngine.MODE_AUTO.equals(mode)) {
			mode = WorkerEngine.MODE_MANUAL;
		} else if (WorkerEngine.MODE_MANUAL.equals(mode)) {
			mode = WorkerEngine.MODE_AUTO;
		}
		this.publishStatus();
	}

	final boolean isHolded() {
		return holded;
	}

	final void hold() {
		if (!holded) {
			holded = true;
			this.publishStatus();
		}
		start();
	}

	final void unhold() {
		if (holded) {
			holded = false;
			this.publishStatus();
		}
		stop();
	}

	final boolean isBusy() {
		return busy;
	}

	private final void busy() {
		if (!busy) {
			busy = true;
			this.publishStatus();
		}
	}

	private final void idle() {
		if (busy) {
			busy = false;
			this.publishStatus();
		}
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	final boolean acquireLock() {
		return lock.acquireLock(key);
	}

	final boolean releaseLock() {
		return lock.releaseLock(key);
	}

	final void initialize(WorkerEngine engine, int order) {
		// initialize properties
		AssertionErrors.assertTrue("key 不能为空，请检查配置", !StringUtils.isEmpty(key));
		AssertionErrors.assertTrue("name 不能为空，请检查配置", !StringUtils.isEmpty(name));
		this.engine = engine;
		this.lock = new RedisDistributedLock(engine);
		this.order = order;
		// initialize scheduledTasks
		this.synchronizeAutomaticallyFuture = this.synchronizeAutomatically();
		this.publishStatusAutomaticallyFuture = this.publishStatusAutomatically();
		this.watchForIdleFuture = this.watchForIdle();
		// initialize subtype
		this.doInitialize();
	}

	protected abstract void doInitialize();

	final void destory() {
		try {
			if (synchronizeAutomaticallyFuture != null) {
				synchronizeAutomaticallyFuture.cancel(false);
			}
			if (publishStatusAutomaticallyFuture != null) {
				publishStatusAutomaticallyFuture.cancel(false);
			}
			if (watchForIdleFuture != null) {
				watchForIdleFuture.cancel(false);
			}
			this.doDestory();
		} catch (Exception e) {
			logger.error("执行 destory 异常。", e);
		}
	}

	protected abstract void doDestory();

	final void start() {
		if (this.isStopped()) {
			this.doStart();
			this.watchForIdle();
		}
	}

	protected abstract void doStart();

	public abstract boolean isStarted();

	final void stop() {
		if (this.isStarted()) {
			this.doStop();
		}
	}

	protected abstract void doStop();

	public abstract boolean isStopped();

	protected final void execute(WorkerCallback callback) {
		long beginTime = System.currentTimeMillis();
		try {
			// 标记为正在运行
			executing = true;
			// 标记为忙碌
			this.busy();
			// 实际的执行
			callback.doInWorker(context);
			// 耗时计算及警告
			long timeConsuming = System.currentTimeMillis() - beginTime;
			if (timeConsuming > duration) {
				logger.warn(key + "（" + name + "）执行完成，耗时 " + timeConsuming + " 毫秒。");
			}
		} catch (Exception e) {
			logger.error(key + "（" + name + "）执行异常。", e);
		} finally {
			// 标记为未运行
			executing = false;
			// 记录最后一次完成的时间，为空闲判定提供依据。
			// 注意，不能每次完成执行后都更新状态为空闲，必须使忙碌状态得到延长，否则：
			// 其一，对于执行耗时极短的任务，web控制台的忙碌状态出现后立即消失（后台状态从忙碌变为空闲的间隔极短，且同步推送），不便于观察。
			// 其二，对于执行频率极高的任务，web控制台的状态会频繁切换，呈现闪烁的效果；通过延时判定空闲的机制可以使其在多次高频率任务执行期间一直呈现忙碌状态，直到频率降低后才显示为空闲。
			executionCompletedTime = System.currentTimeMillis();
		}
	}

	private ScheduledFuture<?> synchronizeAutomatically() {
		return scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					// 对每个 worker 的判断和操作前必须先获取锁，避免并发问题
					// 如果锁被占用acquireLock会有不超过10s的重试时间，所以本任务的执行频率应该大于10s，否则可能会出现大量获取锁的尝试，没有意义。
					// 执行频率不能过低，否则不能快速接管任务；另外，不能小于holder的有效期（有效期不能过大，否则也无法快速接管），否则，任务会在不同节点间跳动，此为bug。
					// acquireLock 10s < 自动同步执行频率 15s < holder的有效期 60s
					if (acquireLock()) {
						String holderNode = engine.getRepository().getCachedWorkerHolder(key);
						if (StringUtils.isEmpty(holderNode)) {
							// 1. worker 没有 holder
							// 1.1 若 worker 为 auto 模式，则使本节点成为 holder 并启动 worker
							// 1.2 若 worker 为 manual 模式则停止 worker
							if (WorkerEngine.MODE_AUTO.equals(mode)) {
								engine.getRepository().cacheWorkerHolder(key);
								hold();
							} else {
								unhold();
							}
						} else {
							// 2. worker 有 holder
							// 2.1 若 holder 为本节点则更新 holder 的 ttl 并启动 worker
							// 2.2 若不为本节点则停止 worker
							if (engine.getNode().equals(holderNode)) {
								engine.getRepository().cacheWorkerHolder(key);
								hold();
							} else {
								unhold();
							}
						}
					}
				} catch (Exception e) {
					logger.error(key + "（" + name + "）同步异常。", e);
				} finally {
					releaseLock();
				}
			}
		}, new Date(System.currentTimeMillis() + (10 * 1000L)), 15 * 1000L);
	}

	private ScheduledFuture<?> publishStatusAutomatically() {
		return scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// 状态至少每分钟推送一次
				if (publishStatusTime == null || (System.currentTimeMillis() - publishStatusTime > 60 * 1000L)) {
					publishStatus();
				}
			}
		}, new Date(System.currentTimeMillis() + (10 * 1000L)), 1000L);
	}

	private ScheduledFuture<?> watchForIdle() {
		return scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// 判定时任务没有在执行中，最后一次完成后，超过1秒，则认定为空闲
				if (!executing && executionCompletedTime != null && (System.currentTimeMillis() - executionCompletedTime > 1000L)) {
					idle();
				}
			}
		}, new Date(System.currentTimeMillis() + (10 * 1000L)), 100L);
	}

}
