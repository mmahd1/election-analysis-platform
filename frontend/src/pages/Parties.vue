<template>
    <div>
      <NavBarComponent />
    </div>

      <div class="page">
    <main class="content">
      <section class="hero-card">
        <h1>{{ translation.partiesPage.title }}</h1>
        <p class="intro">
          {{ translation.partiesPage.intro }}
        </p>
        <div class="election-picker">
          <label class="election-label" for="election-select">
            {{ translation.partiesPage.year }}
          </label>

          <select id="election-select" v-model="selectedElectionId" class="election-select">
            <option v-for="option in electionOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>
      </section>

      <section class="list-card">
        <div class="section-head">
          <h2>{{ translation.partiesPage.parties }}</h2>
          <span class="count" v-if="!isLoading && !errorMessage">
              {{ filteredParties.length }} / {{ parties.length }}
              {{ translation.partiesPage.found }}
          </span>
        </div>

        <p v-if="isLoading" class="status-message">
          {{ translation.partiesPage.loading }}
        </p>

        <p v-else-if="errorMessage" class="status-message error">{{ errorMessage }}</p>

        <div v-else>
          <div class="search-bar">

            <label class="search-label" for="party-search">
              {{ translation.partiesPage.searchLabel }}
            </label>

            <input
                id="party-search"
                v-model="searchQuery"
                type="search"
                class="search-input"
                :placeholder="translation.partiesPage.searchPlaceholder"
            />
          </div>

          <p v-if="!filteredParties.length" class="status-message empty-state">
            {{ translation.partiesPage.noResults }} "{{ searchQuery }}"
          </p>

          <ul v-else class="party-list">
            <li v-for="party in paginatedParties" :key="party.name" class="party-item">
              <div class="party-content">
                <div class="party-text">
                  <h3 class="party-name">{{ party.name }}</h3>
                  <p class="description-label">
                    {{ translation.partiesPage.ideology }}
                  </p>
                  <p class="party-description">
                    {{ party.description || translation.partiesPage.noIdeology }}
                  </p>
                </div>

                <div class="party-actions">
                  <button class="candidate-btn" @click="goToCandidates(party.name)">
                    {{ translation.partiesPage.viewCandidates }}
                  </button>
                </div>
              </div>
            </li>
          </ul>

          <div class="pagination" v-if="filteredParties.length && totalPages > 1">
            <button
              class="page-btn arrow-btn"
              @click="previousPage"
              :disabled="currentPage === 1"
            >
              &lt;
            </button>

            <button
              v-for="page in totalPages"
              :key="page"
              class="page-btn"
              :class="{ active: currentPage === page }"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>

            <button
              class="page-btn arrow-btn"
              @click="nextPage"
              :disabled="currentPage === totalPages"
            >
              &gt;
            </button>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>


<script setup lang="ts">
import { useRouter } from 'vue-router'
import NavBarComponent from '../components/NavBarComponent.vue'
import { Parties } from '../composables/Parties'
import { useLanguage } from "../composables/LanguageButton"
const { translation } = useLanguage()

const router = useRouter()
const {
  electionOptions,
  selectedElectionId,
  parties,
  isLoading,
  errorMessage,
  searchQuery,
  currentPage,
  filteredParties,
  totalPages,
  paginatedParties,
  nextPage,
  previousPage,
  goToPage
} = Parties()

function goToCandidates(partyName: string) {
  router.push({
    name: 'party-candidates',
    params: { partyName }
  })
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 24px;
  background:
      radial-gradient(circle at top right, rgba(14, 116, 144, 0.18), transparent 28%),
      linear-gradient(180deg, #f4f8fb 0%, #eef4f7 100%);
}

.logo {
  font-weight: bold;
  font-size: 32px;
  color: #0f172a;
}

.nav-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.nav-btn {
  background: #2563eb;
  color: white;
  border: none;
  padding: 8px 14px;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
  font-weight: 600;
  display: inline-block;
}

.nav-btn.current {
  background: #1d4ed8;
}

.link-btn {
  text-decoration: none;
}

.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: white;
  min-width: 160px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  margin-top: 5px;
  z-index: 10;
}

.dropdown-content a,
.dropdown-link {
  display: block;
  padding: 10px;
  text-decoration: none;
  color: black;
}

.dropdown-content a:hover,
.dropdown-link:hover {
  background-color: #f3f4f6;
}

.dropdown:hover .dropdown-content {
  display: block;
}

.content {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.hero-card,
.list-card {
  width: 100%;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.24);
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
}

.hero-card {
  padding: 36px;
}

h1 {
  margin: 0;
  font-size: clamp(2.2rem, 5vw, 4.2rem);
  line-height: 1.05;
  color: #0f172a;
}

.intro {
  margin: 16px 0 0;
  font-size: 1.1rem;
  line-height: 1.6;
  color: #334155;
  max-width: 48rem;
}

.election-picker {
  display: grid;
  gap: 10px;
  max-width: 320px;
  margin-top: 24px;
}

.election-label,
.description-label {
  font-weight: 700;
  color: #0f172a;
}

.election-select {
  width: 100%;
  padding: 12px 14px;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: #f8fafc;
  color: #0f172a;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.04);
}

.election-select:focus {
  outline: none;
  border-color: #0f766e;
  background: #ffffff;
  box-shadow:
      0 0 0 4px rgba(15, 118, 110, 0.12),
      0 8px 20px rgba(15, 23, 42, 0.08);
}

.list-card {
  padding: 28px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 20px;
}

h2 {
  margin: 0;
  font-size: 1.75rem;
  color: #0f172a;
}

.count {
  color: #0f766e;
  font-weight: 700;
  font-size: 1.1rem;
}

.search-bar {
  display: grid;
  gap: 10px;
  margin-bottom: 20px;
}

.search-label {
  font-weight: 700;
  color: #0f172a;
}

.search-input {
  width: 100%;
  padding: 12px 14px;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: #f8fafc;
  color: #0f172a;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.04);
}

.search-input::placeholder {
  color: #94a3b8;
}

.search-input:focus {
  outline: none;
  border-color: #0f766e;
  background: #ffffff;
  box-shadow:
      0 0 0 4px rgba(15, 118, 110, 0.12),
      0 8px 20px rgba(15, 23, 42, 0.08);
  transform: translateY(-1px);
}

.status-message {
  margin: 0;
  padding: 18px 20px;
  border-radius: 16px;
  background: #e2e8f0;
  color: #0f172a;
}

.status-message.error {
  background: #fee2e2;
  color: #991b1b;
}

.empty-state {
  margin-bottom: 20px;
}

.party-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 16px;
}

.party-item {
  border-radius: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #ecfeff 100%);
  border: 1px solid #ccfbf1;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.party-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  padding: 24px 28px;
}

.party-text {
  flex: 1;
}

.party-name {
  margin: 0 0 10px;
  font-size: 1.4rem;
  font-weight: 800;
  color: #0f172a;
}

.party-actions {
  display: grid;
  min-width: 180px;
}

.party-description {
  margin: 0;
  font-size: 1rem;
  line-height: 1.6;
  color: #475569;
}

.candidate-btn {
  border: none;
  border-radius: 999px;
  padding: 12px 18px;
  font-weight: 700;
  cursor: pointer;
  background: #facc15;
  color: #1f2937;
  white-space: nowrap;
}

.candidate-btn:hover {
  background: #eab308;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 28px;
  flex-wrap: wrap;
}

.page-btn {
  width: 42px;
  height: 42px;
  border: none;
  border-radius: 999px;
  background: #e5e7eb;
  color: #0f172a;
  font-weight: 700;
  cursor: pointer;
}

.page-btn.active {
  background: #facc15;
}

.page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.arrow-btn {
  font-size: 18px;
}

@media (max-width: 900px) {
  .party-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .party-actions,
  .candidate-btn {
    width: 100%;
  }
}

@media (max-width: 640px) {
  .page {
    padding: 16px;
  }

  .hero-card,
  .list-card {
    padding: 22px;
  }

  h1 {
    font-size: 2.2rem;
  }
}
</style>
