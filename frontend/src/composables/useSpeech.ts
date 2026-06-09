import {ref} from "vue"

const isSpeaking = ref(false)

export function useSpeech() {

    const speak = (text: string, lang: string = "nl-NL") => {
        if (!text) return

        window.speechSynthesis.cancel()

        const utterance = new SpeechSynthesisUtterance(text)

        utterance.lang = lang
        utterance.rate = 1
        utterance.pitch = 1

        utterance.onstart = () => {
            isSpeaking.value = true
        }

        utterance.onend = () => {
            isSpeaking.value = false
        }

        window.speechSynthesis.speak(utterance)
    }

    const stop = () => {
        window.speechSynthesis.cancel()
        isSpeaking.value = false
    }

    const speakPage = (lang: string = "nl-NL") => {

        const elements = document.querySelectorAll("h1, h2, h3, p, .description")

        let text = ""

        elements.forEach(el => {
            const value = el.textContent?.trim()
            if (value) text += value + ". "
        })

        speak(text, lang)
    }

    return {
        speak,
        stop,
        speakPage,
        isSpeaking
    }
}