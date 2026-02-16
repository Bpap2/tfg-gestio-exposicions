import { useMemo } from "react";
import { useForm } from "react-hook-form";
import { artworkSections } from "../forms/artworkFields";

function cleanPayload(values) {
    const out = { ...values };

    // neteja: "" -> null, NaN -> null
    for (const k of Object.keys(out)) {
        const v = out[k];
        if (v === "") out[k] = null;
        if (typeof v === "number" && Number.isNaN(v)) out[k] = null;
    }

    // checkbox: si undefined, false
    if (out.imageAtCccb == null) out.imageAtCccb = false;

    return out;
}

export default function ArtworkForm({ initialValues, onSubmit, onCancel }) {
    const defaults = useMemo(
        () => ({
            title: "",
            imageAtCccb: false,
            ...initialValues,
        }),
        [initialValues]
    );

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm({ defaultValues: defaults });

    const submit = async (data) => {
        await onSubmit(cleanPayload(data));
    };

    return (
        <form onSubmit={handleSubmit(submit)}>
            {artworkSections.map((sec) => (
                <section key={sec.title} style={{ marginBottom: 18 }}>
                    <div className="label" style={{ marginBottom: 10 }}>
                        {sec.title}
                    </div>

                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
                            gap: 12,
                        }}
                    >
                        {sec.fields.map((f) => (
                            <div key={f.name}>
                                <label className="label">
                                    {f.label}
                                    {f.required ? " *" : ""}
                                </label>

                                {f.type === "textarea" ? (
                                    <textarea
                                        className="input"
                                        style={{ minHeight: 96 }}
                                        {...register(f.name, { required: f.required ? "Obligatori" : false })}
                                    />
                                ) : f.type === "checkbox" ? (
                                    <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "8px 0" }}>
                                        <input type="checkbox" {...register(f.name)} />
                                        <span className="subtle">Sí/No</span>
                                    </div>
                                ) : (
                                    <input
                                        className="input"
                                        type={f.type}
                                        {...register(f.name, {
                                            required: f.required ? "Obligatori" : false,
                                            valueAsNumber: f.type === "number",
                                        })}
                                    />
                                )}

                                {errors[f.name] && (
                                    <div className="subtle" style={{ color: "#b00020", marginTop: 6 }}>
                                        {errors[f.name].message}
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>

                    <div style={{ borderBottom: "1px solid var(--line)", marginTop: 16 }} />
                </section>
            ))}

            <div style={{ display: "flex", justifyContent: "flex-end", gap: 10 }}>
                <button type="button" className="btn" onClick={onCancel}>
                    Cancel·la
                </button>
                <button className="btn btnPrimary" disabled={isSubmitting} type="submit">
                    Guardar
                </button>
            </div>
        </form>
    );
}
