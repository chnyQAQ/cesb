package com.dah.desb.infrastructure.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkerEngine {

	private static final Logger logger = LoggerFactory.getLogger(WorkerEngine.class);

	public static final String MODE_AUTO = "auto";
	public static final String MODE_MANUAL = "manual";

	public static final String COMMAND_PUBLISH_ALL = "publish_all";
	public static final String COMMAND_SWITCH_MODE = "switch_mode";
	public static final String COMMAND_HOLD = "hold";
	public static final String COMMAND_UNHOLD = "unhold";

	private String code;
	private String node;

	private RedisConnectionFactory connectionFactory;
	private Repository repository;

	private RedisMessageListenerContainer redisMessageListenerContainer;
	private CommandExecutor commandExecutor;
	private StatusPublisher statusPublisher;

	private WorkerWebsocket workerWebsocket;

	private List<Worker> workers;

	WorkerEngine(String code, RedisConnectionFactory connectionFactory, RedisMessageListenerContainer redisMessageListenerContainer, WorkerWebsocket workerWebsocket, List<Worker> workers) {
		this.code = code;
		this.connectionFactory = connectionFactory;
		this.redisMessageListenerContainer = redisMessageListenerContainer;
		this.workerWebsocket = workerWebsocket;
		this.workers = workers;
	}

	String getCode() {
		return code;
	}

	String getNode() {
		return node;
	}

	Repository getRepository() {
		return repository;
	}

	final List<Worker> getWorkers() {
		return workers;
	}

	final Worker getWorker(String key) {
		for (Worker worker : workers) {
			if (key.equals(worker.getKey())) {
				return worker;
			}
		}
		return null;
	}

	final WorkerEngine startup() {

		logger.info("WorkerEngine 启动。。。");

		// initialize node
		this.node = buildNode();

		// initialize repository
		this.repository = new Repository(this, buildRedisOperations());

		// initialize workerWebsocket
		this.workerWebsocket.setEngine(this);

		// initialize commandExecutor
		this.commandExecutor = new CommandExecutor(this);
		this.redisMessageListenerContainer.addMessageListener(commandExecutor, new ChannelTopic(repository.getCommandTopicName()));

		// initialize statusPublisher
		this.statusPublisher = new StatusPublisher(workerWebsocket);
		this.redisMessageListenerContainer.addMessageListener(statusPublisher, new ChannelTopic(repository.getStatusTopicName()));

		// initialize workers
		try {
			Set<String> keys = new HashSet<>();
			for (int i = 0; i < workers.size(); i++) {
				Worker worker = workers.get(i);
				if (keys.contains(worker.getKey())) {
					throw new RuntimeException("key 不能重复，请检查配置。");
				} else {
					keys.add(worker.getKey());
					worker.initialize(this, i);
				}
			}
			logger.info("WorkerEngine 启动成功！");
		} catch (Exception e) {
			logger.error("启动异常。", e);
			shutdown();
		}

		return this;
	}

	final WorkerEngine shutdown() {

		logger.info("WorkerEngine 关闭。。。");

		for (Worker worker : workers) {
			worker.destory();
		}
		redisMessageListenerContainer.removeMessageListener(commandExecutor);
		redisMessageListenerContainer.removeMessageListener(statusPublisher);

		logger.info("WorkerEngine 关闭成功！");

		return this;
	}

	private String buildNode() {
		try {
			String[] runtimeMXBeanNames = ManagementFactory.getRuntimeMXBean().getName().split("@");
			return getHostIp() + "@" + runtimeMXBeanNames[0];
		} catch (Exception e) {
			throw new RuntimeException("构造节点名称异常。", e);
		}
	}

	private RedisOperations<Serializable, Serializable> buildRedisOperations() {
		RedisTemplate<Serializable, Serializable> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	private String getHostIp() throws SocketException {
		Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = allNetInterfaces.nextElement();
			Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress ip = addresses.nextElement();
				// loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
				if (ip != null && ip instanceof Inet4Address && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
					return ip.getHostAddress();
				}
			}
		}
		return null;
	}

}
