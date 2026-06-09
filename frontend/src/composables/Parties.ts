import { computed, onMounted, ref, watch } from 'vue'
import type { Party } from '../interface/Party'
import { fetchParties } from '../api/partyApi'

const ELECTION_OPTIONS = [
    { value: 'TK2023', label: 'Tweede Kamer 2023' },
    { value: 'TK2025', label: 'Tweede Kamer 2025' }
]

export function Parties() {
    const parties = ref<Party[]>([])
    const isLoading = ref(true)
    const errorMessage = ref('')
    const searchQuery = ref('')
    const selectedElectionId = ref('TK2023')

    const currentPage = ref(1)
    const itemsPerPage = 5

    const filteredParties = computed(() => {
        const normalizedSearchQuery = searchQuery.value.trim().toLowerCase()

        if (!normalizedSearchQuery) {
            return parties.value
        }

        return parties.value.filter((party) => {
            const searchableText = `${party.name} ${party.description || ''}`.toLowerCase()
            return searchableText.includes(normalizedSearchQuery)
        })
    })

    const totalPages = computed(() => {
        return Math.max(1, Math.ceil(filteredParties.value.length / itemsPerPage))
    })

    const paginatedParties = computed(() => {
        const start = (currentPage.value - 1) * itemsPerPage
        const end = start + itemsPerPage
        return filteredParties.value.slice(start, end)
    })

    watch(searchQuery, () => {
        currentPage.value = 1
    })

    watch(selectedElectionId, async () => {
        await loadParties()
    })

    function nextPage() {
        if (currentPage.value < totalPages.value) {
            currentPage.value++
        }
    }

    function previousPage() {
        if (currentPage.value > 1) {
            currentPage.value--
        }
    }

    function goToPage(page: number) {
        currentPage.value = page
    }

    async function loadParties() {
        isLoading.value = true
        errorMessage.value = ''

        try {
            parties.value = await fetchParties(selectedElectionId.value)
            currentPage.value = 1
        } catch (error) {
            console.error('Kon partijen niet ophalen', error)
            errorMessage.value = 'Partijen ophalen is mislukt. Controleer of de backend bereikbaar is.'
        } finally {
            isLoading.value = false
        }
    }

    onMounted(async () => {
        await loadParties()
    })

    return {
        electionOptions: ELECTION_OPTIONS,
        selectedElectionId,
        parties,
        isLoading,
        errorMessage,
        searchQuery,
        currentPage,
        filteredParties,
        totalPages,
        paginatedParties,
        nextPage,
        previousPage,
        goToPage
    }
}
