import { ref } from "vue";
import { loginUser, registerUser, getMe } from "../api/authApi.ts"

/**
 *
 * AUTH COMPOSABLE (STATE MANAGEMENT LAYER)
 *
 * Deze file handelt UI state af:
 *
 * ✔ loading state  → API call bezig
 * ✔ error state    → foutmelding
 * ✔ success state  → data (token/user)
 *
 * ✔ gebruikt authApi.ts voor backend calls
 * =========================================
 */

export function useAuth() {

    /**
     * STATE MANAGEMENT
     */
    const loading = ref(false);
    const error = ref<string | null>(null);
    const user = ref<any>(null);

    /**
     * LOGIN FLOW
     *
     * ✔ sets loading
     * ✔ handles error
     * ✔ stores token on success
     */
    const login = async (email: string, password: string) => {

        loading.value = true;
        error.value = null;

        try {
            const token = await loginUser(email, password);
            localStorage.setItem("token", token);
            return token;
        } catch (err: any) {
            error.value = err.message || "Login failed";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    /**
     * REGISTER FLOW
     */
    const register = async (data: any) => {

        loading.value = true;
        error.value = null;

        try {
            await registerUser(data);
        } catch (err: any) {
            error.value = err.message || "Register failed";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    /**
     * GET CURRENT USER (/me)
     */
    const fetchMe = async () => {

        loading.value = true;
        error.value = null;

        try {
            user.value = await getMe();
            return user.value;
        } catch (err: any) {
            error.value = err.message || "Failed to fetch user";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    return {
        login,
        register,
        fetchMe,
        loading,
        error,
        user
    };
}