<script setup lang="ts">
defineProps<{
  title: string
  intro: string
}>()
</script>

<template>
  <section class="hero-card">
    <h1>{{ title }}</h1>
    <p class="intro">
      {{ intro }}
    </p>
  </section>
</template>

<style scoped>
.hero-card {
  width: 100%;
  box-sizing: border-box;
  background: rgba(255,255,255,0.92);
  border: 1px solid rgba(148,163,184,0.24);
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(15,23,42,0.08);
  backdrop-filter: blur(10px);
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

@media (max-width: 640px) {
  .hero-card {
    padding: 22px;
  }

  h1 {
    font-size: 2.2rem;
  }
}
</style>