import { useEffect, useState, useRef } from "react";
import {
    BarChart2,
    Coins,
    Package,
    TrendingUp,
    AlertTriangle,
    ShieldCheck,
    ShoppingBag,
    Gem,
    Users,
    Download,
} from "lucide-react";

import {
    getSalesReport,
    getInventoryReport,
    getFinancialReport,
} from "../services/reportsApi";

import "./Reports.css";

function Reports() {
    const [activeTab, setActiveTab] = useState("sales");
    const [dateRange, setDateRange] = useState({
        startDate: getDefaultStartDate(),
        endDate: getDefaultEndDate(),
    });

    const [salesReport, setSalesReport] = useState(null);
    const [inventoryReport, setInventoryReport] = useState(null);
    const [financialReport, setFinancialReport] = useState(null);

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const isOwner = localStorage.getItem("role") === "OWNER";

    const mountedRef = useRef(false);

    function getDefaultStartDate() {
        const date = new Date();
        date.setDate(date.getDate() - 30);
        return date.toISOString().split("T")[0];
    }

    function getDefaultEndDate() {
        return new Date().toISOString().split("T")[0];
    }

    const loadSalesReport = async () => {
        if (mountedRef.current) return;
        mountedRef.current = true;

        try {
            setLoading(true);
            setError("");
            const data = await getSalesReport(dateRange.startDate, dateRange.endDate);

            if (!mountedRef.current) return;
            setSalesReport(data);
        } catch (err) {
            if (!mountedRef.current) return;
            console.error(err);
            setError(err.response?.data?.message || "Failed to load sales report.");
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    };

    const loadInventoryReport = async () => {
        if (mountedRef.current) return;
        mountedRef.current = true;

        try {
            setLoading(true);
            setError("");
            const data = await getInventoryReport();

            if (!mountedRef.current) return;
            setInventoryReport(data);
        } catch (err) {
            if (!mountedRef.current) return;
            console.error(err);
            setError(err.response?.data?.message || "Failed to load inventory report.");
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    };

    const loadFinancialReport = async () => {
        if (mountedRef.current) return;
        mountedRef.current = true;

        try {
            setLoading(true);
            setError("");
            const data = await getFinancialReport(dateRange.startDate, dateRange.endDate);

            if (!mountedRef.current) return;
            setFinancialReport(data);
        } catch (err) {
            if (!mountedRef.current) return;
            console.error(err);
            setError(err.response?.data?.message || "Failed to load financial report.");
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    };

    /* eslint-disable react-hooks/set-state-in-effect */
    useEffect(() => {
        if (activeTab === "sales") {
            loadSalesReport();
        } else if (activeTab === "inventory") {
            loadInventoryReport();
        } else if (activeTab === "financial") {
            loadFinancialReport();
        }
        return () => {
            mountedRef.current = false;
        };
    }, [activeTab, dateRange, loadSalesReport, loadInventoryReport, loadFinancialReport]);
/* eslint-enable react-hooks/set-state-in-effect */

    const formatCurrency = (value) =>
        new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 0,
        }).format(value);

    const formatNumber = (value) =>
        new Intl.NumberFormat("en-IN").format(value);

    const formatDate = (dateStr) =>
        new Date(dateStr).toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
        });

    if (!isOwner) {
        return (
            <div className="reports-page">
                <div className="access-denied">
                    <ShieldCheck size={48} strokeWidth={1.5} />
                    <h2>Access Denied</h2>
                    <p>Reports are only accessible to OWNER role.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="reports-page">
            <header className="reports-header">
                <div>
                    <div className="reports-eyebrow">Analytics</div>
                    <h1>Reports</h1>
                    <p>Business insights and analytics</p>
                </div>
            </header>

            <div className="reports-toolbar">
                <div className="date-range-selector">
                    <label htmlFor="start-date">From</label>
                    <input
                        id="start-date"
                        type="date"
                        value={dateRange.startDate}
                        onChange={(e) =>
                            setDateRange({ ...dateRange, startDate: e.target.value })
                        }
                        max={dateRange.endDate}
                    />
                    <label htmlFor="end-date">To</label>
                    <input
                        id="end-date"
                        type="date"
                        value={dateRange.endDate}
                        onChange={(e) =>
                            setDateRange({ ...dateRange, endDate: e.target.value })
                        }
                        max={new Date().toISOString().split("T")[0]}
                    />
                    <button
                        className="refresh-button"
                        onClick={() => {
                            if (activeTab === "sales") loadSalesReport();
                            else if (activeTab === "inventory") loadInventoryReport();
                            else loadFinancialReport();
                        }}
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <span className="spinner" />
                                Loading...
                            </>
                        ) : (
                            "Refresh"
                        )}
                    </button>
                </div>

                <div className="export-actions">
                    <button className="export-button" disabled={loading}>
                        <Download size={16} strokeWidth={1.5} />
                        Export CSV
                    </button>
                </div>
            </div>

            {error && <div className="reports-error">{error}</div>}

            <nav className="reports-tabs" role="tablist">
                <button
                    role="tab"
                    aria-selected={activeTab === "sales"}
                    onClick={() => setActiveTab("sales")}
                    className={activeTab === "sales" ? "active" : ""}
                >
                    <BarChart2 size={16} strokeWidth={1.7} />
                    Sales
                </button>
                <button
                    role="tab"
                    aria-selected={activeTab === "inventory"}
                    onClick={() => setActiveTab("inventory")}
                    className={activeTab === "inventory" ? "active" : ""}
                >
                    <Package size={16} strokeWidth={1.7} />
                    Inventory
                </button>
                <button
                    role="tab"
                    aria-selected={activeTab === "financial"}
                    onClick={() => setActiveTab("financial")}
                    className={activeTab === "financial" ? "active" : ""}
                >
                    <Coins size={16} strokeWidth={1.7} />
                    Financial
                </button>
            </nav>

            {error && <div className="reports-error">{error}</div>}

            {activeTab === "sales" && (
                <SalesReportView report={salesReport} loading={loading} formatCurrency={formatCurrency} formatNumber={formatNumber} formatDate={formatDate} />
            )}

            {activeTab === "inventory" && (
                <InventoryReportView report={inventoryReport} loading={loading} formatCurrency={formatCurrency} formatNumber={formatNumber} />
            )}

            {activeTab === "financial" && (
                <FinancialReportView report={financialReport} loading={loading} formatCurrency={formatCurrency} formatNumber={formatNumber} />
            )}
        </div>
    );
}

function SalesReportView({ report, loading, formatCurrency, formatNumber, formatDate }) {
    if (loading) {
        return <div className="report-loading">Loading sales report...</div>;
    }
    if (!report) {
        return <div className="report-empty">Select a date range to generate sales report</div>;
    }

    return (
        <div className="report-view sales-report">
            <section className="report-summary">
                <SummaryCard label="Total Sales" value={formatCurrency(report.totalSales)} icon={<TrendingUp />} />
                <SummaryCard label="Total Discount" value={formatCurrency(report.totalDiscount)} icon={<AlertTriangle />} color="warning" />
                <SummaryCard label="Total GST" value={formatCurrency(report.totalGst)} icon={<Coins />} />
                <SummaryCard label="Net Sales" value={formatCurrency(report.totalNetSales)} icon={<TrendingUp />} color="success" />
                <SummaryCard label="Transactions" value={formatNumber(report.totalTransactions)} icon={<ShoppingBag />} />
                <SummaryCard label="Items Sold" value={formatNumber(report.totalItemsSold)} icon={<Package />} />
            </section>

            <section className="report-section">
                <h2>Daily Sales</h2>
                <TableContainer>
                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Total Sales</th>
                                <th>Discount</th>
                                <th>GST</th>
                                <th>Net Sales</th>
                                <th>Transactions</th>
                                <th>Items Sold</th>
                            </tr>
                        </thead>
                        <tbody>
                            {report.dailySales.map((day) => (
                                <tr key={day.date}>
                                    <td>{formatDate(day.date)}</td>
                                    <td>{formatCurrency(day.totalSales)}</td>
                                    <td>{formatCurrency(day.discount)}</td>
                                    <td>{formatCurrency(day.gst)}</td>
                                    <td>{formatCurrency(day.netSales)}</td>
                                    <td>{formatNumber(day.transactionCount)}</td>
                                    <td>{formatNumber(day.itemsSold)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </TableContainer>
            </section>

            <section className="report-section two-column">
                <div>
                    <h3>By Category</h3>
                    <TableContainer>
                        <table>
                            <thead>
                                <tr>
                                    <th>Category</th>
                                    <th>Total Sales</th>
                                    <th>Items Sold</th>
                                    <th>Transactions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {report.byCategory.map((cat) => (
                                    <tr key={cat.category}>
                                        <td>{cat.category}</td>
                                        <td>{formatCurrency(cat.totalSales)}</td>
                                        <td>{formatNumber(cat.itemsSold)}</td>
                                        <td>{formatNumber(cat.transactionCount)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </TableContainer>
                </div>

                <div>
                    <h3>By Metal</h3>
                    <TableContainer>
                        <table>
                            <thead>
                                <tr>
                                    <th>Metal</th>
                                    <th>Total Sales</th>
                                    <th>Items Sold</th>
                                    <th>Transactions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {report.byMetal.map((metal) => (
                                    <tr key={metal.metal}>
                                        <td>
                                            <Gem size={14} strokeWidth={1.5} />
                                            {metal.metal}
                                        </td>
                                        <td>{formatCurrency(metal.totalSales)}</td>
                                        <td>{formatNumber(metal.itemsSold)}</td>
                                        <td>{formatNumber(metal.transactionCount)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </TableContainer>
                </div>
            </section>

            <section className="report-section">
                <h3>By Salesperson</h3>
                <TableContainer>
                    <table>
                        <thead>
                            <tr>
                                <th>Salesperson</th>
                                <th>Total Sales</th>
                                <th>Transactions</th>
                                <th>Items Sold</th>
                            </tr>
                        </thead>
                        <tbody>
                            {report.bySalesperson.map((sp) => (
                                <tr key={sp.username}>
                                    <td>
                                        <Users size={14} strokeWidth={1.5} />
                                        {sp.fullName} ({sp.username})
                                    </td>
                                    <td>{formatCurrency(sp.totalSales)}</td>
                                    <td>{formatNumber(sp.transactionCount)}</td>
                                    <td>{formatNumber(sp.itemsSold)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </TableContainer>
            </section>
        </div>
    );
}

function InventoryReportView({ report, loading, formatCurrency, formatNumber }) {
    if (loading) {
        return <div className="report-loading">Loading inventory report...</div>;
    }
    if (!report) {
        return <div className="report-empty">Click refresh to generate inventory report</div>;
    }

    return (
        <div className="report-view inventory-report">
            <section className="report-summary">
                <SummaryCard label="Total Inventory Value" value={formatCurrency(report.totalInventoryValue)} icon={<Coins />} />
                <SummaryCard label="Total Items" value={formatNumber(report.totalItems)} icon={<Package />} />
                <SummaryCard label="Available" value={formatNumber(report.availableItems)} icon={<ShieldCheck />} color="success" />
                <SummaryCard label="Reserved" value={formatNumber(report.reservedItems)} icon={<AlertTriangle />} color="warning" />
                <SummaryCard label="Sold" value={formatNumber(report.soldItems)} icon={<TrendingUp />} />
                <SummaryCard label="Damaged" value={formatNumber(report.damagedItems)} icon={<AlertTriangle />} color="error" />
            </section>

            <section className="report-section two-column">
                <div>
                    <h3>By Category</h3>
                    <TableContainer>
                        <table>
                            <thead>
                                <tr>
                                    <th>Category</th>
                                    <th>Total Items</th>
                                    <th>Available</th>
                                    <th>Total Value</th>
                                    <th>Avg Value</th>
                                </tr>
                            </thead>
                            <tbody>
                                {report.byCategory.map((cat) => (
                                    <tr key={cat.category}>
                                        <td>{cat.category}</td>
                                        <td>{formatNumber(cat.totalItems)}</td>
                                        <td>{formatNumber(cat.availableItems)}</td>
                                        <td>{formatCurrency(cat.totalValue)}</td>
                                        <td>{formatCurrency(cat.averageValue)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </TableContainer>
                </div>

                <div>
                    <h3>By Metal</h3>
                    <TableContainer>
                        <table>
                            <thead>
                                <tr>
                                    <th>Metal</th>
                                    <th>Total Items</th>
                                    <th>Available</th>
                                    <th>Total Weight</th>
                                    <th>Total Value</th>
                                </tr>
                            </thead>
                            <tbody>
                                {report.byMetal.map((metal) => (
                                    <tr key={metal.metal}>
                                        <td>
                                            <Gem size={14} strokeWidth={1.5} />
                                            {metal.metal}
                                        </td>
                                        <td>{formatNumber(metal.totalItems)}</td>
                                        <td>{formatNumber(metal.availableItems)}</td>
                                        <td>{formatNumber(metal.totalWeight)} g</td>
                                        <td>{formatCurrency(metal.totalValue)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </TableContainer>
                </div>
            </section>

            <section className="report-section">
                <h3>Aging Analysis</h3>
                <TableContainer>
                    <table>
                        <thead>
                            <tr>
                                <th>Range</th>
                                <th>Item Count</th>
                                <th>Total Value</th>
                            </tr>
                        </thead>
                        <tbody>
                            {report.aging.map((age) => (
                                <tr key={age.range}>
                                    <td>{age.range}</td>
                                    <td>{formatNumber(age.itemCount)}</td>
                                    <td>{formatCurrency(age.totalValue)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </TableContainer>
            </section>
        </div>
    );
}

function FinancialReportView({ report, loading, formatCurrency, formatNumber }) {
    if (loading) {
        return <div className="report-loading">Loading financial report...</div>;
    }
    if (!report) {
        return <div className="report-empty">Select a date range to generate financial report</div>;
    }

    return (
        <div className="report-view financial-report">
            <section className="report-summary">
                <SummaryCard label="Total Revenue" value={formatCurrency(report.totalRevenue)} icon={<TrendingUp />} color="success" />
                <SummaryCard label="Total Cost" value={formatCurrency(report.totalCost)} icon={<Coins />} />
                <SummaryCard label="Gross Profit" value={formatCurrency(report.grossProfit)} icon={<TrendingUp />} color="success" />
                <SummaryCard label="GST Collected" value={formatCurrency(report.totalGstCollected)} icon={<Coins />} />
                <SummaryCard label="Total Discounts" value={formatCurrency(report.totalDiscounts)} icon={<AlertTriangle />} color="warning" />
                <SummaryCard label="Net Profit" value={formatCurrency(report.netProfit)} icon={<Coins />} color="success" />
            </section>

            <section className="report-section two-column">
                <div>
                    <h3>GST Breakdown</h3>
                    <TableContainer>
                        <table>
                            <thead>
                                <tr>
                                    <th>GST Rate</th>
                                    <th>Taxable Amount</th>
                                    <th>GST Amount</th>
                                    <th>Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                {report.gstBreakdown.map((gst, idx) => (
                                    <tr key={idx}>
                                        <td>{gst.gstRate}</td>
                                        <td>{formatCurrency(gst.taxableAmount)}</td>
                                        <td>{formatCurrency(gst.gstAmount)}</td>
                                        <td>{formatCurrency(gst.totalAmount)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </TableContainer>
                </div>

                <div>
                    <h3>Payment Methods</h3>
                    <TableContainer>
                        <table>
                            <thead>
                                <tr>
                                    <th>Method</th>
                                    <th>Total Amount</th>
                                    <th>Transactions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {report.paymentMethods.map((pm, idx) => (
                                    <tr key={idx}>
                                        <td>{pm.method}</td>
                                        <td>{formatCurrency(pm.totalAmount)}</td>
                                        <td>{formatNumber(pm.transactionCount)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </TableContainer>
                </div>
            </section>
        </div>
    );
}

function SummaryCard({ label, value, icon: Icon, color = "primary" }) {
    return (
        <div className={`summary-card ${color}`}>
            <div className="summary-icon">
                <Icon size={17} strokeWidth={1.5} />
            </div>
            <div>
                <span>{label}</span>
                <strong>{value}</strong>
            </div>
        </div>
    );
}

function TableContainer({ children }) {
    return <div className="table-container">{children}</div>;
}

export default Reports;