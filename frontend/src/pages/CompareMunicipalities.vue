<template>
    <div>
        <NavBarComponent/>
    </div>

    <main id="main-content" tabindex="-1" class="page">

        <RouterLink to="/municipalities" class="back-btn">
            ← Uitslagen
        </RouterLink>

        <div class="container">

            <div class="selectors">

                <h2 class="title">Vergelijk gemeentes</h2>

                <div class="search-box">
                    <select v-model="selectedElectionId" class="year-select">
                        <option value="TK2023">Tweede Kamer 2023</option>
                        <option value="TK2025">Tweede Kamer 2025</option>
                    </select>
                </div>

                <div class="search-box">
                    <input v-model="searchA" placeholder="Zoek eerste gemeente..." />

                    <div v-if="searchA" class="dropdown">
                        <button
                            v-for="m in filteredA"
                            :key="m.regionNumber"
                            class="item"
                            @click="selectA(m)"
                        >
                            {{ m.regionName }}
                        </button>
                    </div>

                    <div v-if="selectedA" class="selected-tag">
                        ✓ {{ liveNameA }}
                    </div>
                </div>

                <div class="search-box">
                    <input v-model="searchB" placeholder="Zoek tweede gemeente..." />

                    <div v-if="searchB" class="dropdown">
                        <button
                            v-for="m in filteredB"
                            :key="m.regionNumber"
                            class="item"
                            @click="selectB(m)"
                        >
                            {{ m.regionName }}
                        </button>
                    </div>

                    <div v-if="selectedB" class="selected-tag">
                        ✓ {{ liveNameB }}
                    </div>
                </div>

                <button class="btn" :disabled="!canCompare" @click="fetchComparison">
                    Vergelijk
                </button>

            </div>

            <div class="results">

                <div v-if="loadingCompare">
                    Vergelijking laden...
                </div>

                <div v-else-if="comparisonData.length">

                    <h2>Vergelijking</h2>

                    <p class="subtitle">
                        {{ compareNameA }} vs {{ compareNameB }} ({{ selectedElectionId }})
                    </p>

                    <div class="chart-container">
                        <Bar :data="chartData" :options="chartOptions" />
                    </div>

                    <table class="table">
                        <thead>
                        <tr>
                            <th>Partij</th>
                            <th>{{ compareNameA }}</th>
                            <th>{{ compareNameB }}</th>
                        </tr>
                        </thead>

                        <tbody>
                        <tr v-for="r in comparisonData" :key="r.party">
                            <td class="party">{{ r.party }}</td>
                            <td>{{ r.votesA }}</td>
                            <td>{{ r.votesB }}</td>
                        </tr>
                        </tbody>
                    </table>

                </div>

                <div v-else class="empty">
                    Selecteer 2 gemeentes en klik op “Vergelijk”
                </div>

            </div>

        </div>
    </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue"
import { Bar } from "vue-chartjs"
import {
    Chart as ChartJS,
    Title,
    Tooltip,
    Legend,
    BarElement,
    CategoryScale,
    LinearScale
} from "chart.js"

import NavBarComponent from "../components/NavBarComponent.vue"

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

interface Municipality {
    regionName: string
    regionNumber: string
}

interface ComparisonResult {
    party: string
    votesA: number
    votesB: number
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL
if (!API_BASE_URL) throw new Error("VITE_API_BASE_URL is not configured")

/* STATE */
const municipalities = ref<Municipality[]>([])
const comparisonData = ref<ComparisonResult[]>([])

const selectedA = ref("")
const selectedB = ref("")
const searchA = ref("")
const searchB = ref("")

const selectedElectionId = ref("TK2025")

const loadingCompare = ref(false)

/* CACHE (SESSION PERSISTENT) */
import { useMunicipalityCache } from "../composables/UseMunicipalityCache"

const cache = useMunicipalityCache()

/* LOAD MUNICIPALITIES (NO RELOAD IF ALREADY LOADED) */
async function loadMunicipalities() {
    const year = selectedElectionId.value

    municipalities.value =
        cache.get(year) ||
        await cache.fetchYear(year, API_BASE_URL)
}

/* INIT */
onMounted(loadMunicipalities)

/* YEAR CHANGE */
watch(selectedElectionId, () => {
    selectedA.value = ""
    selectedB.value = ""
    searchA.value = ""
    searchB.value = ""

    loadMunicipalities()
})

/* FILTERS */
const filteredA = computed(() =>
    municipalities.value.filter(m =>
        m.regionName.toLowerCase().includes(searchA.value.toLowerCase())
    )
)

const filteredB = computed(() =>
    municipalities.value.filter(m =>
        m.regionName.toLowerCase().includes(searchB.value.toLowerCase())
    )
)

/* SELECT */
function selectA(m: Municipality) {
    selectedA.value = m.regionNumber
    searchA.value = ""
}

function selectB(m: Municipality) {
    selectedB.value = m.regionNumber
    searchB.value = ""
}

/* NAMES */
const liveNameA = computed(() =>
    municipalities.value.find(m => m.regionNumber === selectedA.value)?.regionName
)

const liveNameB = computed(() =>
    municipalities.value.find(m => m.regionNumber === selectedB.value)?.regionName
)

const compareNameA = computed(() =>
    municipalities.value.find(m => m.regionNumber === selectedA.value)?.regionName
)

const compareNameB = computed(() =>
    municipalities.value.find(m => m.regionNumber === selectedB.value)?.regionName
)

/* COMPARE */
async function fetchComparison() {
    loadingCompare.value = true

    // optional safety preload (meestal al geladen door main.ts)
    await Promise.all([
        cache.fetchYear(selectedElectionId.value, API_BASE_URL)
    ])

    const res = await fetch(
        `${API_BASE_URL}/api/municipalities/compare?electionId=${selectedElectionId.value}&idA=${selectedA.value}&idB=${selectedB.value}`
    )

    comparisonData.value = await res.json()

    loadingCompare.value = false
}

/* CHART */
const chartData = computed(() => ({
    labels: comparisonData.value.map(r => r.party),
    datasets: [
        {
            label: compareNameA.value,
            data: comparisonData.value.map(r => r.votesA),
            backgroundColor: "rgba(37, 99, 235, 0.8)"
        },
        {
            label: compareNameB.value,
            data: comparisonData.value.map(r => r.votesB),
            backgroundColor: "rgba(15, 23, 42, 0.8)"
        }
    ]
}))

const chartOptions = {
    responsive: true,
    maintainAspectRatio: false
}

const canCompare = computed(() =>
    selectedA.value &&
    selectedB.value &&
    selectedA.value !== selectedB.value
)
</script>

<style scoped>
/* PAGE */
.page {
  font-family: system-ui, Arial;
  min-height: 100vh;
  padding: 32px;
  background: radial-gradient(circle at top left, #dbeafe, transparent 40%),
  linear-gradient(180deg, #f8fbff, #eef3f8);
}

/* BACK BUTTON */
.back-btn {
  display: inline-flex;
  padding: 10px 16px;
  border-radius: 999px;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  color: white;
  text-decoration: none;
  font-weight: 600;
  margin-bottom: 16px;
  transition: 0.2s ease;
}

.back-btn:hover {
  transform: translateY(-2px);
}

/* LAYOUT */
.container {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
}

/* CARDS */
.selectors,
.results {
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
}

/* SELECTORS */
.selectors {
  padding: 24px;
  height: fit-content;
}

/* RESULTS */
.results {
  padding: 28px;
}

/* TITLES */
.title {
  font-size: 1.4rem;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.subtitle {
  color: #64748b;
  font-size: 0.95rem;
  margin-bottom: 18px;
}

/* SEARCH */
.search-box {
  position: relative;
  margin-bottom: 18px;
}

.search-box input {
  width: 100%;
  padding: 13px 14px;
  border-radius: 14px;
  border: 1px solid #dbe3ee;
  background: #f8fafc;
  transition: 0.2s ease;
  font-size: 0.95rem;
}

.search-box input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.15);
}

/* DROPDOWN */
.dropdown {
  position: absolute;
  width: 100%;
  margin-top: 6px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  max-height: 220px;
  overflow-y: auto;
  z-index: 10;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
}

.item {
  padding: 12px;
  cursor: pointer;
  transition: 0.15s ease;
}

.item:hover {
  background: #eff6ff;
}

/* SELECTED */
.selected-tag {
  margin-top: 8px;
  font-size: 0.88rem;
  color: #475569;
  font-weight: 600;
}

/* BUTTON */
.btn {
  width: 100%;
  padding: 14px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #0f172a, #1e293b);
  color: white;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s ease;
}

.btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.2);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ERROR */
.error-text {
  margin-top: 14px;
  color: #dc2626;
  font-weight: 700;
}

/* RESULTS HEADER */
.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* CHART */
.chart-container {
  position: relative;
  height: 420px;
  margin-bottom: 32px;
  padding: 18px;
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff, #f8fafc);
  border: 1px solid #e2e8f0;
}

/* TABLE */
.table {
  width: 100%;
  border-collapse: collapse;
  border-radius: 18px;
  overflow: hidden;
}

.table th {
  text-align: left;
  padding: 14px;
  color: #64748b;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.table td {
  padding: 14px;
  border-bottom: 1px solid #f1f5f9;
}

.table tr:hover {
  background: rgba(241, 245, 249, 0.5);
}

.party {
  font-weight: 700;
  color: #0f172a;
}

/* STATES */
.loading,
.empty {
  color: #64748b;
  font-size: 1rem;
}

@media (max-width: 1100px) {
  .page {
    padding: 20px;
  }

  .container {
    grid-template-columns: 280px 1fr;
    gap: 18px;
  }

  .chart-container {
    height: 360px;
  }
}

@media (max-width: 900px) {
  .container {
    grid-template-columns: 1fr;
  }

  .selectors {
    order: 1;
  }

  .results {
    order: 2;
  }

  .page {
    padding: 16px;
  }

  .selectors,
  .results {
    padding: 16px;
    border-radius: 18px;
  }

  .chart-container {
    height: 300px;
    padding: 12px;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 1.2rem;
  }

  .btn {
    padding: 12px;
    font-size: 0.95rem;
  }

  .search-box input {
    padding: 11px 12px;
  }

  .table th,
  .table td {
    padding: 10px;
    font-size: 0.9rem;
  }

  .back-btn {
    width: 100%;
    justify-content: center;
  }
}

.chart-container canvas {
  max-width: 100% !important;
}
</style>


