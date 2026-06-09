<template>
    <button
        class="speech-btn"
        :class="{ speaking: isSpeaking }"
        @click="toggle"
    >
        {{ isSpeaking ? translation.speechButton.stop : translation.speechButton.read }}
    </button>
</template>

<script setup lang="ts">
import {useSpeech} from "../composables/useSpeech"
import {useLanguage} from "../composables/LanguageButton"

const {speakPage, stop, isSpeaking} = useSpeech()
const {translation, language} = useLanguage()

const toggle = () => {
    if (isSpeaking.value) {
        stop()
        return
    }

    const lang = language.value === "en" ? "en-US" : "nl-NL"

    speakPage(lang)
}
</script>

<style>
.speech-btn {
    position: fixed;
    right: 22px;
    bottom: 22px;

    background: linear-gradient(135deg, #7c3aed, #2563eb);
    color: white;

    border: none;
    border-radius: 999px;

    padding: 12px 16px;

    font-weight: 600;
    font-size: 14px;

    cursor: pointer;

    display: flex;
    align-items: center;
    gap: 8px;

    z-index: 9999;

    /* ✨ softer modern shadow */
    box-shadow: 0 10px 25px rgba(37, 99, 235, 0.25);

    /* smoother transitions */
    transition: transform 0.2s ease, box-shadow 0.2s ease, filter 0.2s ease;

    /* slight blur glass feel */
    backdrop-filter: blur(6px);
}

/* hover = lift + glow */
.speech-btn:hover {
    transform: translateY(-3px) scale(1.02);
    box-shadow: 0 16px 35px rgba(124, 58, 237, 0.35);
    filter: brightness(1.05);
}

/* click feel */
.speech-btn:active {
    transform: translateY(0px) scale(0.98);
    box-shadow: 0 8px 18px rgba(37, 99, 235, 0.2);
}

/* subtle pulse when idle (nice UX touch) */
.speech-btn:not(:active) {
    animation: speechIdle 4s ease-in-out infinite;
}

@keyframes speechIdle {
    0%, 100% {
        box-shadow: 0 10px 25px rgba(37, 99, 235, 0.25);
    }
    50% {
        box-shadow: 0 12px 30px rgba(124, 58, 237, 0.35);
    }
}

.speech-btn.speaking {
    background: linear-gradient(135deg, #10b981, #2563eb);
    animation: pulse 1.2s infinite;
}

@keyframes pulse {
    0% {
        transform: scale(1);
    }
    50% {
        transform: scale(1.05);
    }
    100% {
        transform: scale(1);
    }
}
</style>
