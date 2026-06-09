<script setup lang="ts">
import { computed } from 'vue'
import type { NationalPartyResult } from '../types/NationalResults'
import { useLanguage } from "../composables/LanguageButton"
const { translation } = useLanguage()

const props = defineProps<{
    parties: NationalPartyResult[]
}>()

const donutStyle = computed(() => {
    if (!props.parties.length) {
        return { background: '#e2e8f0' }
    }

    let currentAngle = 0
    const segments = props.parties.map((party) => {
        const nextAngle = currentAngle + (party.percentage / 100) * 360
        const segment = `${party.color} ${currentAngle}deg ${nextAngle}deg`
        currentAngle = nextAngle
        return segment
    })

    return {
        background: `conic-gradient(${segments.join(', ')})`
    }
})
</script>

<template>
  <section class="chart-card">
    <div class="chart-header">
      <div>
        <p class="eyebrow">{{ translation.nationalPage.donutLabel }}</p>

        <h2>{{ translation.nationalPage.donutTitle }}</h2>
      </div>
      <p class="chart-caption">
        {{ translation.nationalPage.donutCaption }}
      </p>    </div>

    <div class="content">
      <div class="donut" :style="donutStyle">
        <div class="donut-hole">
          <span>{{ translation.nationalPage.categories }}</span>
          <strong>{{ parties.length }}</strong>
        </div>
      </div>

      <div class="legend">
        <div v-for="party in parties" :key="party.partyName" class="legend-row">
          <span class="dot" :style="{ backgroundColor: party.color }"></span>
          <span class="legend-name">{{ party.partyName }}</span>
          <strong>{{ party.percentage }}%</strong>
        </div>
      </div>
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

.content {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 24px;
  align-items: center;
}

.donut {
  position: relative;
  width: 220px;
  height: 220px;
  border-radius: 50%;
}

.donut-hole {
  position: absolute;
  inset: 28px;
  border-radius: 50%;
  background: white;
  display: grid;
  place-items: center;
  text-align: center;
  color: #0f172a;
}

.donut-hole span {
  font-size: 0.9rem;
  color: #64748b;
}

.donut-hole strong {
  font-size: 1.8rem;
}

.legend {
  display: grid;
  gap: 12px;
}

.legend-row {
  display: grid;
  grid-template-columns: 16px 1fr auto;
  gap: 12px;
  align-items: center;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
}

.legend-name {
  color: #0f172a;
  font-weight: 600;
}

@media (max-width: 700px) {
  .chart-header,
  .content {
    grid-template-columns: 1fr;
  }

  .donut {
    margin: 0 auto;
  }
}
</style>
