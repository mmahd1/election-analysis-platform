import type { ConstituencyMapSummary, ProvinceMapSummary } from '../types/ElectionMap'

const BASE_URL = import.meta.env.VITE_API_BASE_URL

if (!BASE_URL) {
    throw new Error('VITE_API_BASE_URL is not configured')
}

export async function fetchRegionalMapSummary(electionId: string): Promise<ProvinceMapSummary[]> {
    const response = await fetch(`${BASE_URL}/elections/${electionId}/regional/map/summary`)

    if (!response.ok) {
        throw new Error(`GET gaf status ${response.status}`)
    }

    return await response.json()
}

export async function fetchConstituencyMapSummary(electionId: string): Promise<ConstituencyMapSummary[]> {
    const response = await fetch(`${BASE_URL}/elections/${electionId}/regional/map/constituency/summary`)

    if (!response.ok) {
        throw new Error(`GET gaf status ${response.status}`)
    }

    return await response.json()
}

