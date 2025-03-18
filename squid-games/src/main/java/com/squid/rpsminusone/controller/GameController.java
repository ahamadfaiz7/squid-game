package com.squid.rpsminusone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squid.rpsminusone.component.GameWebSocketHandler;
import com.squid.rpsminusone.entity.Game;
import com.squid.rpsminusone.service.GameService;
import com.squid.rpsminusone.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final GameWebSocketHandler webSocketHandler;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameController(GameService gameService, GameWebSocketHandler webSocketHandler, JwtUtil jwtUtil) {
        this.gameService = gameService;
        this.webSocketHandler = webSocketHandler;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = authHeader.replace("Bearer ", "");
        String playerOne = jwtUtil.extractUsername(token);

        Game game = gameService.createGame(playerOne);
        webSocketHandler.sendGameStateUpdate(game);

        return ResponseEntity.ok(game);
    }

    @PostMapping("/join/{gameId}")
    public ResponseEntity<?> joinGame(@PathVariable Long gameId, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        String token = authHeader.replace("Bearer ", "");
        String playerTwo = jwtUtil.extractUsername(token);

        try {
            Game game = gameService.joinGame(gameId, playerTwo);
            webSocketHandler.sendGameStateUpdate(game);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/move")
    public ResponseEntity<?> submitMove(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        Long gameId = Long.parseLong(request.get("gameId"));
        String move = request.get("move");

        try {
            String response = gameService.submitMove(gameId, username, move);
            Optional<Game> updatedGame = gameService.findGameById(gameId);
            updatedGame.ifPresent(webSocketHandler::sendGameStateUpdate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status/{gameId}")
    public ResponseEntity<?> getGameStatus(@PathVariable Long gameId) {
        try {
            Optional<Game> game = gameService.findGameById(gameId);
            if (game.isPresent()) {
                return ResponseEntity.ok(game.get());  //  Return the actual game object
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", " Game not found for ID: " + gameId));  //  Return a JSON error response
            }
        } catch (Exception e) {
            System.err.println(" Error fetching game status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", " Internal server error while retrieving game status."));
        }
    }


}
