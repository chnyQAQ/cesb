package com.dah.desb.infrastructure.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class WorkerWebsocket extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(WorkerWebsocket.class);

	private static Map<String, WebSocketSession> sessionMap = new HashMap<>();

	private ObjectMapper objectMapper = new ObjectMapper();

	private WorkerEngine engine;

	void setEngine(WorkerEngine engine) {
		this.engine = engine;
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		if (sessionMap.get(session.getId()) != null) {
			sessionMap.remove(session.getId());
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		if (sessionMap.get(session.getId()) == null) {
			sessionMap.put(session.getId(), session);
			Command command = new Command();
			command.setType(WorkerEngine.COMMAND_PUBLISH_ALL);
			engine.getRepository().sendCommandToTopic(command);
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		Command command = objectMapper.readValue(message.getPayload(), Command.class);
		engine.getRepository().sendCommandToTopic(command);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
		logger.error("传输异常。", throwable);
	}

	public void sendWorkerStatus(String workerStatusJson) {
		for (WebSocketSession session : sessionMap.values()) {
			try {
				synchronized (session) {
					session.sendMessage(new TextMessage(workerStatusJson));
				}
			} catch (Exception e) {
				logger.error("推送状态异常。" + workerStatusJson, e);
			}
		}

	}

}
