package com.dah.desb.infrastructure.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class StatusPublisher implements MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(StatusPublisher.class);

	private RedisSerializer<String> serializer = new StringRedisSerializer();

	private WorkerWebsocket workerWebsocket;

	StatusPublisher(WorkerWebsocket workerWebsocket) {
		this.workerWebsocket = workerWebsocket;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String workerStatusJson = serializer.deserialize(message.getBody());
			workerWebsocket.sendWorkerStatus(workerStatusJson);
		} catch (Exception e) {
			logger.info("监听status状态变化时发生错误" + e);
		}
	}

}
