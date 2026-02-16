import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Helpers opcionals (per les pàgines)
export const apiGet = (path, config = {}) => api.get(path, config).then(r => r.data);
export const apiPost = (path, body, config = {}) => api.post(path, body, config).then(r => r.data);
export const apiPut = (path, body, config = {}) => api.put(path, body, config).then(r => r.data);
export const apiDelete = (path, config = {}) => api.delete(path, config).then(r => r.data);

// IMPORTANT: mantenim el default per AuthContext.jsx
export default api;
