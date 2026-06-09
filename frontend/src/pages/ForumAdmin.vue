<template>
  <div>
    <NavBarComponent />
  </div>

  <div class="page">
    <div class="top-bar">
      <RouterLink to="/ForumPage" class="back-btn">
        {{ translation.forumAdmin.back }}
      </RouterLink>
    </div>

    <main class="admin-layout">
      <section class="form-card list-panel">
        <p class="label">{{ translation.forumAdmin.label }}</p>
        <h1>{{ translation.forumAdmin.title }}</h1>
        <p class="panel-description">
          {{ translation.forumAdmin.description }}
        </p>

        <p v-if="isLoading" class="status-message">
          {{ translation.forumAdmin.loading }}
        </p>

        <p v-else-if="errorMessage" class="status-message error">
          {{ errorMessage }}
        </p>

        <div v-else class="post-list">
          <button
            v-for="post in posts"
            :key="post.id"
            class="post-item"
            :class="{ active: selectedPost?.id === post.id }"
            @click="selectPost(post)"
          >
            <strong>{{ post.title }}</strong>
            <span>{{ post.author }}</span>
          </button>
        </div>
      </section>

      <section class="editor-panel">
        <div v-if="selectedPost" class="form-card editor-card">
          <p class="label">{{ translation.forumAdmin.label }}</p>
          <h2>{{ translation.forumAdmin.editTitle }}</h2>

          <form class="edit-form" @submit.prevent="savePost">
            <div class="form-group">
              <label for="title">{{ translation.postForum.title }}</label>
              <input id="title" v-model="form.title" type="text" />
            </div>

            <div class="form-group">
              <label for="author">{{ translation.postForum.author }}</label>
              <input id="author" v-model="form.author" type="text" disabled />
            </div>

            <div class="form-group">
              <label for="description">{{ translation.postForum.message }}</label>
              <textarea id="description" v-model="form.description" rows="10" />
            </div>

            <p v-if="validationMessage" class="status-message error">
              {{ validationMessage }}
            </p>

            <p v-if="saveMessage" class="status-message success">
              {{ saveMessage }}
            </p>

            <button type="submit" class="save-btn" :disabled="isSaving">
              {{ isSaving ? translation.forumAdmin.saving : translation.forumAdmin.save }}
            </button>
          </form>
        </div>

        <div v-else class="form-card empty-card">
          {{ translation.forumAdmin.selectPrompt }}
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import NavBarComponent from '../components/NavBarComponent.vue'
import { fetchForumPosts, updateForumPost, type ForumPost } from '../api/forumApi'
import { useLanguage } from '../composables/LanguageButton'

const { translation } = useLanguage()

const posts = ref<ForumPost[]>([])
const selectedPost = ref<ForumPost | null>(null)
const isLoading = ref(true)
const isSaving = ref(false)
const errorMessage = ref('')
const validationMessage = ref('')
const saveMessage = ref('')

const form = ref({
  title: '',
  author: '',
  description: ''
})

onMounted(async () => {
  await loadPosts()
})

async function loadPosts() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    posts.value = await fetchForumPosts(0, 50)

    if (posts.value.length > 0) {
      selectPost(posts.value[0])
    }
  } catch (error) {
    console.error('Kon forum posts niet laden', error)
    errorMessage.value = translation.value.forumAdmin.loadError
  } finally {
    isLoading.value = false
  }
}

function selectPost(post: ForumPost) {
  selectedPost.value = post
  form.value = {
    title: post.title,
    author: post.author,
    description: post.description
  }
  validationMessage.value = ''
  saveMessage.value = ''
}

async function savePost() {
  if (!selectedPost.value) {
    return
  }

  if (!form.value.title.trim() || !form.value.description.trim()) {
    validationMessage.value = translation.value.forumAdmin.validationError
    saveMessage.value = ''
    return
  }

  isSaving.value = true
  validationMessage.value = ''
  saveMessage.value = ''

  try {
    const updatedPost = await updateForumPost(selectedPost.value.id, {
      title: form.value.title.trim(),
      author: form.value.author,
      description: form.value.description.trim()
    })

    const index = posts.value.findIndex(post => post.id === updatedPost.id)
    if (index >= 0) {
      posts.value[index] = updatedPost
    }

    selectPost(updatedPost)
    saveMessage.value = translation.value.forumAdmin.saveSuccess
  } catch (error) {
    console.error('Kon forum post niet opslaan', error)
    validationMessage.value = translation.value.forumAdmin.saveError
  } finally {
    isSaving.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 32px;
  background:
      radial-gradient(circle at top left, rgba(37, 99, 235, 0.14), transparent 24%),
      linear-gradient(180deg, #f8fbff 0%, #eef3f8 100%);
  font-family: Arial, sans-serif;
  color: #0f172a;
  box-sizing: border-box;
}

.top-bar {
  max-width: 1180px;
  margin: 0 auto 20px;
}

.back-btn,
.save-btn {
  display: inline-block;
  padding: 10px 16px;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 10px;
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
}

.save-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.admin-layout {
  max-width: 1180px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(320px, 380px) minmax(0, 1fr);
  gap: 28px;
}

.form-card {
  background: white;
  border-radius: 20px;
  padding: 36px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
}

.label {
  margin: 0 0 10px;
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #64748b;
}

.list-panel h1,
.editor-card h2 {
  margin: 0;
}

.list-panel h1 {
  font-size: 2rem;
}

.editor-card h2 {
  font-size: 1.75rem;
}

.panel-description {
  margin-top: 12px;
  color: #475569;
  line-height: 1.6;
  max-width: 56ch;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 28px;
}

.post-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%;
  padding: 18px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 12px;
  text-align: left;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.04);
}

.post-item.active {
  border-color: #2563eb;
  background: #eff6ff;
}

.post-item span {
  color: #64748b;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #cbd5e1;
  background: #f8fafc;
  box-sizing: border-box;
  font: inherit;
}

.form-group textarea {
  min-height: 220px;
  resize: vertical;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #2563eb;
}

.status-message {
  margin: 0;
  padding: 14px 16px;
  border-radius: 12px;
}

.status-message.error {
  background: #fee2e2;
  color: #991b1b;
}

.status-message.success {
  background: #dcfce7;
  color: #166534;
}

.empty-card {
  color: #64748b;
}

@media (max-width: 900px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page {
    padding: 16px;
  }

  .form-card {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .form-card {
    padding: 16px;
    border-radius: 16px;
  }

  .post-item {
    padding: 14px;
  }

  .back-btn,
  .save-btn {
    width: 100%;
    text-align: center;
  }

  .editor-card h2 {
    font-size: 1.4rem;
  }

  .list-panel h1 {
    font-size: 1.6rem;
  }

  .form-group input,
  .form-group textarea {
    padding: 12px;
  }
}
</style>
