import { useEffect, useMemo, useState } from "react";
import api from "../api/client";

export default function LenderSelect({ value, onChange }) {
    const [items, setItems] = useState([]);
    const [q, setQ] = useState("");
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                setErr("");
                const res = await api.get("/api/lenders");
                setItems(res.data || []);
            } catch (e) {
                console.error(e);
                setErr("No s’han pogut carregar els prestadors");
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const filtered = useMemo(() => {
        const query = q.trim().toLowerCase();
        if (!query) return items;

        return items.filter((l) => {
            const name = (l?.name ?? "").toLowerCase();
            const code = (l?.code ?? "").toLowerCase();
            const email = (l?.email ?? "").toLowerCase();
            return name.includes(query) || code.includes(query) || email.includes(query);
        });
    }, [items, q]);

    return (
        <div>
            <label className="subtle">Prestador</label>

            <div style={{ display: "flex", gap: 10, flexWrap: "wrap", marginTop: 8 }}>
                <input
                    className="input"
                    style={{ flex: "1 1 280px" }}
                    placeholder="Cerca per nom / codi / email…"
                    value={q}
                    onChange={(e) => setQ(e.target.value)}
                />

                <select
                    className="input"
                    style={{ flex: "1 1 280px" }}
                    value={value ?? ""}
                    onChange={(e) => onChange(e.target.value ? Number(e.target.value) : null)}
                    disabled={loading}
                >
                    <option value="">— Sense prestador —</option>
                    {filtered.map((l) => (
                        <option key={l.id} value={l.id}>
                            {l.name}
                            {l.code ? ` (${l.code})` : ""}
                        </option>
                    ))}
                </select>
            </div>

            {err && <div className="alert" style={{ marginTop: 10 }}>{err}</div>}
            {loading && <div className="subtle" style={{ marginTop: 10 }}>Carregant prestadors…</div>}
        </div>
    );
}