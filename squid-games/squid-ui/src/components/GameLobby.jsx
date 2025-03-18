import { useNavigate } from "react-router-dom";
import axios from "axios";

const GameLobby = () => {
    const token = localStorage.getItem("token");
    const navigate = useNavigate();

    const handleCreateGame = async () => {
        if (!token) {
            alert("You must be logged in to create a game!");
            return;
        }

        try {
            const response = await axios.post(
                "http://localhost:8787/game/create",
                {},
                {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                    }
                }
            );

            console.log("Game created:", response.data);
            alert(`Game created! ID: ${response.data.id}`);

            navigate(`/game/${response.data.id}`);
        } catch (error) {
            console.error("Error creating game:", error);
            alert(`Failed to create game: ${error.response?.data || error.message}`);
        }
    };

    return (
        <div>
            <h2>Game Lobby</h2>
            <button onClick={handleCreateGame}>Create Game</button>
        </div>
    );
};

export default GameLobby;
