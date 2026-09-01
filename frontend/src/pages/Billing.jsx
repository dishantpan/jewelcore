import { useMemo, useState } from "react";
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
} from "lucide-react";

import "./Billing.css";

function Billing() {
    const navigate = useNavigate();

    const [customerName, setCustomerName] = useState("");
    const [customerPhone, setCustomerPhone] = useState("");
    const [search, setSearch] = useState("");

    const [cart, setCart] = useState([]);

    const [discount, setDiscount] = useState("");
    const [paymentMethod, setPaymentMethod] = useState("CASH");

    const jewellery = [
        {
            id: 1,
            name: "Classic Gold Ring",
            code: "JR-001",
            metal: "Gold",
            purity: "22K",
            weight: 4.82,
            price: 38500,
        },
        {
            id: 2,
            name: "Traditional Gold Chain",
            code: "JC-014",
            metal: "Gold",
            purity: "22K",
            weight: 18.45,
            price: 142800,
        },
        {
            id: 3,
            name: "Diamond Stud Earrings",
            code: "DE-008",
            metal: "Gold",
            purity: "18K",
            weight: 3.21,
            price: 42800,
        },
        {
            id: 4,
            name: "Silver Bracelet",
            code: "SB-006",
            metal: "Silver",
            purity: "925",
            weight: 12.8,
            price: 6800,
        },
    ];

    const filteredJewellery = jewellery.filter((item) =>
        `${item.name} ${item.code} ${item.metal}`
            .toLowerCase()
            .includes(search.toLowerCase())
    );

    const addToCart = (item) => {
        setCart((current) => {
            const existing = current.find(
                (cartItem) => cartItem.id === item.id
            );

            if (existing) {
                return current.map((cartItem) =>
                    cartItem.id === item.id
                        ? {
                            ...cartItem,
                            quantity: cartItem.quantity + 1,
                        }
                        : cartItem
                );
            }

            return [
                ...current,
                {
                    ...item,
                    quantity: 1,
                },
            ];
        });
    };

    const updateQuantity = (id, quantity) => {
        if (quantity <= 0) {
            removeFromCart(id);
            return;
        }

        setCart((current) =>
            current.map((item) =>
                item.id === id
                    ? { ...item, quantity }
                    : item
            )
        );
    };

    const removeFromCart = (id) => {
        setCart((current) =>
            current.filter((item) => item.id !== id)
        );
    };

    const totals = useMemo(() => {
        const subtotal = cart.reduce(
            (total, item) =>
                total + item.price * item.quantity,
            0
        );

        const discountAmount =
            Math.min(
                Number(discount) || 0,
                subtotal
            );

        const taxableAmount =
            subtotal - discountAmount;

        const gst = taxableAmount * 0.03;

        const grandTotal = taxableAmount + gst;

        return {
            subtotal,
            discountAmount,
            taxableAmount,
            gst,
            grandTotal,
        };
    }, [cart, discount]);

    const formatCurrency = (value) =>
        new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 0,
        }).format(value);

    const handleCreateBill = () => {
        if (cart.length === 0) {
            alert("Add at least one jewellery item.");
            return;
        }

        if (!customerName.trim()) {
            alert("Enter customer name.");
            return;
        }

        alert(
            `Bill ready for ${customerName}. Backend billing integration will be connected next.`
        );
    };

    return (
        <div className="billing-page">

            {/* HEADER */}

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
                        <div className="billing-eyebrow">
                            Sales & Billing
                        </div>

                        <h1>Create Bill</h1>
                    </div>

                </div>

                <div className="billing-header-action">

                    <button
                        className="calculator-button"
                        onClick={() => navigate("/pricing")}
                    >
                        <Calculator size={16} />
                        Pricing
                    </button>

                </div>

            </header>


            {/* MAIN */}

            <main className="billing-content">

                <section className="billing-workspace">


                    {/* LEFT */}

                    <div className="billing-products panel">

                        <div className="panel-header">

                            <div>
                                <h2>Jewellery</h2>
                                <span>
                                    Select items to add to bill
                                </span>
                            </div>

                            <div className="item-count">
                                {filteredJewellery.length} items
                            </div>

                        </div>


                        <div className="billing-search">

                            <Search size={17} />

                            <input
                                type="text"
                                placeholder="Search jewellery by name or code..."
                                value={search}
                                onChange={(event) =>
                                    setSearch(event.target.value)
                                }
                            />

                            {search && (
                                <button
                                    onClick={() => setSearch("")}
                                    className="clear-search"
                                >
                                    <X size={15} />
                                </button>
                            )}

                        </div>


                        <div className="product-list">

                            {filteredJewellery.map((item) => (

                                <button
                                    key={item.id}
                                    className="product-item"
                                    onClick={() => addToCart(item)}
                                >

                                    <div className="product-icon">
                                        {item.metal === "Silver"
                                            ? "Ag"
                                            : "Au"}
                                    </div>

                                    <div className="product-info">

                                        <strong>
                                            {item.name}
                                        </strong>

                                        <span>
                                            {item.code}
                                            {" · "}
                                            {item.purity}
                                            {" · "}
                                            {item.weight}g
                                        </span>

                                    </div>

                                    <div className="product-price">

                                        <strong>
                                            {formatCurrency(item.price)}
                                        </strong>

                                        <ChevronRight size={17} />

                                    </div>

                                </button>

                            ))}

                            {filteredJewellery.length === 0 && (

                                <div className="empty-products">
                                    No jewellery found.
                                </div>

                            )}

                        </div>

                    </div>


                    {/* RIGHT */}

                    <aside className="billing-sidebar">


                        {/* CUSTOMER */}

                        <div className="panel customer-panel">

                            <div className="panel-header">

                                <div>
                                    <h2>Customer</h2>
                                    <span>
                                        Billing information
                                    </span>
                                </div>

                                <User size={18} />

                            </div>

                            <div className="customer-form">

                                <div className="field">

                                    <label>
                                        Customer Name
                                    </label>

                                    <input
                                        type="text"
                                        placeholder="Enter name"
                                        value={customerName}
                                        onChange={(event) =>
                                            setCustomerName(
                                                event.target.value
                                            )
                                        }
                                    />

                                </div>

                                <div className="field">

                                    <label>
                                        Phone Number
                                    </label>

                                    <input
                                        type="tel"
                                        placeholder="Enter phone number"
                                        value={customerPhone}
                                        onChange={(event) =>
                                            setCustomerPhone(
                                                event.target.value
                                            )
                                        }
                                    />

                                </div>

                            </div>

                        </div>


                        {/* CART */}

                        <div className="panel cart-panel">

                            <div className="panel-header">

                                <div>
                                    <h2>Bill Items</h2>
                                    <span>
                                        {cart.length} selected
                                    </span>
                                </div>

                                <Receipt size={18} />

                            </div>


                            <div className="cart-items">

                                {cart.length === 0 ? (

                                    <div className="empty-cart">

                                        <Plus size={22} />

                                        <strong>
                                            No items added
                                        </strong>

                                        <span>
                                            Select jewellery from the list
                                        </span>

                                    </div>

                                ) : (

                                    cart.map((item) => (

                                        <div
                                            className="cart-item"
                                            key={item.id}
                                        >

                                            <div className="cart-item-info">

                                                <strong>
                                                    {item.name}
                                                </strong>

                                                <span>
                                                    {formatCurrency(item.price)}
                                                </span>

                                            </div>


                                            <div className="cart-item-actions">

                                                <div className="quantity-control">

                                                    <button
                                                        onClick={() =>
                                                            updateQuantity(
                                                                item.id,
                                                                item.quantity - 1
                                                            )
                                                        }
                                                    >
                                                        −
                                                    </button>

                                                    <span>
                                                        {item.quantity}
                                                    </span>

                                                    <button
                                                        onClick={() =>
                                                            updateQuantity(
                                                                item.id,
                                                                item.quantity + 1
                                                            )
                                                        }
                                                    >
                                                        +
                                                    </button>

                                                </div>

                                                <button
                                                    className="remove-item"
                                                    onClick={() =>
                                                        removeFromCart(
                                                            item.id
                                                        )
                                                    }
                                                >
                                                    <Trash2 size={15} />
                                                </button>

                                            </div>

                                        </div>

                                    ))

                                )}

                            </div>

                        </div>


                        {/* TOTAL */}

                        <div className="panel total-panel">

                            <div className="total-row">
                                <span>Subtotal</span>
                                <strong>
                                    {formatCurrency(
                                        totals.subtotal
                                    )}
                                </strong>
                            </div>

                            <div className="discount-row">

                                <span>Discount</span>

                                <input
                                    type="number"
                                    min="0"
                                    placeholder="₹ 0"
                                    value={discount}
                                    onChange={(event) =>
                                        setDiscount(
                                            event.target.value
                                        )
                                    }
                                />

                            </div>

                            <div className="total-row">
                                <span>GST · 3%</span>
                                <strong>
                                    {formatCurrency(totals.gst)}
                                </strong>
                            </div>

                            <div className="grand-total">

                                <span>Total</span>

                                <strong>
                                    {formatCurrency(
                                        totals.grandTotal
                                    )}
                                </strong>

                            </div>


                            <div className="payment-section">

                                <label>
                                    Payment Method
                                </label>

                                <div className="payment-options">

                                    {["CASH", "UPI", "CARD"].map(
                                        (method) => (

                                            <button
                                                key={method}
                                                className={
                                                    paymentMethod === method
                                                        ? "payment-option active"
                                                        : "payment-option"
                                                }
                                                onClick={() =>
                                                    setPaymentMethod(
                                                        method
                                                    )
                                                }
                                            >
                                                {method}
                                            </button>

                                        )
                                    )}

                                </div>

                            </div>


                            <button
                                className="create-bill-button"
                                onClick={handleCreateBill}
                            >
                                <CreditCard size={17} />
                                Create Bill
                                <span>
                                    <IndianRupee size={13} />
                                    {Math.round(
                                        totals.grandTotal
                                    ).toLocaleString("en-IN")}
                                </span>
                            </button>

                        </div>

                    </aside>

                </section>

            </main>

        </div>
    );
}

export default Billing;