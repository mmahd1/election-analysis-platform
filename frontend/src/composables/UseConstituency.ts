import { ref } from 'vue'

const BASE_URL = import.meta.env.VITE_API_BASE_URL

if (!BASE_URL) {
    throw new Error('VITE_API_BASE_URL is not configured')
}

export function useConstituencies() {
    const constituencies = ref([])
    const loading = ref(false)
    const error = ref('')
    const selectedConstituency = ref(null)

    async function loadConstituencies(year: string) {
        loading.value = true
        error.value = ''
        selectedConstituency.value = null

        try {
            const response = await fetch(
                `${BASE_URL}/api/constituencies/TK${year}`
            )

            if (!response.ok) {
                throw new Error('FETCH_CONSTITUENCIES_FAILED')
            }

            constituencies.value = await response.json()
        } catch (err) {

            if (err instanceof Error) {
                error.value = err.message
            } else {
                error.value = 'Unknown error'
            }
        } finally {
            loading.value = false
        }
    }

    return {
        constituencies,
        loading,
        error,
        selectedConstituency,
        loadConstituencies
    }
}
