import { ref, onMounted } from 'vue'
import type { Candidate } from '../types/Candidate'
import { loadElectionData, fetchCandidatesByParty } from '../api/candidateApi'

export function Candidates(partyName: string, electionId = 'TK2025') {
    const candidates = ref<Candidate[]>([])
    const isLoading = ref(true)
    const errorMessage = ref('')

    onMounted(async () => {
        isLoading.value = true
        errorMessage.value = ''

        try {
            await loadElectionData(electionId)
            candidates.value = await fetchCandidatesByParty(electionId, partyName)
        } catch (error) {
            console.error('Kon kandidaten niet ophalen', error)
            errorMessage.value = 'Kandidaten ophalen is mislukt.'
        } finally {
            isLoading.value = false
        }
    })

    return {
        candidates,
        isLoading,
        errorMessage
    }
}
