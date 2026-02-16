import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function Login() {
    const [email, setEmail] = useState("admin@demo.local");
    const [password, setPassword] = useState("admin123");
    const [error, setError] = useState("");
    const { login } = useAuth();
    const navigate = useNavigate();

    const onSubmit = async (e) => {
        e.preventDefault();
        setError("");
        try {
            await login(email, password);
            navigate("/exhibitions", { replace: true });
        } catch (err) {
            setError("Login incorrecte o backend no disponible");
        }
    };

    return (
        <div style={{ maxWidth: 420, margin: "60px auto", fontFamily: "sans-serif" }}>
            <h2>Login</h2>

            <form onSubmit={onSubmit}>
                <div style={{ marginBottom: 12 }}>
                    <label>Email</label>
                    <input
                        style={{ width: "100%", padding: 8 }}
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>

                <div style={{ marginBottom: 12 }}>
                    <label>Password</label>
                    <input
                        style={{ width: "100%", padding: 8 }}
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>

                {error && <div style={{ color: "crimson", marginBottom: 12 }}>{error}</div>}

                <button style={{ padding: "8px 12px" }}>Entrar</button>
            </form>

            <p style={{ marginTop: 16, opacity: 0.7 }}>
                admin@demo.local / admin123
            </p>
        </div>
    );
}
