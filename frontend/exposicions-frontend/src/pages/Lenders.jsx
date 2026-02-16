import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { apiGet } from "../api/client";

export default function Lenders() {
    const [items, setItems] = useState([]);
    const [q, setQ] = useState("");
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let alive = true;

        (async () => {
            try {
                setLoading(true);
                setErr("");

                const data = await apiGet("/api/lenders");
                // per si algun endpoint torna {content:...} o {value:...}
                const arr = Array.isArray(data) ? data : (data?.content ?? data?.value ?? []);

                if (alive) setItems(arr);
            } catch (e) {
                if (alive) setErr(e?.message || String(e));
            } finally {
                if (alive) setLoading(false);
            }
        })();

        return () => {
            alive = false;
        };
    }, []);

    const filtered = useMemo(() => {
        const query = q.trim().toLowerCase();
        if (!query) return items;

        return items.filter((l) => {
            const name = (l?.name ?? "").toLowerCase();
            const code = (l?.code ?? "").toLowerCase();
            const count = l?.artworksCount != null ? String(l.artworksCount) : "";
            return (
                name.includes(query) ||
                code.includes(query) ||
                count.includes(query)
            );
        });
    }, [items, q]);

    return (
        <div style={{ maxWidth: 900, margin: "24px auto", fontFamily: "sans-serif" }}>
            <div style={{ display: "flex", alignItems: "baseline", justifyContent: "space-between", gap: 12 }}>
                <h2 style={{ margin: 0 }}>Prestadors</h2>
                <div style={{ opacity: 0.75 }}>
                    {!loading && !err ? `${filtered.length} resultat(s)` : ""}
                </div>
            </div>

            <input
                placeholder="Cerca per nom, codi o nº d’obres..."
                value={q}
                onChange={(e) => setQ(e.target.value)}
                style={{ width: "100%", padding: 10, margin: "12px 0" }}
            />

            {loading && <div>Carregant...</div>}
            {err && <div style={{ color: "crimson" }}>{err}</div>}

            {!loading && !err && (
                <div style={{ border: "1px solid #ddd", borderRadius: 8, overflow: "hidden" }}>
                    {filtered.map((l) => {
                        const countText =
                            l?.artworksCount != null ? `Obres: ${l.artworksCount}` : "Obres: —";

                        return (
                            <Link
                                key={l.id}
                                to={`/lenders/${l.id}`}
                                style={{
                                    display: "block",
                                    padding: 12,
                                    borderBottom: "1px solid #eee",
                                    textDecoration: "none",
                                    color: "inherit",
                                }}
                            >
                                <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
                                    <div style={{ fontWeight: 700 }}>{l.name}</div>
                                    <div style={{ opacity: 0.75 }}>{countText}</div>
                                </div>

                                <div style={{ opacity: 0.75 }}>{l.code ?? ""}</div>
                            </Link>
                        );
                    })}

                    {filtered.length === 0 && <div style={{ padding: 12 }}>Cap resultat.</div>}
                </div>
            )}
        </div>
    );
}