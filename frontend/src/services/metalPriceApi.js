import api from "./api";

/*
 * Daily Metal Price API
 *
 * This service keeps all metal-price communication
 * in one place so the UI does not contain API logic.
 */

export async function getMetalPrices() {
    const response = await api.get("/metal-prices");

    return response.data?.data ?? response.data;
}

export async function createMetalPrice(payload) {
    const response = await api.post(
        "/metal-prices",
        payload
    );

    return response.data?.data ?? response.data;
}

export async function updateMetalPrice(id, payload) {
    const response = await api.put(
        `/metal-prices/${id}`,
        payload
    );

    return response.data?.data ?? response.data;
}