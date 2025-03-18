import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const GameRoom = () => {
    const { gameId } = useParams();
    const [gameState, setGameState] = useState(null);
    const [move, setMove] = useState("rock"); // Default move selection
    const token = localStorage.getItem("token");

    useEffect(() => {
        if (!token) {
            console.error("No JWT Token found in local storage!");
            alert("You need to be logged in!");
            return;
        }

        if (!gameId) {
            console.error("Game ID is missing!");
            alert("Invalid Game ID!");
            return;
        }

        console.log("Connecting to WebSocket:", `ws://localhost:8787/gameplay?gameId=${gameId}&token=${token}`);

        const newSocket = new WebSocket(`ws://localhost:8787/gameplay?gameId=${gameId}&token=${token}`);

        newSocket.onopen = () => {
            console.log("WebSocket Connected Successfully!");
        };

        newSocket.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                console.log("WebSocket Message:", data);
                setGameState(data);
            } catch (error) {
                console.error("WebSocket Message Parse Error:", error, "Received Data:", event.data);
            }
        };

        newSocket.onclose = (event) => {
            console.warn(`WebSocket Disconnected (Code: ${event.code}, Reason: ${event.reason || "Unknown"})`);
        };

        newSocket.onerror = (error) => {
            console.error("WebSocket Error:", error);
        };

        return () => {
            console.log("Closing WebSocket connection...");
            newSocket.close();
        };
    }, [gameId, token]);

    const handleSubmitMove = async () => {
        if (!gameId) {
            alert("Game ID is missing!");
            console.error("gameId is missing before submitting move!");
            return;
        }

        console.log("Submitting move for gameId:", gameId, "Move:", move);

        try {
            const response = await fetch(`http://localhost:8787/game/move`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ gameId, move })
            });

            if (!response.ok) {
                throw new Error(`Failed to submit move: ${response.status} - ${response.statusText}`);
            }

            console.log("Move submitted successfully!");
        } catch (error) {
            console.error("Error submitting move:", error);
            alert("Failed to submit move.");
        }
    };

    const getLeader = () => {
        if (!gameState || gameState.roundsPlayed === 0) return "Waiting for the game to start...";

        if (gameState.status === "IN_PROGRESS") {
            if (gameState.playerOneScore > gameState.playerTwoScore) {
                return `${gameState.playerOne} is leading`;
            } else if (gameState.playerTwoScore > gameState.playerOneScore) {
                return `${gameState.playerTwo} is leading`;
            } else {
                return "Scores are tied! Next round decides the winner.";
            }
        }
        if (gameState.status === "COMPLETED" && gameState.playerOneScore === gameState.playerTwoScore) {
            return "It's a tie!";
        }

        return "";
    };



    return (
        <div>
            <h2>Game Room (ID: {gameId})</h2>

            {gameState ? (
                <div>
                    <h3>Status: {gameState.status}</h3>
                    <p>Player One: {gameState.playerOne}</p>
                    <p>Player Two: {gameState.playerTwo ? gameState.playerTwo : "Waiting for Player 2 to join..."}</p>

                    <h3>Rounds Played: {gameState.roundsPlayed}</h3>
                    {getLeader() && <h3>{getLeader()}</h3>}


                    <h3>Score</h3>
                    <p>{gameState.playerOne}: {gameState.playerOneScore} | {gameState.playerTwo}: {gameState.playerTwoScore}</p>


                    {gameState.playerOneMove && gameState.playerTwoMove && gameState.playerOneMove === gameState.playerTwoMove ? (
                        <h3>It's a tie!</h3>
                    ) : null}

                    {gameState.winner ? (
                        <h2>Winner: {gameState.winner}</h2>
                    ) : (
                        <div>
                            {gameState.playerTwoMove && !gameState.playerOneMove ? (
                                <p>Player 2 has made a move, now it's your turn!</p>
                            ) : gameState.playerOneMove && !gameState.playerTwoMove ? (
                                <p>Waiting for Player 2 to make a move...</p>
                            ) : (
                                <p>Waiting for both players to make moves...</p>
                            )}


                            <select value={move} onChange={(e) => setMove(e.target.value)}>
                                <option value="">-- Select Move --</option>
                                <option value="rock">Rock</option>
                                <option value="paper">Paper</option>
                                <option value="scissors">Scissors</option>
                            </select>

                            <button onClick={handleSubmitMove} disabled={!move}>Submit Move</button>
                        </div>
                    )}
                </div>
            ) : (
                <p>Loading game state...</p>
            )}
        </div>
    );
};

export default GameRoom;
