import { useEffect, useState, useRef, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    ArrowLeft,
    Gem,
    Scale,
    Tag,
    CircleDollarSign,
    Layers3,
    ShieldCheck,
    LoaderCircle
} from "lucide-react";

import { getJewelleryById } from "../services/jewelleryApi";
import "./JewelleryDetails.css";

function JewelleryDetails() {

    const { id } = useParams();
    const navigate = useNavigate();

    const [jewellery, setJewellery] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const mountedRef = useRef(false);

    const loadJewellery = useCallback(async () => {
        if (mountedRef.current) return;
        mountedRef.current = true;

        try {
            setLoading(true);
            setError("");

            const data = await getJewelleryById(id);

            if (!mountedRef.current) return;
            setJewellery(data);
        } catch (error) {
            if (!mountedRef.current) return;
            console.error(
                "Jewellery details error:",
                error
            );

            setError(
                "Unable to load jewellery details."
            );
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    }, [id]);

    // eslint-disable-next-line react-hooks/set-state-in-effect
    /* eslint-disable react-hooks/set-state-in-effect */
    useEffect(() => {
        loadJewellery();
        return () => {
            mountedRef.current = false;
        };
    }, [id, loadJewellery]);
/* eslint-enable react-hooks/set-state-in-effect */

    if (loading) {
        return (
            <div className="jewellery-details-page">

                <div className="details-state">
                    <LoaderCircle
                        size={22}
                        className="details-spinner"
                    />

                    <span>
                        Loading jewellery...
                    </span>
                </div>

            </div>
        );
    }

    if (error || !jewellery) {
        return (
            <div className="jewellery-details-page">

                <button
                    className="back-button"
                    onClick={() =>
                        navigate("/jewellery")
                    }
                >
                    <ArrowLeft size={17} />
                    Back to Jewellery
                </button>

                <div className="details-error">
                    {error || "Jewellery not found."}
                </div>

            </div>
        );
    }

    return (
        <div className="jewellery-details-page">

            {/* ================= HEADER ================= */}

            <header className="details-header">

                <button
                    className="back-button"
                    onClick={() =>
                        navigate("/jewellery")
                    }
                >
                    <ArrowLeft size={17} />

                    <span>
                        Jewellery
                    </span>
                </button>

                <div className="details-status">

                    <span
                        className={
                            jewellery.active
                                ? "status active"
                                : "status inactive"
                        }
                    >
                        {jewellery.active
                            ? "Active"
                            : "Inactive"}
                    </span>

                </div>

            </header>


            {/* ================= HERO ================= */}

            <section className="details-hero">

                <div className="hero-icon">
                    <Gem size={38} />
                </div>

                <div className="hero-info">

                    <div className="details-eyebrow">
                        Jewellery Item
                    </div>

                    <h1>
                        {jewellery.name}
                    </h1>

                    <p>
                        SKU · {jewellery.sku}
                    </p>

                </div>

            </section>


            {/* ================= MAIN GRID ================= */}

            <div className="details-grid">

                {/* ================= BASIC INFORMATION ================= */}

                <section className="details-panel">

                    <div className="details-panel-header">

                        <div>
                            <span>
                                INFORMATION
                            </span>

                            <h2>
                                Jewellery Details
                            </h2>
                        </div>

                        <Tag size={18} />

                    </div>


                    <div className="details-panel-body">

                        <div className="info-row">

                            <span>
                                Name
                            </span>

                            <strong>
                                {jewellery.name}
                            </strong>

                        </div>


                        <div className="info-row">

                            <span>
                                SKU
                            </span>

                            <strong>
                                {jewellery.sku}
                            </strong>

                        </div>


                        <div className="info-row">

                            <span>
                                Category
                            </span>

                            <strong>
                                {jewellery.categoryName ||
                                    "—"}
                            </strong>

                        </div>


                        <div className="info-row">

                            <span>
                                Category Code
                            </span>

                            <strong>
                                {jewellery.categoryCode ||
                                    "—"}
                            </strong>

                        </div>


                        <div className="info-row">

                            <span>
                                Metal
                            </span>

                            <strong>
                                {jewellery.metalName ||
                                    "—"}
                            </strong>

                        </div>


                        <div className="info-row">

                            <span>
                                Metal Code
                            </span>

                            <strong>
                                {jewellery.metalCode ||
                                    "—"}
                            </strong>

                        </div>


                        <div className="info-row">

                            <span>
                                Purity
                            </span>

                            <strong>
                                {jewellery.purityName ||
                                    "—"}
                            </strong>

                        </div>


                        <div className="info-row">

                            <span>
                                Purity Code
                            </span>

                            <strong>
                                {jewellery.purityCode ||
                                    "—"}
                            </strong>

                        </div>

                    </div>

                </section>


                {/* ================= WEIGHT & CHARGES ================= */}

                <section className="details-panel">

                    <div className="details-panel-header">

                        <div>
                            <span>
                                SPECIFICATIONS
                            </span>

                            <h2>
                                Weight & Charges
                            </h2>
                        </div>

                        <Scale size={18} />

                    </div>


                    <div className="details-panel-body">

                        <div className="metric-card">

                            <div className="metric-icon">
                                <Scale size={17} />
                            </div>

                            <div>
                                <span>
                                    Gross Weight
                                </span>

                                <strong>
                                    {jewellery.grossWeight}
                                    {" "}g
                                </strong>
                            </div>

                        </div>


                        <div className="metric-card">

                            <div className="metric-icon">
                                <Gem size={17} />
                            </div>

                            <div>
                                <span>
                                    Stone Weight
                                </span>

                                <strong>
                                    {jewellery.stoneWeight}
                                    {" "}g
                                </strong>

                            </div>

                        </div>


                        <div className="metric-card featured">

                            <div className="metric-icon">
                                <Scale size={17} />
                            </div>

                            <div>
                                <span>
                                    Net Weight
                                </span>

                                <strong>
                                    {jewellery.netWeight}
                                    {" "}g
                                </strong>

                            </div>

                        </div>


                        <div className="metric-card">

                            <div className="metric-icon">
                                <CircleDollarSign
                                    size={17}
                                />
                            </div>

                            <div>

                                <span>
                                    Making Charge
                                </span>

                                <strong>
                                    ₹
                                    {Number(
                                        jewellery.makingCharge ||
                                        0
                                    ).toLocaleString(
                                        "en-IN"
                                    )}
                                </strong>

                            </div>

                        </div>

                    </div>

                </section>


                {/* ================= DESCRIPTION ================= */}

                <section className="details-panel description-panel">

                    <div className="details-panel-header">

                        <div>

                            <span>
                                DESCRIPTION
                            </span>

                            <h2>
                                About this Piece
                            </h2>

                        </div>

                        <Layers3 size={18} />

                    </div>


                    <div className="description-body">

                        <p>
                            {jewellery.description ||
                                "No description has been added for this jewellery piece."}
                        </p>

                    </div>

                </section>


                {/* ================= ACTION PANEL ================= */}

                <section className="details-panel action-panel">

                    <div className="details-panel-header">

                        <div>

                            <span>
                                NEXT STEP
                            </span>

                            <h2>
                                Inventory & Pricing
                            </h2>

                        </div>

                        <ShieldCheck size={18} />

                    </div>


                    <div className="action-body">

                        <p>
                            Pricing is calculated from an
                            inventory item using the current
                            metal rate, purity, net weight
                            and making charge.
                        </p>


                        <button
                            className="inventory-action"
                            onClick={() =>
                                navigate(
                                    "/inventory"
                                )
                            }
                        >
                            <span>
                                View Inventory
                            </span>

                            <ArrowLeft
                                size={17}
                                className="arrow-right"
                            />
                        </button>

                    </div>

                </section>

            </div>

        </div>
    );
}

export default JewelleryDetails;