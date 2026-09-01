import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    ArrowLeft,
    Calculator,
    ChevronDown,
    IndianRupee,
    RotateCcw,
    Scale,
} from "lucide-react";

import "./Pricing.css";
import { calculatePrice } from "../services/pricingApi";
import { getInventoryItems } from "../services/inventoryApi";

function Pricing() {
    const navigate = useNavigate();

    const [inventory, setInventory] = useState([]);
    const [inventoryItemId, setInventoryItemId] = useState("");
    const [priceDate, setPriceDate] = useState(
        new Date().toISOString().split("T")[0]
    );

    const [result, setResult] = useState(null);
    const [loadingInventory, setLoadingInventory] = useState(true);
    const [calculating, setCalculating] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        loadInventory();
    }, []);

    async function loadInventory() {
        try {
            setLoadingInventory(true);
            setError("");

            const data = await getInventoryItems();

            setInventory(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error(error);
            setError("Unable to load inventory items.");
        } finally {
            setLoadingInventory(false);
        }
    }

    useEffect(() => {
        loadInventory();
    }, []);

    async function handleCalculate(event) {
        event.preventDefault();

        if (!inventoryItemId || !priceDate) {
            setError("Please select an inventory item and price date.");
            return;
        }

        try {
            setCalculating(true);
            setError("");
            setResult(null);

            const data = await calculatePrice({
                inventoryItemId: Number(inventoryItemId),
                priceDate,
            });

            setResult(data);
        } catch (error) {
            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to calculate jewellery price."
            );
        } finally {
            setCalculating(false);
        }
    }

    function handleReset() {
        setInventoryItemId("");
        setPriceDate(new Date().toISOString().split("T")[0]);
        setResult(null);
        setError("");
    }

    return (
        <main className="pricing-page">

            {/* TOPBAR */}
            <header className="pricing-topbar">

                <div className="pricing-title">
                    <span>JewelCore</span>
                    <strong>Price Calculator</strong>
                </div>

                <button
                    className="pricing-back-button"
                    onClick={() => navigate("/dashboard")}
                >
                    <ArrowLeft size={14} />
                    Back
                </button>

            </header>


            {/* CONTENT */}
            <section className="pricing-content">

                <div className="pricing-intro">

                    <div className="pricing-eyebrow">
                        Jewellery Pricing
                    </div>

                    <h1>
                        Calculate the selling price.
                    </h1>

                    <p>
                        Select an inventory item and pricing date.
                        JewelCore will calculate the metal value,
                        making charge and final jewellery price.
                    </p>

                </div>


                <div className="pricing-layout">

                    {/* CALCULATOR */}
                    <div className="pricing-card">

                        <div className="pricing-card-header">

                            <div>
                                <span className="section-label">
                                    PRICE ENGINE
                                </span>

                                <h2>
                                    Calculation Details
                                </h2>
                            </div>

                            <Calculator size={19} />

                        </div>


                        <form
                            className="pricing-form"
                            onSubmit={handleCalculate}
                        >

                            {/* INVENTORY */}
                            <div className="pricing-field">

                                <label htmlFor="inventory">
                                    Inventory Item
                                    <span>
                                        REQUIRED
                                    </span>
                                </label>

                                <div className="select-wrapper">

                                    <select
                                        id="inventory"
                                        value={inventoryItemId}
                                        onChange={(event) =>
                                            setInventoryItemId(
                                                event.target.value
                                            )
                                        }
                                        disabled={loadingInventory}
                                        required
                                    >

                                        <option value="">
                                            {loadingInventory
                                                ? "Loading inventory..."
                                                : "Select inventory item"}
                                        </option>

                                        {inventory.map((item) => (
                                            <option
                                                key={item.id}
                                                value={item.id}
                                            >
                                                {item.itemCode}
                                                {" — "}
                                                {item.jewelleryName ||
                                                    item.jewellery?.name ||
                                                    "Jewellery"}
                                            </option>
                                        ))}

                                    </select>

                                    <ChevronDown size={15} />

                                </div>

                            </div>


                            {/* DATE */}
                            <div className="pricing-field">

                                <label htmlFor="priceDate">
                                    Price Date
                                    <span>
                                        METAL RATE DATE
                                    </span>
                                </label>

                                <input
                                    id="priceDate"
                                    type="date"
                                    value={priceDate}
                                    onChange={(event) =>
                                        setPriceDate(
                                            event.target.value
                                        )
                                    }
                                    required
                                />

                            </div>


                            {/* ERROR */}
                            {error && (
                                <div className="login-error">
                                    {error}
                                </div>
                            )}


                            {/* SELECTED ITEM PREVIEW */}
                            {inventoryItemId && (
                                <div
                                    style={{
                                        marginTop: "4px",
                                        marginBottom: "8px",
                                        padding: "15px",
                                        border: "1px solid #292721",
                                        background: "#0d0d0c",
                                    }}
                                >

                                    <div
                                        style={{
                                            display: "flex",
                                            alignItems: "center",
                                            gap: "10px",
                                            color: "#b99750",
                                            fontSize: "11px",
                                            letterSpacing: "1px",
                                        }}
                                    >
                                        <Scale size={15} />
                                        SELECTED INVENTORY
                                    </div>

                                    {(() => {
                                        const item = inventory.find(
                                            (entry) =>
                                                String(entry.id) ===
                                                String(inventoryItemId)
                                        );

                                        if (!item) return null;

                                        return (
                                            <div
                                                style={{
                                                    marginTop: "10px",
                                                    color: "#aaa196",
                                                    fontSize: "12px",
                                                    lineHeight: "1.7",
                                                }}
                                            >
                                                <strong
                                                    style={{
                                                        color: "#e8e0d4",
                                                        fontWeight: 400,
                                                    }}
                                                >
                                                    {item.itemCode}
                                                </strong>

                                                <br />

                                                {item.jewelleryName ||
                                                    item.jewellery?.name ||
                                                    "Jewellery item"}
                                            </div>
                                        );
                                    })()}

                                </div>
                            )}


                            {/* ACTIONS */}
                            <div className="pricing-actions">

                                <button
                                    type="button"
                                    className="reset-button"
                                    onClick={handleReset}
                                >
                                    <RotateCcw size={13} />
                                    Reset
                                </button>

                                <button
                                    type="submit"
                                    className="calculate-button"
                                    disabled={
                                        calculating ||
                                        loadingInventory
                                    }
                                >
                                    <Calculator size={14} />

                                    {calculating
                                        ? "Calculating..."
                                        : "Calculate Price"}
                                </button>

                            </div>

                        </form>

                    </div>


                    {/* RESULT */}
                    <div className="pricing-result">

                        <div className="result-header">

                            <div>
                                <span className="section-label">
                                    CALCULATION RESULT
                                </span>

                                <h2>
                                    Final Price
                                </h2>
                            </div>

                            <IndianRupee size={18} />

                        </div>


                        {!result ? (

                            <div className="result-empty">

                                <div className="result-icon">
                                    <Calculator size={22} />
                                </div>

                                <h3>
                                    Nothing calculated yet.
                                </h3>

                                <p>
                                    Select an inventory item and
                                    calculate its current selling
                                    price to see the complete
                                    breakdown here.
                                </p>

                            </div>

                        ) : (

                            <>

                                <div className="result-main">

                                    <span>
                                        FINAL SELLING PRICE
                                    </span>

                                    <strong>
                                        ₹{" "}
                                        {Number(
                                            result.finalPrice
                                        ).toLocaleString(
                                            "en-IN",
                                            {
                                                minimumFractionDigits: 2,
                                                maximumFractionDigits: 2,
                                            }
                                        )}
                                    </strong>

                                    <small>
                                        {result.itemCode}
                                        {" · "}
                                        {result.jewelleryName}
                                    </small>

                                </div>


                                <div className="breakdown">

                                    <div>
                                        <span>Metal</span>
                                        <strong>
                                            {result.metalName}
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Purity</span>
                                        <strong>
                                            {result.purityName}
                                            {" "}
                                            ({result.purityPercentage}%)
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Net Weight</span>
                                        <strong>
                                            {result.netWeight} g
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Metal Rate</span>
                                        <strong>
                                            ₹{" "}
                                            {Number(
                                                result.metalRatePerGram
                                            ).toLocaleString(
                                                "en-IN",
                                                {
                                                    minimumFractionDigits: 2,
                                                }
                                            )}
                                            /g
                                        </strong>
                                    </div>

                                    <div>
                                        <span>
                                            Pure Metal Rate
                                        </span>
                                        <strong>
                                            ₹{" "}
                                            {Number(
                                                result.pureMetalRatePerGram
                                            ).toLocaleString(
                                                "en-IN",
                                                {
                                                    minimumFractionDigits: 2,
                                                }
                                            )}
                                            /g
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Metal Value</span>
                                        <strong>
                                            ₹{" "}
                                            {Number(
                                                result.metalValue
                                            ).toLocaleString(
                                                "en-IN",
                                                {
                                                    minimumFractionDigits: 2,
                                                }
                                            )}
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Making Charge</span>
                                        <strong>
                                            ₹{" "}
                                            {Number(
                                                result.makingCharge
                                            ).toLocaleString(
                                                "en-IN",
                                                {
                                                    minimumFractionDigits: 2,
                                                }
                                            )}
                                        </strong>
                                    </div>

                                    <div className="breakdown-subtotal">
                                        <span>
                                            Final Price
                                        </span>

                                        <strong>
                                            ₹{" "}
                                            {Number(
                                                result.finalPrice
                                            ).toLocaleString(
                                                "en-IN",
                                                {
                                                    minimumFractionDigits: 2,
                                                }
                                            )}
                                        </strong>
                                    </div>

                                </div>

                            </>

                        )}

                    </div>

                </div>

            </section>

        </main>
    );
}

export default Pricing;