package com.dah.desb.config;

import com.dah.desb.infrastructure.worker.WorkerWebsocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(workerWebsocket(), "/websocket/workers").addInterceptors(new HttpSessionHandshakeInterceptor());
    }

    @Bean
    public WorkerWebsocket workerWebsocket() {
        return new WorkerWebsocket();
    }

}
