import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { apiGet } from "../api/client";
import api from "../api/client";

export default function LenderDetail() {
    const { id } = useParams();
    const [lender, setLender] = useState(null);
    const [arts, setArts] = useState([]);
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(true);

    async function openArtworkPdf(artworkId) {
        try {
            const res = await api.get(`/api/artworks/${artworkId}/report.pdf`, {
                responseType: "blob",
            });

            const blob = new Blob([res.data], { type: "application/pdf" });
            const url = URL.createObjectURL(blob);

            window.open(url, "_blank");
            setTimeout(() => URL.revokeObjectURL(url), 60_000);
        } catch (e) {
            console.error(e);
            alert("No s'ha pogut generar el PDF (401/permís o error al backend).");
        }
    }

    useEffect(() => {
        let alive = true;
        (async () => {
            try {
                setLoading(true);
                setErr("");

                const [l, a] = await Promise.all([
                    apiGet(`/api/lenders/${id}`),
                    apiGet(`/api/lenders/${id}/artworks`),
                ]);

                const arr = Array.isArray(a) ? a : (a?.content ?? a?.value ?? []);
                if (!alive) return;

                setLender(l);
                setArts(arr);
            } catch (e) {
                if (alive) setErr(e?.message || String(e));
            } finally {
                if (alive) setLoading(false);
            }
        })();

        return () => {
            alive = false;
        };
    }, [id]);

    return (
        <div style={{ maxWidth: 900, margin: "24px auto", fontFamily: "sans-serif" }}>
            <Link to="/lenders">← Tornar</Link>

            {loading && <div style={{ marginTop: 12 }}>Carregant...</div>}
            {err && <div style={{ marginTop: 12, color: "crimson" }}>{err}</div>}

            {!loading && !err && lender && (
                <>
                    <h2 style={{ marginBottom: 4 }}>{lender.name}</h2>
                    <div style={{ opacity: 0.75, marginBottom: 16 }}>{lender.code ?? ""}</div>

                    <div style={{ marginBottom: 16 }}>
                        {lender.email && <div>Email: {lender.email}</div>}
                        {lender.phone && <div>Tel: {lender.phone}</div>}
                        {lender.notes && <div>Notes: {lender.notes}</div>}
                    </div>

                    <h3>Obres ({arts.length})</h3>

                    <div style={{ border: "1px solid #ddd", borderRadius: 8, overflow: "hidden" }}>
                        {arts.map((a) => (
                            <div key={a.id} style={{ padding: 12, borderBottom: "1px solid #eee" }}>
                                <div
                                    style={{
                                        display: "flex",
                                        justifyContent: "space-between",
                                        gap: 12,
                                        alignItems: "center",
                                    }}
                                >
                                    <div>
                                        <div style={{ fontWeight: 700 }}>{a.title || "Sense títol"}</div>
                                        <div style={{ opacity: 0.75 }}>
                                            {a.author ? `${a.author} ` : ""}
                                            {a.workNo != null ? `· #${a.workNo}` : ""}
                                        </div>
                                    </div>

                                    <button
                                        onClick={() => openArtworkPdf(a.id)}
                                        style={{
                                            padding: "8px 10px",
                                            border: "1px solid #ccc",
                                            borderRadius: 8,
                                            background: "white",
                                            cursor: "pointer",
                                            whiteSpace: "nowrap",
                                        }}
                                        title="Generar i obrir PDF"
                                    >
                                        Exportar PDF
                                    </button>
                                </div>
                            </div>
                        ))}

                        {arts.length === 0 && (
                            <div style={{ padding: 12 }}>Aquest prestador no té obres linkades.</div>
                        )}
                    </div>
                </>
            )}
        </div>
    );
}
