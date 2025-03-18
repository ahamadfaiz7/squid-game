package com.squid.rpsminusone.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squid.rpsminusone.entity.Game;
import com.squid.rpsminusone.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //  Store sessions by username
    private static final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();

    public GameWebSocketHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println(" Received WebSocket Message: " + payload);
        session.sendMessage(new TextMessage(" Message received: " + payload));
    }

    //  Register a new WebSocket session
    public void registerSession(String username, WebSocketSession session) {
        playerSessions.put(username, session);
        System.out.println(" WebSocket session registered for user: " + username);
    }

    //  Send game update with exception handling
    public void sendGameUpdate(String message) {
        for (WebSocketSession session : playerSessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println(" Failed to send WebSocket message: " + e.getMessage());
                }
            }
        }
    }

    //  Remove a session when a player logs out
    public void removeSession(String username) {
        playerSessions.remove(username);
        System.out.println(" WebSocket session removed for user: " + username);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        String query = session.getUri().getQuery();
        String token = null;

        //  Extract token safely from query parameters
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                token = param.substring(6);
                break;
            }
        }

        if (token == null || token.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }


        String username = jwtUtil.extractUsername(token);

        if (jwtUtil.validateToken(token)) {
            if (username != null) {
                System.out.println(" WebSocket Connected: " + username);
                registerSession(username, session);
                session.sendMessage(new TextMessage("{\"message\": \" Connected as " + username + "\"}"));
            } else {
                System.out.println(" WebSocket connection failed: No username in token!");
                session.close(CloseStatus.NOT_ACCEPTABLE);
            }
        } else {
            System.out.println(" WebSocket connection failed: Invalid token!");
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String disconnectedUser = null;

        //  Find the user associated with this session
        for (Map.Entry<String, WebSocketSession> entry : playerSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                disconnectedUser = entry.getKey();
                break;
            }
        }

        if (disconnectedUser != null) {
            removeSession(disconnectedUser);
        }

        System.out.println(" WebSocket Disconnected: " + session.getId() + " (User: " + disconnectedUser + ")");
    }


    public void sendGameStateUpdate(Game game) {
        for (WebSocketSession session : playerSessions.values()) {
            if (session.isOpen()) {
                try {
                    String gameJson = objectMapper.writeValueAsString(game);
                    session.sendMessage(new TextMessage(gameJson));
                    System.out.println(" WebSocket Game State Sent: " + gameJson);
                } catch (IOException e) {
                    System.err.println(" WebSocket error: Failed to send game state - " + e.getMessage());
                }
            }
        }
    }
}
