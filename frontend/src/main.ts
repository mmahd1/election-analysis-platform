import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'

import { useMunicipalityCache } from './composables/UseMunicipalityCache'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

async function preloadMunicipalities() {
    const cache = useMunicipalityCache()

    await Promise.all([
        cache.fetchYear('TK2023', API_BASE_URL),
        cache.fetchYear('TK2025', API_BASE_URL)
    ])
}

createApp(App).use(router).mount('#app')

// fire & forget preload (UI blokkeert niet)
preloadMunicipalities()