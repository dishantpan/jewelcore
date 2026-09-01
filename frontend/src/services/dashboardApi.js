import api from "./api";

function getLocalDate() {
    const date = new Date();

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

export async function getDashboardData() {
    const today = getLocalDate();

    const [
        jewelleryResponse,
        inventoryResponse,
        metalsResponse,
    ] = await Promise.all([
        api.get("/jewellery"),
        api.get("/inventory-items"),
        api.get("/metals"),
    ]);

    const jewellery =
        jewelleryResponse.data.data || [];

    const inventory =
        inventoryResponse.data.data || [];

    const metals =
        metalsResponse.data.data || [];

    /*
     * Fetch today's price for every active metal.
     */
    const rateResponses = await Promise.all(
        metals
            .filter((metal) => metal.active)
            .map(async (metal) => {
                try {
                    const response = await api.get(
                        `/metal-prices/metal/${metal.id}`
                    );

                    const prices =
                        response.data.data || [];

                    const todayPrice =
                        prices.find(
                            (price) =>
                                price.priceDate === today
                        );

                    return {
                        ...metal,
                        pricePerGram:
                            todayPrice?.pricePerGram ?? null,
                    };
                } catch {
                    return {
                        ...metal,
                        pricePerGram: null,
                    };
                }
            })
    );

    /*
     * Current stock excludes sold and damaged items.
     */
    const currentStock = inventory.filter(
        (item) =>
            item.status !== "SOLD" &&
            item.status !== "DAMAGED"
    );

    /*
     * Current inventory purchase value.
     */
    const inventoryValue = currentStock.reduce(
        (total, item) =>
            total + Number(item.purchaseCost || 0),
        0
    );

    return {
        jewelleryCount: jewellery.filter(
            (item) => item.active
        ).length,

        inventoryCount: currentStock.length,

        inventoryValue,

        metalRates: rateResponses,
    };
}