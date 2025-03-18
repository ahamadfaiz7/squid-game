package com.squid.rpsminusone.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String playerOne;

    @Column(nullable = true)
    private String playerTwo;

    private int playerOneScore = 0;
    private int playerTwoScore = 0;
    private int roundsPlayed = 0;

    @Column(nullable = true)
    private String playerOneMove;

    @Column(nullable = true)
    private String playerTwoMove;

    @Column(nullable = true)
    private String winner;

    @Enumerated(EnumType.STRING) //  Store as String in DB
    private GameStatus status;   //  Added game status field

    public Game() {}

    public Game(String playerOne) {
        this.playerOne = playerOne;
        this.status = GameStatus.WAITING_FOR_PLAYER; //  Default status
    }

    public Long getId() { return id; }
    public String getPlayerOne() { return playerOne; }
    public void setPlayerOne(String playerOne) { this.playerOne = playerOne; }
    public String getPlayerTwo() { return playerTwo; }
    public void setPlayerTwo(String playerTwo) { this.playerTwo = playerTwo; }
    public int getPlayerOneScore() { return playerOneScore; }
    public void setPlayerOneScore(int score) { this.playerOneScore = score; }
    public int getPlayerTwoScore() { return playerTwoScore; }
    public void setPlayerTwoScore(int score) { this.playerTwoScore = score; }
    public int getRoundsPlayed() { return roundsPlayed; }
    public void setRoundsPlayed(int rounds) { this.roundsPlayed = rounds; }
    public String getPlayerOneMove() { return playerOneMove; }
    public void setPlayerOneMove(String move) { this.playerOneMove = move; }
    public String getPlayerTwoMove() { return playerTwoMove; }
    public void setPlayerTwoMove(String move) { this.playerTwoMove = move; }
    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
}
