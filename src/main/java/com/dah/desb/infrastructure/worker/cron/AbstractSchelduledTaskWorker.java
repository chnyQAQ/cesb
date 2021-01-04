package com.dah.desb.infrastructure.worker.cron;

import com.dah.desb.infrastructure.worker.Worker;
import com.dah.desb.infrastructure.worker.WorkerCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.util.AssertionErrors;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public abstract class AbstractSchelduledTaskWorker extends Worker {

	private String cron;

	@Autowired
	private ThreadPoolTaskScheduler scheduler;
	private ScheduledFuture<?> scheduledFuture;

	public final void setCron(String cron) {
		this.cron = cron;
	}

	@Override
	public final void doInitialize() {
		AssertionErrors.assertTrue("cron不能为空，请检查配置", !StringUtils.isEmpty(cron));
	}

	@Override
	public final void doDestory() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(false);
		}
	}

	@Override
	public final boolean isStarted() {
		return scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone();
	}

	@Override
	public final boolean isStopped() {
		return scheduledFuture == null || (scheduledFuture.isCancelled() && scheduledFuture.isDone());
	}

	@Override
	protected final void doStart() {
		scheduledFuture = scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				execute(new WorkerCallback() {
					@Override
					public void doInWorker(Map<Object, Object> context) {
						execute(context);
					}
				});
			}
		}, new CronTrigger(cron));
	}

	@Override
	protected final void doStop() {
		scheduledFuture.cancel(false);
	}

	protected abstract void execute(Map<Object, Object> context);

}
