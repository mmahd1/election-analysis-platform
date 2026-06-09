<script setup lang="ts">
import type { Candidate } from '../types/Candidate'
import CandidateCard from './CandidateCard.vue'

defineProps<{
  candidates: Candidate[]
}>()
</script>

<template>
  <ul class="party-list">
    <CandidateCard
        v-for="(candidate, index) in candidates"
        :key="candidate.firstName + candidate.lastName"
        :candidate="candidate"
        :is-party-leader="index === 0"
    />
  </ul>
</template>

<style scoped>
.party-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 16px;
}

@media (max-width: 900px) {
  .party-list {
    gap: 14px;
  }
}

@media (max-width: 600px) {
  .party-list {
    gap: 12px;
  }
}
</style>