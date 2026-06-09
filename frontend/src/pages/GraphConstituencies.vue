<template>
  <div>
    <NavBarComponent />
  </div>

  <div class="page">
    <main id="main-content" tabindex="-1" class="content">

      <section class="hero-card">
        <h1>{{ translation.chartPage.title }}</h1>
        <p class="description">
          {{ translation.chartPage.description }}
        </p>
      </section>

      <section class="chart-section">

        <div class="party-list">
          <h2>{{ translation.chartPage.parties }}</h2>

          <p v-if="isLoading">{{ translation.chartPage.loading }}</p>
          <p v-else-if="errorMessage" class="error">{{ errorMessage }}</p>

          <ul v-else>
            <li v-for="party in parties" :key="party.name">
              <button
                  type="button"
                  class="party-item"
                  :class="{ selected: selectedParty.includes(party.name) }"
                  @click="selectParty(party.name)"
              >
                {{ party.name }}
              </button>
            </li>
          </ul>
        </div>

        <div class="chart-container">
          <h2>{{ translation.chartPage.chart }}</h2>

          <p v-if="isChartLoading">
            {{ translation.chartPage.loading }}
          </p>

          <Bar
            v-if="chartData.datasets.length > 0"
              :key="chartKey"
              :data="chartData"
              :options="{ responsive: true, maintainAspectRatio: false }"
          />

          <p v-else-if="!isChartLoading && chartData.datasets.length === 0">
            {{ translation.chartPage.selectParty }}
          </p>

          <div
              class="chart-message"
              v-if="!isChartLoading && hasNoDataParties"
          >
            {{ translation.constituency.partiesWithoutData }}
          </div>

        </div>

      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import NavBarComponent from '../components/NavBarComponent.vue'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js'
import { Bar } from 'vue-chartjs'
import type { ChartData } from 'chart.js'
import { useLanguage } from "../composables/LanguageButton"

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

if (!API_BASE_URL) {
  throw new Error('VITE_API_BASE_URL is not configured')
}

const hasNoDataParties = computed(() => {
  return partiesWithoutData.value.length > 0
})

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

type Party = {
  name: string
  description?: string
}

const { translation } = useLanguage()
const parties = ref<Party[]>([])
const partiesWithoutData = ref<string[]>([])
const isLoading = ref(true)
const errorMessage = ref('')
const isChartLoading = ref(false)
const selectedParty = ref<string[]>([])
const chartData = ref<ChartData<'bar'>>({
  labels: [],
  datasets: []
})
const chartKey = ref(0)

async function updateChart() {
  isChartLoading.value = true

  partiesWithoutData.value = []

  try {
    const datasets: any[] = []
    let labels: string[] = []

    for (const party of selectedParty.value) {
      try {
        const res = await fetch(
            `${API_BASE_URL}/api/chart/party/${encodeURIComponent(party)}/data`
        )

        if (!res.ok) {
          partiesWithoutData.value.push(party)
          continue
        }

        const data = await res.json()

        if (!data || data.length === 0) {
          partiesWithoutData.value.push(party)
          continue
        }

        labels = data.map((d: any) => d.kieskring)

        datasets.push({
          label: party,
          data: data.map((d: any) => d.stemmen),
          backgroundColor:
              selectedParty.value.indexOf(party) === 0
                  ? 'rgba(54,162,235,0.5)'
                  : 'rgba(255,99,132,0.5)'
        })
      } catch (err) {
        partiesWithoutData.value.push(party)
      }
    }

    chartData.value = { labels, datasets }
    chartKey.value++
  } finally {
    isChartLoading.value = false
  }
}

function selectParty(partyName: string) {
  if (selectedParty.value.includes(partyName)) {
    selectedParty.value = selectedParty.value.filter(p => p !== partyName)
  } else if (selectedParty.value.length < 2) {
    selectedParty.value.push(partyName)
  }

  updateChart()
}

onMounted(async () => {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await fetch(`${API_BASE_URL}/api/parties`)

    if (!response.ok) {
      throw new Error(translation.value.chartPage.fetchError)
    }
    const data = (await response.json()) as Party[]
    parties.value = data
  } catch (error) {
    errorMessage.value = translation.value.chartPage.fetchError
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>

.page {
  min-height: 100vh;
  padding: 24px;
  background:
      radial-gradient(circle at top right, rgba(14, 116, 144, 0.18), transparent 28%),
      linear-gradient(180deg, #f4f8fb 0%, #eef4f7 100%);
}

.content {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.hero-card {
  width: 100%;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.24);
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 36px;
}

h1 {
  margin: 0;
  font-size: clamp(2.2rem, 5vw, 4.2rem);
  line-height: 1.05;
  color: #0f172a;
}

.description {
  margin: 16px 0 0;
  font-size: 1.1rem;
  line-height: 1.6;
  color: #334155;
  max-width: 48rem;
}

.chart-section {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
  width: 100%;
}

.party-list {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.24);
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 28px;
}

.party-list h2 {
  margin: 0 0 20px;
  font-size: 1.75rem;
  color: #0f172a;
}

.party-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.party-list li {
  margin: 0;
  padding: 0;
}

.party-item {
  padding: 12px;
  cursor: pointer;
  border-radius: 8px;
  margin-bottom: 8px;
  background: #f8fafc;
  transition: background 0.2s;
  width: 100%;
  text-align: left;
  border: none;
  font: inherit;
}

.party-item:hover {
  background: #e2e8f0;
}

.party-item.selected {
  background: #2563eb;
  color: white;
}

.chart-message {
  margin-top: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  background: #fff7ed;
  border: 1px solid #fdba74;
  color: #9a3412;
  font-weight: 600;
  font-size: 0.95rem;
}

.chart-container {
  height: 500px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.24);
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 28px;
}

.chart-container h2 {
  margin: 0 0 20px;
  font-size: 1.75rem;
  color: #0f172a;
}

.error {
  color: #991b1b;
}

@media (max-width: 900px) {
  .chart-section {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 420px;
  }
}

@media (max-width: 640px) {
  .page {
    padding: 16px;
  }

  .hero-card {
    padding: 22px;
  }

  .party-list,
  .chart-container {
    padding: 18px;
    border-radius: 18px;
  }

  h1 {
    font-size: 2.2rem;
  }

  .chart-container {
    height: 340px;
  }

  .party-list li {
    padding: 10px;
  }
}

</style>
