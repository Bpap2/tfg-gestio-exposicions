import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/client";
import Layout from "../components/Layout";
import ColumnManager from "../components/ColumnManager";
import { artworkSections } from "../forms/artworkFields";
import LenderSelect from "../components/LenderSelect";

function buildFieldCatalog() {
    const fromForm = artworkSections.flatMap((sec) =>
        sec.fields.map((f) => ({ key: f.name, label: f.label, type: f.type }))
    );
    const all = [{ key: "id", label: "ID", type: "number" }, ...fromForm];
    const seen = new Set();
    return all.filter((f) => (seen.has(f.key) ? false : (seen.add(f.key), true)));
}

const ALL_FIELDS = buildFieldCatalog();
const getType = (key) => ALL_FIELDS.find((f) => f.key === key)?.type ?? "text";
const normalize = (v) => (v === null || v === undefined ? "" : String(v));

export default function ExhibitionDetail() {
    const { id } = useParams();

    const [ex, setEx] = useState(null);
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const defaultCols = ["id", "title", "author", "yearInt", "workType", "techniqueMaterial"];
    const [selectedCols, setSelectedCols] = useState(() => {
        const saved = localStorage.getItem("exDetail.columns");
        return saved ? JSON.parse(saved) : defaultCols;
    });
    const [colsOpen, setColsOpen] = useState(false);

    const [sort, setSort] = useState({ key: "id", dir: "asc" });
    const [search, setSearch] = useState("");

    // ✅ Modal assignació prestador
    const [assignOpen, setAssignOpen] = useState(false);
    const [assignArtwork, setAssignArtwork] = useState(null);
    const [assignLenderId, setAssignLenderId] = useState(null);
    const [assignSaving, setAssignSaving] = useState(false);
    const [assignErr, setAssignErr] = useState("");

    useEffect(() => {
        localStorage.setItem("exDetail.columns", JSON.stringify(selectedCols));
    }, [selectedCols]);

    const load = async () => {
        const [exRes, awRes] = await Promise.all([
            api.get(`/api/exhibitions/${id}`),
            api.get(`/api/exhibitions/${id}/artworks`),
        ]);
        setEx(exRes.data);
        setItems(Array.isArray(awRes.data) ? awRes.data : []);
    };

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                setError("");
                await load();
            } catch (e) {
                setError("No es poden carregar les dades de l’exposició");
                console.error(e);
            } finally {
                setLoading(false);
            }
        })();
    }, [id]);

    const columns = useMemo(() => {
        const byKey = new Map(ALL_FIELDS.map((f) => [f.key, f]));
        return selectedCols
            .map((key) => {
                const meta = byKey.get(key) || { key, label: key, type: "text" };
                return {
                    key: meta.key,
                    label: meta.label,
                    type: meta.type,
                    render: (row) => {
                        const v = row?.[meta.key];
                        if (meta.type === "checkbox") return v ? "Sí" : "No";
                        if (v === null || v === undefined || v === "") return "-";
                        return String(v);
                    },
                };
            })
            .filter(Boolean);
    }, [selectedCols]);

    const filteredItems = useMemo(() => {
        let out = [...items];

        const q = search.trim().toLowerCase();
        if (q) {
            out = out.filter((it) =>
                columns.some((c) => normalize(it?.[c.key]).toLowerCase().includes(q))
            );
        }

        const { key, dir } = sort;
        const mul = dir === "asc" ? 1 : -1;

        out.sort((a, b) => {
            const t = getType(key);
            const av = a?.[key];
            const bv = b?.[key];
            if (t === "number") return (Number(av ?? 0) - Number(bv ?? 0)) * mul;
            return (
                normalize(av).toLowerCase().localeCompare(normalize(bv).toLowerCase(), "ca") * mul
            );
        });

        return out;
    }, [items, search, columns, sort]);

    const toggleSort = (key) => {
        setSort((prev) =>
            prev.key === key
                ? { key, dir: prev.dir === "asc" ? "desc" : "asc" }
                : { key, dir: "asc" }
        );
    };

    const openAssign = (artworkRow) => {
        setAssignArtwork(artworkRow);
        setAssignLenderId(artworkRow?.lenderId ?? artworkRow?.lender_id ?? null);
        setAssignErr("");
        setAssignOpen(true);
    };

    const saveAssign = async () => {
        if (!assignArtwork?.id) return;

        try {
            setAssignSaving(true);
            setAssignErr("");

            // Nota: ara mateix si tries "Sense prestador" només tanquem.
            // Si vols "desassignar", cal endpoint al backend.
            if (!assignLenderId) {
                setAssignOpen(false);
                return;
            }

            await api.post(`/api/artworks/${assignArtwork.id}/lender/${assignLenderId}`);

            await load();
            setAssignOpen(false);
        } catch (e) {
            console.error(e);
            setAssignErr("No s’ha pogut assignar el prestador");
        } finally {
            setAssignSaving(false);
        }
    };

    const Th = ({ col }) => (
        <th onClick={() => toggleSort(col.key)} style={{ cursor: "pointer", userSelect: "none" }}>
      <span style={{ display: "inline-flex", alignItems: "center", whiteSpace: "nowrap" }}>
        {col.label}{" "}
          <span style={{ marginLeft: 8, opacity: sort.key === col.key ? 1 : 0.35 }}>
          {sort.key === col.key ? (sort.dir === "asc" ? "↑" : "↓") : "↕"}
        </span>
      </span>
        </th>
    );

    return (
        <Layout title={ex?.name ? `Exposició: ${ex.name}` : `Exposició #${id}`}>
            <div className="card">
                {error && <div className="alert">{error}</div>}

                {loading ? (
                    <p className="subtle">Carregant…</p>
                ) : (
                    <>
                        <div
                            style={{
                                display: "flex",
                                flexWrap: "wrap",
                                gap: 10,
                                alignItems: "center",
                                justifyContent: "space-between",
                            }}
                        >
                            <div className="subtle">Obres associades a aquesta exposició.</div>

                            <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                                <button className="btn" onClick={() => setColsOpen(true)}>
                                    Columnes
                                </button>
                                <input
                                    className="input"
                                    style={{ width: 320 }}
                                    placeholder="Cerca dins la taula…"
                                    value={search}
                                    onChange={(e) => setSearch(e.target.value)}
                                />
                            </div>
                        </div>

                        <div className="tableWrap">
                            <table className="table">
                                <thead>
                                <tr>
                                    {columns.map((c) => (
                                        <Th key={c.key} col={c} />
                                    ))}
                                    <th>Accions</th>
                                </tr>
                                </thead>

                                <tbody>
                                {filteredItems.map((row) => (
                                    <tr key={row.id}>
                                        {columns.map((c) => (
                                            <td key={c.key}>
                          <span
                              className={c.key === "description" ? "cellTruncate" : undefined}
                              title={normalize(row?.[c.key])}
                          >
                            {c.render(row)}
                          </span>
                                            </td>
                                        ))}

                                        <td>
                                            <button className="btn" onClick={() => openAssign(row)}>
                                                Assignar prestador
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>

                        <p className="subtle" style={{ marginTop: 10 }}>
                            Resultats: {filteredItems.length} / {items.length}
                        </p>
                    </>
                )}
            </div>

            <ColumnManager
                open={colsOpen}
                onClose={() => setColsOpen(false)}
                allColumns={ALL_FIELDS.map((f) => ({ key: f.key, label: f.label }))}
                selected={selectedCols}
                setSelected={setSelectedCols}
            />

            {/* ✅ MODAL */}
            {assignOpen && (
                <div
                    style={{
                        position: "fixed",
                        inset: 0,
                        background: "rgba(0,0,0,.35)",
                        display: "grid",
                        placeItems: "center",
                        padding: 16,
                        zIndex: 50,
                    }}
                    onClick={() => !assignSaving && setAssignOpen(false)}
                >
                    <div
                        className="card"
                        style={{ width: "min(720px, 96vw)" }}
                        onClick={(e) => e.stopPropagation()}
                    >
                        <div style={{ display: "flex", justifyContent: "space-between", gap: 12, alignItems: "center" }}>
                            <div style={{ fontWeight: 800 }}>Assignar prestador</div>

                            <button className="btn" onClick={() => !assignSaving && setAssignOpen(false)}>
                                Tancar
                            </button>
                        </div>

                        <div className="subtle" style={{ marginTop: 10 }}>
                            Obra: <b>{assignArtwork?.title ?? `#${assignArtwork?.id}`}</b>
                        </div>

                        <div style={{ marginTop: 12 }}>
                            <LenderSelect value={assignLenderId} onChange={setAssignLenderId} />
                        </div>

                        {assignErr && <div className="alert" style={{ marginTop: 12 }}>{assignErr}</div>}

                        <div style={{ display: "flex", justifyContent: "flex-end", gap: 10, marginTop: 14 }}>
                            <button className="btn" disabled={assignSaving} onClick={() => setAssignOpen(false)}>
                                Cancel·lar
                            </button>
                            <button className="btn" disabled={assignSaving} onClick={saveAssign}>
                                {assignSaving ? "Guardant…" : "Guardar"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </Layout>
    );
}
