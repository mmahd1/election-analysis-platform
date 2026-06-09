<template>
  <div class="page">
    <NavBarComponent />

    <main id="main-content" tabindex="-1" class="content">
      <section class="hero-card">
        <div>
          <p class="eyebrow">Resultaten</p>
          <h1>{{ selectedLevel === 'province' ? 'Provinciale uitslagen op de kaart' : 'Kieskring-uitslagen op de kaart' }}</h1>
          <p class="description">
            <span v-if="selectedLevel === 'province'">
              Bekijk per provincie welke partij de meeste stemmen heeft. Hover voor details, klik om een provincie te selecteren.
            </span>
            <span v-else>
              Bekijk per kieskring welke partij de meeste stemmen heeft. Hover voor details, klik op een marker om een kieskring te selecteren.
            </span>
          </p>
        </div>

        <div class="controls-card">
          <label class="year-select">
            <span>Verkiezingsjaar</span>
            <select v-model="selectedYear">
              <option v-for="year in AVAILABLE_YEARS" :key="year" :value="year">{{ year }}</option>
            </select>
          </label>

          <label class="year-select">
            <span>Niveau</span>
            <select v-model="selectedLevel" @change="onLevelChange">
              <option value="province">Provincies</option>
              <option value="constituency">Kieskringen</option>
            </select>
          </label>

          <div class="tabs" role="tablist" aria-label="weergave">
            <button
                class="tab"
                :class="{ active: currentView === 'map' }"
                type="button"
                @click="goToMap"
            >
              Kaart
            </button>
            <button
                class="tab"
                :class="{ active: currentView === 'chart' }"
                type="button"
                :disabled="!selectedRegion"
                @click="currentView = 'chart'"
            >
              Detail
            </button>
          </div>
        </div>
      </section>

      <template v-if="currentView === 'map'">
        <ElectionMapChart :election-id="electionId" :level="selectedLevel" @region-clicked="(region) => onRegionClick(region)" />
      </template>

      <template v-else>
        <section class="detail-card">
          <header class="detail-header">
            <div>
              <p class="eyebrow">Detail</p>
              <h2>{{ selectedRegion?.regionName || (selectedLevel === 'province' ? 'Geen provincie geselecteerd' : 'Geen kieskring geselecteerd') }}</h2>
            </div>
            <button class="back-btn" type="button" @click="goToMap">Terug naar kaart</button>
          </header>

          <div v-if="selectedRegion" class="detail-grid">
            <article
              v-if="selectedLevel === 'constituency' && 'constituencyNumber' in selectedRegion"
              class="pill"
            >
              <span class="pill-label">Kieskring</span>
              <strong class="pill-big">{{ selectedRegion.constituencyNumber }}</strong>
            </article>

            <article class="pill">
              <span class="pill-label">Koploper</span>
              <div class="pill-value">
                <span class="dot" :style="{ backgroundColor: selectedRegion.winningPartyColor }"></span>
                <strong>{{ selectedRegion.winningPartyName }}</strong>
              </div>
            </article>

            <article class="pill">
              <span class="pill-label">Stemmen koploper</span>
              <strong class="pill-big">{{ selectedRegion.winningPartyVotes.toLocaleString('nl-NL') }}</strong>
            </article>

            <article class="pill">
              <span class="pill-label">Totaal stemmen</span>
              <strong class="pill-big">{{ selectedRegion.totalVotes.toLocaleString('nl-NL') }}</strong>
            </article>
          </div>

          <p v-else class="detail-empty">
            Klik eerst op een {{ selectedLevel === 'province' ? 'provincie' : 'kieskring' }} in de kaart.
          </p>

        </section>
      </template>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import NavBarComponent from '../components/NavBarComponent.vue'
import ElectionMapChart from '../components/ElectionMapChart.vue'
import type { RegionLevel, RegionMapSummary } from '../types/ElectionMap'

const AVAILABLE_YEARS = ['2023', '2025']

type ViewMode = 'map' | 'chart'

const selectedYear = ref('2023')
const currentView = ref<ViewMode>('map')
const selectedLevel = ref<RegionLevel>('province')

const electionId = computed(() => `TK${selectedYear.value}`)

const selectedRegion = ref<RegionMapSummary | null>(null)

function onRegionClick(region: RegionMapSummary) {
    selectedRegion.value = region
    currentView.value = 'chart'
}

function goToMap() {
    currentView.value = 'map'
}

function onLevelChange() {
    // Bij wisselen van niveau: selectie en detail resetten zodat UI consistent blijft.
    selectedRegion.value = null
    currentView.value = 'map'
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background:
      radial-gradient(circle at top left, rgba(245, 158, 11, 0.18), transparent 22%),
      radial-gradient(circle at right, rgba(37, 99, 235, 0.12), transparent 28%),
      linear-gradient(180deg, #f8fbff 0%, #eef4f8 100%);
}

.content {
  max-width: 1240px;
  margin: 0 auto;
  padding: 28px 24px 48px;
  display: grid;
  gap: 24px;
}

.hero-card {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(260px, 0.7fr);
  gap: 24px;
  padding: 32px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(12px);
}

.eyebrow {
  margin: 0 0 10px;
  font-size: 0.82rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #0f766e;
}

h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.4rem);
  line-height: 1;
  color: #0f172a;
}

.description {
  margin: 18px 0 0;
  max-width: 52rem;
  font-size: 1.05rem;
  line-height: 1.7;
  color: #475569;
}

.controls-card {
  padding: 22px;
  border-radius: 24px;
  background: linear-gradient(155deg, #0f172a 0%, #1e293b 100%);
  color: white;
  display: grid;
  gap: 16px;
}

.year-select {
  display: grid;
  gap: 10px;
}

.year-select span {
  font-weight: 700;
}

.year-select select {
  width: 100%;
  padding: 12px 14px;
  border: none;
  border-radius: 14px;
  background: white;
  color: #0f172a;
  font-size: 1rem;
}

.tabs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.tab {
  padding: 10px 12px;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(255, 255, 255, 0.08);
  color: white;
  font-weight: 800;
  cursor: pointer;
}

.tab.active {
  background: white;
  color: #0f172a;
}

.tab:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.detail-card {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.detail-header h2 {
  margin: 0;
  font-size: 1.7rem;
  color: #0f172a;
}

.back-btn {
  border: none;
  border-radius: 999px;
  padding: 10px 14px;
  font-weight: 800;
  cursor: pointer;
  background: #2563eb;
  color: white;
}

.back-btn:hover {
  background: #1d4ed8;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.pill {
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e2e8f0;
}

.pill-label {
  display: block;
  font-size: 0.8rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
  margin-bottom: 10px;
}

.pill-value {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
}

.pill-big {
  font-size: 1.4rem;
  color: #0f172a;
}

.detail-empty {
  margin: 0;
  padding: 14px 16px;
  border-radius: 16px;
  background: #e2e8f0;
  color: #0f172a;
}


@media (max-width: 980px) {
  .hero-card {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 700px) {
  .content {
    padding: 20px 16px 32px;
  }

  .hero-card,
  .detail-card {
    padding: 20px;
  }

  .detail-header {
    flex-direction: column;
  }

  h1 {
    font-size: 2rem;
  }
}

@media (max-width: 420px) {
  .controls-card {
    padding: 16px;
  }

  .tab {
    font-size: 0.9rem;
    padding: 8px 10px;
  }

  .pill {
    padding: 12px;
  }
}
</style>

