import { ref, computed } from "vue"
import en from "../locales/en.json"
import nl from "../locales/nl.json"

const savedLanguage = localStorage.getItem("language") as "nl" | "en" | null
const language = ref<"nl" | "en">(savedLanguage ?? "nl")
const allText = { nl, en }
const translation = computed(() => allText[language.value])

function setLanguage(newLanguage: "nl" | "en") {
    language.value = newLanguage
    localStorage.setItem("language", newLanguage)

}
export function useLanguage() {
    return {
        language,
        setLanguage,
        translation
    }
}