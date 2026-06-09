import { computed, ref, watch } from 'vue'

const STORAGE_KEY = 'app-font-size-scale'
const DEFAULT_SCALE = 1
const MIN_SCALE = 0.9
const MAX_SCALE = 1.25
const STEP = 0.05

function normalizeScale(value: number) {
  return Math.min(MAX_SCALE, Math.max(MIN_SCALE, Number(value.toFixed(2))))
}

function getSavedScale() {
  const savedScale = Number(localStorage.getItem(STORAGE_KEY))

  if (!Number.isFinite(savedScale)) {
    return DEFAULT_SCALE
  }

  return normalizeScale(savedScale)
}

const fontScale = ref(getSavedScale())

function applyFontScale(scale: number) {
  document.documentElement.style.setProperty('--app-font-scale', String(scale))
  document.documentElement.style.setProperty('--app-root-font-size', `${16 * scale}px`)
  document.body.style.setProperty('zoom', String(scale))
}

watch(
  fontScale,
  (scale) => {
    const normalizedScale = normalizeScale(scale)
    localStorage.setItem(STORAGE_KEY, String(normalizedScale))
    applyFontScale(normalizedScale)
  },
  { immediate: true },
)

export function useFontSize() {
  const percentage = computed(() => Math.round(fontScale.value * 100))
  const canDecrease = computed(() => fontScale.value > MIN_SCALE)
  const canIncrease = computed(() => fontScale.value < MAX_SCALE)
  const isDefault = computed(() => fontScale.value === DEFAULT_SCALE)

  function increaseFontSize() {
    fontScale.value = normalizeScale(fontScale.value + STEP)
  }

  function decreaseFontSize() {
    fontScale.value = normalizeScale(fontScale.value - STEP)
  }

  function resetFontSize() {
    fontScale.value = DEFAULT_SCALE
  }

  return {
    fontScale,
    percentage,
    canDecrease,
    canIncrease,
    isDefault,
    increaseFontSize,
    decreaseFontSize,
    resetFontSize,
  }
}
