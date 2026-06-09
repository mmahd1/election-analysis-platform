<template>
    <div
        ref="dropdownRef"
        class="language-dropdown"
        @focusout="onFocusOut"
    >
        <button
            ref="buttonRef"
            class="language-btn"
            type="button"
            aria-haspopup="menu"
            :aria-expanded="isOpen"
            aria-controls="language-menu"
            @click="toggle"
            @keydown.esc.stop.prevent="close(true)"
            @keydown.down.prevent="open(true)"
        >
            {{ language.toUpperCase() }} ▾
        </button>

        <div
            v-show="isOpen"
            id="language-menu"
            class="language-menu"
            role="menu"
            @keydown.esc.stop.prevent="close(true)"
        >
            <button class="language-option" type="button" role="menuitem" @click="select('nl')" :class="{ active: language === 'nl' }">
                <span class="flag">🇳🇱</span>
                <span>Nederlands</span>
            </button>

            <button class="language-option" type="button" role="menuitem" @click="select('en')" :class="{ active: language === 'en' }">
                <span class="flag">🇬🇧</span>
                <span>English</span>
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">

import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import {useLanguage} from "../composables/LanguageButton"

const {language, setLanguage} = useLanguage()

const isOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)
const buttonRef = ref<HTMLButtonElement | null>(null)

function focusFirstItem() {
    const first = dropdownRef.value?.querySelector<HTMLElement>('.language-menu button')
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

function select(lang: 'nl' | 'en') {
    setLanguage(lang)
    close(true)
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
.language-dropdown {
    position: relative;
    display: inline-flex;
    align-items: center;
}

/* BUTTON (match speech button style) */
.language-btn {
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
}

.language-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px rgba(124, 58, 237, 0.25);
}

/* HOVER BRIDGE (fix flicker) */
.language-dropdown::after {
    content: "";
    position: absolute;
    left: 0;
    right: 0;
    top: 100%;
    height: 10px;
}

/* MENU */
.language-menu {
    position: absolute;
    top: calc(100% + 4px);
    right: 0;

    background: white;

    border-radius: 12px;

    box-shadow: 0 14px 35px rgba(0, 0, 0, 0.18);

    transition: transform 0.2s ease;

    min-width: 120px;

    z-index: 999;
}

.language-menu {
    transform: translateY(0);
}

.language-option {
    width: 100%;
    padding: 10px 12px;

    background: none;
    border: none;

    cursor: pointer;

    font-weight: 600;
    font-size: 14px;
    color: #1f2937;

    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;

    gap: 10px;

    line-height: 1;
    white-space: nowrap;
}

.language-option .flag {
    display: inline-flex;
    align-items: center;
    justify-content: center;

    width: 22px;
    height: 22px;

    font-size: 18px;
    line-height: 1;
}

/* hover */
.language-option:hover {
    background: #f3f4f6;
}

/* active */
.language-option.active {
    background: #2563eb;
    color: white;
}

.language-option.active .flag {
    filter: brightness(1.2);
}

/* checkmark */
.language-option.active::after {
    content: "✓";
    margin-left: auto;
    font-weight: 900;
}
@media (max-width: 768px) {
  .language-btn {
    width: 100%;
    justify-content: center;
    font-size: 13px;
    padding: 8px 12px;
  }

  .language-menu {
    right: auto;
    left: 0;
    min-width: 100%;
  }

  .language-option {
    font-size: 13px;
    padding: 12px;
  }

  .language-option .flag {
    font-size: 16px;
    width: 20px;
    height: 20px;
  }
}

@media (max-width: 480px) {
  .language-dropdown {
    width: 100%;
  }

  .language-btn {
    width: 100%;
  }

  .language-menu {
    width: 100%;
    min-width: unset;
  }
}
</style>