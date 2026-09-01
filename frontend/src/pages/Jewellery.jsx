import { useEffect, useState } from "react";
import {
    Search,
    Plus,
    Gem,
    ChevronRight,
    RefreshCw
} from "lucide-react";
import { useNavigate } from "react-router-dom";

import { getJewellery } from "../services/jewelleryApi";
import "./Jewellery.css";

function Jewellery() {

    const navigate = useNavigate();

    const [jewellery, setJewellery] = useState([]);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        loadJewellery();
    }, []);

    async function loadJewellery() {
        try {
            setLoading(true);
            setError("");

            const data = await getJewellery();

            setJewellery(data);
        } catch (error) {
            console.error(
                "Jewellery loading error:",
                error
            );

            setError(
                "Unable to load jewellery."
            );
        } finally {
            setLoading(false);
        }
    }

    const filteredJewellery =
        jewellery.filter((item) => {

            const query =
                search.toLowerCase().trim();

            if (!query) {
                return true;
            }

            return (
                item.name
                    ?.toLowerCase()
                    .includes(query) ||
                item.sku
                    ?.toLowerCase()
                    .includes(query) ||
                item.categoryName
                    ?.toLowerCase()
                    .includes(query) ||
                item.metalName
                    ?.toLowerCase()
                    .includes(query)
            );
        });

    return (
        <div className="jewellery-page">

            {/* ================= HEADER ================= */}

            <header className="page-header">

                <div>

                    <div className="page-eyebrow">
                        Catalogue
                    </div>

                    <h1>
                        Jewellery
                    </h1>

                    <p>
                        Manage jewellery pieces and
                        their pricing information.
                    </p>

                </div>

                <button
                    className="primary-action"
                    onClick={() =>
                        navigate("/jewellery/new")
                    }
                >
                    <Plus size={17} />

                    Add Jewellery
                </button>

            </header>


            {/* ================= SEARCH ================= */}

            <section className="jewellery-toolbar">

                <div className="search-box">

                    <Search size={17} />

                    <input
                        type="text"
                        placeholder="Search by name, SKU, category or metal..."
                        value={search}
                        onChange={(event) =>
                            setSearch(
                                event.target.value
                            )
                        }
                    />

                </div>

                <button
                    className="refresh-button"
                    onClick={loadJewellery}
                    disabled={loading}
                    title="Refresh"
                >
                    <RefreshCw
                        size={16}
                        className={
                            loading
                                ? "spinning"
                                : ""
                        }
                    />
                </button>

            </section>


            {/* ================= ERROR ================= */}

            {error && (
                <div className="page-error">
                    {error}
                </div>
            )}


            {/* ================= DESKTOP TABLE ================= */}

            <section className="jewellery-table-wrapper">

                {loading ? (

                    <div className="page-state">
                        Loading jewellery...
                    </div>

                ) : filteredJewellery.length === 0 ? (

                    <div className="page-state empty-state">

                        <Gem size={30} />

                        <strong>
                            No jewellery found
                        </strong>

                        <span>
                            {search
                                ? "Try a different search."
                                : "Your jewellery catalogue is empty."}
                        </span>

                    </div>

                ) : (

                    <table className="jewellery-table">

                        <thead>

                        <tr>
                            <th>Item</th>
                            <th>Category</th>
                            <th>Metal</th>
                            <th>Purity</th>
                            <th>Net Weight</th>
                            <th>Making Charge</th>
                            <th>Status</th>
                            <th></th>
                        </tr>

                        </thead>

                        <tbody>

                        {filteredJewellery.map(
                            (item) => (

                                <tr key={item.id}>

                                    <td>

                                        <div className="item-main">

                                            <strong>
                                                {item.name}
                                            </strong>

                                            <span>
                                                    {item.sku}
                                                </span>

                                        </div>

                                    </td>

                                    <td>
                                        {item.categoryName}
                                    </td>

                                    <td>
                                        {item.metalName}
                                    </td>

                                    <td>
                                        {item.purityName}
                                    </td>

                                    <td>
                                        {item.netWeight} g
                                    </td>

                                    <td>
                                        ₹
                                        {Number(
                                            item.makingCharge || 0
                                        ).toLocaleString(
                                            "en-IN"
                                        )}
                                    </td>

                                    <td>

                                            <span
                                                className={
                                                    item.active
                                                        ? "status active"
                                                        : "status inactive"
                                                }
                                            >
                                                {item.active
                                                    ? "Active"
                                                    : "Inactive"}
                                            </span>

                                    </td>

                                    <td>

                                        <button
                                            className="view-button"
                                            onClick={() =>
                                                navigate(
                                                    `/jewellery/${item.id}`
                                                )
                                            }
                                        >
                                            <ChevronRight
                                                size={17}
                                            />
                                        </button>

                                    </td>

                                </tr>

                            )
                        )}

                        </tbody>

                    </table>

                )}

            </section>


            {/* ================= MOBILE CARDS ================= */}

            <section className="jewellery-mobile-list">

                {loading ? (

                    <div className="page-state">
                        Loading jewellery...
                    </div>

                ) : filteredJewellery.length === 0 ? (

                    <div className="page-state empty-state">
                        <Gem size={30} />

                        <strong>
                            No jewellery found
                        </strong>

                        <span>
                            {search
                                ? "Try a different search."
                                : "Your catalogue is empty."}
                        </span>
                    </div>

                ) : (

                    filteredJewellery.map((item) => (

                        <article
                            className="jewellery-card"
                            key={item.id}
                            onClick={() =>
                                navigate(
                                    `/jewellery/${item.id}`
                                )
                            }
                        >

                            <div className="card-icon">
                                <Gem size={20} />
                            </div>

                            <div className="card-content">

                                <div className="card-top">

                                    <div>

                                        <h2>
                                            {item.name}
                                        </h2>

                                        <span>
                                            {item.sku}
                                        </span>

                                    </div>

                                    <ChevronRight
                                        size={18}
                                    />

                                </div>


                                <div className="card-details">

                                    <span>
                                        {item.metalName}
                                    </span>

                                    <span>
                                        {item.purityName}
                                    </span>

                                    <span>
                                        {item.netWeight} g
                                    </span>

                                </div>


                                <div className="card-bottom">

                                    <strong>
                                        ₹
                                        {Number(
                                            item.makingCharge || 0
                                        ).toLocaleString(
                                            "en-IN"
                                        )}
                                    </strong>

                                    <span
                                        className={
                                            item.active
                                                ? "status active"
                                                : "status inactive"
                                        }
                                    >
                                        {item.active
                                            ? "Active"
                                            : "Inactive"}
                                    </span>

                                </div>

                            </div>

                        </article>

                    ))

                )}

            </section>

        </div>
    );
}

export default Jewellery;