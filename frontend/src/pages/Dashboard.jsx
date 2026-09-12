import { useNavigate } from "react-router-dom";
import { useEffect, useState, useRef, useCallback } from "react";
import "./Dashboard.css";
import { getDashboardData } from "../services/dashboardApi";

function Dashboard() {
    const navigate = useNavigate();

    const username =
        localStorage.getItem("username") || "User";

    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const mountedRef = useRef(false);

    const loadDashboard = useCallback(async () => {
        if (mountedRef.current) return;
        mountedRef.current = true;

        try {
            setLoading(true);
            setError("");

            const data = await getDashboardData();

            if (!mountedRef.current) return;
            setDashboardData(data);
        } catch (error) {
            if (!mountedRef.current) return;
            console.error("Dashboard loading error:", error);

            setError(
                "Unable to load dashboard data."
            );
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    }, []);

    /* eslint-disable react-hooks/set-state-in-effect */
    useEffect(() => {
        loadDashboard();
        return () => {
            mountedRef.current = false;
        };
    }, [loadDashboard]);
/* eslint-enable react-hooks/set-state-in-effect */
    const jewelleryCount =
        dashboardData?.jewelleryCount ?? 0;

    const inventoryCount =
        dashboardData?.inventoryCount ?? 0;

    const inventoryValue =
        dashboardData?.inventoryValue ?? 0;

    const metalRates =
        dashboardData?.metalRates ?? [];

    return (
        <div className="dashboard-page">

            {/* ================= TOPBAR ================= */}

            <header className="dashboard-topbar">

                <div className="topbar-title">

                    <span>
                        Jewellery Management
                    </span>

                    <strong>
                        Overview
                    </strong>

                </div>

                <div className="topbar-right">

                    <span className="date-display">
                        {new Date().toLocaleDateString(
                            "en-IN",
                            {
                                day: "2-digit",
                                month: "long",
                                year: "numeric",
                            }
                        )}
                    </span>

                </div>

            </header>


            {/* ================= CONTENT ================= */}

            <section className="dashboard-content">

                {/* Error */}

                {error && (
                    <div className="dashboard-error">
                        {error}
                    </div>
                )}


                {/* ================= INTRO ================= */}

                <div className="dashboard-intro">

                    <div className="dashboard-eyebrow">
                        JewelCore Dashboard
                    </div>

                    <h1>
                        Good to see you, {username}.
                    </h1>

                    <p>
                        A clear view of your jewellery
                        business at a glance.
                    </p>

                </div>


                {/* ================= STATISTICS ================= */}

                <div className="stats-grid">

                    {/* Jewellery */}

                    <div className="stat-card">

                        <div className="stat-label">
                            Jewellery Pieces
                        </div>

                        <div className="stat-value">

                            {loading
                                ? "..."
                                : jewelleryCount}

                        </div>

                        <div className="stat-meta">
                            Active catalogue
                        </div>

                    </div>


                    {/* Inventory */}

                    <div className="stat-card">

                        <div className="stat-label">
                            Inventory Items
                        </div>

                        <div className="stat-value">

                            {loading
                                ? "..."
                                : inventoryCount}

                        </div>

                        <div className="stat-meta">
                            Current stock
                        </div>

                    </div>


                    {/* Metal Rate */}

                    <div className="stat-card">

                        <div className="stat-label">
                            Metal Rates
                        </div>

                        <div className="stat-value">

                            {loading
                                ? "..."
                                : metalRates.length}

                        </div>

                        <div className="stat-meta">
                            Metals tracked today
                        </div>

                    </div>


                    {/* Inventory Value */}

                    <div className="stat-card">

                        <div className="stat-label">
                            Inventory Value
                        </div>

                        <div className="stat-value">

                            {loading
                                ? "..."
                                : `₹${Number(
                                    inventoryValue
                                ).toLocaleString(
                                    "en-IN"
                                )}`}

                        </div>

                        <div className="stat-meta">
                            Current stock cost
                        </div>

                    </div>

                </div>


                {/* ================= LOWER GRID ================= */}

                <div className="dashboard-grid">


                    {/* ================= METAL RATES ================= */}

                    <div className="panel">

                        <div className="panel-header">

                            <h2>
                                Today's Metal Rates
                            </h2>

                            <span>
                                LIVE DATA
                            </span>

                        </div>


                        <div className="panel-body">

                            {loading ? (

                                <div className="dashboard-loading">
                                    Loading today's rates...
                                </div>

                            ) : metalRates.length > 0 ? (

                                metalRates.map((metal) => (

                                    <div
                                        className="rate-item"
                                        key={metal.id}
                                    >

                                        <div className="rate-metal">

                                            <strong>
                                                {metal.name}
                                            </strong>

                                            <span>
                                                {metal.code}
                                            </span>

                                        </div>


                                        <div className="rate-price">

                                            {metal.pricePerGram !== null &&
                                            metal.pricePerGram !== undefined
                                                ? `₹${Number(
                                                    metal.pricePerGram
                                                ).toLocaleString(
                                                    "en-IN"
                                                )}/g`
                                                : "No rate"}

                                        </div>

                                    </div>

                                ))

                            ) : (

                                <div className="dashboard-empty">
                                    No metal rates available
                                    for today.
                                </div>

                            )}

                        </div>

                    </div>


                    {/* ================= QUICK ACTIONS ================= */}

                    <div className="panel">

                        <div className="panel-header">

                            <h2>
                                Quick Actions
                            </h2>

                        </div>


                        <div className="panel-body">

                            <div className="quick-actions">


                                {/* Jewellery */}

                                <button
                                    className="quick-action"
                                    onClick={() =>
                                        navigate(
                                            "/jewellery"
                                        )
                                    }
                                >

                                    <strong>
                                        Add Jewellery
                                    </strong>

                                    <span>
                                        Create a new piece
                                    </span>

                                </button>


                                {/* Inventory */}

                                <button
                                    className="quick-action"
                                    onClick={() =>
                                        navigate(
                                            "/inventory"
                                        )
                                    }
                                >

                                    <strong>
                                        Add Inventory
                                    </strong>

                                    <span>
                                        Record stock
                                    </span>

                                </button>


                                {/* Metal Rates */}

                                <button
                                    className="quick-action"
                                    onClick={() =>
                                        navigate(
                                            "/metal-prices"
                                        )
                                    }
                                >

                                    <strong>
                                        Update Rates
                                    </strong>

                                    <span>
                                        Set today's metal price
                                    </span>

                                </button>


                                {/* Pricing */}

                                <button
                                    className="quick-action"
                                    onClick={() =>
                                        navigate(
                                            "/pricing"
                                        )
                                    }
                                >

                                    <strong>
                                        Calculate Price
                                    </strong>

                                    <span>
                                        Calculate jewellery value
                                    </span>

                                </button>

                            </div>

                        </div>

                    </div>

                </div>

            </section>

        </div>
    );
}

export default Dashboard;