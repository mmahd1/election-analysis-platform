<template>
  <div class="header-bar">
    <div class="logo">SW</div>

    <div class="nav">

      <RouterLink to="/" class="nav-btn link-btn">
        Home
      </RouterLink>

      <div class="dropdown">
        <button class="nav-btn dropdown-btn">
          {{ translation.navbar.results }} ▾
        </button>

        <div class="dropdown-content">
          <RouterLink to="/map-results" class="dropdown-link">Kaart</RouterLink>

          <RouterLink to="/national-results" class="dropdown-link">
            {{ translation.navbar.national }}
          </RouterLink>

          <RouterLink to="/constituencies" class="dropdown-link">
            {{ translation.navbar.constituency }}
          </RouterLink>

          <RouterLink to="/municipalities" class="dropdown-link">
            {{ translation.navbar.municipalities }}
          </RouterLink>
        </div>
      </div>

      <RouterLink to="/parties" class="nav-btn link-btn">
        {{ translation.navbar.parties }}
      </RouterLink>

      <RouterLink
          v-if="!isLoggedIn"
          to="/logIn"
          class="nav-btn link-btn login-link"
      >
        <User class="icon"/>
        Inloggen
      </RouterLink>

      <RouterLink
          v-if="!isLoggedIn"
          to="/register"
          class="nav-btn link-btn login-link"
      >
        Registreren
      </RouterLink>

      <RouterLink
          v-else
          to="/profile"
          class="nav-btn link-btn login-link"
      >
        <User class="icon"/>
        Profiel
      </RouterLink>

      <button
          v-if="isLoggedIn"
          class="nav-btn"
          @click="logout"
      >
        Uitloggen
      </button>

    </div>

    <div class="accessibility">
      <SpeechButton/>
      <FontSizeButton/>
      <LanguageButton/>
    </div>

  </div>
</template>

<script setup lang="ts">

import { useLanguage } from "../composables/LanguageButton"
import LanguageButton from "./LanguageButton.vue"
import FontSizeButton from "./FontSizeButton.vue"
import SpeechButton from "./SpeechButton.vue"
import { User } from "lucide-vue-next"

import { computed } from "vue"
import { useRouter } from "vue-router"

const { translation } = useLanguage()
const router = useRouter()

const token = localStorage.getItem("token")

const isLoggedIn = computed(() => {
  return !!token
})

function logout() {
  localStorage.removeItem("token")
  router.push("/logIn")
}

</script>

<style>
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;

  width: 100%;
  padding: 12px 24px;

  background: linear-gradient(to right, #e5e7eb, #f3f4f6);
  border-bottom: 1px solid #d1d5db;

  flex-wrap: wrap;
  gap: 12px;
}

.logo {
  font-weight: 800;
  font-size: 28px;
  color: rebeccapurple;
  flex-shrink: 0;
}

.nav {
  display: flex;
  gap: 24px;
  align-items: center;
  flex-wrap: wrap;
}

.accessibility {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.nav-btn {
  background: #2563eb;
  color: #fff;

  border: none;
  padding: 7px 14px;
  border-radius: 10px;

  cursor: pointer;

  font-weight: 600;
  font-size: 14px;

  transition: 0.2s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);

  white-space: nowrap;
}

.nav-btn:hover {
  background: #1d4ed8;
  transform: translateY(-1px);
}

.link-btn {
  display: inline-flex;
  align-items: center;
  text-decoration: none;
}

.login-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.icon {
  width: 18px;
  height: 18px;
}

.dropdown {
  position: relative;
}

.dropdown-content {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;

  background: white;
  min-width: 200px;

  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);

  opacity: 0;
  transform: translateY(8px);
  pointer-events: none;

  transition: 0.2s ease;
  z-index: 999;
}

.dropdown:hover .dropdown-content,
.dropdown:focus-within .dropdown-content {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

.dropdown-link {
  display: block;
  padding: 12px 16px;
  color: #1f2937;
  text-decoration: none;
}

.dropdown-link:hover {
  background: #f3f4f6;
}

@media (max-width: 768px) {

  .header-bar {
    padding: 8px 12px;
    gap: 8px;
  }

  .logo {
    font-size: 20px;
  }

  .nav {
    gap: 8px;
    justify-content: center;
  }

  .nav-btn {
    font-size: 12px;
    padding: 5px 10px;
    border-radius: 8px;
  }

  .icon {
    width: 14px;
    height: 14px;
  }

  .accessibility {
    gap: 4px;
  }

  .accessibility button,
  .accessibility select {
    font-size: 11px !important;
    padding: 3px 6px !important;
    min-height: 28px !important;
    border-radius: 6px;
  }

  .accessibility :deep(button),
  .accessibility :deep {
    font-size: 11px !important;
    padding: 3px 6px !important;
    min-height: 28px !important;
  }

  .accessibility svg {
    width: 14px;
    height: 14px;
  }

  .dropdown-content {
    min-width: 160px;
  }

  .dropdown-link {
    padding: 8px 10px;
    font-size: 12px;
  }
}
</style>