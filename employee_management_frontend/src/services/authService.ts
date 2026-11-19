import axios from "axios";
import type { AuthRequest, AuthResponse } from "../types/auth";

const API_URL = "http://localhost:8080/api/auth";

export const registerUser = async (
    userData: AuthRequest
): Promise<AuthResponse> => {
    const response = await axios.post<AuthResponse>(
        `${API_URL}/register`,
        userData
    );

    return response.data;
};

export const loginUser = async (
    userData: AuthRequest
): Promise<AuthResponse> => {
    const response = await axios.post<AuthResponse>(
        `${API_URL}/login`,
        userData
    );

    return response.data;
}