import Modal from "./Modal";

function move(arr, from, to) {
    const copy = [...arr];
    const [item] = copy.splice(from, 1);
    copy.splice(to, 0, item);
    return copy;
}

export default function ColumnManager({
                                          open,
                                          onClose,
                                          allColumns,
                                          selected,
                                          setSelected,
                                      }) {
    const isSelected = (key) => selected.includes(key);

    const toggle = (key) => {
        setSelected((prev) => {
            if (prev.includes(key)) return prev.filter((k) => k !== key);
            return [...prev, key];
        });
    };

    const up = (idx) => {
        if (idx <= 0) return;
        setSelected((prev) => move(prev, idx, idx - 1));
    };

    const down = (idx) => {
        if (idx >= selected.length - 1) return;
        setSelected((prev) => move(prev, idx, idx + 1));
    };

    return (
        <Modal open={open} onClose={onClose} title="Columnes visibles">
            <div className="subtle" style={{ marginBottom: 10 }}>
                Marca columnes i ordena-les amb ↑ ↓. (Es guarda al navegador)
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
                <div>
                    <div className="label" style={{ marginBottom: 8 }}>Disponibles</div>
                    <div style={{ display: "grid", gap: 6 }}>
                        {allColumns.map((c) => (
                            <label key={c.key} style={{ display: "flex", gap: 10, alignItems: "center" }}>
                                <input
                                    type="checkbox"
                                    checked={isSelected(c.key)}
                                    onChange={() => toggle(c.key)}
                                />
                                <span>{c.label}</span>
                            </label>
                        ))}
                    </div>
                </div>

                <div>
                    <div className="label" style={{ marginBottom: 8 }}>Ordre actual</div>
                    <div style={{ display: "grid", gap: 6 }}>
                        {selected.map((key, idx) => {
                            const col = allColumns.find((c) => c.key === key);
                            return (
                                <div key={key} style={{ display: "flex", gap: 8, alignItems: "center" }}>
                                    <div style={{ flex: 1 }}>
                                        {col ? col.label : key}
                                    </div>
                                    <button className="btn" onClick={() => up(idx)} title="Pujar">↑</button>
                                    <button className="btn" onClick={() => down(idx)} title="Baixar">↓</button>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>

            <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 14 }}>
                <button className="btn btnPrimary" onClick={onClose}>Fet</button>
            </div>
        </Modal>
    );
}
