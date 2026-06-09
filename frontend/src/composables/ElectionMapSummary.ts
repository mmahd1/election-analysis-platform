import { onMounted, ref, unref, watch, type ComputedRef, type Ref } from 'vue'
import { fetchConstituencyMapSummary, fetchRegionalMapSummary } from '../api/electionMapApi'
import type { ConstituencyMapSummary, ProvinceMapSummary, RegionLevel } from '../types/ElectionMap'

type MaybeRef<T> = T | Ref<T> | ComputedRef<T>

/**
 * Composable to load province map summary data for a given electionId.
 *
 * Responsibilities:
 * - Fetch summary data from the API
 * - Expose loading + error state
 * - Automatically reload when electionId changes
 */
export function ElectionMapSummary(electionId: MaybeRef<string>, level: MaybeRef<RegionLevel> = 'province') {
    const summary = ref<(ProvinceMapSummary | ConstituencyMapSummary)[]>([])
    const isLoading = ref(true)
    const errorMessage = ref('')

    async function reload() {
        isLoading.value = true
        errorMessage.value = ''

        try {
            const election = unref(electionId)
            const selectedLevel = unref(level)
            summary.value =
                selectedLevel === 'province'
                    ? await fetchRegionalMapSummary(election)
                    : await fetchConstituencyMapSummary(election)
        } catch (error) {
            console.error('Kon kaartdata niet ophalen', error)
            summary.value = []
            errorMessage.value = 'Kaartdata ophalen is mislukt.'
        } finally {
            isLoading.value = false
        }
    }

    // Load once and refresh when electionId changes
    watch([() => unref(electionId), () => unref(level)], () => reload(), { immediate: true })

    // In case this composable is used without any watcher (e.g. passing a plain string),
    // still ensure a load happens when the component is mounted.
    onMounted(() => {
        if (!summary.value.length && !errorMessage.value && !isLoading.value) {
            reload()
        }
    })

    return {
        summary,
        isLoading,
        errorMessage,
        reload
    }
}

