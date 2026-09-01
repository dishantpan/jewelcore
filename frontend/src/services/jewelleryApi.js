import api from "./api";

export async function getJewellery() {
    const response = await api.get("/jewellery");

    return response.data.data || [];
}

export async function getJewelleryById(id) {
    const response = await api.get(`/jewellery/${id}`);

    return response.data.data;
}