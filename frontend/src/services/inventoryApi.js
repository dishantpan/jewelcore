import api from "./api";

export async function getInventoryItems() {
    const response = await api.get("/inventory-items");

    return response.data.data || [];
}

export async function getInventoryItemById(id) {
    const response = await api.get(
        `/inventory-items/${id}`
    );

    return response.data.data;
}

export async function getInventoryByStatus(status) {
    const response = await api.get(
        `/inventory-items/status/${status}`
    );

    return response.data.data || [];
}

export async function createInventoryItem(data) {
    const response = await api.post(
        "/inventory-items",
        data
    );

    return response.data.data;
}

export async function reserveInventoryItem(id) {
    const response = await api.put(
        `/inventory-items/${id}/reserve`
    );

    return response.data.data;
}

export async function sellInventoryItem(id) {
    const response = await api.put(
        `/inventory-items/${id}/sell`
    );

    return response.data.data;
}

export async function damageInventoryItem(id) {
    const response = await api.put(
        `/inventory-items/${id}/damage`
    );

    return response.data.data;
}