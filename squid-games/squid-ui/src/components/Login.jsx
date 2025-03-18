import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Login = ({ setAuthToken }) => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post("http://localhost:8787/auth/login", {
                username,
                password
            });

            console.log("Server Response:", response.data);

            if (response.data.token) {
                localStorage.setItem("token", response.data.token); //  Save token
                if (setAuthToken) {
                    setAuthToken(response.data.token); //  setAuthToken exists before calling
                }
                console.log("Token saved:", response.data.token);
                alert("Login successful!");
                navigate("/lobby"); // Redirect to lobby page
            } else {
                console.error("Login failed: No token received.");
                alert("Login failed! No token received.");
            }
        } catch (error) {
            console.error("Login Error:", error.response?.data || error.message);
            alert("Login failed! Invalid credentials.");
        }
    };

    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <input type="text" placeholder="Username" onChange={(e) => setUsername(e.target.value)} required />
                <input type="password" placeholder="Password" onChange={(e) => setPassword(e.target.value)} required />
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

export default Login;
