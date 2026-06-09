<template>
  <div>
    <NavBarComponent />
  </div>

  <div class="forum-page">

    <div class="top-bar">
      <RouterLink to="/" class="back-btn">
        ← {{ translation.forumPage.back }}
      </RouterLink>
    </div>

    <div v-if="isLoading" class="status-banner loading">
       {{ translation.forumPage.loadingPosts}}
    </div>

    <div v-if="errorMessage" class="status-banner error">
      ⚠️ {{ errorMessage }}
    </div>

    <div class="forum-header">
      <div>
        <h1>{{ translation.forumPage.title }}</h1>

        <p class="forum-description">
          {{ translation.forumPage.description }}
        </p>
      </div>

      <div class="forum-actions">

        <RouterLink
          v-if="isLoggedIn"
          to="/createPost"
          class="create-btn"
        >
          {{ translation.forumPage.createPost }}
        </RouterLink>

        <button
          v-else
          @click="showLoginMessage"
          class="create-btn create-btn"
          :title="translation.forumPage.loginRequired"
        >
          {{ translation.forumPage.createPost }}
        </button>

        <RouterLink v-if="isAdmin" to="/forum-admin" class="admin-btn">
          {{ translation.forumPage.admin }}
        </RouterLink>

      </div>

      <div v-if="showMessage" class="login-message">
        <div class="message-content">
          <h3>{{ translation.forumPage.loginRequired }}</h3>
          <p>{{ translation.forumPage.loginMessage }}</p>
          <button @click="closeMessage" class="close-btn">✕</button>
          <RouterLink to="/logIn" class="login-link">
            {{ translation.forumPage.goToLogin }}
          </RouterLink>
        </div>
      </div>
    </div>
    <main class="posts-section">

      <div class="search-bar">
        <input
            v-model="shearch"
            type="text"
            :placeholder="translation.forumPage.searchPlaceholder"
        />
      </div>

      <div class="posts-list">

        <button
            v-if="hasMorePosts"
            @click="loadPosts"
            class="load-more-btn"
        >
          Meer laden
        </button>

        <p v-if="isLoading">
          Posts laden...
        </p>

        <div
            v-for="post in filteredPosts"
            :key="post.id"
            class="post-card"
        >
          <h2>{{ post.title }}</h2>

          <div class="post-meta">
            <span>{{ post.author }}</span>
            <span>•</span>
            <span>{{ timeAgo(post.createdAt) }}</span>
          </div>

          <p class="post-content">
            {{ post.description }}
          </p>

          <RouterLink
              :to="`/forumPost/${post.id}`"
              class="view-btn"
          >
            {{ translation.forumPage.viewPost }}
          </RouterLink>
        </div>

      </div>

    </main>
  </div>
</template>

<script setup lang="ts">
import NavBarComponent from '../components/NavBarComponent.vue'
import { useLanguage } from "../composables/LanguageButton"
import { computed, onMounted, ref } from 'vue'
import { fetchForumPosts, type ForumPost } from '../api/forumApi'
import { getMe } from '../api/authApi'

const ADMIN_EMAIL = 'Marouan@admin.nl'

const posts = ref<ForumPost[]>([])
const page = ref(0)
const size = 10
const isLoading = ref(false)
const hasMorePosts = ref(true)
const isAdmin = ref(false)
const isLoggedIn = ref(false)
const showMessage = ref(false)
const errorMessage = ref('')

const { translation } = useLanguage() as any
const shearch = ref('')
const filteredPosts = computed(() => {
  const query = shearch.value.trim().toLowerCase()

  if (!query) {
    return posts.value
  }

  return posts.value.filter((post) => {
    const shearchText = `${post.title} ${post.author} ${post.description}`.toLowerCase()
    return shearchText.includes(query)
  })
})

onMounted(async () => {
  try {
    const token = localStorage.getItem('token')
    isLoggedIn.value = !!token

    if (token) {
      const user = await getMe()
      isAdmin.value = user.email.toLowerCase() === ADMIN_EMAIL.toLowerCase()
    }
  } catch {
    isAdmin.value = false
  }

  await loadPosts()
})

async function loadPosts() {
  if (isLoading.value || !hasMorePosts.value) {
    return
  }

  isLoading.value = true

  const newPosts = await fetchForumPosts(page.value, size)

  posts.value.push(...newPosts)

  if (newPosts.length < size) {
    hasMorePosts.value = false
  }

  page.value++
  isLoading.value = false
}

function timeAgo(dateString: string) {
  const seconds = Math.floor(
      (Date.now() - new Date(dateString).getTime()) / 1000
  )

  if (seconds < 60) {
    return translation.value.forumPage.timeAgoNow
  }

  const minutes = Math.floor(seconds / 60)

  if (minutes < 60) {
    return translation.value.forumPage.timeAgoMinutes
        .replace('{count}', String(minutes))
  }

  const hours = Math.floor(minutes / 60)

  if (hours < 24) {
    return translation.value.forumPage.timeAgoHours
        .replace('{count}', String(hours))
  }

  const days = Math.floor(hours / 24)

  return translation.value.forumPage.timeAgoDays
      .replace('{count}', String(days))
}

function showLoginMessage() {
  showMessage.value = true
}

function closeMessage() {
  showMessage.value = false
}
</script>

<style scoped>
.forum-page {
  font-family: Arial, sans-serif;
  min-height: 100vh;
  padding: 32px;
  background:
      radial-gradient(circle at top left, rgba(37, 99, 235, 0.14), transparent 24%),
      linear-gradient(180deg, #f8fbff 0%, #eef3f8 100%);
  color: #0f172a;
  box-sizing: border-box;
}

.top-bar {
  max-width: 1000px;
  margin: 0 auto 20px;
}

.back-btn {
  display: inline-block;
  padding: 10px 16px;
  background: #2563eb;
  color: white;
  border-radius: 10px;
  text-decoration: none;
  font-weight: bold;
}

.status-banner {
  max-width: 1000px;
  margin: 16px auto;
  padding: 16px;
  border-radius: 12px;
  font-weight: 600;
  text-align: center;
}

.status-banner.loading {
  background: #dbeafe;
  color: #1e40af;
  border: 1px solid #bfdbfe;
}

.status-banner.error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.forum-header {
  max-width: 1000px;
  margin: 0 auto 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.forum-header h1 {
  margin: 0;
}

.forum-description {
  color: #475569;
}

.forum-actions {
  display: flex;
  gap: 10px;
}

.create-btn,
.admin-btn,
.view-btn {
  border: none;
  cursor: pointer;
  padding: 10px 16px;
  border-radius: 10px;
  font-weight: bold;
  background: #2563eb;
  color: white;
  text-decoration: none;
  display: inline-block;
  transition: opacity 0.2s ease;
}

.create-btn:hover,
.admin-btn:hover,
.view-btn:hover {
  opacity: 0.9;
}

.login-message {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.message-content {
  background: white;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  position: relative;
}

.message-content h3 {
  margin-top: 0;
  color: #1e293b;
}

.message-content p {
  color: #475569;
  margin: 16px 0;
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #64748b;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #1e293b;
}

.login-link {
  display: inline-block;
  margin-top: 16px;
  padding: 10px 20px;
  background: #2563eb;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  font-weight: bold;
}

.posts-section {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-bar {
  background: white;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.search-bar input {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  box-sizing: border-box;
}

.posts-list {
  background: #E9E8E8;
  border: 1px solid #dbe2ea;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.post-card h2 {
  margin-top: 0;
}

.post-meta {
  display: flex;
  gap: 8px;
  color: #64748b;
  font-size: 0.9rem;
  margin-bottom: 16px;
}

.post-content {
  line-height: 1.6;
  margin-bottom: 16px;
}

/* =========================
   RESPONSIVE
   ========================= */

@media (max-width: 900px) {
  .forum-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .forum-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .create-btn,
  .admin-btn {
    flex: 1;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .forum-page {
    padding: 20px;
  }

  .posts-list {
    padding: 16px;
  }

  .post-card {
    padding: 18px;
  }
}

@media (max-width: 480px) {
  .forum-page {
    padding: 16px;
  }

  .back-btn {
    width: 100%;
    text-align: center;
  }

  .forum-actions {
    flex-direction: column;
  }

  .create-btn,
  .admin-btn,
  .view-btn {
    width: 100%;
    text-align: center;
  }

  .search-bar input {
    padding: 10px;
  }

  .post-card {
    padding: 14px;
  }
}
</style>
