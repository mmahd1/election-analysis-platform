<template>
    <div
        ref="dropdownRef"
        class="font-size-dropdown"
        @focusout="onFocusOut"
    >
        <button
            ref="buttonRef"
            class="font-size-btn"
            type="button"
            aria-haspopup="menu"
            :aria-expanded="isOpen"
            aria-controls="font-size-menu"
            @click="toggle"
            @keydown.esc.stop.prevent="close(true)"
            @keydown.down.prevent="open(true)"
        >
            {{ translation.accessibility.fontSize }} {{ percentage }}%
        </button>

        <div
            v-show="isOpen"
            id="font-size-menu"
            class="font-size-menu"
            role="menu"
            @keydown.esc.stop.prevent="close(true)"
        >
            <button
                class="font-size-option"
                type="button"
                role="menuitem"
                :disabled="!canDecrease"
                @click="decreaseFontSize(); close(false)"
            >
                <span class="font-symbol small-a">A-</span>
                <span>{{ translation.accessibility.decreaseFont }}</span>
            </button>

            <button
                class="font-size-option"
                type="button"
                role="menuitem"
                :disabled="!canIncrease"
                @click="increaseFontSize(); close(false)"
            >
                <span class="font-symbol large-a">A+</span>
                <span>{{ translation.accessibility.increaseFont }}</span>
            </button>

            <button
                class="font-size-option"
                type="button"
                role="menuitem"
                :disabled="isDefault"
                @click="resetFontSize(); close(false)"
            >
                <span class="font-symbol">R</span>
                <span>{{ translation.accessibility.resetFont }}</span>
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useFontSize } from '../composables/FontSize'
import { useLanguage } from '../composables/LanguageButton'

const {
    percentage,
    canDecrease,
    canIncrease,
    isDefault,
    decreaseFontSize,
    increaseFontSize,
    resetFontSize,
} = useFontSize()

const { translation } = useLanguage()

const isOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)
const buttonRef = ref<HTMLButtonElement | null>(null)

function focusFirstItem() {
    const first = dropdownRef.value?.querySelector<HTMLElement>('.font-size-menu button')
    first?.focus()
}

function open(focusFirst = false) {
    isOpen.value = true
    if (focusFirst) {
        nextTick(() => focusFirstItem())
    }
}

function close(returnFocus = false) {
    isOpen.value = false
    if (returnFocus) {
        nextTick(() => buttonRef.value?.focus())
    }
}

function toggle() {
    if (isOpen.value) {
        close(false)
        return
    }
    open(false)
}

function onFocusOut(event: FocusEvent) {
    const next = event.relatedTarget as Node | null
    if (!next) return
    if (!dropdownRef.value?.contains(next)) {
        close(false)
    }
}

function onDocumentClick(event: MouseEvent) {
    const target = event.target as Node | null
    if (!target) return
    if (!dropdownRef.value?.contains(target)) {
        close(false)
    }
}

onMounted(() => {
    document.addEventListener('click', onDocumentClick, true)
})

onBeforeUnmount(() => {
    document.removeEventListener('click', onDocumentClick, true)
})
</script>

<style scoped>
.font-size-dropdown {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.font-size-btn {
  background: linear-gradient(135deg, #7c3aed, #2563eb);
  border: none;
  border-radius: 999px;
  padding: 8px 14px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s ease;
  box-shadow: 0 6px 18px rgba(37, 99, 235, 0.25);
  white-space: nowrap;
}

.font-size-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(124, 58, 237, 0.25);
}

.font-size-dropdown::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  top: 100%;
  height: 10px;
}

.font-size-menu {
  position: absolute;
  top: calc(100% + 4px);
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 14px 35px rgba(0, 0, 0, 0.18);
  opacity: 0;
  transform: translateY(8px);
  pointer-events: none;
  transition: all 0.2s ease;
  min-width: 190px;
  z-index: 999;
}

.font-size-dropdown:hover .font-size-menu,
.font-size-dropdown:focus-within .font-size-menu {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

.font-size-option {
  width: 100%;
  padding: 10px 12px;
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  color: #1f2937;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
  line-height: 1;
  white-space: nowrap;
}

.font-size-option:hover:not(:disabled) {
  background: #f3f4f6;
}

.font-size-option:disabled {
  cursor: not-allowed;
  opacity: 0.45;
}

.font-symbol {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  min-width: 28px;
  color: #2563eb;
  font-weight: 900;
}

.small-a {
  font-size: 13px;
}

.large-a {
  font-size: 18px;
}

@media (max-width: 768px) {
  .font-size-dropdown {
    width: 100%;
  }

  .font-size-btn {
    width: 100%;
    justify-content: center;
  }

  .font-size-menu {
    right: auto;
    left: 0;
    width: 100%;
    min-width: 100%;
  }
}

@media (max-width: 480px) {
  .font-size-btn {
    font-size: 13px;
    padding: 8px 12px;
  }

  .font-size-option {
    font-size: 13px;
    padding: 10px;
  }

  .font-symbol {
    width: 24px;
    min-width: 24px;
  }
}
</style>
