import { useEffect, useMemo, useState } from "react";
import {
    CircleDollarSign,
    Edit3,
    Plus,
    Search,
    ShieldCheck,
    X,
} from "lucide-react";

import {
    createMetal,
    deactivateMetal,
    getMetals,
    updateMetal,
} from "../services/metalsApi";

import "./Metals.css";


function Metals() {

    const [metals, setMetals] = useState([]);

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const [error, setError] = useState("");
    const [formError, setFormError] = useState("");

    const [search, setSearch] = useState("");

    const [showModal, setShowModal] = useState(false);

    const [editingMetal, setEditingMetal] = useState(null);

    const [form, setForm] = useState({
        name: "",
        code: "",
    });


    /* =========================================================
       LOAD METALS
       ========================================================= */

    const loadMetals = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getMetals();

            setMetals(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error(err);

            setError(
                err.response?.data?.message ||
                "Unable to load metals."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {
        loadMetals();
    }, []);


    /* =========================================================
       SEARCH
       ========================================================= */

    const filteredMetals = useMemo(() => {

        const query = search.trim().toLowerCase();

        if (!query) {
            return metals;
        }

        return metals.filter((metal) =>
            metal.name?.toLowerCase().includes(query) ||
            metal.code?.toLowerCase().includes(query)
        );

    }, [metals, search]);


    /* =========================================================
       MODAL
       ========================================================= */

    const openCreateModal = () => {

        setEditingMetal(null);

        setForm({
            name: "",
            code: "",
        });

        setFormError("");
        setShowModal(true);
    };


    const openEditModal = (metal) => {

        setEditingMetal(metal);

        setForm({
            name: metal.name || "",
            code: metal.code || "",
        });

        setFormError("");
        setShowModal(true);
    };


    const closeModal = () => {

        if (saving) {
            return;
        }

        setShowModal(false);
        setEditingMetal(null);

        setForm({
            name: "",
            code: "",
        });

        setFormError("");
    };


    /* =========================================================
       FORM
       ========================================================= */

    const handleChange = (event) => {

        const { name, value } = event.target;

        setForm((previous) => ({
            ...previous,
            [name]:
                name === "code"
                    ? value.toUpperCase()
                    : value,
        }));

    };


    const handleSubmit = async (event) => {

        event.preventDefault();

        setFormError("");

        const name = form.name.trim();
        const code = form.code.trim();

        if (!name) {
            setFormError("Metal name is required.");
            return;
        }

        if (!code) {
            setFormError("Metal code is required.");
            return;
        }

        if (name.length > 50) {
            setFormError(
                "Metal name must not exceed 50 characters."
            );
            return;
        }

        if (code.length > 20) {
            setFormError(
                "Metal code must not exceed 20 characters."
            );
            return;
        }

        try {

            setSaving(true);

            if (editingMetal) {

                await updateMetal(
                    editingMetal.id,
                    {
                        name,
                        code,
                    }
                );

            } else {

                await createMetal({
                    name,
                    code,
                });

            }

            closeModal();

            await loadMetals();

        } catch (err) {

            console.error(err);

            setFormError(
                err.response?.data?.message ||
                "Unable to save metal."
            );

        } finally {

            setSaving(false);

        }
    };


    /* =========================================================
       DEACTIVATE
       ========================================================= */

    const handleDeactivate = async (metal) => {

        const confirmed = window.confirm(
            `Deactivate "${metal.name}"?`
        );

        if (!confirmed) {
            return;
        }

        try {

            setError("");

            await deactivateMetal(metal.id);

            await loadMetals();

        } catch (err) {

            console.error(err);

            setError(
                err.response?.data?.message ||
                "Unable to deactivate metal."
            );
        }
    };


    /* =========================================================
       COUNTS
       ========================================================= */

    const activeCount = metals.filter(
        (metal) => metal.active
    ).length;

    const inactiveCount = metals.filter(
        (metal) => !metal.active
    ).length;


    return (

        <div className="metals-page">

            {/* =================================================
                HEADER
               ================================================= */}

            <header className="metals-header">

                <div>

                    <div className="metals-eyebrow">
                        Master Data
                    </div>

                    <h1>
                        Metals
                    </h1>

                    <p>
                        Manage the metals used across
                        your jewellery catalogue.
                    </p>

                </div>


                <button
                    className="metal-add-button"
                    onClick={openCreateModal}
                >

                    <Plus
                        size={17}
                        strokeWidth={1.7}
                    />

                    <span>
                        Add Metal
                    </span>

                </button>

            </header>


            {/* =================================================
                SUMMARY
               ================================================= */}

            <section className="metal-summary">

                <div className="summary-card">

                    <div className="summary-icon">
                        <CircleDollarSign
                            size={17}
                            strokeWidth={1.5}
                        />
                    </div>

                    <div>

                        <span>
                            Total Metals
                        </span>

                        <strong>
                            {metals.length}
                        </strong>

                    </div>

                </div>


                <div className="summary-card">

                    <div className="summary-icon">
                        <ShieldCheck
                            size={17}
                            strokeWidth={1.5}
                        />
                    </div>

                    <div>

                        <span>
                            Active
                        </span>

                        <strong>
                            {activeCount}
                        </strong>

                    </div>

                </div>


                <div className="summary-card">

                    <div className="summary-icon muted">
                        <CircleDollarSign
                            size={17}
                            strokeWidth={1.5}
                        />
                    </div>

                    <div>

                        <span>
                            Inactive
                        </span>

                        <strong>
                            {inactiveCount}
                        </strong>

                    </div>

                </div>

            </section>


            {/* =================================================
                TOOLBAR
               ================================================= */}

            <section className="metals-toolbar">

                <div className="metal-search">

                    <Search
                        size={16}
                        strokeWidth={1.5}
                    />

                    <input
                        type="text"
                        placeholder="Search metals or codes..."
                        value={search}
                        onChange={(event) =>
                            setSearch(event.target.value)
                        }
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
                    {filteredMetals.length} result
                    {filteredMetals.length !== 1 ? "s" : ""}
                </span>

            </section>


            {/* =================================================
                ERROR
               ================================================= */}

            {error && (

                <div className="metals-error">
                    {error}
                </div>

            )}


            {/* =================================================
                CONTENT
               ================================================= */}

            <section className="metals-panel">

                <div className="metals-panel-header">

                    <div>
                        <span>
                            METAL DIRECTORY
                        </span>

                        <strong>
                            Available metals
                        </strong>
                    </div>

                </div>


                {loading ? (

                    <div className="metal-state">

                        <div className="loading-line"></div>
                        <div className="loading-line"></div>
                        <div className="loading-line"></div>

                        <p>
                            Loading metals...
                        </p>

                    </div>

                ) : filteredMetals.length === 0 ? (

                    <div className="metal-empty">

                        <CircleDollarSign
                            size={30}
                            strokeWidth={1}
                        />

                        <h2>
                            {search
                                ? "No metals found"
                                : "No metals yet"}
                        </h2>

                        <p>
                            {search
                                ? "Try a different search term."
                                : "Create your first metal to start building the master data."
                            }
                        </p>

                        {!search && (
                            <button
                                onClick={openCreateModal}
                                className="empty-add-button"
                            >
                                <Plus size={15} />
                                Add Metal
                            </button>
                        )}

                    </div>

                ) : (

                    <div className="metal-table-wrapper">

                        <table className="metal-table">

                            <thead>

                            <tr>

                                <th>
                                    Metal
                                </th>

                                <th>
                                    Code
                                </th>

                                <th>
                                    Status
                                </th>

                                <th>
                                    Updated
                                </th>

                                <th>
                                    Actions
                                </th>

                            </tr>

                            </thead>

                            <tbody>

                            {filteredMetals.map((metal) => (

                                <tr key={metal.id}>

                                    <td>

                                        <div className="metal-name-cell">

                                            <div className="metal-symbol">
                                                {metal.name
                                                    ?.charAt(0)
                                                    ?.toUpperCase()}
                                            </div>

                                            <div>

                                                <strong>
                                                    {metal.name}
                                                </strong>

                                                <span>
                                                        ID #{metal.id}
                                                    </span>

                                            </div>

                                        </div>

                                    </td>


                                    <td>

                                            <span className="metal-code">
                                                {metal.code}
                                            </span>

                                    </td>


                                    <td>

                                            <span
                                                className={
                                                    metal.active
                                                        ? "status active"
                                                        : "status inactive"
                                                }
                                            >

                                                <i></i>

                                                {metal.active
                                                    ? "Active"
                                                    : "Inactive"}

                                            </span>

                                    </td>


                                    <td>

                                            <span className="updated-date">

                                                {metal.updatedAt
                                                    ? new Date(
                                                        metal.updatedAt
                                                    ).toLocaleDateString(
                                                        "en-IN",
                                                        {
                                                            day: "2-digit",
                                                            month: "short",
                                                            year: "numeric",
                                                        }
                                                    )
                                                    : "—"}

                                            </span>

                                    </td>


                                    <td>

                                        <div className="metal-actions">

                                            <button
                                                type="button"
                                                className="icon-action"
                                                title="Edit metal"
                                                onClick={() =>
                                                    openEditModal(
                                                        metal
                                                    )
                                                }
                                            >

                                                <Edit3
                                                    size={15}
                                                    strokeWidth={1.5}
                                                />

                                            </button>


                                            {metal.active && (

                                                <button
                                                    type="button"
                                                    className="deactivate-action"
                                                    onClick={() =>
                                                        handleDeactivate(
                                                            metal
                                                        )
                                                    }
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


            {/* =================================================
                MODAL
               ================================================= */}

            {showModal && (

                <div
                    className="metal-modal-backdrop"
                    onMouseDown={(event) => {

                        if (
                            event.target ===
                            event.currentTarget
                        ) {
                            closeModal();
                        }

                    }}
                >

                    <div className="metal-modal">

                        <div className="modal-header">

                            <div>

                                <span>
                                    {editingMetal
                                        ? "EDIT METAL"
                                        : "NEW METAL"}
                                </span>

                                <h2>
                                    {editingMetal
                                        ? "Update metal"
                                        : "Add a metal"}
                                </h2>

                            </div>


                            <button
                                type="button"
                                className="modal-close"
                                onClick={closeModal}
                            >

                                <X
                                    size={18}
                                    strokeWidth={1.5}
                                />

                            </button>

                        </div>


                        <form
                            className="metal-form"
                            onSubmit={handleSubmit}
                        >

                            <div className="form-field">

                                <label htmlFor="metal-name">
                                    Metal name
                                </label>

                                <input
                                    id="metal-name"
                                    name="name"
                                    type="text"
                                    value={form.name}
                                    onChange={handleChange}
                                    placeholder="e.g. Gold"
                                    maxLength={50}
                                    autoFocus
                                />

                                <span className="field-hint">
                                    Maximum 50 characters
                                </span>

                            </div>


                            <div className="form-field">

                                <label htmlFor="metal-code">
                                    Metal code
                                </label>

                                <input
                                    id="metal-code"
                                    name="code"
                                    type="text"
                                    value={form.code}
                                    onChange={handleChange}
                                    placeholder="e.g. GOLD"
                                    maxLength={20}
                                />

                                <span className="field-hint">
                                    Short unique identifier
                                </span>

                            </div>


                            {formError && (

                                <div className="form-error">
                                    {formError}
                                </div>

                            )}


                            <div className="modal-footer">

                                <button
                                    type="button"
                                    className="cancel-button"
                                    onClick={closeModal}
                                    disabled={saving}
                                >
                                    Cancel
                                </button>

                                <button
                                    type="submit"
                                    className="save-button"
                                    disabled={saving}
                                >

                                    {saving
                                        ? "Saving..."
                                        : editingMetal
                                            ? "Save Changes"
                                            : "Create Metal"}

                                </button>

                            </div>

                        </form>

                    </div>

                </div>

            )}

        </div>
    );
}

export default Metals;