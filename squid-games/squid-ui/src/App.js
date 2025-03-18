import { BrowserRouter as Router, Routes, Route, Navigate, Link, useNavigate } from "react-router-dom";
import Login from "./components/Login";
import Register from "./components/Register";
import GameLobby from "./components/GameLobby";
import GameRoom from "./components/GameRoom";
import "./App.css";

function Navbar() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token");
        alert("You have been logged out!");
        navigate("/login");
    };

    return (
        <nav className="nav-bar">
            <Link to="/login" className="nav-link">Login</Link>
            <Link to="/register" className="nav-link">Register</Link>
            <Link to="/lobby" className="nav-link">Game Lobby</Link>
            <button className="logout-button" onClick={handleLogout}>Logout</button>
        </nav>
    );
}

function App() {
    return (
        <Router>
            <Navbar />
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/lobby" element={<GameLobby />} />
                <Route path="/game/:gameId" element={<GameRoom />} />
            </Routes>
        </Router>
    );
}

export default App;
