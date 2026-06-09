<script setup lang="ts">
import type { Candidate } from '../types/Candidate'

defineProps<{
  candidate: Candidate
  isPartyLeader?: boolean
}>()
</script>

<template>
  <li class="party-item">
    <div class="party-content">
      <div class="party-text">
        <span v-if="isPartyLeader" class="leader-label">
          Partijleider
        </span>

        <h3 class="party-name">
          {{ candidate.firstName }} {{ candidate.lastName }}
        </h3>

        <p class="party-description">
          Gender: {{ candidate.gender || 'Onbekend' }}
        </p>

        <p class="party-description">
          {{ candidate.votes.toLocaleString() }} stemmen
        </p>
      </div>
    </div>
  </li>
</template>

<style scoped>
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
  min-width: 0;
}

.leader-label {
  display: inline-block;
  margin-bottom: 10px;
  padding: 6px 12px;
  border-radius: 999px;
  background: #f59e0b;
  color: white;
  font-size: 0.85rem;
  font-weight: 700;
}

.party-name {
  margin: 0 0 10px;
  font-size: 1.4rem;
  font-weight: 800;
  color: #0f172a;
  word-break: break-word;
}

.party-description {
  margin: 0 0 6px;
  min-height: 24px;
  font-size: 1rem;
  line-height: 1.5;
  color: #475569;
  word-break: break-word;
}

@media (max-width: 900px) {
  .party-content {
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
  }
}

@media (max-width: 600px) {
  .party-content {
    padding: 16px;
    gap: 12px;
  }

  .party-name {
    font-size: 1.2rem;
  }

  .party-description {
    font-size: 0.95rem;
  }

  .leader-label {
    font-size: 0.8rem;
    padding: 5px 10px;
  }
}
</style>