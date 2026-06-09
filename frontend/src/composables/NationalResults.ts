import { computed, onMounted, ref, watch } from 'vue'
import {
    fetchNationalConstituencies,
    loadNationalElectionData
} from '../api/nationalResultsApi'
import type { NationalPartyResult } from '../types/NationalResults'

const AVAILABLE_YEARS = ['2023', '2025']
const CHART_COLORS = ['#0f766e', '#2563eb', '#f59e0b', '#dc2626', '#7c3aed', '#0891b2', '#65a30d', '#ea580c']

export function NationalResults() {
    const selectedYear = ref('2023')
    const nationalResults = ref<NationalPartyResult[]>([])
    const isLoading = ref(true)
    const errorMessage = ref('')

    const totalVotes = computed(() => {
        return nationalResults.value.reduce((sum, party) => sum + party.votes, 0)
    })

    const topParties = computed(() => {
        return nationalResults.value.slice(0, 8)
    })

    const chartSlices = computed(() => {
        const mainSlices = nationalResults.value.slice(0, 5)
        const otherVotes = nationalResults.value
            .slice(5)
            .reduce((sum, party) => sum + party.votes, 0)

        if (!otherVotes) {
            return mainSlices
        }

        return [
            ...mainSlices,
            {
                partyName: 'Overig',
                votes: otherVotes,
                percentage: totalVotes.value === 0 ? 0 : Number(((otherVotes / totalVotes.value) * 100).toFixed(1)),
                color: '#cbd5e1'
            }
        ]
    })

    const leadingParty = computed(() => {
        return nationalResults.value[0] ?? null
    })

    async function loadResults() {
        isLoading.value = true
        errorMessage.value = ''

        try {
            const electionId = `TK${selectedYear.value}`
            await loadNationalElectionData(electionId)
            const constituencies = await fetchNationalConstituencies(electionId)
            const aggregatedVotes = new Map<string, number>()

            for (const constituency of constituencies) {
                for (const result of constituency.partyResults) {
                    const currentVotes = aggregatedVotes.get(result.partyName) ?? 0
                    aggregatedVotes.set(result.partyName, currentVotes + result.votes)
                }
            }

            const totalAggregatedVotes = Array.from(aggregatedVotes.values()).reduce((sum, votes) => sum + votes, 0)

            nationalResults.value = Array.from(aggregatedVotes.entries())
                .sort((a, b) => b[1] - a[1])
                .map(([partyName, votes], index) => ({
                    partyName,
                    votes,
                    percentage: totalAggregatedVotes === 0 ? 0 : Number(((votes / totalAggregatedVotes) * 100).toFixed(1)),
                    color: CHART_COLORS[index % CHART_COLORS.length]
                }))
        } catch (error) {
            console.error('Kon nationale resultaten niet ophalen', error)
            errorMessage.value = 'Nationale resultaten ophalen is mislukt.'
        } finally {
            isLoading.value = false
        }
    }

    watch(selectedYear, loadResults)
    onMounted(loadResults)

    return {
        availableYears: AVAILABLE_YEARS,
        selectedYear,
        nationalResults,
        topParties,
        chartSlices,
        leadingParty,
        totalVotes,
        isLoading,
        errorMessage
    }
}
