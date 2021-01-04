package com.dah.desb.infrastructure.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

public class CommandExecutor implements MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

	private RedisSerializer<String> serializer = new StringRedisSerializer();
	private ObjectMapper objectMapper = new ObjectMapper();

	private WorkerEngine engine;

	CommandExecutor(WorkerEngine engine) {
		this.engine = engine;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String commandJson = serializer.deserialize(message.getBody());
			Command command = objectMapper.readValue(commandJson, Command.class);
			if (engine.getNode().equals(command.getNode())) {
				Worker worker = engine.getWorker(command.getKey());
				if (worker != null) {
					if (WorkerEngine.COMMAND_SWITCH_MODE.equals(command.getType())) {
						worker.switchMode();
					} else if (WorkerEngine.COMMAND_HOLD.equals(command.getType())) {
						hold(worker);
					} else if (WorkerEngine.COMMAND_UNHOLD.equals(command.getType())) {
						unhold(worker);
					}
				}
			} else if (WorkerEngine.COMMAND_PUBLISH_ALL.equals(command.getType())) {
				for (Worker worker : engine.getWorkers()) {
					worker.publishStatus();
				}
			}
		} catch (Throwable throwable) {
			logger.error("执行命令异常。", throwable);
		}
	}

	private void hold(Worker worker) {
		try {
			if (worker.acquireLock()) {
				if (StringUtils.isEmpty(engine.getRepository().getCachedWorkerHolder(worker.getKey()))) {
					engine.getRepository().cacheWorkerHolder(worker.getKey());
					worker.hold();
				}
			}
		} finally {
			worker.releaseLock();
		}
	}

	private void unhold(Worker worker) {
		try {
			if (worker.acquireLock()) {
				if (engine.getNode().equals(engine.getRepository().getCachedWorkerHolder(worker.getKey()))) {
					engine.getRepository().removeCachedWorkerHolder(worker.getKey());
					worker.unhold();
				}
			}
		} finally {
			worker.releaseLock();
		}
	}

}
