import { useEffect, useMemo, useState } from "react";
import api from "../api/client";
import Layout from "../components/Layout";
import Modal from "../components/Modal";
import ArtworkForm from "../components/ArtworkForm";
import ColumnManager from "../components/ColumnManager";
import { artworkSections } from "../forms/artworkFields";

const SortIcon = ({ active, dir }) => (
    <span style={{ marginLeft: 8, opacity: active ? 1 : 0.35, lineHeight: 1 }}>
    {active ? (dir === "asc" ? "↑" : "↓") : "↕"}
  </span>
);

// Catàleg de camps (id + tots els del formulari)
function buildFieldCatalog() {
    const fromForm = artworkSections.flatMap((sec) =>
        sec.fields.map((f) => ({ key: f.name, label: f.label, type: f.type }))
    );

    // afegeix id com a camp especial
    const all = [{ key: "id", label: "ID", type: "number" }, ...fromForm];

    // dedupe per key
    const seen = new Set();
    return all.filter((f) => {
        if (seen.has(f.key)) return false;
        seen.add(f.key);
        return true;
    });
}

const ALL_FIELDS = buildFieldCatalog();

// helpers
const getType = (key) => ALL_FIELDS.find((f) => f.key === key)?.type ?? "text";

function normalize(v) {
    if (v === null || v === undefined) return "";
    return String(v);
}

function matchRule(item, rule) {
    const { field, op, value } = rule;
    const t = getType(field);
    const raw = item?.[field];

    // empties
    if (op === "isEmpty") return raw === null || raw === undefined || String(raw).trim() === "";
    if (op === "notEmpty") return !(raw === null || raw === undefined || String(raw).trim() === "");

    // checkbox boolean
    if (t === "checkbox") {
        if (op === "isTrue") return Boolean(raw) === true;
        if (op === "isFalse") return Boolean(raw) === false;
    }

    // numbers
    if (t === "number") {
        const n = raw === null || raw === undefined || raw === "" ? null : Number(raw);
        const v = value === "" || value === null || value === undefined ? null : Number(value);
        if (n === null || Number.isNaN(n)) return false;
        if (v === null || Number.isNaN(v)) return false;

        if (op === "eq") return n === v;
        if (op === "gt") return n > v;
        if (op === "gte") return n >= v;
        if (op === "lt") return n < v;
        if (op === "lte") return n <= v;
        return false;
    }

    // text ops
    const s = normalize(raw).toLowerCase();
    const q = normalize(value).toLowerCase();

    if (op === "contains") return s.includes(q);
    if (op === "equals") return s === q;
    if (op === "starts") return s.startsWith(q);
    if (op === "ends") return s.endsWith(q);
    return true;
}

export default function Artworks() {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // ordenar
    const [sort, setSort] = useState({ key: "id", dir: "asc" });

    // modal editar/crear
    const [editOpen, setEditOpen] = useState(false);
    const [editing, setEditing] = useState(null);

    // gestor columnes
    const [colsOpen, setColsOpen] = useState(false);

    // preferències (columns)
    const defaultCols = ["id", "title", "author", "yearInt", "workType", "techniqueMaterial"];
    const [selectedCols, setSelectedCols] = useState(() => {
        const saved = localStorage.getItem("artworks.columns");
        return saved ? JSON.parse(saved) : defaultCols;
    });

    // filtres (rules)
    const [filterOpen, setFilterOpen] = useState(false);
    const [rules, setRules] = useState(() => {
        const saved = localStorage.getItem("artworks.filters");
        return saved ? JSON.parse(saved) : [];
    });

    // cerca global (opcional)
    const [search, setSearch] = useState("");

    useEffect(() => {
        localStorage.setItem("artworks.columns", JSON.stringify(selectedCols));
    }, [selectedCols]);

    useEffect(() => {
        localStorage.setItem("artworks.filters", JSON.stringify(rules));
    }, [rules]);

    const load = async () => {
        const res = await api.get("/api/artworks");
        setItems(res.data);
    };

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                await load();
            } catch {
                setError("No es poden carregar obres");
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const toggleSort = (key) => {
        setSort((prev) => {
            if (prev.key === key) return { key, dir: prev.dir === "asc" ? "desc" : "asc" };
            return { key, dir: "asc" };
        });
    };

    const columns = useMemo(() => {
        // column defs (label + render)
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

        // aplica rules (AND)
        if (rules.length > 0) {
            out = out.filter((it) => rules.every((r) => matchRule(it, r)));
        }

        // cerca global (sobre columnes visibles)
        const q = search.trim().toLowerCase();
        if (q) {
            out = out.filter((it) =>
                columns.some((c) => normalize(it?.[c.key]).toLowerCase().includes(q))
            );
        }

        // sort
        const { key, dir } = sort;
        const mul = dir === "asc" ? 1 : -1;

        out.sort((a, b) => {
            const ta = getType(key);
            const av = a?.[key];
            const bv = b?.[key];

            if (ta === "number") return ((Number(av ?? 0) - Number(bv ?? 0)) * mul);

            const as = normalize(av).toLowerCase();
            const bs = normalize(bv).toLowerCase();
            return as.localeCompare(bs, "ca") * mul;
        });

        return out;
    }, [items, rules, search, columns, sort]);

    const openCreate = () => {
        setEditing({ title: "", imageAtCccb: false });
        setEditOpen(true);
    };

    const openEdit = async (row) => {
        try {
            const res = await api.get(`/api/artworks/${row.id}`);
            setEditing(res.data);
            setEditOpen(true);
        } catch {
            setError("No s'ha pogut carregar l'obra");
        }
    };

    const save = async (data) => {
        setError("");
        try {
            if (editing?.id) await api.put(`/api/artworks/${editing.id}`, data);
            else await api.post(`/api/artworks`, data);

            setEditOpen(false);
            setEditing(null);
            await load();
        } catch {
            setError("Error guardant l'obra");
        }
    };

    const remove = async (row) => {
        if (!confirm(`Eliminar l'obra "${row.title}"?`)) return;
        try {
            await api.delete(`/api/artworks/${row.id}`);
            await load();
        } catch {
            setError("Error eliminant l'obra");
        }
    };

    // UI filtres
    const addRule = () => {
        setRules((prev) => [
            ...prev,
            { field: "title", op: "contains", value: "" },
        ]);
    };

    const updateRule = (idx, patch) => {
        setRules((prev) => prev.map((r, i) => (i === idx ? { ...r, ...patch } : r)));
    };

    const deleteRule = (idx) => {
        setRules((prev) => prev.filter((_, i) => i !== idx));
    };

    const opOptions = (field) => {
        const t = getType(field);
        if (t === "number") {
            return [
                { v: "eq", label: "=" },
                { v: "gt", label: ">" },
                { v: "gte", label: ">=" },
                { v: "lt", label: "<" },
                { v: "lte", label: "<=" },
                { v: "isEmpty", label: "és buit" },
                { v: "notEmpty", label: "no és buit" },
            ];
        }
        if (t === "checkbox") {
            return [
                { v: "isTrue", label: "és Sí" },
                { v: "isFalse", label: "és No" },
            ];
        }
        return [
            { v: "contains", label: "conté" },
            { v: "equals", label: "igual a" },
            { v: "starts", label: "comença per" },
            { v: "ends", label: "acaba en" },
            { v: "isEmpty", label: "és buit" },
            { v: "notEmpty", label: "no és buit" },
        ];
    };

    const RuleValueInput = ({ rule, idx }) => {
        const t = getType(rule.field);
        if (rule.op === "isEmpty" || rule.op === "notEmpty" || t === "checkbox") return null;

        return (
            <input
                className="input"
                style={{ minWidth: 220 }}
                type={t === "number" ? "number" : "text"}
                value={rule.value ?? ""}
                onChange={(e) => updateRule(idx, { value: e.target.value })}
                placeholder="valor…"
            />
        );
    };

    const Th = ({ col }) => (
        <th
            onClick={() => toggleSort(col.key)}
            style={{ cursor: "pointer", userSelect: "none" }}
        >
      <span style={{ display: "inline-flex", alignItems: "center", whiteSpace: "nowrap" }}>
        {col.label} <SortIcon active={sort.key === col.key} dir={sort.dir} />
      </span>
        </th>
    );

    return (
        <Layout title="Obres">
            <div className="card">
                <div style={{ display: "flex", flexWrap: "wrap", gap: 10, alignItems: "center", justifyContent: "space-between" }}>
                    <div style={{ display: "flex", gap: 10, flexWrap: "wrap", alignItems: "center" }}>
                        <button className="btn" onClick={() => setColsOpen(true)}>Columnes</button>
                        <button className="btn" onClick={() => setFilterOpen((v) => !v)}>
                            Filtres {rules.length ? `(${rules.length})` : ""}
                        </button>
                        <input
                            className="input"
                            style={{ width: 320 }}
                            placeholder="Cerca global (sobre columnes visibles)…"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>

                    <div style={{ display: "flex", gap: 10 }}>
                        <button className="btn btnPrimary" onClick={openCreate}>Nova obra</button>
                    </div>
                </div>

                {filterOpen && (
                    <div style={{ marginTop: 12, borderTop: "1px solid var(--line)", paddingTop: 12 }}>
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 12 }}>
                            <div className="label">Filtres (AND)</div>
                            <div style={{ display: "flex", gap: 10 }}>
                                <button className="btn" onClick={addRule}>+ Afegir filtre</button>
                                <button className="btn" onClick={() => setRules([])}>Netejar</button>
                            </div>
                        </div>

                        {rules.length === 0 ? (
                            <p className="subtle" style={{ marginTop: 8 }}>Cap filtre aplicat.</p>
                        ) : (
                            <div style={{ display: "grid", gap: 8, marginTop: 10 }}>
                                {rules.map((r, idx) => (
                                    <div key={idx} style={{ display: "flex", gap: 8, flexWrap: "wrap", alignItems: "center" }}>
                                        <select
                                            className="input"
                                            value={r.field}
                                            onChange={(e) => {
                                                const field = e.target.value;
                                                const ops = opOptions(field);
                                                updateRule(idx, { field, op: ops[0].v, value: "" });
                                            }}
                                        >
                                            {ALL_FIELDS.map((f) => (
                                                <option key={f.key} value={f.key}>{f.label}</option>
                                            ))}
                                        </select>

                                        <select
                                            className="input"
                                            value={r.op}
                                            onChange={(e) => updateRule(idx, { op: e.target.value })}
                                        >
                                            {opOptions(r.field).map((o) => (
                                                <option key={o.v} value={o.v}>{o.label}</option>
                                            ))}
                                        </select>

                                        <RuleValueInput rule={r} idx={idx} />

                                        <button className="btn" onClick={() => deleteRule(idx)}>Eliminar</button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {error && <div className="alert">{error}</div>}

                {loading ? (
                    <p className="subtle" style={{ marginTop: 12 }}>Carregant…</p>
                ) : (
                    <div className="tableWrap">
                        <table className="table">
                            <thead>
                            <tr>
                                {columns.map((c) => <Th key={c.key} col={c} />)}
                                <th style={{ width: 220 }}>Accions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {filteredItems.map((row) => (
                                <tr key={row.id}>
                                    {columns.map((c) => (
                                        <td key={c.key}>
                        <span className={c.type === "textarea" || c.key === "description" ? "cellTruncate" : undefined} title={normalize(row?.[c.key])}>
                          {c.render(row)}
                        </span>
                                        </td>
                                    ))}
                                    <td>
                                        <div style={{ display: "flex", gap: 10, justifyContent: "flex-end" }}>
                                            <button className="btn" onClick={() => openEdit(row)}>Editar</button>
                                            <button className="btn" onClick={() => remove(row)}>Eliminar</button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {!loading && (
                    <p className="subtle" style={{ marginTop: 10 }}>
                        Resultats: {filteredItems.length} / {items.length}
                    </p>
                )}
            </div>

            {/* modal columnes */}
            <ColumnManager
                open={colsOpen}
                onClose={() => setColsOpen(false)}
                allColumns={ALL_FIELDS.map((f) => ({ key: f.key, label: f.label }))}
                selected={selectedCols}
                setSelected={setSelectedCols}
            />

            {/* modal editar/crear */}
            <Modal
                open={editOpen}
                onClose={() => { setEditOpen(false); setEditing(null); }}
                title={editing?.id ? `Editar obra #${editing.id}` : "Nova obra"}
            >
                <ArtworkForm
                    initialValues={editing}
                    onSubmit={save}
                    onCancel={() => { setEditOpen(false); setEditing(null); }}
                />
            </Modal>
        </Layout>
    );
}
