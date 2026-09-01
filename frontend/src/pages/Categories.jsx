import { useEffect, useState } from "react";
import {
    Plus,
    Search,
    Pencil,
    Trash2,
    X,
} from "lucide-react";

import {
    getCategories,
    createCategory,
    updateCategory,
    deleteCategory,
} from "../services/categoryApi";

import "./Categories.css";

function Categories() {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const [search, setSearch] = useState("");

    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);

    const [form, setForm] = useState({
        name: "",
        code: "",
    });

    useEffect(() => {
        loadCategories();
    }, []);

    async function loadCategories() {
        try {
            setLoading(true);
            setError("");

            const data = await getCategories();

            setCategories(data || []);
        } catch (err) {
            console.error(err);

            setError(
                err.response?.data?.message ||
                "Unable to load categories."
            );
        } finally {
            setLoading(false);
        }
    }

    function openCreateForm() {
        setEditingId(null);

        setForm({
            name: "",
            code: "",
        });

        setShowForm(true);
        setError("");
    }

    function openEditForm(category) {
        setEditingId(category.id);

        setForm({
            name: category.name || "",
            code: category.code || "",
        });

        setShowForm(true);
        setError("");
    }

    function closeForm() {
        if (saving) return;

        setShowForm(false);
        setEditingId(null);

        setForm({
            name: "",
            code: "",
        });
    }

    function handleChange(event) {
        const { name, value } = event.target;

        setForm((previous) => ({
            ...previous,
            [name]: value,
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();

        if (!form.name.trim() || !form.code.trim()) {
            setError("Category name and code are required.");
            return;
        }

        try {
            setSaving(true);
            setError("");

            if (editingId) {
                await updateCategory(editingId, {
                    name: form.name.trim(),
                    code: form.code.trim(),
                });
            } else {
                await createCategory({
                    name: form.name.trim(),
                    code: form.code.trim(),
                });
            }

            closeForm();
            await loadCategories();
        } catch (err) {
            console.error(err);

            setError(
                err.response?.data?.message ||
                "Unable to save category."
            );
        } finally {
            setSaving(false);
        }
    }

    async function handleDelete(category) {
        const confirmed = window.confirm(
            `Delete category "${category.name}"?`
        );

        if (!confirmed) return;

        try {
            setError("");

            await deleteCategory(category.id);

            await loadCategories();
        } catch (err) {
            console.error(err);

            setError(
                err.response?.data?.message ||
                "Unable to delete category."
            );
        }
    }

    const filteredCategories = categories.filter((category) => {
        const value = search.toLowerCase();

        return (
            category.name?.toLowerCase().includes(value) ||
            category.code?.toLowerCase().includes(value)
        );
    });

    return (
        <div className="categories-page">

            <div className="page-header">

                <div>
                    <div className="page-eyebrow">
                        Master Data
                    </div>

                    <h1>Categories</h1>

                    <p>
                        Manage jewellery categories used
                        throughout JewelCore.
                    </p>
                </div>

                <button
                    className="primary-button"
                    onClick={openCreateForm}
                >
                    <Plus size={17} />
                    Add Category
                </button>

            </div>

            {error && (
                <div className="page-error">
                    {error}
                </div>
            )}

            <div className="toolbar">

                <div className="search-box">

                    <Search size={17} />

                    <input
                        type="text"
                        placeholder="Search categories..."
                        value={search}
                        onChange={(event) =>
                            setSearch(event.target.value)
                        }
                    />

                </div>

                <div className="category-count">
                    {filteredCategories.length} categories
                </div>

            </div>

            <div className="category-panel">

                <div className="category-table-header">
                    <span>Category</span>
                    <span>Code</span>
                    <span>Actions</span>
                </div>

                {loading ? (
                    <div className="empty-state">
                        Loading categories...
                    </div>
                ) : filteredCategories.length === 0 ? (
                    <div className="empty-state">

                        <strong>
                            No categories found
                        </strong>

                        <span>
                            Add your first jewellery category
                            to get started.
                        </span>

                    </div>
                ) : (
                    filteredCategories.map((category) => (
                        <div
                            className="category-row"
                            key={category.id}
                        >

                            <div className="category-name">
                                {category.name}
                            </div>

                            <div className="category-code">
                                {category.code}
                            </div>

                            <div className="category-actions">

                                <button
                                    title="Edit"
                                    onClick={() =>
                                        openEditForm(category)
                                    }
                                >
                                    <Pencil size={16} />
                                </button>

                                <button
                                    title="Delete"
                                    onClick={() =>
                                        handleDelete(category)
                                    }
                                >
                                    <Trash2 size={16} />
                                </button>

                            </div>

                        </div>
                    ))
                )}

            </div>

            {showForm && (
                <div className="modal-overlay">

                    <div className="category-modal">

                        <div className="modal-header">

                            <div>
                                <div className="page-eyebrow">
                                    {editingId
                                        ? "Edit Category"
                                        : "New Category"}
                                </div>

                                <h2>
                                    {editingId
                                        ? "Update category"
                                        : "Add category"}
                                </h2>
                            </div>

                            <button
                                className="modal-close"
                                onClick={closeForm}
                                disabled={saving}
                            >
                                <X size={19} />
                            </button>

                        </div>

                        <form
                            className="category-form"
                            onSubmit={handleSubmit}
                        >

                            <div className="form-group">

                                <label>
                                    Category Name
                                </label>

                                <input
                                    name="name"
                                    value={form.name}
                                    onChange={handleChange}
                                    placeholder="e.g. Ring"
                                    autoFocus
                                    required
                                />

                            </div>

                            <div className="form-group">

                                <label>
                                    Category Code
                                </label>

                                <input
                                    name="code"
                                    value={form.code}
                                    onChange={handleChange}
                                    placeholder="e.g. RING"
                                    required
                                />

                            </div>

                            <div className="modal-actions">

                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={closeForm}
                                    disabled={saving}
                                >
                                    Cancel
                                </button>

                                <button
                                    type="submit"
                                    className="primary-button"
                                    disabled={saving}
                                >
                                    {saving
                                        ? "Saving..."
                                        : editingId
                                            ? "Update Category"
                                            : "Create Category"}
                                </button>

                            </div>

                        </form>

                    </div>

                </div>
            )}

        </div>
    );
}

export default Categories;