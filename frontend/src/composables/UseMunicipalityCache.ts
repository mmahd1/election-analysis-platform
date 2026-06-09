interface PartyResult {
    partyName: string
    votes: number
}

export interface Municipality {
    regionName: string
    regionNumber: string
    partyResults: PartyResult[]
}

// 🔥 Singleton cache (blijft leven zolang app draait)
const cache = new Map<string, Municipality[]>()

// voorkomt dubbele fetches tegelijk
const loading = new Map<string, Promise<Municipality[]>>()

export function useMunicipalityCache() {

    async function fetchYear(electionId: string, API_BASE_URL: string) {
        // 1. al in cache → direct return
        if (cache.has(electionId)) {
            return cache.get(electionId)!
        }

        // 2. al bezig → wacht dezelfde request
        if (loading.has(electionId)) {
            return loading.get(electionId)!
        }

        // 3. fetch starten
        const promise = (async () => {
            const res = await fetch(
                `${API_BASE_URL}/api/municipalities?electionId=${electionId}`
            )

            if (!res.ok) {
                throw new Error(`Failed loading ${electionId}`)
            }

            const data: Municipality[] = await res.json()

            cache.set(electionId, data)
            return data
        })()

        loading.set(electionId, promise)

        const result = await promise
        loading.delete(electionId)

        return result
    }

    function get(electionId: string) {
        return cache.get(electionId) || null
    }

    function has(electionId: string) {
        return cache.has(electionId)
    }

    return {
        fetchYear,
        get,
        has
    }
}