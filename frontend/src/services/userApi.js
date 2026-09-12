import api from "./api";

export async function getUsers() {
    const response = await api.get("/users");
    return response.data.data;
}

export async function createUser(user) {
    const response = await api.post("/users", user);
    return response.data.data;
}

export async function deactivateUser(id) {
    await api.delete(`/users/${id}`);
}

export async function getUserByUsername(username) {
    const response = await api.get(`/users/${username}`);
    return response.data.data;
}