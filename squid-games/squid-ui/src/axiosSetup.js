import axios from "axios";

//  Automatically attach JWT token to requests
const api = axios.create({
    baseURL: "http://localhost:8787",
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default api;
