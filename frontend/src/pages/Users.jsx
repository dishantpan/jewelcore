import { useEffect, useMemo, useState, useRef } from "react";
import {
    UserPlus,
    Edit3,
    ShieldCheck,
    Search,
    X,
    ShieldAlert,
} from "lucide-react";

import {
    getUsers,
    createUser,
    deactivateUser,
} from "../services/userApi";

import "./Users.css";

function Users() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [formError, setFormError] = useState("");
    const [search, setSearch] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [editingUser, setEditingUser] = useState(null);

const [form, setForm] = useState({
        username: "",
        password: "",
        role: "SALESPERSON",
    });

    const mountedRef = useRef(false);

    const loadUsers = async () => {
        if (mountedRef.current) return;
        mountedRef.current = true;

        try {
            setLoading(true);
            setError("");
            const data = await getUsers();

            if (!mountedRef.current) return;
            setUsers(Array.isArray(data) ? data : []);
        } catch (err) {
            if (!mountedRef.current) return;
            console.error(err);
            setError(
                err.response?.data?.message || "Unable to load users."
            );
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    };

    /* eslint-disable react-hooks/set-state-in-effect */
    useEffect(() => {
        loadUsers();
        return () => {
            mountedRef.current = false;
        };
    }, []);
/* eslint-enable react-hooks/set-state-in-effect */
    const filteredUsers = useMemo(() => {
        const query = search.trim().toLowerCase();
        if (!query) return users;

        return users.filter(
            (user) =>
                user.username?.toLowerCase().includes(query) ||
                user.role?.toLowerCase().includes(query)
        );
    }, [users, search]);

    const openCreateModal = () => {
        setEditingUser(null);
        setForm({
            username: "",
            password: "",
            role: "SALESPERSON",
        });
        setFormError("");
        setShowModal(true);
    };

    const openEditModal = (user) => {
        setEditingUser(user);
        setForm({
            username: user.username || "",
            password: "",
            role: user.role || "SALESPERSON",
        });
        setFormError("");
        setShowModal(true);
    };

    const closeModal = () => {
        if (saving) return;
        setShowModal(false);
        setEditingUser(null);
        setForm({
            username: "",
            password: "",
            role: "SALESPERSON",
        });
        setFormError("");
    };

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((previous) => ({
            ...previous,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setFormError("");

        const username = form.username.trim();
        const password = form.password;
        const role = form.role;

        if (!username) {
            setFormError("Username is required.");
            return;
        }

        if (!editingUser && !password) {
            setFormError("Password is required for new users.");
            return;
        }

        if (username.length > 50) {
            setFormError("Username must not exceed 50 characters.");
            return;
        }

        if (password && password.length < 6) {
            setFormError("Password must be at least 6 characters.");
            return;
        }

        try {
            setSaving(true);

            if (editingUser) {
                await createUser({
                    username,
                    password,
                    role,
                });
            } else {
                await createUser({
                    username,
                    password,
                    role,
                });
            }

            closeModal();
            await loadUsers();
        } catch (err) {
            console.error(err);
            setFormError(
                err.response?.data?.message || "Unable to save user."
            );
        } finally {
            setSaving(false);
        }
    };

    const handleDeactivate = async (user) => {
        const confirmed = window.confirm(
            `Deactivate user "${user.username}"?`
        );
        if (!confirmed) return;

        try {
            setError("");
            await deactivateUser(user.id);
            await loadUsers();
        } catch (err) {
            console.error(err);
            setError(
                err.response?.data?.message || "Unable to deactivate user."
            );
        }
    };

    const activeCount = users.filter((u) => u.active).length;
    const inactiveCount = users.filter((u) => !u.active).length;

    return (
        <div className="users-page">
            <header className="users-header">
                <div>
                    <div className="users-eyebrow">Administration</div>
                    <h1>Users</h1>
                    <p>Manage system users and roles.</p>
                </div>

                <button
                    className="user-add-button"
                    onClick={openCreateModal}
                    type="button"
                >
                    <UserPlus size={17} strokeWidth={1.7} />
                    <span>Add User</span>
                </button>
            </header>

            <section className="user-summary">
                <div className="summary-card">
                    <div className="summary-icon">
                        <UserPlus size={17} strokeWidth={1.5} />
                    </div>
                    <div>
                        <span>Total Users</span>
                        <strong>{users.length}</strong>
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
                        <ShieldAlert size={17} strokeWidth={1.5} />
                    </div>
                    <div>
                        <span>Inactive</span>
                        <strong>{inactiveCount}</strong>
                    </div>
                </div>
            </section>

            <section className="users-toolbar">
                <div className="user-search">
                    <Search size={16} strokeWidth={1.5} />
                    <input
                        type="text"
                        placeholder="Search users or roles..."
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
                    {filteredUsers.length} result
                    {filteredUsers.length !== 1 ? "s" : ""}
                </span>
            </section>

            {error && <div className="users-error">{error}</div>}

            <section className="users-panel">
                {loading ? (
                    <div className="user-state">
                        <p>Loading users...</p>
                    </div>
                ) : filteredUsers.length === 0 ? (
                    <div className="user-empty">
                        <ShieldAlert size={30} strokeWidth={1} />
                        <h2>{search ? "No users found" : "No users yet"}</h2>
                        <p>
                            {search
                                ? "Try a different search query."
                                : "Create your first user to enable system access."}
                        </p>
                        {!search && (
                            <button
                                onClick={openCreateModal}
                                className="empty-add-button"
                            >
                                <UserPlus size={15} />
                                Add User
                            </button>
                        )}
                    </div>
                ) : (
                    <div className="users-table-wrapper">
                        <table className="users-table">
                            <thead>
                                <tr>
                                    <th>User</th>
                                    <th>Role</th>
                                    <th>Status</th>
                                    <th>Created</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredUsers.map((user) => (
                                    <tr key={user.id}>
                                        <td>
                                            <div className="user-name-cell">
                                                <div className="user-avatar">
                                                    {user.username
                                                        ?.charAt(0)
                                                        ?.toUpperCase()}
                                                </div>
                                                <div>
                                                    <strong>{user.username}</strong>
                                                    <span>ID #{user.id}</span>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span className="user-role-badge">
                                                {user.role}
                                            </span>
                                        </td>
                                        <td>
                                            <span
                                                className={
                                                    user.active
                                                        ? "status active"
                                                        : "status inactive"
                                                }
                                            >
                                                <i></i>
                                                {user.active ? "Active" : "Inactive"}
                                            </span>
                                        </td>
                                        <td>
                                            <span className="created-date">
                                                {user.createdAt
                                                    ? new Date(
                                                          user.createdAt
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
                                            <div className="user-actions">
                                                <button
                                                    type="button"
                                                    className="icon-action"
                                                    title="Edit user"
                                                    onClick={() =>
                                                        openEditModal(user)
                                                    }
                                                >
                                                    <Edit3
                                                        size={15}
                                                        strokeWidth={1.5}
                                                    />
                                                </button>
                                                {user.active && (
                                                    <button
                                                        type="button"
                                                        className="deactivate-action"
                                                        onClick={() =>
                                                            handleDeactivate(user)
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

            {showModal && (
                <div
                    className="user-modal-backdrop"
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget) {
                            closeModal();
                        }
                    }}
                >
                    <div className="user-modal">
                        <div className="modal-header">
                            <div>
                                <span>
                                    {editingUser ? "EDIT USER" : "NEW USER"}
                                </span>
                                <h2>
                                    {editingUser
                                        ? "Update user"
                                        : "Add user"}
                                </h2>
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

                        <form className="user-form" onSubmit={handleSubmit}>
                            {formError && <div className="users-error">{formError}</div>}

                            <div className="form-field">
                                <label htmlFor="user-username">Username</label>
                                <input
                                    id="user-username"
                                    name="username"
                                    type="text"
                                    value={form.username}
                                    onChange={handleChange}
                                    placeholder="e.g. johndoe"
                                    maxLength={50}
                                    autoFocus
                                    required
                                />
                                <span className="field-hint">
                                    Unique username for login
                                </span>
                            </div>

                            <div className="form-field">
                                <label htmlFor="user-password">
                                    {editingUser ? "New Password (optional)" : "Password"}
                                </label>
                                <input
                                    id="user-password"
                                    name="password"
                                    type="password"
                                    value={form.password}
                                    onChange={handleChange}
                                    placeholder={editingUser ? "Leave blank to keep current" : "Min 6 characters"}
                                    minLength={6}
                                    required={!editingUser}
                                />
                                <span className="field-hint">
                                    {editingUser ? "Leave blank to keep current password" : "Minimum 6 characters"}
                                </span>
                            </div>

                            <div className="form-field">
                                <label htmlFor="user-role">Role</label>
                                <select
                                    id="user-role"
                                    name="role"
                                    value={form.role}
                                    onChange={handleChange}
                                >
                                    <option value="OWNER">Owner</option>
                                    <option value="SALESPERSON">Salesperson</option>
                                </select>
                                <span className="field-hint">
                                    Owner: full access | Salesperson: operational only
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
                                        : editingUser
                                        ? "Update User"
                                        : "Create User"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Users;