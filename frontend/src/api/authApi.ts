const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

if (!API_BASE_URL) {
    throw new Error("VITE_API_BASE_URL is not configured");
}

/**
 *
 * AUTH API SERVICE LAYER
 *
 * Deze file bevat alleen HTTP requests naar de backend.
 *
 * - login / register / me API calls
 * - pure data handling
 * =========================================
 */

/**
 * Login request
 * Returns: JWT token (string)
 */
export async function loginUser(email: string, password: string): Promise<string> {

    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
        throw {
            status: response.status,
            message: await response.text()
        };
    }

    return await response.text();
}

/**
 * Register new user
 * Returns: void
 */
export type RegisterUserRequest = {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
};

export async function registerUser(user: RegisterUserRequest): Promise<void> {

    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user)
    });

    if (!response.ok) {
        throw {
            status: response.status,
            message: await response.text()
        };
    }
}

/**
 * Get authenticated user (/me)
 * Returns: user object
 */
export async function getMe(): Promise<{
    email: string;
    firstName?: string;
    lastName?: string;
}> {

    const token = localStorage.getItem("token");

    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`Me request mislukt: ${response.status}`);
    }

    return await response.json();
}