export default function Modal({ open, title, children, onClose }) {
    if (!open) return null;

    return (
        <div
            onClick={onClose}
            style={{
                position: "fixed",
                inset: 0,
                background: "rgba(0,0,0,.35)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                padding: 18,
                zIndex: 9999,
            }}
        >
            <div
                onClick={(e) => e.stopPropagation()}
                style={{
                    width: "min(1100px, 100%)",
                    maxHeight: "90vh",
                    overflow: "auto",
                    background: "#fff",
                    borderRadius: 16,
                    border: "1px solid var(--line)",
                    boxShadow: "0 20px 60px rgba(0,0,0,.25)",
                    padding: 16,
                }}
            >
                <div style={{ display: "flex", justifyContent: "space-between", gap: 12, alignItems: "center" }}>
                    <div className="h1" style={{ fontSize: 24, margin: 0 }}>{title}</div>
                    <button className="btn" onClick={onClose}>Tancar</button>
                </div>

                <div style={{ marginTop: 14 }}>{children}</div>
            </div>
        </div>
    );
}
