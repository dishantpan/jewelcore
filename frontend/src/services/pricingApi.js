import axios from "axios";

const API_URL = "http://localhost:8080/api/v1/pricing";

export async function calculatePrice(
    inventoryItemId,
    priceDate
) {
    const token = localStorage.getItem("token");

    const response = await axios.post(
        `${API_URL}/calculate`,
        {
            inventoryItemId: Number(inventoryItemId),
            priceDate,
        },
        {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        }
    );

    return response.data.data;
}