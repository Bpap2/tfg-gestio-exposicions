import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/client";
import Layout from "../components/Layout";

export default function Exhibitions() {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                const res = await api.get("/api/exhibitions");
                setItems(res.data);
            } catch (e) {
                setError("No es poden carregar exposicions");
                console.error(e);
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    return (
        <Layout title="Exposicions">
            <div className="card">
                {error && <div className="alert">{error}</div>}

                {loading ? (
                    <p className="subtle">Carregant…</p>
                ) : items.length === 0 ? (
                    <p className="subtle">No tens cap exposició assignada.</p>
                ) : (
                    <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))", gap: 12 }}>
                        {items.map((ex) => (
                            <Link
                                key={ex.id}
                                to={`/exhibitions/${ex.id}`}
                                className="card"
                                style={{ textDecoration: "none", color: "inherit" }}
                            >
                                <div style={{ fontWeight: 800, letterSpacing: ".02em" }}>{ex.name || `Exposició #${ex.id}`}</div>
                                <div className="subtle" style={{ marginTop: 6 }}>
                                    {ex.startDate ? `Inici: ${ex.startDate}` : ""}{ex.endDate ? ` · Final: ${ex.endDate}` : ""}
                                </div>
                                <div className="subtle" style={{ marginTop: 10 }}>
                                    Clica per veure obres i detalls →
                                </div>
                            </Link>
                        ))}
                    </div>
                )}
            </div>
        </Layout>
    );
}
