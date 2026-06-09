<template>
  <div>
    <NavBarComponent />
  </div>

  <div class="page">

    <h1 class="h1">
      {{ translation.home.titleLine1 }} {{ translation.home.titleLine2 }}
    </h1>
    <p class="line">▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</p>

    <p class="description">
      {{ translation.home.description }}
    </p>

    <div class="container">
      <div class="hero">
        <h3>
          {{ translation.home.discoverText }}
        </h3>

        <div class="hero-controls">
          <label class="year-select">
            <span class="year-select-label">{{ translation.home.year }}</span>
            <select v-model="selectedYear">
              <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
            </select>
          </label>
        </div>

        <p v-if="isLoading" class="hero-status">
          {{ translation.home.loadingNational }}
        </p>
        <p v-else-if="errorMessage" class="hero-status error">{{ errorMessage }}</p>
        <div v-else class="hero-chart">
          <div class="hero-summary">
            <div class="summary-pill">
              <span class="summary-label">{{ translation.home.leader }}</span>
              <strong>{{ leadingParty?.partyName || translation.home.unknown }}</strong>
            </div>
            <div class="summary-pill">
              <span class="summary-label">{{ translation.home.share }}</span>
              <strong>{{ leadingParty?.percentage ?? 0 }}%</strong>
            </div>
            <div class="summary-pill">
              <span class="summary-label">{{ translation.home.votes }}</span>
              <strong>{{ totalVotes.toLocaleString('nl-NL') }}</strong>
            </div>
          </div>
          <div class="hero-graph-preview">
            <NationalResultsBarChart :parties="topThreeParties" />
          </div>
        </div>
      </div>

      <div class="map">
        <div class="home-map" aria-label="Kaartweergave">
          <ElectionMapChart :election-id="electionId" level="province" :chart-height="420" />
        </div>
      </div>

      <div class="card">
        <h2>{{ translation.home.nationalWinners }}</h2>
        <p v-if="isLoading" class="result-status">
          {{ translation.home.resultsLoading }}
        </p>
        <p v-else-if="errorMessage" class="result-status error">{{ errorMessage }}</p>
        <div v-else class="results">
          <div v-for="party in topFiveParties" :key="party.partyName" class="result-row">
            <div class="result-text">
              <span>{{ party.partyName }}</span>
            </div>
            <b>{{ party.percentage }}%</b>
          </div>
        </div>
      </div>

      <div class="card">
        <h2>Grootste partijen</h2>
        <p v-if="isLoading" class="result-status">Partijleiders laden...</p>
        <p v-else-if="errorMessage" class="result-status error">{{ errorMessage }}</p>
        <div v-else class="big-parties">
          <div v-for="(party, index) in topThreeParties" :key="party.partyName" class="party">
            <strong>{{ index + 1 }} {{ party.partyName }}</strong>
          </div>
        </div>
        <RouterLink to="/parties" class="main-btn link-btn">
          {{ translation.home.seeMoreParties }}
        </RouterLink>
      </div>

      <div class="bottom">

        <div class="form-card">
          <h1>{{ translation.home.forum }}</h1>
          <img :src="imageForm" alt="image" class="form-img" />

          <RouterLink to="/ForumPage" class="main-btn">
            {{ translation.home.viewForum }}
          </RouterLink>
        </div>

        <div class="card graph-card">
          <h2>{{ translation.home.charts }}</h2>
          <p v-if="isLoading" class="graph-status">
            {{ translation.home.chartsLoading }}
          </p>
          <p v-else-if="errorMessage" class="graph-status error">{{ errorMessage }}</p>
          <div v-else class="graph-preview">
            <NationalResultsDonutChart :parties="chartSlices" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import NationalResultsBarChart from '../components/NationalResultsBarChart.vue'
import NationalResultsDonutChart from '../components/NationalResultsDonutChart.vue'
import NavBarComponent from '../components/NavBarComponent.vue'
import ElectionMapChart from '../components/ElectionMapChart.vue'
import { NationalResults } from '../composables/NationalResults'
import imageForm from '../images/image-form.png'

import { useLanguage } from "../composables/LanguageButton"
const { translation } = useLanguage()

const {
  availableYears,
  selectedYear,
  topParties,
  chartSlices,
  leadingParty,
  totalVotes,
  isLoading,
  errorMessage
} = NationalResults()

const electionId = computed(() => `TK${selectedYear.value}`)


const topThreeParties = computed(() => {
  return topParties.value.slice(0, 3)
})

const topFiveParties = computed(() => {
  return topParties.value.slice(0, 5)
})
</script>

<style scoped>
body {
  font-family: Arial, sans-serif;
  background: #f9fafb;
  margin: 0;
  box-sizing: border-box;
}

.h1 {
  max-width: 1200px;
  margin: 20px auto 0;
  padding: 0 20px;
  color: #1f2028;
  font-size: clamp(1.5rem, 4vw, 2.5rem);
  text-align: center;
}

.line {
  color: #f59e0b;
  font-weight: bold;
  text-align: center;
  margin: 10px 0 30px;
}

.description {
  max-width: 800px;
  margin: 0 auto 30px;
  padding: 0 20px;
  text-align: center;
  color: #4b5563;
  line-height: 1.6;
}

.form-img {
  width: 200px;
  height: auto;
}

.container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin: 30px auto;
  max-width: 1200px;
  padding: 0 20px;
}

.hero {
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.hero-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: end;
}

.year-select {
  display: grid;
  gap: 6px;
}

.year-select span {
  font-size: 0.9rem;
  color: #475569;
}

.year-select select {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #cbd5e1;
}

.hero-status,
.graph-status {
  color: #64748b;
}

.hero-status.error,
.graph-status.error {
  color: #991b1b;
}

.hero-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-chart {
  display: grid;
  gap: 14px;
  margin-top: 14px;
}

.summary-pill {
  min-width: 120px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.summary-label {
  display: block;
  margin-bottom: 4px;
  font-size: 0.8rem;
  color: #64748b;
  text-transform: uppercase;
}

.map {
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: stretch;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.home-map {
  width: 100%;
  flex: 1;
}

/* Make the map chart act as a preview inside the Home grid tile. */
.home-map :deep(.map-card) {
  padding: 0;
  border: none;
  box-shadow: none;
  background: transparent;
}

.home-map :deep(.map-header) {
  display: none;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.results {
  margin-top: 10px;
}

.result-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.result-text {
  display: grid;
  gap: 4px;
}

.result-text small,
.party small {
  color: #64748b;
}

.big-parties {
  display: flex;
  gap: 15px;
  margin: 10px 0;
}

.party {
  display: grid;
  gap: 4px;
  background: #f3f4f6;
  padding: 15px;
  border-radius: 10px;
  flex: 1;
  text-align: center;
}

.main-btn {
  display: inline-block;
  background: #2563eb;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 10px;
  text-decoration: none;
}

.bottom {
  grid-column: span 2;
  display: flex;
  gap: 30px;
}

.bottom .card {
  flex: 1;
}

.graph-card {
  display: flex;
  flex-direction: column;
}

.graph-preview {
  margin-top: 10px;
}

.hero-graph-preview :deep(.chart-card),
.graph-preview :deep(.chart-card) {
  padding: 0;
  border: none;
  box-shadow: none;
  background: transparent;
}

.hero-graph-preview :deep(.chart-header),
.graph-preview :deep(.chart-header) {
  display: none;
}

/* RESPONSIVE */
@media (max-width: 900px) {
  .container {
    grid-template-columns: 1fr;
  }

  .bottom {
    flex-direction: column;
  }

  .map {
    height: auto;
  }
}

@media (max-width: 600px) {
  .h1 {
    font-size: 1.8rem;
    padding: 0 12px;
  }

  .description {
    padding: 0 12px;
    font-size: 0.95rem;
  }

  .container {
    padding: 0 12px;
    gap: 20px;
  }

  .hero,
  .card,
  .form-card {
    padding: 16px;
    border-radius: 10px;
  }

  .summary-pill {
    min-width: 100%;
  }

  .big-parties {
    flex-direction: column;
  }

  .main-btn {
    width: 100%;
    text-align: center;
  }

  .map {
    height: 160px;
  }
}
</style>
