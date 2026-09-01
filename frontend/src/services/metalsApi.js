import axios from "axios";

const API_URL = "http://localhost:8080/api/v1/metals";

const getAuthConfig = () => {
    const token = localStorage.getItem("token");

    return {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    };
};

export const getMetals = async () => {
    const response = await axios.get(
        API_URL,
        getAuthConfig()
    );

    return response.data.data;
};

export const createMetal = async (metal) => {
    const response = await axios.post(
        API_URL,
        metal,
        getAuthConfig()
    );

    return response.data.data;
};

export const updateMetal = async (id, metal) => {
    const response = await axios.put(
        `${API_URL}/${id}`,
        metal,
        getAuthConfig()
    );

    return response.data.data;
};

export const deactivateMetal = async (id) => {
    const response = await axios.delete(
        `${API_URL}/${id}`,
        getAuthConfig()
    );

    return response.data;
};