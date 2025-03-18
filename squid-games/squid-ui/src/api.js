import axios from "axios";

const API_BASE_URL = "http://localhost:8787";

export const registerUser = async (username, password) => {
    return axios.post(`${API_BASE_URL}/auth/register`, { username, password });
};

export const loginUser = async (username, password) => {
    return axios.post(`${API_BASE_URL}/auth/login`, { username, password });
};

export const createGame = async (token) => {
    return axios.post(`${API_BASE_URL}/game/create`, {}, {
        headers: { Authorization: `Bearer ${token}` }
    });
};

export const joinGame = async (gameId, username, token) => {
    return axios.post(`${API_BASE_URL}/game/join/${gameId}`, { playerTwo: username }, {
        headers: { Authorization: `Bearer ${token}` }
    });
};

export const submitMove = async (gameId, move, token) => {
    return axios.post(`${API_BASE_URL}/game/move/${gameId}`, { move }, {
        headers: { Authorization: `Bearer ${token}` }
    });
};
