package com.dah.desb.infrastructure.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Repository {

	private static final Logger logger = LoggerFactory.getLogger(Repository.class);

	private static final int WORKER_HOLDER_TTL = 60;

	private WorkerEngine engine;

	private RedisOperations<Serializable, Serializable> redisOperations;
	private RedisSerializer<String> serializer = new StringRedisSerializer();

	private ObjectMapper objectMapper = new ObjectMapper();

	Repository(WorkerEngine engine, RedisOperations<Serializable, Serializable> redisOperations) {
		this.engine = engine;
		this.redisOperations = redisOperations;
	}

	RedisOperations<Serializable, Serializable> getRedisOperations() {
		return redisOperations;
	}

	String getCachedWorkerHolder(String workerKey) {
		return redisOperations.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] cacheKey = serializer.serialize(buildHolderCacheKey(workerKey));
				byte[] cacheValue = connection.get(cacheKey);
				return cacheValue == null ? null : serializer.deserialize(cacheValue);
			}
		});
	}

	void cacheWorkerHolder(String workerKey) {
		redisOperations.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] cacheKey = serializer.serialize(buildHolderCacheKey(workerKey));
				byte[] cacheValue = serializer.serialize(engine.getNode());
				connection.set(cacheKey, cacheValue);
				connection.expire(cacheKey, WORKER_HOLDER_TTL);
				return null;
			}
		});
	}

	void removeCachedWorkerHolder(String workerKey) {
		redisOperations.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] cacheKey = serializer.serialize(buildHolderCacheKey(workerKey));
				connection.del(cacheKey);
				return null;
			}
		});
	}

	void sendCommandToTopic(Command command) {
		redisOperations.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					connection.publish(serializer.serialize(getCommandTopicName()), serializer.serialize(objectMapper.writeValueAsString(command)));
				} catch (Exception e) {
					logger.error("发送command到topic异常", e);
				}
				return null;
			}
		});
	}

	void sendStatusToTopic(Worker worker) {
		redisOperations.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					Map<String, Object> status = new HashMap<>();
					status.put("key", worker.getKey());
					status.put("name", worker.getName());
					status.put("order", worker.getOrder());
					status.put("mode", worker.getMode());
					status.put("holded", worker.isHolded());
					status.put("busy", worker.isBusy());
					status.put("node", engine.getNode());
					connection.publish(serializer.serialize(getStatusTopicName()), serializer.serialize(objectMapper.writeValueAsString(status)));
				} catch (Exception e) {
					logger.error("发送worker状态到topic异常", e);
				}
				return null;
			}
		});
	}

	String getCommandTopicName() {
		return "workerEngine" + ":" + engine.getCode() + ":" + "command" + ":" + "topic";
	}

	String getStatusTopicName() {
		return "workerEngine" + ":" + engine.getCode() + ":" + "status" + ":" + "topic";
	}

	private String buildHolderCacheKey(String workerKey) {
		return "workerEngine" + ":" + engine.getCode() + ":" + workerKey + ":" + "holder";
	}

}
