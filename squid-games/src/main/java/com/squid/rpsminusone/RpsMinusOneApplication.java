package com.squid.rpsminusone;

import com.squid.rpsminusone.component.GameWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SpringBootApplication
@EnableWebSocket
public class RpsMinusOneApplication implements WebSocketConfigurer {
	@Autowired
	private GameWebSocketHandler gameWebSocketHandler;

	public static void main(String[] args) {
		SpringApplication.run(RpsMinusOneApplication.class, args);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(gameWebSocketHandler, "/gameplay").setAllowedOrigins("*");
		System.out.println("WebSocket handler registered at /gameplay");
	}

}