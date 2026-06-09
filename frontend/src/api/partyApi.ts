import type { Party } from '../interface/Party'

/** Basis-URL van de backend, bij voorkeur geladen vanuit environment variables. */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

if (!API_BASE_URL) {
    throw new Error('VITE_API_BASE_URL is not configured')
}

/**
 * Haalt partijen op van de backend.
 *
 * @param electionId De verkiezing waarvoor partijen opgehaald moeten worden.
 * @returns Een lijst van partijen.
 * @throws Error Als de backend geen succesvolle response terugstuurt.
 */
export async function fetchParties(electionId: string): Promise<Party[]> {
    const partyEndpoint = `${API_BASE_URL}/api/parties?electionId=${encodeURIComponent(electionId)}`
    const response = await fetch(partyEndpoint)

    if (!response.ok) {
        throw new Error(`Server gaf status ${response.status}`)
    }

    return (await response.json()) as Party[]
}
