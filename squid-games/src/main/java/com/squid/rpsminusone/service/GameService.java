package com.squid.rpsminusone.service;

import com.squid.rpsminusone.component.GameWebSocketHandler;
import com.squid.rpsminusone.entity.Game;
import com.squid.rpsminusone.entity.GameStatus;
import com.squid.rpsminusone.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final GameWebSocketHandler webSocketHandler;

    public GameService(GameRepository gameRepository, GameWebSocketHandler webSocketHandler) {
        this.gameRepository = gameRepository;
        this.webSocketHandler = webSocketHandler;
    }

    private static final Map<String, String> WIN_RULES = new HashMap<>();

    static {
        WIN_RULES.put("rock", "scissors");
        WIN_RULES.put("scissors", "paper");
        WIN_RULES.put("paper", "rock");
    }

    //  Create a new game
    public Game createGame(String playerOne) {
        Game game = new Game();
        game.setPlayerOne(playerOne);
        game.setStatus(GameStatus.WAITING_FOR_PLAYER);
        Game savedGame = gameRepository.save(game);

        //  WebSocket Update (Fix: Convert gameId to String)
        sendWebSocketUpdate(savedGame.getId(), "New game created: " + savedGame.toString());

        return savedGame;
    }

    //  Join an existing game
    public Game joinGame(Long gameId, String playerTwo) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (game.getPlayerTwo() != null) {
            throw new RuntimeException("Game already has two players");
        }

        game.setPlayerTwo(playerTwo);
        game.setStatus(GameStatus.IN_PROGRESS);
        Game updatedGame = gameRepository.save(game);

        //  WebSocket Update (Fix: Convert gameId to String)
        sendWebSocketUpdate(game.getId(), "Player " + playerTwo + " joined the game!");

        return updatedGame;
    }

    //  Find a game by ID
    public Optional<Game> findGameById(Long gameId) {
        return gameRepository.findById(gameId);
    }

    public String submitMove(Long gameId, String username, String move) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) return "Game not found";
        Game game = gameOpt.get();

        boolean isPlayerOne = game.getPlayerOne().equals(username);
        if (isPlayerOne) {
            game.setPlayerOneMove(move);
        } else if (game.getPlayerTwo() != null && game.getPlayerTwo().equals(username)) {
            game.setPlayerTwoMove(move);
        } else {
            return "Invalid player";
        }

        if (game.getPlayerOneMove() != null && game.getPlayerTwoMove() != null) {
            game.setRoundsPlayed(game.getRoundsPlayed() + 1);
            determineRoundWinner(game);
            game.setPlayerOneMove(null);
            game.setPlayerTwoMove(null);
        }

        gameRepository.save(game);
        webSocketHandler.sendGameStateUpdate(game);

        return "Move submitted successfully";
    }

    private void determineRoundWinner(Game game) {
        String p1Move = game.getPlayerOneMove();
        String p2Move = game.getPlayerTwoMove();

        if (p1Move.equals(p2Move)) {
            // Draw, no score change
        } else if (WIN_RULES.get(p1Move).equals(p2Move)) {
            game.setPlayerOneScore(game.getPlayerOneScore() + 1);
        } else {
            game.setPlayerTwoScore(game.getPlayerTwoScore() + 1);
        }
        if (game.getPlayerOneScore() == 2) {
            game.setWinner(game.getPlayerOne());
            game.setStatus(GameStatus.COMPLETED);
        } else if (game.getPlayerTwoScore() == 2) {
            game.setWinner(game.getPlayerTwo());
            game.setStatus(GameStatus.COMPLETED);
        }
    }

    private void sendWebSocketUpdate(Long gameId, String message) {
        try {
            webSocketHandler.sendGameUpdate("{\"gameId\": " + gameId + ", \"message\": \"" + message + "\"}");
        } catch (Exception e) {
            System.err.println(" Error sending WebSocket update: " + e.getMessage());
        }
    }

}
