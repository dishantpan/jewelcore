import api from "./api";

export async function getAvailableInventory() {
    const response = await api.get("/inventory-items", {
        params: { status: "AVAILABLE" }
    });
    return response.data.data;
}

export async function calculatePrice(inventoryItemId, priceDate) {
    const response = await api.post("/pricing/calculate", {
        inventoryItemId,
        priceDate
    });
    return response.data.data;
}

export async function createCustomer(customer) {
    const response = await api.post("/customers", customer);
    return response.data.data;
}

export async function searchCustomers(query) {
    const response = await api.get("/customers/search", {
        params: { q: query }
    });
    return response.data.data;
}

export async function createSale(saleData) {
    const response = await api.post("/bills", saleData);
    return response.data.data;
}

export async function getSales() {
    const response = await api.get("/bills");
    return response.data.data;
}

export async function getSaleById(id) {
    const response = await api.get(`/bills/${id}`);
    return response.data.data;
}