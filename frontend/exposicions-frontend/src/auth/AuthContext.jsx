import { createContext, useContext, useMemo, useState } from "react";
import api from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => {
        const token = localStorage.getItem("token");
        const email = localStorage.getItem("email");
        const role = localStorage.getItem("role");
        return token ? { token, email, role } : null;
    });

    const login = async (email, password) => {
        const res = await api.post("/api/auth/login", { email, password });
        const { token, role } = res.data;

        localStorage.setItem("token", token);
        localStorage.setItem("email", email);
        localStorage.setItem("role", role);

        setUser({ token, email, role });
    };

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("email");
        localStorage.removeItem("role");
        setUser(null);
    };

    const value = useMemo(() => ({ user, login, logout }), [user]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    return useContext(AuthContext);
}
