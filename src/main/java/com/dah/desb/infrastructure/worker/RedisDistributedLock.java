package com.dah.desb.infrastructure.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.List;

public class RedisDistributedLock {

	private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

	private static final String UNLOCK_LUA;
	
	private static final int LOCK_TTL_SECONDS = 60;

	static {
		StringBuilder sb = new StringBuilder();
		sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
		sb.append("then ");
		sb.append("    return redis.call(\"del\",KEYS[1]) ");
		sb.append("else ");
		sb.append("    return 0 ");
		sb.append("end ");
		UNLOCK_LUA = sb.toString();
	}

	private WorkerEngine engine;

	RedisDistributedLock(WorkerEngine engine) {
		this.engine = engine;
	}

	boolean acquireLock(String workerKey) {
		String lockKey = getLockKey(workerKey);
		boolean result = setRedis(lockKey, engine.getNode());
		int retryTimes = 10;
		while ((!result) && retryTimes-- > 0) {
			try {
				logger.debug("lock failed, retrying..." + retryTimes);
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				return false;
			}
			result = setRedis(lockKey, engine.getNode());
		}
		return result;
	}

	boolean releaseLock(String workerKey) {
		try {
			List<String> keys = new ArrayList<String>();
			keys.add(getLockKey(workerKey));
			List<String> args = new ArrayList<String>();
			args.add(engine.getNode());
			// 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
			// spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
			Long result = engine.getRepository().getRedisOperations().execute(new RedisCallback<Long>() {
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					Object nativeConnection = connection.getNativeConnection();
					// 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
					// 集群模式
					if (nativeConnection instanceof JedisCluster) {
						return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
					}
					// 单机模式
					else if (nativeConnection instanceof Jedis) {
						return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
					}
					return 0L;
				}
			});

			return result != null && result > 0;
		} catch (Exception e) {
			logger.error("release lock occured an exception", e);
		}
		return false;
	}

	private boolean setRedis(String lockKey, String lockValue) {
		try {
			String result = engine.getRepository().getRedisOperations().execute(new RedisCallback<String>() {
				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					JedisCommands commands = (JedisCommands) connection.getNativeConnection();
					return commands.set(lockKey, lockValue, "NX", "PX", LOCK_TTL_SECONDS * 1000L);
				}
			});
			return !StringUtils.isEmpty(result);
		} catch (Exception e) {
			logger.error("set redis occured an exception", e);
		}
		return false;
	}

	private String getLockKey(String workerKey) {
		return "workerEngine" + ":" + engine.getCode() + ":" + workerKey + ":" + "lock";
	}

}
