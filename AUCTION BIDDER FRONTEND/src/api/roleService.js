import axiosInstance from "./axiosInstance";

export const getRoles = () => axiosInstance.get("/api/roles");
export const createRole = (role) => axiosInstance.post("/api/roles", role);
export const deleteRole = (id) => axiosInstance.delete(`/api/roles/${id}`);
