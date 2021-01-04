package com.dah.desb.infrastructure.worker;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.util.AssertionErrors;

import java.util.List;

public class WorkerEngineFactoryBean implements FactoryBean<WorkerEngine>, InitializingBean, DisposableBean {

	private String code;
	private RedisConnectionFactory connectionFactory;
	private RedisMessageListenerContainer redisMessageListenerContainer;
	private WorkerWebsocket websocket;
	private List<Worker> workers;

	private WorkerEngine workerEngine;

	public WorkerEngineFactoryBean(String code, RedisConnectionFactory connectionFactory, RedisMessageListenerContainer redisMessageListenerContainer, WorkerWebsocket websocket, List<Worker> workers) {
		this.code = code;
		this.connectionFactory = connectionFactory;
		this.redisMessageListenerContainer = redisMessageListenerContainer;
		this.websocket = websocket;
		this.workers = workers;
	}

	@Override
	public WorkerEngine getObject() throws Exception {
		return this.workerEngine;
	}

	@Override
	public Class<?> getObjectType() {
		return WorkerEngine.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		AssertionErrors.assertTrue("connectionFactory can not be null", connectionFactory != null);
		AssertionErrors.assertTrue("websocket can not be null", websocket != null);
		AssertionErrors.assertTrue("workers can not be null", workers != null);
		workerEngine = new WorkerEngine(code, connectionFactory, redisMessageListenerContainer, websocket, workers).startup();
	}

	@Override
	public void destroy() throws Exception {
		workerEngine.shutdown();
	}

}
