import api from "./api";

export async function getPurities() {
    const response = await api.get("/purities");
    return response.data.data;
}

export async function getPurityById(id) {
    const response = await api.get(`/purities/${id}`);
    return response.data.data;
}

export async function createPurity(purity) {
    const response = await api.post("/purities", purity);
    return response.data.data;
}

export async function updatePurity(id, purity) {
    const response = await api.put(`/purities/${id}`, purity);
    return response.data.data;
}

export async function deactivatePurity(id) {
    await api.delete(`/purities/${id}`);
}
