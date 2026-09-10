import { useEffect, useMemo, useState } from "react";
import {
    Percent,
    Edit3,
    Plus,
    Search,
    ShieldCheck,
    X,
} from "lucide-react";

import {
    getPurities,
    createPurity,
    updatePurity,
    deactivatePurity,
} from "../services/purityApi";

import "./Purities.css";

function Purities() {
    const [purities, setPurities] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [formError, setFormError] = useState("");
    const [search, setSearch] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [editingPurity, setEditingPurity] = useState(null);

    const [form, setForm] = useState({
        name: "",
        code: "",
        percentage: "",
    });

    useEffect(() => {
        loadPurities();
    }, []);

    const loadPurities = async () => {
        try {
            setLoading(true);
            setError("");
            const data = await getPurities();
            setPurities(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error(err);
            setError(
                err.response?.data?.message || "Unable to load purities."
            );
        } finally {
            setLoading(false);
        }
    };

    const filteredPurities = useMemo(() => {
        const query = search.trim().toLowerCase();
        if (!query) return purities;

        return purities.filter(
            (purity) =>
                purity.name?.toLowerCase().includes(query) ||
                purity.code?.toLowerCase().includes(query) ||
                String(purity.percentage).includes(query)
        );
    }, [purities, search]);

    const openCreateModal = () => {
        setEditingPurity(null);
        setForm({
            name: "",
            code: "",
            percentage: "",
        });
        setFormError("");
        setShowModal(true);
    };

    const openEditModal = (purity) => {
        setEditingPurity(purity);
        setForm({
            name: purity.name || "",
            code: purity.code || "",
            percentage: purity.percentage ?? "",
        });
        setFormError("");
        setShowModal(true);
    };

    const closeModal = () => {
        if (saving) return;
        setShowModal(false);
        setEditingPurity(null);
        setForm({
            name: "",
            code: "",
            percentage: "",
        });
        setFormError("");
    };

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((previous) => ({
            ...previous,
            [name]: name === "code" ? value.toUpperCase() : value,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setFormError("");

        const name = form.name.trim();
        const code = form.code.trim();
        const percentageNum = Number(form.percentage);

        if (!name) {
            setFormError("Purity name is required.");
            return;
        }

        if (!code) {
            setFormError("Purity code is required.");
            return;
        }

        if (isNaN(percentageNum) || percentageNum <= 0 || percentageNum > 100) {
            setFormError("Percentage must be a valid number between 0.01 and 100.");
            return;
        }

        try {
            setSaving(true);

            if (editingPurity) {
                await updatePurity(editingPurity.id, {
                    name,
                    code,
                    percentage: percentageNum,
                });
            } else {
                await createPurity({
                    name,
                    code,
                    percentage: percentageNum,
                });
            }

            closeModal();
            await loadPurities();
        } catch (err) {
            console.error(err);
            setFormError(
                err.response?.data?.message || "Unable to save purity."
            );
        } finally {
            setSaving(false);
        }
    };

    const handleDeactivate = async (purity) => {
        const confirmed = window.confirm(`Deactivate "${purity.name}"?`);
        if (!confirmed) return;

        try {
            setError("");
            await deactivatePurity(purity.id);
            await loadPurities();
        } catch (err) {
            console.error(err);
            setError(
                err.response?.data?.message || "Unable to deactivate purity."
            );
        }
    };

    const activeCount = purities.filter((p) => p.active).length;
    const inactiveCount = purities.filter((p) => !p.active).length;

    return (
        <div className="purities-page">
            {/* HEADER */}
            <header className="purities-header">
                <div>
                    <div className="purities-eyebrow">Master Data</div>
                    <h1>Purities</h1>
                    <p>
                        Configure metal purities and percentages used by the pricing engine.
                    </p>
                </div>

                <button
                    className="purity-add-button"
                    onClick={openCreateModal}
                    type="button"
                >
                    <Plus size={17} strokeWidth={1.7} />
                    <span>Add Purity</span>
                </button>
            </header>

            {/* SUMMARY CARDS */}
            <section className="purity-summary">
                <div className="summary-card">
                    <div className="summary-icon">
                        <Percent size={17} strokeWidth={1.5} />
                    </div>
                    <div>
                        <span>Total Purities</span>
                        <strong>{purities.length}</strong>
                    </div>
                </div>

                <div className="summary-card">
                    <div className="summary-icon">
                        <ShieldCheck size={17} strokeWidth={1.5} />
                    </div>
                    <div>
                        <span>Active</span>
                        <strong>{activeCount}</strong>
                    </div>
                </div>

                <div className="summary-card">
                    <div className="summary-icon muted">
                        <Percent size={17} strokeWidth={1.5} />
                    </div>
                    <div>
                        <span>Inactive</span>
                        <strong>{inactiveCount}</strong>
                    </div>
                </div>
            </section>

            {/* TOOLBAR */}
            <section className="purities-toolbar">
                <div className="purity-search">
                    <Search size={16} strokeWidth={1.5} />
                    <input
                        type="text"
                        placeholder="Search purities, codes, or percentages..."
                        value={search}
                        onChange={(event) => setSearch(event.target.value)}
                    />
                    {search && (
                        <button
                            className="clear-search"
                            onClick={() => setSearch("")}
                            type="button"
                        >
                            <X size={14} />
                        </button>
                    )}
                </div>

                <span className="result-count">
                    {filteredPurities.length} result
                    {filteredPurities.length !== 1 ? "s" : ""}
                </span>
            </section>

            {/* ERROR NOTIFICATION */}
            {error && <div className="purities-error">{error}</div>}

            {/* PURITIES LIST / TABLE */}
            <section className="purities-panel">
                {loading ? (
                    <div className="purity-state">
                        <p>Loading purities...</p>
                    </div>
                ) : filteredPurities.length === 0 ? (
                    <div className="purity-empty">
                        <Percent size={30} strokeWidth={1} />
                        <h2>{search ? "No purities found" : "No purities yet"}</h2>
                        <p>
                            {search
                                ? "Try a different search query."
                                : "Create your first purity (e.g., 22K @ 91.60%) to enable accurate pricing."}
                        </p>
                    </div>
                ) : (
                    <div className="purities-table-wrapper">
                        <table className="purities-table">
                            <thead>
                                <tr>
                                    <th>Purity</th>
                                    <th>Code</th>
                                    <th>Percentage</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredPurities.map((purity) => (
                                    <tr key={purity.id}>
                                        <td>
                                            <div className="purity-name-cell">
                                                <div className="purity-symbol">
                                                    {purity.code || "%"}
                                                </div>
                                                <div>
                                                    <strong>{purity.name}</strong>
                                                    <span>ID #{purity.id}</span>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span className="purity-code">{purity.code}</span>
                                        </td>
                                        <td>
                                            <span className="purity-percentage-badge">
                                                {Number(purity.percentage).toFixed(2)}%
                                            </span>
                                        </td>
                                        <td>
                                            <span
                                                className={
                                                    purity.active
                                                        ? "status active"
                                                        : "status inactive"
                                                }
                                            >
                                                <i></i>
                                                {purity.active ? "Active" : "Inactive"}
                                            </span>
                                        </td>
                                        <td>
                                            <div className="purity-actions">
                                                <button
                                                    type="button"
                                                    className="icon-action"
                                                    title="Edit purity"
                                                    onClick={() => openEditModal(purity)}
                                                >
                                                    <Edit3 size={15} strokeWidth={1.5} />
                                                </button>
                                                {purity.active && (
                                                    <button
                                                        type="button"
                                                        className="deactivate-action"
                                                        onClick={() => handleDeactivate(purity)}
                                                    >
                                                        Deactivate
                                                    </button>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            {/* MODAL (ADD / EDIT) */}
            {showModal && (
                <div
                    className="purity-modal-backdrop"
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget) {
                            closeModal();
                        }
                    }}
                >
                    <div className="purity-modal">
                        <div className="modal-header">
                            <div>
                                <span>{editingPurity ? "EDIT PURITY" : "NEW PURITY"}</span>
                                <h2>{editingPurity ? "Update purity" : "Add purity"}</h2>
                            </div>
                            <button
                                type="button"
                                className="modal-close"
                                onClick={closeModal}
                                disabled={saving}
                            >
                                <X size={18} strokeWidth={1.5} />
                            </button>
                        </div>

                        <form className="purity-form" onSubmit={handleSubmit}>
                            {formError && <div className="purities-error">{formError}</div>}

                            <div className="form-field">
                                <label htmlFor="purity-name">Purity Name</label>
                                <input
                                    id="purity-name"
                                    name="name"
                                    type="text"
                                    value={form.name}
                                    onChange={handleChange}
                                    placeholder="e.g. 22 Karat Gold"
                                    maxLength={100}
                                    autoFocus
                                    required
                                />
                                <span className="field-hint">
                                    Display name for catalogues and bills
                                </span>
                            </div>

                            <div className="form-field">
                                <label htmlFor="purity-code">Purity Code</label>
                                <input
                                    id="purity-code"
                                    name="code"
                                    type="text"
                                    value={form.code}
                                    onChange={handleChange}
                                    placeholder="e.g. 22K"
                                    maxLength={20}
                                    required
                                />
                                <span className="field-hint">
                                    Unique short code (e.g. 24K, 22K, 18K, 925)
                                </span>
                            </div>

                            <div className="form-field">
                                <label htmlFor="purity-percentage">
                                    Purity Percentage (%)
                                </label>
                                <input
                                    id="purity-percentage"
                                    name="percentage"
                                    type="number"
                                    step="0.01"
                                    min="0.01"
                                    max="100.00"
                                    value={form.percentage}
                                    onChange={handleChange}
                                    placeholder="e.g. 91.60"
                                    required
                                />
                                <span className="field-hint">
                                    Used directly by the Pricing Engine for pure metal calculations
                                </span>
                            </div>

                            <div className="modal-actions">
                                <button
                                    type="button"
                                    className="btn-secondary"
                                    onClick={closeModal}
                                    disabled={saving}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="btn-primary"
                                    disabled={saving}
                                >
                                    {saving
                                        ? "Saving..."
                                        : editingPurity
                                        ? "Update Purity"
                                        : "Create Purity"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Purities;
