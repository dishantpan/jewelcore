import { useEffect, useMemo, useState } from "react";
import {
    ArrowDown,
    ArrowUp,
    CalendarDays,
    ChevronRight,
    Clock3,
    Plus,
    RefreshCw,
    TrendingUp,
} from "lucide-react";

import {
    getMetalPrices,
} from "../services/metalPriceApi";

import "./MetalPrices.css";


function MetalPrices() {

    const [prices, setPrices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [error, setError] = useState("");
    const [showUpdatePanel, setShowUpdatePanel] = useState(false);


    useEffect(() => {
        loadPrices();
    }, []);


    async function loadPrices() {

        try {

            setLoading(true);
            setError("");

            const data = await getMetalPrices();

            setPrices(
                Array.isArray(data)
                    ? data
                    : data?.content || []
            );

        } catch (err) {

            console.error(err);

            setError(
                "Unable to load today's metal prices."
            );

        } finally {

            setLoading(false);

        }
    }


    async function refreshPrices() {

        try {

            setRefreshing(true);
            setError("");

            const data = await getMetalPrices();

            setPrices(
                Array.isArray(data)
                    ? data
                    : data?.content || []
            );

        } catch (err) {

            console.error(err);

            setError(
                "Unable to refresh metal prices."
            );

        } finally {

            setRefreshing(false);

        }
    }


    const groupedPrices = useMemo(() => {

        const groups = {};

        prices.forEach((item) => {

            const metal =
                item.metalName ||
                item.metal ||
                item.name ||
                "Metal";

            if (!groups[metal]) {
                groups[metal] = [];
            }

            groups[metal].push(item);

        });

        return groups;

    }, [prices]);


    const getPurity = (item) => {

        return (
            item.purityName ||
            item.purity ||
            item.purityCode ||
            "—"
        );

    };


    const getPrice = (item) => {

        const value =
            item.price ??
            item.rate ??
            item.pricePerUnit ??
            item.amount;

        if (
            value === null ||
            value === undefined ||
            value === ""
        ) {
            return "—";
        }

        return Number(value).toLocaleString("en-IN", {
            maximumFractionDigits: 2,
        });

    };


    const getUnit = (item) => {

        return (
            item.unit ||
            item.priceUnit ||
            "10g"
        );

    };


    const getTrend = (item) => {

        const trend =
            item.changeDirection ||
            item.trend;

        if (
            trend === "UP" ||
            trend === "INCREASED"
        ) {
            return "up";
        }

        if (
            trend === "DOWN" ||
            trend === "DECREASED"
        ) {
            return "down";
        }

        return "neutral";

    };


    const formatDate = () => {

        return new Intl.DateTimeFormat(
            "en-IN",
            {
                day: "2-digit",
                month: "long",
                year: "numeric",
            }
        ).format(new Date());

    };


    return (

        <div className="metal-prices-page">

            {/* =========================================
                PAGE HEADER
               ========================================= */}

            <header className="metal-page-header">

                <div>

                    <div className="metal-eyebrow">
                        MASTER DATA
                    </div>

                    <h1>
                        Metal Prices
                    </h1>

                    <p>
                        Manage today's rates used across
                        pricing and billing.
                    </p>

                </div>


                <div className="metal-header-actions">

                    <button
                        className="metal-refresh-button"
                        onClick={refreshPrices}
                        disabled={refreshing}
                    >

                        <RefreshCw
                            size={15}
                            className={
                                refreshing
                                    ? "spin"
                                    : ""
                            }
                        />

                        <span>
                            Refresh
                        </span>

                    </button>


                    <button
                        className="metal-primary-button"
                        onClick={() =>
                            setShowUpdatePanel(true)
                        }
                    >

                        <Plus size={16} />

                        <span>
                            Update Rates
                        </span>

                    </button>

                </div>

            </header>


            {/* =========================================
                DATE / STATUS
               ========================================= */}

            <div className="metal-status-bar">

                <div className="metal-status-item">

                    <CalendarDays size={15} />

                    <span>
                        {formatDate()}
                    </span>

                </div>


                <div className="metal-live-status">

                    <span className="live-dot" />

                    LIVE DATA

                </div>

            </div>


            {/* =========================================
                ERROR
               ========================================= */}

            {error && (

                <div className="metal-error">

                    <span>
                        {error}
                    </span>

                    <button
                        onClick={loadPrices}
                    >
                        Try again
                    </button>

                </div>

            )}


            {/* =========================================
                LOADING
               ========================================= */}

            {loading ? (

                <div className="metal-loading">

                    <div className="loading-line" />
                    <div className="loading-line" />
                    <div className="loading-line" />

                </div>

            ) : (

                <>

                    {/* =================================
                        CURRENT RATES
                       ================================= */}

                    <section className="current-rates-section">

                        <div className="section-heading">

                            <div>

                                <span>
                                    TODAY
                                </span>

                                <h2>
                                    Current Rates
                                </h2>

                            </div>

                            <p>
                                Used for jewellery
                                price calculations
                            </p>

                        </div>


                        {Object.keys(groupedPrices).length === 0 ? (

                            <div className="empty-rates">

                                <div className="empty-icon">
                                    <TrendingUp size={20} />
                                </div>

                                <h3>
                                    No rates available
                                </h3>

                                <p>
                                    Add today's metal rates
                                    to start pricing jewellery.
                                </p>

                                <button
                                    onClick={() =>
                                        setShowUpdatePanel(true)
                                    }
                                >
                                    Update Rates
                                </button>

                            </div>

                        ) : (

                            <div className="metal-groups">

                                {Object.entries(
                                    groupedPrices
                                ).map(
                                    ([metalName, items]) => (

                                        <article
                                            className="metal-card"
                                            key={metalName}
                                        >

                                            <div className="metal-card-header">

                                                <div>

                                                    <span className="metal-card-label">
                                                        METAL
                                                    </span>

                                                    <h3>
                                                        {metalName}
                                                    </h3>

                                                </div>

                                                <div className="metal-card-symbol">
                                                    {metalName
                                                        .charAt(0)
                                                        .toUpperCase()}
                                                </div>

                                            </div>


                                            <div className="rate-list">

                                                {items.map(
                                                    (
                                                        item,
                                                        index
                                                    ) => {

                                                        const trend =
                                                            getTrend(
                                                                item
                                                            );

                                                        return (

                                                            <div
                                                                className="rate-row"
                                                                key={
                                                                    item.id ||
                                                                    index
                                                                }
                                                            >

                                                                <div className="rate-purity">

                                                                    <strong>
                                                                        {getPurity(
                                                                            item
                                                                        )}
                                                                    </strong>

                                                                    <span>
                                                                        {getUnit(
                                                                            item
                                                                        )}
                                                                    </span>

                                                                </div>


                                                                <div className="rate-value">

                                                                    <strong>
                                                                        ₹{" "}
                                                                        {getPrice(
                                                                            item
                                                                        )}
                                                                    </strong>


                                                                    {trend ===
                                                                        "up" && (

                                                                            <span className="trend up">
                                                                            <ArrowUp
                                                                                size={
                                                                                    12
                                                                                }
                                                                            />
                                                                            Up
                                                                        </span>

                                                                        )}


                                                                    {trend ===
                                                                        "down" && (

                                                                            <span className="trend down">
                                                                            <ArrowDown
                                                                                size={
                                                                                    12
                                                                                }
                                                                            />
                                                                            Down
                                                                        </span>

                                                                        )}

                                                                </div>

                                                            </div>

                                                        );

                                                    }
                                                )}

                                            </div>

                                        </article>

                                    )
                                )}

                            </div>

                        )}

                    </section>


                    {/* =================================
                        INFO STRIP
                       ================================= */}

                    <section className="pricing-info">

                        <div className="info-icon">
                            <Clock3 size={17} />
                        </div>

                        <div>

                            <strong>
                                Rates affect pricing
                            </strong>

                            <p>
                                Changes made here are used
                                by JewelCore's pricing and
                                billing calculations.
                            </p>

                        </div>

                        <ChevronRight
                            size={17}
                            className="info-arrow"
                        />

                    </section>


                    {/* =================================
                        HISTORY
                       ================================= */}

                    <section className="history-section">

                        <div className="section-heading">

                            <div>

                                <span>
                                    RECORDS
                                </span>

                                <h2>
                                    Rate History
                                </h2>

                            </div>

                            <p>
                                Previous recorded rates
                            </p>

                        </div>


                        <div className="history-placeholder">

                            <CalendarDays size={19} />

                            <div>

                                <strong>
                                    Historical rates
                                </strong>

                                <p>
                                    Previous daily rates will
                                    appear here as they are recorded.
                                </p>

                            </div>

                        </div>

                    </section>

                </>

            )}


            {/* =========================================
                UPDATE PANEL
               ========================================= */}

            {showUpdatePanel && (

                <div
                    className="metal-modal-overlay"
                    onClick={() =>
                        setShowUpdatePanel(false)
                    }
                >

                    <div
                        className="metal-modal"
                        onClick={(event) =>
                            event.stopPropagation()
                        }
                    >

                        <div className="modal-header">

                            <div>

                                <span>
                                    DAILY UPDATE
                                </span>

                                <h2>
                                    Update Metal Rates
                                </h2>

                            </div>

                            <button
                                onClick={() =>
                                    setShowUpdatePanel(false)
                                }
                            >
                                ×
                            </button>

                        </div>


                        <div className="modal-body">

                            <p className="modal-note">
                                Rate entry will be connected
                                to the DailyMetalPrice backend.
                            </p>

                            <div className="coming-soon-box">

                                <TrendingUp size={20} />

                                <strong>
                                    Rate entry form
                                </strong>

                                <span>
                                    We will connect the exact
                                    backend fields next.
                                </span>

                            </div>

                        </div>


                        <div className="modal-footer">

                            <button
                                className="modal-cancel"
                                onClick={() =>
                                    setShowUpdatePanel(false)
                                }
                            >
                                Cancel
                            </button>

                        </div>

                    </div>

                </div>

            )}

        </div>

    );

}

export default MetalPrices;