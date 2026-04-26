import axios from "axios";

const API_URL = "http://localhost:8080/api/auth";

export const login = async (email: string, password: string) => {
    const response = await axios.post(`${API_URL}/login`, { email, password });
    localStorage.setItem("token", response.data.accessToken);
    return response.data;
};

export const register = async (name: String, email: string, password: string) => {
    const response = await axios.post(`${API_URL}/register`, { name, email, password });
    return response.data;
};

export const logout = async () => {
    const token = localStorage.getItem("token");
    console.log("token:", token); // confirma aqui
    await axios.post(`${API_URL}/logout`, {}, {
        headers: { Authorization: `Bearer ${token}` }
    });
    localStorage.removeItem("token");
};