export const API_URL = "http://localhost:3001"

export interface ApiResponse<T> {
    body: T | null;
    status: number;
    message: string
}
