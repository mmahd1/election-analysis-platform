import type { NationalConstituency } from '../types/NationalResults'

const BASE_URL = import.meta.env.VITE_API_BASE_URL

if (!BASE_URL) {
    throw new Error('VITE_API_BASE_URL is not configured')
}

export async function loadNationalElectionData(electionId: string): Promise<void> {
    void electionId
    return Promise.resolve()
}

export async function fetchNationalConstituencies(electionId: string): Promise<NationalConstituency[]> {
    const response = await fetch(`${BASE_URL}/api/constituencies/${electionId}`)

    if (!response.ok) {
        throw new Error(`GET gaf status ${response.status}`)
    }

    return await response.json()
}
