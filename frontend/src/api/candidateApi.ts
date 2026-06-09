import type { Candidate } from '../types/Candidate'

const BASE_URL = import.meta.env.VITE_API_BASE_URL

if (!BASE_URL) {
    throw new Error('VITE_API_BASE_URL is not configured')
}

export async function loadElectionData(electionId = 'TK2025'): Promise<void> {
    const response = await fetch(`${BASE_URL}/elections/${electionId}`, {
        method: 'POST'
    })

    if (!response.ok) {
        throw new Error(`POST gaf status ${response.status}`)
    }
}

export async function fetchCandidatesByParty(
    electionId: string,
    partyName: string
): Promise<Candidate[]> {
    const response = await fetch(
        `${BASE_URL}/elections/${encodeURIComponent(electionId)}/parties/${encodeURIComponent(partyName)}/candidates`
    )

    if (!response.ok) {
        throw new Error(`GET gaf status ${response.status}`)
    }
    return await response.json()
}
