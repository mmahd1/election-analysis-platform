<template>
    <div>
        <NavBarComponent/>
    </div>

    <div class="page">
        <div class="top-actions">
            <RouterLink to="/" class="back-btn">
                {{ translation.municipalitiesPage.back }}
            </RouterLink>

            <RouterLink to="/compareMunicipalities" class="compare-btn">
                {{ translation.municipalitiesPage.compareMunicipalities }}
            </RouterLink>
        </div>

        <div class="container">
            <!-- Sidebar -->
            <div class="sidebar">
                <p v-if="loading">
                    {{ translation.municipalitiesPage.loading }}
                </p>

                <p v-else-if="error" class="error">
                    {{ error }}
                </p>

                <div v-else>
                    <!-- Election selector -->
                    <div class="dropdown">
                        <button class="dropdown-button">
                            {{ ELECTION_OPTIONS.find(o => o.value === selectedElectionId)?.label }}
                        </button>

                        <div class="dropdown-menu">
                            <div
                                v-for="option in ELECTION_OPTIONS"
                                :key="option.value"
                                class="dropdown-item"
                                :class="{ active: option.value === selectedElectionId }"
                                @click="
                  selectedElectionId = option.value;
                  loadMunicipalities();
                "
                            >
                                {{ option.label }}
                            </div>
                        </div>
                    </div>

                    <!-- Zoek -->
                    <input
                        v-model="search"
                        @input="searchMunicipalities"
                        type="text"
                        :placeholder="translation.municipalitiesPage.searchPlaceholder"
                        class="search-input"
                    />

                    <p v-if="search && !municipalities.length" class="empty-search">
                        {{ translation.municipalitiesPage.noResults }}
                    </p>

                    <!-- Gemeente lijst -->
                    <ul class="constituency-list">
                        <li
                            v-for="m in municipalities"
                            :key="m.regionNumber"
                            class="constituency-item"
                            :class="{ active: selectedMunicipality?.regionNumber === m.regionNumber }"
                            @click="selectedMunicipality = m"
                        >
              <span class="constituency-number">
                {{ m.regionNumber }}
              </span>

                            <span class="constituency-name">
                {{ m.regionName }}
              </span>
                        </li>
                    </ul>
                </div>
            </div>

            <!-- Resultaten -->
            <div class="results">
                <div v-if="selectedMunicipality">
                    <p class="results-label">
                        {{ translation.municipalitiesPage.resultsPerParty }}
                    </p>

                    <h2>
                        {{ selectedMunicipality.regionName }}
                    </h2>

                    <div class="party-results">
                        <div
                            v-for="party in sortedPartyResults"
                            :key="party.partyName"
                            class="party-result"
                        >
                            <span>{{ party.partyName }}</span>
                            <strong>{{ party.votes }}</strong>
                        </div>
                    </div>
                </div>

                <div v-else class="empty-state">
                    <p class="results-label">
                        {{ translation.municipalitiesPage.overview }}
                    </p>

                    <h2>
                        {{ translation.municipalitiesPage.select }}
                    </h2>

                    <p>
                        {{ translation.municipalitiesPage.selectDescription }}
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import {computed, onMounted, ref} from 'vue'
import NavBarComponent from '../components/NavBarComponent.vue'
import {useLanguage} from '../composables/LanguageButton'
import {useMunicipalityCache} from "../composables/UseMunicipalityCache.ts";


const cache = useMunicipalityCache()

const {translation} = useLanguage()

const ELECTION_OPTIONS = [
    {value: 'TK2023', label: 'Tweede Kamer 2023'},
    {value: 'TK2025', label: 'Tweede Kamer 2025'}
]

interface PartyResult {
    partyName: string
    votes: number
}

interface Municipality {
    regionName: string
    regionNumber: string
    partyResults: PartyResult[]
}

const selectedElectionId = ref('TK2023')

const municipalities = ref<Municipality[]>([])
const loading = ref(true)
const error = ref('')
const selectedMunicipality = ref<Municipality | null>(null)
const search = ref('')
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

if (!API_BASE_URL) {
    throw new Error('VITE_API_BASE_URL is not configured')
}

const sortedPartyResults = computed(() => {
    if (!selectedMunicipality.value) {
        return []
    }

    return [...selectedMunicipality.value.partyResults].sort(
        (a, b) => b.votes - a.votes
    )
})

async function loadMunicipalities() {
    loading.value = true
    error.value = ''
    search.value = ''
    selectedMunicipality.value = null

    try {
        municipalities.value =
            cache.get(selectedElectionId.value) ||
            await cache.fetchYear(selectedElectionId.value, API_BASE_URL)

    } catch (err) {
        console.error(err)
        error.value = translation.value.municipalitiesPage.fetchError
        municipalities.value = []
    } finally {
        loading.value = false
    }
}

async function searchMunicipalities() {
    if (!search.value.trim()) {
        await loadMunicipalities()
        return
    }

    try {
        const response = await fetch(
            `${API_BASE_URL}/api/municipalities/search?electionId=${selectedElectionId.value}&regionName=${encodeURIComponent(search.value)}`
        )

        if (!response.ok) {
            throw new Error()
        }

        municipalities.value = await response.json()
        selectedMunicipality.value = null
    } catch (err) {
        console.error('Zoeken mislukt', err)

        error.value = translation.value.municipalitiesPage.fetchError
        municipalities.value = []
    }
}

onMounted(loadMunicipalities)
</script>

<style scoped>
.page {
    font-family: Arial, sans-serif;
    min-height: 100vh;
    padding: 32px;
    background: radial-gradient(circle at top left, rgba(37, 99, 235, 0.14), transparent 24%),
    linear-gradient(180deg, #f8fbff 0%, #eef3f8 100%);
    color: #0f172a;
    box-sizing: border-box;
}

.top-actions {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
}

.back-btn {
    display: inline-flex;
    align-items: center;
    padding: 10px 14px;
    border-radius: 999px;
    background: linear-gradient(135deg, #1d4ed8, #2563eb);
    color: #fff;
    text-decoration: none;
    font-weight: 600;
}

.compare-btn {
    display: inline-flex;
    align-items: center;
    padding: 10px 14px;
    border-radius: 999px;
    background: #0f172a;
    color: #fff;
    text-decoration: none;
    font-weight: 600;
}

.container {
    display: grid;
    grid-template-columns: 320px minmax(0, 1fr);
    gap: 24px;
}

/* SIDEBAR */
.sidebar {
    background: rgba(255, 255, 255, 0.88);
    padding: 20px;
    border-radius: 24px;
}

/* DROPDOWN (FIXED VISUAL ONLY) */
.dropdown {
    position: relative;
    margin-bottom: 14px;
}

.dropdown-button {
    width: 100%;
    padding: 12px 14px;
    border-radius: 14px;
    border: 1px solid rgba(148, 163, 184, 0.35);
    background: white;
    text-align: left;
    cursor: pointer;
    font-weight: 600;
    transition: 0.2s ease;
}

.dropdown-button:hover {
    border-color: #93c5fd;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* BELANGRIJK: verborgen tenzij hover */
.dropdown-menu {
    display: none;
    position: absolute;
    top: calc(100% + 6px);
    left: 0;
    right: 0;
    background: white;
    border-radius: 14px;
    border: 1px solid #e2e8f0;
    overflow: hidden;
    z-index: 50;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
}

/* hover open */
.dropdown:hover .dropdown-menu {
    display: block;
}

.dropdown-item {
    padding: 10px 12px;
    cursor: pointer;
    transition: 0.15s;
}

.dropdown-item:hover {
    background: #eff6ff;
}

.dropdown-item.active {
    background: #dbeafe;
    font-weight: 600;
}

/* rest unchanged */
.search-input {
    width: 100%;
    padding: 12px 14px;
    margin-bottom: 14px;
    border-radius: 14px;
    border: 1px solid rgba(148, 163, 184, 0.35);
}

.constituency-list {
    list-style: none;
    margin: 0;
    padding: 0;
}

.constituency-item {
    cursor: pointer;
    padding: 12px 14px;
    border-radius: 16px;
    display: flex;
    gap: 12px;
    margin-bottom: 10px;
    background: #f8fafc;
    transition: 0.2s ease;
}

.constituency-item.active {
    background: #dbeafe;
}

.constituency-item:hover {
    background: #eff6ff;
}

.constituency-number {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #dbeafe;
    color: #1d4ed8;
    font-weight: 700;
}

.results {
    background: rgba(255, 255, 255, 0.78);
    min-height: 520px;
    padding: 28px;
    border-radius: 24px;
}

.party-results {
    display: grid;
    gap: 12px;
}

.party-result {
    display: flex;
    justify-content: space-between;
    padding: 14px 16px;
    border-radius: 16px;
    background: #fff;
    border: 1px solid #e2e8f0;
}
</style>
