<script setup lang="ts">
import NavBarComponent from '../components/NavBarComponent.vue'
import NationalResultsBarChart from '../components/NationalResultsBarChart.vue'
import NationalResultsDonutChart from '../components/NationalResultsDonutChart.vue'
import { NationalResults } from '../composables/NationalResults'
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
</script>

<template>
  <div class="page">
    <NavBarComponent />

    <main id="main-content" tabindex="-1" class="content">
      <section class="hero-card">
        <div>
          <p class="eyebrow">{{ translation.nationalPage.eyebrow }}</p>

          <h1>{{ translation.nationalPage.title }}</h1>

          <p class="description">
            {{ translation.nationalPage.description }}
          </p>
        </div>

        <div class="controls-card">
          <label class="year-select">
            <span>{{ translation.nationalPage.year }}</span>
            <select v-model="selectedYear">
              <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
            </select>
          </label>
        </div>
      </section>

      <p v-if="isLoading" class="status-message">
        {{ translation.nationalPage.loading }}
      </p>
      <p v-else-if="errorMessage" class="status-message error">{{ errorMessage }}</p>

      <template v-else>
        <section class="summary-grid">
          <article class="summary-card accent-card">
            <p class="card-label">{{ translation.nationalPage.leader }}</p>
            <h2>{{ leadingParty?.partyName || translation.nationalPage.unknown }}</h2>
            <p class="card-value">{{ leadingParty?.percentage ?? 0 }}%</p>
            <p class="card-meta">
              {{ leadingParty?.votes?.toLocaleString('nl-NL') ?? '0' }}
              {{ translation.nationalPage.votes }}
            </p>
          </article>

          <article class="summary-card">
            <p class="card-label">{{ translation.nationalPage.totalVotes }}</p>
            <p class="card-value">{{ totalVotes.toLocaleString('nl-NL') }}</p>
            <p class="card-meta">
              {{ translation.nationalPage.totalVotesDescription }}
            </p>
          </article>

          <article class="summary-card">
            <p class="card-label">{{ translation.nationalPage.partiesShown }}</p>
            <p class="card-value">{{ topParties.length }}</p>
            <p class="card-meta">
              {{ translation.nationalPage.partiesShownDescription }}
            </p>
          </article>
        </section>

        <section class="charts-grid">
          <NationalResultsBarChart :parties="topParties" />
          <NationalResultsDonutChart :parties="chartSlices" />
        </section>

        <section class="results-card">
          <div class="results-head">
            <div>
              <p class="eyebrow">{{ translation.nationalPage.results }}</p>
              <h2>{{ translation.nationalPage.topParties }}</h2>
            </div>
            <p class="results-caption">
              {{ translation.nationalPage.sortedByVotes }}
            </p>
          </div>

          <div class="results-list">
            <article v-for="party in topParties" :key="party.partyName" class="result-row">
              <div class="result-party">
                <span class="result-dot" :style="{ backgroundColor: party.color }"></span>
                <strong>{{ party.partyName }}</strong>
              </div>
              <div class="result-values">
                <span>
            {{ party.votes.toLocaleString('nl-NL') }}
            {{ translation.nationalPage.votes }}
                </span>
                <b>{{ party.percentage }}%</b>
              </div>
            </article>
          </div>
        </section>
      </template>
    </main>
  </div>
</template>

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
  grid-template-columns: minmax(0, 1.5fr) minmax(0, 0.7fr);
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
  font-size: clamp(2.2rem, 5vw, 4.4rem);
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

.status-message {
  margin: 0;
  padding: 18px 20px;
  border-radius: 18px;
  background: #e2e8f0;
  color: #0f172a;
}

.status-message.error {
  background: #fee2e2;
  color: #991b1b;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.summary-card {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.accent-card {
  background: linear-gradient(135deg, #0f766e 0%, #0f172a 100%);
  color: white;
}

.card-label {
  margin: 0 0 12px;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-weight: 800;
  opacity: 0.75;
}

.summary-card h2,
.results-card h2 {
  margin: 0;
  font-size: 1.7rem;
  color: inherit;
}

.card-value {
  margin: 10px 0 0;
  font-size: 2.2rem;
  font-weight: 800;
  color: inherit;
}

.card-meta {
  margin: 10px 0 0;
  color: inherit;
  opacity: 0.75;
}

.charts-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 1fr);
  gap: 24px;
}

.results-card {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.results-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 20px;
}

.results-caption {
  margin: 0;
  color: #64748b;
}

.results-list {
  display: grid;
  gap: 14px;
}

.result-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 18px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e2e8f0;
}

.result-party,
.result-values {
  display: flex;
  align-items: center;
  gap: 12px;
}

.result-dot {
  width: 14px;
  height: 14px;
  border-radius: 999px;
}

.result-values {
  color: #475569;
}

.result-values b {
  color: #0f172a;
}

@media (max-width: 980px) {
  .hero-card {
    grid-template-columns: 1fr;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 700px) {
  .content {
    padding: 20px 16px 32px;
  }

  .hero-card,
  .summary-card,
  .results-card {
    padding: 22px;
  }

  .results-head,
  .result-row,
  .result-values {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
