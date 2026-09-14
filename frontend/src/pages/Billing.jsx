import { useEffect, useMemo, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import {
    ArrowLeft,
    Calculator,
    ChevronRight,
    CreditCard,
    IndianRupee,
    Plus,
    Receipt,
    Search,
    Trash2,
    User,
    X,
    LoaderCircle,
    Camera,
    Barcode,
} from "lucide-react";

import {
    getAvailableInventory,
    calculatePrice,
    createSale,
    createCustomer,
    searchCustomers,
} from "../services/billingApi";

import {
    getInventoryItemById,
} from "../services/inventoryApi";

import "./Billing.css";

function Billing() {
    const navigate = useNavigate();

    const [customerName, setCustomerName] = useState("");
    const [customerPhone, setCustomerPhone] = useState("");
    const [customerId, setCustomerId] = useState(null);
    const [search, setSearch] = useState("");
    const [barcodeInput, setBarcodeInput] = useState("");
    const [scanningBarcode, setScanningBarcode] = useState(false);
    const [barcodeError, setBarcodeError] = useState("");
    const [discount, setDiscount] = useState("");
    const [paymentMethod, setPaymentMethod] = useState("CASH");

    const [inventory, setInventory] = useState([]);
    const [cart, setCart] = useState([]);
    const [loading, setLoading] = useState(true);
    const [calculating, setCalculating] = useState({});
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    const mountedRef = useRef(false);

    const loadInventory = async () => {
        if (mountedRef.current) return;
        mountedRef.current = true;

        try {
            setLoading(true);
            setError("");
            const data = await getAvailableInventory();

            if (!mountedRef.current) return;
            setInventory(Array.isArray(data) ? data : []);
        } catch (err) {
            if (!mountedRef.current) return;
            console.error(err);
            setError(err.response?.data?.message || "Unable to load inventory.");
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    };

    /* eslint-disable react-hooks/set-state-in-effect */
    useEffect(() => {
        loadInventory();
        return () => {
            mountedRef.current = false;
        };
    }, []);
/* eslint-enable react-hooks/set-state-in-effect */

    const filteredInventory = useMemo(() => {
        const query = search.trim().toLowerCase();
        if (!query) return inventory;

        return inventory.filter(
            (item) =>
                item.jewelleryName?.toLowerCase().includes(query) ||
                item.itemCode?.toLowerCase().includes(query) ||
                item.metalName?.toLowerCase().includes(query) ||
                item.purityName?.toLowerCase().includes(query)
        );
    }, [inventory, search]);

    const handleBarcodeScan = async () => {
        if (!barcodeInput.trim() || scanningBarcode) return;

        setScanningBarcode(true);
        setBarcodeError("");

        try {
            const data = await getInventoryItemById(barcodeInput.trim());
            if (data) {
                await handleAddToCart(data);
                setBarcodeInput("");
            } else {
                setBarcodeError("Item not found");
            }
        } catch (err) {
            console.error(err);
            setBarcodeError("Invalid barcode/item code");
        } finally {
            setScanningBarcode(false);
        }
    };

    const handleAddToCart = async (item) => {
        const itemId = item.id;
        if (calculating[itemId]) return;

        setCalculating((prev) => ({ ...prev, [itemId]: true }));

        try {
            const today = new Date().toISOString().split("T")[0];
            const priceData = await calculatePrice(itemId, today);

            setCart((current) => {
                const existing = current.find((cartItem) => cartItem.inventoryItemId === itemId);
                if (existing) {
                    return current.map((cartItem) =>
                        cartItem.inventoryItemId === itemId
                            ? { ...cartItem, quantity: cartItem.quantity + 1 }
                            : cartItem
                    );
                }

                return [
                    ...current,
                    {
                        inventoryItemId: itemId,
                        itemCode: item.itemCode,
                        jewelleryName: item.jewelleryName,
                        metalName: item.metalName,
                        purityName: item.purityName,
                        netWeight: item.netWeight,
                        price: priceData.finalPrice,
                        quantity: 1,
                        itemData: { ...item, priceData },
                    },
                ];
            });
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || "Failed to calculate price.");
        } finally {
            setCalculating((prev) => ({ ...prev, [itemId]: false }));
        }
    };

    const updateQuantity = (id, quantity) => {
        if (quantity <= 0) {
            removeFromCart(id);
            return;
        }

        setCart((current) =>
            current.map((item) =>
                item.inventoryItemId === id
                    ? { ...item, quantity }
                    : item
            )
        );
    };

    const removeFromCart = (id) => {
        setCart((current) => current.filter((item) => item.inventoryItemId !== id));
    };

    const totals = useMemo(() => {
        const subtotal = cart.reduce(
            (total, item) => total + item.price * item.quantity,
            0
        );

        const discountAmount = Math.min(Number(discount) || 0, subtotal);
        const taxableAmount = subtotal - discountAmount;
        const gst = taxableAmount * 0.03;
        const grandTotal = taxableAmount + gst;

        return { subtotal, discountAmount, taxableAmount, gst, grandTotal };
    }, [cart, discount]);

    const formatCurrency = (value) =>
        new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 0,
        }).format(value);

    const handleCreateBill = async () => {
        if (cart.length === 0) {
            alert("Add at least one jewellery item.");
            return;
        }

        if (!customerName.trim()) {
            alert("Enter customer name.");
            return;
        }

        if (!customerPhone.trim()) {
            alert("Enter customer phone.");
            return;
        }

        if (!customerId) {
            alert("Please select or create a customer.");
            return;
        }

        setSubmitting(true);
        setError("");

        try {
            const items = cart.map((item) => ({
                inventoryItemId: item.inventoryItemId,
                discountAmount: 0,
            }));

            const payments = [{
                paymentMethod,
                amount: totals.grandTotal,
                referenceNumber: "",
                notes: "",
            }];

            const saleData = {
                customerId,
                items,
                discountAmount: totals.discountAmount,
                notes: "",
                payments,
            };

            const response = await createSale(saleData);
            alert(`Bill created successfully! Sale: ${response.saleNumber}`);
            navigate("/billing");
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || "Failed to create bill.");
        } finally {
            setSubmitting(false);
        }
    };

    const handleCustomerSearch = async () => {
        if (!customerPhone.trim()) return;

        try {
            setError("");
            const customers = await searchCustomers(customerPhone);
            if (customers.length > 0) {
                setCustomerId(customers[0].id);
                setCustomerName(customers[0].name);
            } else {
                // Create new customer
                const newCustomer = await createCustomer({
                    name: customerName,
                    phone: customerPhone,
                    email: "",
                    address: "",
                    gstNumber: "",
                });
                setCustomerId(newCustomer.id);
            }
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || "Failed to find/create customer.");
        }
    };

    return (
        <div className="billing-page">
            <header className="billing-header">
                <div className="billing-header-left">
                    <button
                        className="back-button"
                        onClick={() => navigate("/dashboard")}
                        title="Back to dashboard"
                    >
                        <ArrowLeft size={18} />
                    </button>
                    <div>
                        <div className="billing-eyebrow">Sales & Billing</div>
                        <h1>Create Bill</h1>
                    </div>
                </div>
                <div className="billing-header-action">
                    <button className="calculator-button" onClick={() => navigate("/pricing")}>
                        <Calculator size={16} />
                        Pricing
                    </button>
                </div>
            </header>

            <main className="billing-content">
                <section className="billing-workspace">
                    <div className="billing-products panel">
                        <div className="panel-header">
                            <div>
                                <h2>Available Inventory</h2>
                                <span>Select items to add to bill</span>
                            </div>
                            <div className="item-count">{filteredInventory.length} items</div>
                        </div>

                        <div className="billing-search">
                            <Search size={17} />
                            <input
                                type="text"
                                placeholder="Search by name, code, metal, or purity..."
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                            />
                            {search && (
                                <button className="clear-search" onClick={() => setSearch("")} type="button">
                                    <X size={15} />
                                </button>
                            )}
                        </div>

                        <div className="barcode-scanner-section">
                            <div className="scanner-input-group">
                                <Camera size={18} />
                                <input
                                    type="text"
                                    placeholder="Scan barcode/QR or enter item code..."
                                    value={barcodeInput}
                                    onChange={(e) => setBarcodeInput(e.target.value)}
                                    onKeyDown={(e) => e.key === "Enter" && handleBarcodeScan()}
                                />
                                <button
                                    className="scan-btn"
                                    onClick={handleBarcodeScan}
                                    disabled={!barcodeInput.trim() || scanningBarcode}
                                >
                                    {scanningBarcode ? <LoaderCircle size={16} className="spin" /> : <Barcode size={16} />}
                                    {scanningBarcode ? "Scanning..." : "Scan"}
                                </button>
                                {scanningBarcode && <span className="scanner-status">Looking up item...</span>}
                            </div>
                            {barcodeError && <div className="scanner-error">{barcodeError}</div>}
                        </div>

                        <div className="product-list">
                            {loading ? (
                                <div className="inventory-loading">
                                    <LoaderCircle size={24} className="spin" />
                                    <p>Loading inventory...</p>
                                </div>
                            ) : filteredInventory.length === 0 ? (
                                <div className="empty-products">
                                    No items found.
                                </div>
                            ) : (
                                filteredInventory.map((item) => (
                                    <button
                                        key={item.id}
                                        className="product-item"
                                        onClick={() => handleAddToCart(item)}
                                        disabled={calculating[item.id]}
                                    >
                                        <div className="product-icon">
                                            {item.metalName === "Silver" ? "Ag" : "Au"}
                                        </div>
                                        <div className="product-info">
                                            <strong>{item.jewelleryName}</strong>
                                            <span>
                                                {item.itemCode} · {item.purityName} · {item.netWeight}g
                                            </span>
                                        </div>
                                        <div className="product-price">
                                            <strong>{calculating[item.id] ? "..." : formatCurrency(item.itemData?.priceData?.finalPrice || 0)}</strong>
                                            <ChevronRight size={17} />
                                        </div>
                                    </button>
                                ))
                            )}
                        </div>
                    </div>

                    <aside className="billing-sidebar">
                        <div className="panel customer-panel">
                            <div className="panel-header">
                                <div>
                                    <h2>Customer</h2>
                                    <span>Billing information</span>
                                </div>
                                <User size={18} />
                            </div>
                            <div className="customer-form">
                                <div className="field">
                                    <label>Customer Name</label>
                                    <input
                                        type="text"
                                        placeholder="Enter name"
                                        value={customerName}
                                        onChange={(e) => setCustomerName(e.target.value)}
                                    />
                                </div>
                                <div className="field">
                                    <label>Phone Number</label>
                                    <input
                                        type="tel"
                                        placeholder="Enter phone number"
                                        value={customerPhone}
                                        onChange={(e) => setCustomerPhone(e.target.value)}
                                    />
                                </div>
                                <button
                                    className="search-customer-btn"
                                    onClick={handleCustomerSearch}
                                    disabled={!customerPhone.trim()}
                                >
                                    {customerId ? "Customer Linked" : "Find / Create Customer"}
                                </button>
                                {customerId && (
                                    <div className="customer-linked">
                                        <span>✓ {customerName} ({customerPhone})</span>
                                        <button
                                            className="unlink-btn"
                                            onClick={() => { setCustomerId(null); setCustomerName(""); setCustomerPhone(""); }}
                                        >
                                            <X size={14} />
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="panel cart-panel">
                            <div className="panel-header">
                                <div>
                                    <h2>Bill Items</h2>
                                    <span>{cart.length} selected</span>
                                </div>
                                <Receipt size={18} />
                            </div>

                            <div className="cart-items">
                                {cart.length === 0 ? (
                                    <div className="empty-cart">
                                        <Plus size={22} />
                                        <strong>No items added</strong>
                                        <span>Select jewellery from the list</span>
                                    </div>
                                ) : (
                                    cart.map((item) => (
                                        <div className="cart-item" key={item.inventoryItemId}>
                                            <div className="cart-item-info">
                                                <strong>{item.jewelleryName}</strong>
                                                <span>{formatCurrency(item.price)}</span>
                                            </div>
                                            <div className="cart-item-actions">
                                                <div className="quantity-control">
                                                    <button onClick={() => updateQuantity(item.inventoryItemId, item.quantity - 1)}>−</button>
                                                    <span>{item.quantity}</span>
                                                    <button onClick={() => updateQuantity(item.inventoryItemId, item.quantity + 1)}>+</button>
                                                </div>
                                                <button className="remove-item" onClick={() => removeFromCart(item.inventoryItemId)}>
                                                    <Trash2 size={15} />
                                                </button>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>

                        <div className="panel total-panel">
                            <div className="total-row">
                                <span>Subtotal</span>
                                <strong>{formatCurrency(totals.subtotal)}</strong>
                            </div>
                            <div className="discount-row">
                                <span>Discount</span>
                                <input
                                    type="number"
                                    min="0"
                                    placeholder="₹ 0"
                                    value={discount}
                                    onChange={(e) => setDiscount(e.target.value)}
                                />
                            </div>
                            <div className="total-row">
                                <span>GST · 3%</span>
                                <strong>{formatCurrency(totals.gst)}</strong>
                            </div>
                            <div className="grand-total">
                                <span>Total</span>
                                <strong>{formatCurrency(totals.grandTotal)}</strong>
                            </div>

                            <div className="payment-section">
                                <label>Payment Method</label>
                                <div className="payment-options">
                                    {["CASH", "UPI", "CARD"].map((method) => (
                                        <button
                                            key={method}
                                            className={paymentMethod === method ? "payment-option active" : "payment-option"}
                                            onClick={() => setPaymentMethod(method)}
                                        >
                                            {method}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <button
                                className="create-bill-button"
                                onClick={handleCreateBill}
                                disabled={submitting}
                            >
                                <CreditCard size={17} />
                                {submitting ? "Processing..." : "Create Bill"}
                                <span>
                                    <IndianRupee size={13} />
                                    {formatCurrency(totals.grandTotal)}
                                </span>
                            </button>
                        </div>
                    </aside>
                </section>
            </main>

            {error && <div className="billing-error">{error}</div>}
        </div>
    );
}

export default Billing;