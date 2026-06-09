export interface ConstituencyPartyResult {
    partyName: string
    votes: number
}

export interface NationalConstituency {
    number: string
    name: string
    partyResults: ConstituencyPartyResult[]
}

export interface NationalPartyResult {
    partyName: string
    votes: number
    percentage: number
    color: string
}
