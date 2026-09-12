import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    Search,
    Plus,
    SlidersHorizontal,
    Package,
    ChevronRight,
    RefreshCw,
    X,
    ArrowUpDown,
    Boxes,
} from "lucide-react";
import { getInventoryItems } from "../services/inventoryApi";
import "./Inventory.css";

function Inventory() {
    const navigate = useNavigate();

    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [search, setSearch] = useState("");
    const [status, setStatus] = useState("ALL");
    const [sort, setSort] = useState("recent");
    const [showFilters, setShowFilters] = useState(false);

    useEffect(() => {
        loadInventory();
    }, []);

    /*
     * ============================================================
     * LOAD INVENTORY
     * ============================================================
     *
     * Keep your existing inventoryApi.js connection here.
     *
     * Once we verify the exact function exported by your service,
     * replace the temporary empty response with:
     *
     * const data = await getInventoryItems();
     * setItems(normalizeInventory(data));
     *
     * We deliberately don't invent your backend function name here.
     */

    async function loadInventory() {
        try {
            setLoading(true);
            setError("");

            const data = await getInventoryItems();
            setItems(Array.isArray(data) ? data : []);

        } catch (err) {
            console.error(err);
            setError("Unable to load inventory.");
        } finally {
            setLoading(false);
        }
    }

    const filteredItems = useMemo(() => {
        let result = [...items];

        const query = search.trim().toLowerCase();

        if (query) {
            result = result.filter((item) => {
                return (
                    String(item.itemCode || "")
                        .toLowerCase()
                        .includes(query) ||
                    String(item.jewelleryName || item.name || "")
                        .toLowerCase()
                        .includes(query) ||
                    String(item.jewellerySku || item.category || "")
                        .toLowerCase()
                        .includes(query) ||
                    String(item.location || item.metal || "")
                        .toLowerCase()
                        .includes(query)
                );
            });
        }

        if (status !== "ALL") {
            result = result.filter(
                (item) =>
                    String(item.status || "").toUpperCase() === status
            );
        }

        if (sort === "name") {
            result.sort((a, b) =>
                String(a.jewelleryName || a.name || "").localeCompare(
                    String(b.jewelleryName || b.name || "")
                )
            );
        }

        if (sort === "weight") {
            result.sort(
                (a, b) =>
                    Number(b.netWeight || b.weight || 0) -
                    Number(a.netWeight || a.weight || 0)
            );
        }

        return result;
    }, [items, search, status, sort]);

    const totalItems = items.length;

    const availableItems = items.filter(
        (item) =>
            String(item.status || "").toUpperCase() ===
            "AVAILABLE"
    ).length;

    const soldItems = items.filter(
        (item) =>
            String(item.status || "").toUpperCase() ===
            "SOLD"
    ).length;

    function openItem(item) {
        if (!item?.id) return;

        navigate(`/inventory/${item.id}`);
    }

    function clearSearch() {
        setSearch("");
    }

    return (
        <div className="inventory-page">

            {/* =====================================================
                HEADER
            ====================================================== */}

            <header className="inventory-header">

                <div className="inventory-header-left">

                    <div className="inventory-eyebrow">
                        Jewellery Management
                    </div>

                    <h1>Inventory</h1>

                    <p>
                        Track every stock item with clarity and control.
                    </p>

                </div>

                <div className="inventory-header-actions">

                    <button
                        className="inventory-refresh"
                        onClick={loadInventory}
                        title="Refresh inventory"
                        type="button"
                    >
                        <RefreshCw size={16} />
                        <span>Refresh</span>
                    </button>

                    <button
                        className="inventory-add-button"
                        onClick={() => navigate("/inventory/new")}
                        type="button"
                    >
                        <Plus size={17} />
                        <span>Add Inventory</span>
                    </button>

                </div>

            </header>


            {/* =====================================================
                SUMMARY
            ====================================================== */}

            <section className="inventory-summary">

                <div className="inventory-summary-item">

                    <div className="summary-icon">
                        <Boxes size={17} />
                    </div>

                    <div>
                        <span>Total Items</span>
                        <strong>
                            {loading ? "—" : totalItems}
                        </strong>
                    </div>

                </div>

                <div className="inventory-summary-item">

                    <div className="summary-icon available">
                        <Package size={17} />
                    </div>

                    <div>
                        <span>Available</span>
                        <strong>
                            {loading ? "—" : availableItems}
                        </strong>
                    </div>

                </div>

                <div className="inventory-summary-item">

                    <div className="summary-icon sold">
                        <Package size={17} />
                    </div>

                    <div>
                        <span>Sold</span>
                        <strong>
                            {loading ? "—" : soldItems}
                        </strong>
                    </div>

                </div>

            </section>


            {/* =====================================================
                TOOLBAR
            ====================================================== */}

            <section className="inventory-toolbar">

                <div className="inventory-search">

                    <Search size={17} />

                    <input
                        type="search"
                        placeholder="Search item code, name, category or metal..."
                        value={search}
                        onChange={(event) =>
                            setSearch(event.target.value)
                        }
                    />

                    {search && (
                        <button
                            type="button"
                            className="clear-search"
                            onClick={clearSearch}
                            aria-label="Clear search"
                        >
                            <X size={15} />
                        </button>
                    )}

                </div>

                <div className="toolbar-actions">

                    <button
                        type="button"
                        className={
                            showFilters
                                ? "toolbar-button active"
                                : "toolbar-button"
                        }
                        onClick={() =>
                            setShowFilters((value) => !value)
                        }
                    >
                        <SlidersHorizontal size={16} />
                        <span>Filters</span>
                    </button>

                    <div className="sort-wrapper">

                        <ArrowUpDown size={15} />

                        <select
                            value={sort}
                            onChange={(event) =>
                                setSort(event.target.value)
                            }
                        >
                            <option value="recent">
                                Recent
                            </option>
                            <option value="name">
                                Name
                            </option>
                            <option value="weight">
                                Weight
                            </option>
                        </select>

                    </div>

                </div>

            </section>


            {/* =====================================================
                FILTERS
            ====================================================== */}

            {showFilters && (
                <section className="inventory-filters">

                    <div className="filter-group">

                        <label>Status</label>

                        <div className="filter-options">

                            {[
                                ["ALL", "All"],
                                ["AVAILABLE", "Available"],
                                ["SOLD", "Sold"],
                                ["RESERVED", "Reserved"],
                            ].map(([value, label]) => (
                                <button
                                    key={value}
                                    type="button"
                                    className={
                                        status === value
                                            ? "filter-option active"
                                            : "filter-option"
                                    }
                                    onClick={() =>
                                        setStatus(value)
                                    }
                                >
                                    {label}
                                </button>
                            ))}

                        </div>

                    </div>

                </section>
            )}


            {/* =====================================================
                ERROR
            ====================================================== */}

            {error && (
                <div className="inventory-error">
                    <span>{error}</span>

                    <button
                        type="button"
                        onClick={loadInventory}
                    >
                        Try again
                    </button>
                </div>
            )}


            {/* =====================================================
                DESKTOP / TABLET VIEW
            ====================================================== */}

            <section className="inventory-table-section">

                <div className="section-heading">

                    <div>
                        <span>STOCK REGISTER</span>
                        <h2>Inventory Items</h2>
                    </div>

                    <small>
                        {loading
                            ? "Loading..."
                            : `${filteredItems.length} item${
                                filteredItems.length === 1
                                    ? ""
                                    : "s"
                            }`}
                    </small>

                </div>

                {loading ? (
                    <InventoryLoading />
                ) : filteredItems.length === 0 ? (
                    <InventoryEmpty
                        search={search}
                        onAdd={() =>
                            navigate("/inventory/new")
                        }
                    />
                ) : (
                    <div className="inventory-table-wrapper">

                        <table className="inventory-table">

                            <thead>
                            <tr>
                                <th>Item</th>
                                <th>Category</th>
                                <th>Metal</th>
                                <th>Weight</th>
                                <th>Status</th>
                                <th></th>
                            </tr>
                            </thead>

                            <tbody>

                            {filteredItems.map((item) => (
                                <tr
                                    key={item.id}
                                    onClick={() =>
                                        openItem(item)
                                    }
                                >

                                    <td>
                                        <div className="item-cell">

                                            <div className="item-image">
                                                {item.image ? (
                                                    <img
                                                        src={
                                                            item.image
                                                        }
                                                        alt=""
                                                    />
                                                ) : (
                                                    <Package
                                                        size={18}
                                                    />
                                                )}
                                            </div>

                                            <div>
                                                <strong>
                                                    {item.jewelleryName ||
                                                        item.name ||
                                                        "Unnamed item"}
                                                </strong>

                                                <span>
                                                        {item.itemCode ||
                                                            "No item code"}
                                                    </span>
                                            </div>

                                        </div>
                                    </td>

                                    <td>
                                        {item.jewellerySku || item.category || "—"}
                                    </td>

                                    <td>
                                        {item.location || item.metal || "—"}
                                    </td>

                                    <td>
                                        {item.netWeight
                                            ? `${item.netWeight} g`
                                            : item.weight
                                            ? `${item.weight} g`
                                            : "—"}
                                    </td>

                                    <td>
                                        <StatusBadge
                                            status={
                                                item.status
                                            }
                                        />
                                    </td>

                                    <td>
                                        <button
                                            type="button"
                                            className="row-arrow"
                                            onClick={(event) => {
                                                event.stopPropagation();
                                                openItem(item);
                                            }}
                                        >
                                            <ChevronRight
                                                size={17}
                                            />
                                        </button>
                                    </td>

                                </tr>
                            ))}

                            </tbody>

                        </table>

                    </div>
                )}

            </section>


            {/* =====================================================
                MOBILE VIEW
            ====================================================== */}

            <section className="inventory-mobile-list">

                {!loading &&
                    filteredItems.map((item) => (
                        <button
                            key={item.id}
                            type="button"
                            className="inventory-mobile-card"
                            onClick={() => openItem(item)}
                        >

                            <div className="mobile-card-image">
                                {item.image ? (
                                    <img
                                        src={item.image}
                                        alt=""
                                    />
                                ) : (
                                    <Package size={20} />
                                )}
                            </div>

                            <div className="mobile-card-content">

                                <div className="mobile-card-top">

                                    <strong>
                                        {item.jewelleryName ||
                                            item.name ||
                                            "Unnamed item"}
                                    </strong>

                                    <StatusBadge
                                        status={item.status}
                                    />

                                </div>

                                <span className="mobile-item-code">
                                    {item.itemCode ||
                                        "No item code"}
                                </span>

                                <div className="mobile-card-meta">

                                    <span>
                                        {item.jewellerySku || item.category || "—"}
                                    </span>

                                    <span>
                                        {item.location || item.metal || "—"}
                                    </span>

                                    <span>
                                        {item.netWeight
                                            ? `${item.netWeight} g`
                                            : item.weight
                                            ? `${item.weight} g`
                                            : "—"}
                                    </span>

                                </div>

                            </div>

                            <ChevronRight size={18} />

                        </button>
                    ))}

            </section>

        </div>
    );
}


/* ================================================================
   STATUS BADGE
================================================================ */

function StatusBadge({ status }) {
    const normalized = String(
        status || "UNKNOWN"
    ).toUpperCase();

    const label =
        normalized.charAt(0) +
        normalized.slice(1).toLowerCase();

    return (
        <span
            className={`status-badge status-${normalized.toLowerCase()}`}
        >
            <span className="status-dot" />
            {label}
        </span>
    );
}


/* ================================================================
   LOADING
================================================================ */

function InventoryLoading() {
    return (
        <div className="inventory-loading">

            {[1, 2, 3, 4].map((item) => (
                <div
                    className="loading-row"
                    key={item}
                >
                    <div />
                    <div />
                    <div />
                    <div />
                </div>
            ))}

        </div>
    );
}


/* ================================================================
   EMPTY STATE
================================================================ */

function InventoryEmpty({ search, onAdd }) {
    return (
        <div className="inventory-empty">

            <div className="empty-icon">
                <Package size={24} />
            </div>

            <h3>
                {search
                    ? "No matching inventory"
                    : "Inventory is empty"}
            </h3>

            <p>
                {search
                    ? "Try another search term or clear your filters."
                    : "Add your first stock item to begin tracking inventory."}
            </p>

            {!search && (
                <button
                    type="button"
                    onClick={onAdd}
                >
                    <Plus size={16} />
                    Add Inventory
                </button>
            )}

        </div>
    );
}

export default Inventory;