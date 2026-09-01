import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
    ArrowLeft,
    Package,
    Scale,
    MapPin,
    CircleDollarSign,
    Gem,
    ShieldCheck,
    LoaderCircle,
    Calculator,
} from "lucide-react";

import {
    getInventoryItemById,
    reserveInventoryItem,
    sellInventoryItem,
    damageInventoryItem,
} from "../services/inventoryApi";

import "./InventoryDetails.css";

function InventoryDetails() {

    const { id } = useParams();
    const navigate = useNavigate();

    const [item, setItem] = useState(null);

    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] =
        useState(false);

    const [error, setError] = useState("");
    const [actionError, setActionError] =
        useState("");

    useEffect(() => {
        loadItem();
    }, [id]);

    async function loadItem() {

        try {
            setLoading(true);
            setError("");

            const data =
                await getInventoryItemById(id);

            setItem(data);

        } catch (error) {

            console.error(
                "Inventory details error:",
                error
            );

            setError(
                "Unable to load inventory item."
            );

        } finally {
            setLoading(false);
        }
    }


    async function handleAction(action) {

        try {

            setActionLoading(true);
            setActionError("");

            let updatedItem;

            if (action === "reserve") {
                updatedItem =
                    await reserveInventoryItem(id);
            }

            if (action === "sell") {
                updatedItem =
                    await sellInventoryItem(id);
            }

            if (action === "damage") {
                updatedItem =
                    await damageInventoryItem(id);
            }

            setItem(updatedItem);

        } catch (error) {

            console.error(
                "Inventory action error:",
                error
            );

            setActionError(
                error.response?.data?.message ||
                "Unable to update inventory status."
            );

        } finally {
            setActionLoading(false);
        }
    }


    if (loading) {

        return (
            <div className="inventory-details-page">

                <div className="inventory-details-state">

                    <LoaderCircle
                        size={23}
                        className="inventory-details-spinner"
                    />

                    Loading inventory item...

                </div>

            </div>
        );
    }


    if (error || !item) {

        return (
            <div className="inventory-details-page">

                <button
                    className="inventory-back"
                    onClick={() =>
                        navigate("/inventory")
                    }
                >
                    <ArrowLeft size={17} />

                    Back to Inventory
                </button>

                <div className="inventory-details-error">
                    {error ||
                        "Inventory item not found."}
                </div>

            </div>
        );
    }


    const canReserve =
        item.status === "AVAILABLE";

    const canSell =
        item.status === "AVAILABLE" ||
        item.status === "RESERVED";

    const canDamage =
        item.status === "AVAILABLE" ||
        item.status === "RESERVED";


    return (
        <div className="inventory-details-page">

            {/* HEADER */}

            <header className="inventory-details-header">

                <button
                    className="inventory-back"
                    onClick={() =>
                        navigate("/inventory")
                    }
                >
                    <ArrowLeft size={17} />

                    <span>
                        Inventory
                    </span>
                </button>


                <span
                    className={`inventory-status ${item.status.toLowerCase()}`}
                >
                    {item.status}
                </span>

            </header>


            {/* HERO */}

            <section className="inventory-details-hero">

                <div className="inventory-hero-icon">
                    <Package size={34} />
                </div>

                <div>

                    <div className="inventory-details-eyebrow">
                        Inventory Item
                    </div>

                    <h1>
                        {item.itemCode}
                    </h1>

                    <p>
                        {item.jewelleryName}
                        {" · "}
                        {item.jewellerySku}
                    </p>

                </div>

            </section>


            {actionError && (
                <div className="inventory-action-error">
                    {actionError}
                </div>
            )}


            {/* MAIN GRID */}

            <div className="inventory-details-grid">


                {/* ITEM INFORMATION */}

                <section className="inventory-details-panel">

                    <div className="inventory-panel-header">

                        <div>

                            <span>
                                ITEM INFORMATION
                            </span>

                            <h2>
                                Stock Details
                            </h2>

                        </div>

                        <Package size={18} />

                    </div>


                    <div className="inventory-info-body">

                        <div className="inventory-info-row">

                            <span>
                                Item Code
                            </span>

                            <strong>
                                {item.itemCode}
                            </strong>

                        </div>


                        <div className="inventory-info-row">

                            <span>
                                Jewellery
                            </span>

                            <strong>
                                {item.jewelleryName}
                            </strong>

                        </div>


                        <div className="inventory-info-row">

                            <span>
                                Jewellery SKU
                            </span>

                            <strong>
                                {item.jewellerySku}
                            </strong>

                        </div>


                        <div className="inventory-info-row">

                            <span>
                                Inventory ID
                            </span>

                            <strong>
                                #{item.id}
                            </strong>

                        </div>


                        <div className="inventory-info-row">

                            <span>
                                Location
                            </span>

                            <strong>
                                {item.location ||
                                    "Not specified"}
                            </strong>

                        </div>

                    </div>

                </section>


                {/* WEIGHT */}

                <section className="inventory-details-panel">

                    <div className="inventory-panel-header">

                        <div>

                            <span>
                                WEIGHT
                            </span>

                            <h2>
                                Weight Breakdown
                            </h2>

                        </div>

                        <Scale size={18} />

                    </div>


                    <div className="inventory-metrics">

                        <div className="inventory-metric">

                            <Scale size={17} />

                            <span>
                                Gross Weight
                            </span>

                            <strong>
                                {item.grossWeight} g
                            </strong>

                        </div>


                        <div className="inventory-metric">

                            <Gem size={17} />

                            <span>
                                Stone Weight
                            </span>

                            <strong>
                                {item.stoneWeight} g
                            </strong>

                        </div>


                        <div className="inventory-metric featured">

                            <Scale size={17} />

                            <span>
                                Net Weight
                            </span>

                            <strong>
                                {item.netWeight} g
                            </strong>

                        </div>

                    </div>

                </section>


                {/* PURCHASE */}

                <section className="inventory-details-panel">

                    <div className="inventory-panel-header">

                        <div>

                            <span>
                                COST
                            </span>

                            <h2>
                                Purchase Information
                            </h2>

                        </div>

                        <CircleDollarSign
                            size={18}
                        />

                    </div>


                    <div className="inventory-cost-body">

                        <span>
                            Purchase Cost
                        </span>

                        <strong>
                            ₹
                            {Number(
                                item.purchaseCost ||
                                0
                            ).toLocaleString(
                                "en-IN"
                            )}
                        </strong>

                    </div>

                </section>


                {/* ACTIONS */}

                <section className="inventory-details-panel">

                    <div className="inventory-panel-header">

                        <div>

                            <span>
                                STOCK CONTROL
                            </span>

                            <h2>
                                Update Status
                            </h2>

                        </div>

                        <ShieldCheck size={18} />

                    </div>


                    <div className="inventory-actions">

                        {canReserve && (

                            <button
                                className="inventory-action-button reserve"
                                disabled={
                                    actionLoading
                                }
                                onClick={() =>
                                    handleAction(
                                        "reserve"
                                    )
                                }
                            >
                                Reserve Item
                            </button>

                        )}


                        {canSell && (

                            <button
                                className="inventory-action-button sell"
                                disabled={
                                    actionLoading
                                }
                                onClick={() =>
                                    handleAction(
                                        "sell"
                                    )
                                }
                            >
                                Mark Sold
                            </button>

                        )}


                        {canDamage && (

                            <button
                                className="inventory-action-button damage"
                                disabled={
                                    actionLoading
                                }
                                onClick={() =>
                                    handleAction(
                                        "damage"
                                    )
                                }
                            >
                                Mark Damaged
                            </button>

                        )}

                    </div>

                </section>


                {/* PRICING */}

                <section className="inventory-pricing-panel">

                    <div className="inventory-pricing-icon">
                        <Calculator size={23} />
                    </div>

                    <div className="inventory-pricing-content">

                        <span>
                            SELLING PRICE
                        </span>

                        <h2>
                            Calculate current price
                        </h2>

                        <p>
                            Use today's metal rate,
                            purity, net weight and
                            making charge to calculate
                            the selling price.
                        </p>

                    </div>


                    <button
                        className="inventory-price-button"
                        disabled={
                            item.status === "SOLD" ||
                            item.status === "DAMAGED"
                        }
                        onClick={() =>
                            navigate(
                                `/pricing?inventoryItemId=${item.id}`
                            )
                        }
                    >
                        Calculate Price

                        <ChevronRightIcon />

                    </button>

                </section>

            </div>

        </div>
    );
}


/* Small local icon component */

function ChevronRightIcon() {
    return (
        <svg
            width="17"
            height="17"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
        >
            <path d="m9 18 6-6-6-6" />
        </svg>
    );
}

export default InventoryDetails;