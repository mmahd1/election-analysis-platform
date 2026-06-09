<script setup lang="ts">
defineProps<{
  message: string
  isError?: boolean
}>()
</script>

<template>
  <p :class="['status-message', { error: isError }]">
    {{ message }}
  </p>
</template>

<style scoped>
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
</style>