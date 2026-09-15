import api from "./api";

export async function getSalesReport(startDate, endDate) {
    const response = await api.get("/reports/sales", {
        params: { startDate, endDate },
    });
    return response.data.data;
}

export async function getInventoryReport() {
    const response = await api.get("/reports/inventory");
    return response.data.data;
}

export async function getFinancialReport(startDate, endDate) {
    const response = await api.get("/reports/financial", {
        params: { startDate, endDate },
    });
    return response.data.data;
}