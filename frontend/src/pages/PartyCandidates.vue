<template>


    <NavBarComponent />
    <div class="page">
    <main class="content">
      <PageHeader
          :title="translation.candidatesPage.title.replace('{party}', partyName)"
          :intro="translation.candidatesPage.intro.replace('{party}', partyName)"
      />
      <section class="list-card">
        <SectionHeader
            :title="translation.candidatesPage.sectionTitle"
            :count="!isLoading && !errorMessage ? candidates.length : undefined"
        />
        <StatusMessage
            v-if="isLoading"
            :message="translation.candidatesPage.loading"
        />
        <StatusMessage
            v-else-if="errorMessage"
            :message="errorMessage"
            :isError="true"
        />
        <CandidateList
            v-else
            :candidates="candidates"
        />
      </section>
    </main>
  </div>
</template>


<script setup lang="ts">
import { useRoute } from 'vue-router'
import NavBarComponent from '../components/NavBarComponent.vue'
import PageHeader from '../components/PageHeader.vue'
import SectionHeader from '../components/SectionHeader.vue'
import StatusMessage from '../components/StatusMessage.vue'
import CandidateList from '../components/CandidateList.vue'
import { Candidates } from '../composables/Candidates'
import { useLanguage } from "../composables/LanguageButton"

const { translation } = useLanguage()

const route = useRoute()
const partyName = route.params.partyName as string

const { candidates, isLoading, errorMessage } = Candidates(partyName)
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 24px;
  background:
      radial-gradient(circle at top right, rgba(14,116,144,0.18), transparent 28%),
      linear-gradient(180deg, #f4f8fb 0%, #eef4f7 100%);
}

.content {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.list-card {
  width: 100%;
  box-sizing: border-box;
  background: rgba(255,255,255,0.92);
  border: 1px solid rgba(148,163,184,0.24);
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(15,23,42,0.08);
  backdrop-filter: blur(10px);
  padding: 28px;
}

@media (max-width: 980px) {
  .list-card {
    padding: 24px;
  }
}

@media (max-width: 640px) {
  .page {
    padding: 16px;
  }

  .list-card {
    padding: 22px;
  }
}
</style>