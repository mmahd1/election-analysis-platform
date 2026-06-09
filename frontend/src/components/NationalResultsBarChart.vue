<script setup lang="ts">
import type { NationalPartyResult } from '../types/NationalResults'
import { useLanguage } from "../composables/LanguageButton"
const { translation } = useLanguage()

defineProps<{
    parties: NationalPartyResult[]
}>()
</script>

<template>
  <section class="chart-card">
    <div class="chart-header">
      <div>
        <p class="eyebrow">{{ translation.nationalPage.chartLabel }}</p>
        <h2>{{ translation.nationalPage.chartTitle }}</h2>
      </div>
      <p class="chart-caption">
        {{ translation.nationalPage.chartCaption }}
      </p>
    </div>

    <div class="bars">
      <article v-for="party in parties" :key="party.partyName" class="bar-row">
        <div class="bar-meta">
          <strong>{{ party.partyName }}</strong>
           <span>
            {{ party.percentage }}% ·
             {{ party.votes.toLocaleString('nl-NL') }}
  8           {{ translation.nationalPage.votes }}
           </span>
        </div>
        <div class="bar-track">
          <div class="bar-fill" :style="{ width: `${party.percentage}%`, backgroundColor: party.color }"></div>
        </div>
      </article>
    </div>
  </section>
</template>

<style scoped>
.chart-card {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 0.8rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #0f766e;
}

h2 {
  margin: 0;
  color: #0f172a;
  font-size: 1.5rem;
}

.chart-caption {
  margin: 0;
  color: #64748b;
}

.bars {
  display: grid;
  gap: 16px;
}

.bar-meta {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 8px;
  color: #334155;
}

.bar-track {
  height: 14px;
  border-radius: 999px;
  overflow: hidden;
  background: #e2e8f0;
}

.bar-fill {
  height: 100%;
  border-radius: 999px;
}

@media (max-width: 700px) {
  .chart-header,
  .bar-meta {
    flex-direction: column;
  }
}
</style>
