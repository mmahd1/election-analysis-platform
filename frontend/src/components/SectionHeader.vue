<script setup lang="ts">
defineProps<{
  title: string
  count?: number
}>()
</script>

<template>
  <div class="section-head">
    <h2>{{ title }}</h2>
    <span v-if="count !== undefined" class="count">
      {{ count }} gevonden
    </span>
  </div>
</template>

<style scoped>
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
</style>