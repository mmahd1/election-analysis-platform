export type RegionLevel = 'province' | 'constituency'

interface BaseMapSummary {
    regionName: string
    winningPartyName: string
    winningPartyVotes: number
    totalVotes: number
    winningPartyColor: string
}

export interface ProvinceMapSummary extends BaseMapSummary {
    /**
     * Highcharts map key (bijv. 'nl-gr') om polygonen te joinen.
     */
    hcKey: string
}

export interface ConstituencyMapSummary extends BaseMapSummary {
    /**
     * Kieskringnummer volgens Verkiezingsdefinitie (1..20).
     */
    constituencyNumber: string
}

export type RegionMapSummary = ProvinceMapSummary | ConstituencyMapSummary

