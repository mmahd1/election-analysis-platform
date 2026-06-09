<template>

  <div>
    <NavBarComponent />
  </div>

  <main id="main-content" tabindex="-1" class="page">

    <div class="top-bar">
      <RouterLink to="/" class="back-btn">
        {{ translation.constituency.back }}

      </RouterLink>

      <RouterLink to="/compareConstituency" class="compare-btn">
        {{ translation.constituency.compare }}
      </RouterLink>
    </div>

    <div class="year-select">
      <label for="year">
        {{ translation.constituency.chooseYear }}
      </label>
      <select v-model="year" @change="reloadData">
        <option value="2023">2023</option>
        <option value="2025">2025</option>
      </select>
    </div>

    <div class="container">
      <div class="sidebar">
        <p v-if="loading">
          {{ translation.constituency.loading }}
        </p>

        <p v-else-if="error">
          {{ translation.constituency.fetchError }}
        </p>

        <ul v-else class="constituency-list">
          <li v-for="constituency in sortedConstituencies" :key="constituency.number">
            <button
                type="button"
                class="constituency-item"
                :class="{ active: selectedConstituency?.number === constituency.number }"
                @click="selectedConstituency = constituency"
            >
              <span class="constituency-number">{{ constituency.number }}</span>
              <span class="constituency-name">{{ constituency.name }}</span>
            </button>
          </li>
        </ul>
      </div>

      <div class="results">
        <div v-if="selectedConstituency">
          <p class="results-label">
            {{ translation.constituency.resultsPerParty }}
          </p>
          <h2>{{ selectedConstituency.name }}</h2>

          <div class="party-results">
            <div
                v-for="constituencyResult in sortPartyResults"
                :key="constituencyResult.partyName"
                class="party-result"
            >
              <span>{{ constituencyResult.partyName }}</span>
              <strong>{{ constituencyResult.votes }}</strong>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <p class="results-label">
            {{ translation.constituency.overview }}
          </p>
          <h2>
            {{ translation.constituency.selectPrompt }}
          </h2>
          <p>
            {{ translation.constituency.selectDescription }}
          </p>
        </div>

      </div>

    </div>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useLanguage } from "../composables/LanguageButton"
import { computed } from 'vue'
import NavBarComponent from "../components/NavBarComponent.vue";
import { useConstituencies } from "../composables/UseConstituency.ts";


/**
 * Sorteren van de stemmen
 *
 * Stemmen worden gesorteerd van hoog naar laag
 */

const sortPartyResults = computed(() => {
  if (!selectedConstituency.value) return []

  return [...selectedConstituency.value.partyResults]
      .sort((a, b) => b.votes - a.votes)
})

const { translation } = useLanguage()
const year = ref('2023')

const {
  constituencies,
  loading,
  error,
  selectedConstituency,
  loadConstituencies
} = useConstituencies()

const sortedConstituencies = computed(() => {
  return [...constituencies.value].sort((a, b) => a.number - b.number)
})

function reloadData() {
  loadConstituencies(year.value)
}

onMounted(() => {
  loadConstituencies(year.value)
})
</script>


<style scoped>
.page {
  font-family: Arial, sans-serif;
  min-height: 100vh;
  padding: 32px;
  background:
      radial-gradient(circle at top left, rgba(37, 99, 235, 0.14), transparent 24%),
      linear-gradient(180deg, #f8fbff 0%, #eef3f8 100%);
  color: #0f172a;
  box-sizing: border-box;
}

.year-select {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.year-select select {
  padding: 6px 10px;
  border-radius: 8px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.compare-btn {
  padding: 10px 16px;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  color: #fff;
  border-radius: 999px;
  border: none;
  font-weight: 600;
  letter-spacing: 0.01em;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.22);
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.compare-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 28px rgba(37, 99, 235, 0.28);
}

.container {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 24px;
  align-items: start;
  max-width: 1200px;
}

.results {
  background: rgba(255, 255, 255, 0.78);
  min-height: 520px;
  padding: 28px;
  border-radius: 24px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(12px);
}

.sidebar {
  background: rgba(255, 255, 255, 0.88);
  padding: 20px;
  border-radius: 24px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  max-height: calc(100vh - 140px);
  overflow: auto;
}

.back-btn {
  display: inline-block;
  margin-bottom: 20px;
  padding: 10px 16px;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  color: #fff;
  border-radius: 999px;
  text-decoration: none;
  font-weight: 600;
  letter-spacing: 0.01em;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.22);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.back-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 28px rgba(37, 99, 235, 0.28);
}

.constituency-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.constituency-list li {
  margin: 0;
  padding: 0;
}

.constituency-item {
  cursor: pointer;
  padding: 12px 14px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8fafc;
  border: 1px solid transparent;
  width: 100%;
  text-align: left;
  font: inherit;
  transition:
      background-color 0.18s ease,
      transform 0.18s ease,
      border-color 0.18s ease,
      box-shadow 0.18s ease;
}

.constituency-item:hover {
  background-color: #eff6ff;
  border-color: rgba(37, 99, 235, 0.25);
  transform: translateX(2px);
}

.constituency-item.active {
  background: linear-gradient(135deg, #dbeafe, #eff6ff);
  border-color: rgba(37, 99, 235, 0.32);
  box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.08);
}

.constituency-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 0.9rem;
  font-weight: 700;
}

.constituency-name {
  font-weight: 600;
  color: #1e293b;
}

.results-label {
  margin: 0 0 8px;
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
}

.results h2 {
  margin: 0 0 20px;
  font-size: clamp(1.6rem, 2vw, 2rem);
  line-height: 1.1;
}

.party-results {
  display: grid;
  gap: 12px;
}

.party-result {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 14px 16px;
  border-radius: 16px;
  background: linear-gradient(180deg, #ffffff, #f8fafc);
  border: 1px solid #e2e8f0;
}

.party-result span {
  color: #334155;
}

.party-result strong {
  color: #0f172a;
  font-size: 1rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 100%;
  color: #475569;
}

.empty-state p:last-child {
  margin: 0;
  max-width: 38ch;
  line-height: 1.6;
}

@media (max-width: 900px) {
  .page {
    padding: 20px;
  }

  .container {
    grid-template-columns: 1fr;
  }

  .sidebar {
    max-height: none;
  }

  .results {
    min-height: 360px;
  }
}

@media (max-width: 600px) {
  .page {
    padding: 16px;
  }

  .sidebar,
  .results {
    padding: 18px;
    border-radius: 18px;
  }

  .party-result {
    padding: 12px 14px;
  }
}

</style>
