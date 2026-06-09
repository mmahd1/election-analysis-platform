<template>
  <div>
    <NavBarComponent />
  </div>

  <div class="page">

    <div class="top-bar">
      <RouterLink to="/ForumPage" class="back-btn">
        {{ translation.postForum.backToForm }}
      </RouterLink>
    </div>

    <div v-if="successMessage" class="status-banner success">
      ✓ {{ successMessage }}
    </div>

    <div v-if="errorMessage" class="status-banner error">
      ⚠️ {{ errorMessage }}
    </div>

    <div class="form-card">

      <p class="label">
        {{ translation.postForum.createPost }}
      </p>

      <h1>
        {{ translation.postForum.newPost }}
      </h1>

      <p class="description">
        {{ translation.postForum.newPostDescription }}
      </p>

      <!--form for making a new post
            submitPost() is called when the user submits and page reload is prevented-->
      <form class="post-form" @submit.prevent="submitPost">

        <div v-if="successMessage" class="success-message">
          {{ translation.postForum.successMessage }}
        </div>

        <div class="form-group">
          <label>
            {{ translation.postForum.title }}
          </label>

          <input v-model="title" type="text" required />

        </div>

        <div class="form-group">
          <label>
            {{ translation.postForum.message }}
          </label>

          <textarea v-model="description" rows="8" required />

        </div>

        <button type="submit" class="submit-btn" :disabled="isSubmitting">
          {{ isSubmitting ? 'Plaatsen...' : translation.postForum.publish }}
        </button>

      </form>

    </div>
  </div>
</template>

<script setup lang="ts">
import NavBarComponent from "../components/NavBarComponent.vue";
import { useLanguage } from "../composables/LanguageButton";
import { ref } from 'vue'
import { createForumPost } from '../api/forumApi'
import router from "../router";

const { translation } = useLanguage() as any

const title = ref('')
const description = ref('')
const successMessage = ref('')
const errorMessage = ref('')
const isSubmitting = ref(false)

async function submitPost() {
  if (isSubmitting.value) return

  isSubmitting.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    await createForumPost({
      title: title.value,
      description: description.value
    })
    successMessage.value = translation.value.postForum.publishSuccess

    setTimeout(async () => {
      await router.push('/ForumPage')
    }, 2000)

  } catch (e: any) {
    console.error('Error creating post:', e)

    errorMessage.value =
        e?.message
            ? translation.postForum.publishError
            : translation.postForum.networkError
  } finally {
    isSubmitting.value = false
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
}

.top-bar {
  max-width: 900px;
  margin: 0 auto 20px;
}

.back-btn {
  display: inline-block;
  padding: 10px 16px;
  background: #2563eb;
  color: white;
  text-decoration: none;
  border-radius: 10px;
  font-weight: 600;
}

.status-banner {
  max-width: 900px;
  margin: 16px auto;
  padding: 16px;
  border-radius: 12px;
  font-weight: 600;
  text-align: center;
}

.status-banner.success {
  background: #dcfce7;
  color: #166534;
  border: 1px solid #86efac;
}

.status-banner.error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.form-card {
  max-width: 900px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  padding: 32px;
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

h1 {
  margin: 0;
  font-size: 2rem;
}

.description {
  margin-top: 12px;
  color: #475569;
  line-height: 1.6;
}

.post-form {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
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
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #cbd5e1;
  background: #f8fafc;
  font-size: 1rem;
  box-sizing: border-box;
  resize: vertical;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #2563eb;
}

.success-message {
  background: #dcfce7;
  color: #166534;
  border: 1px solid #86efac;
  padding: 12px 16px;
  border-radius: 12px;
  margin-bottom: 20px;
  font-weight: 600;
}

.submit-btn {
  width: fit-content;
  padding: 12px 20px;
  border: none;
  border-radius: 12px;
  background: #2563eb;
  color: white;
  font-weight: 600;
  cursor: pointer;
}

.submit-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .page {
    padding: 16px;
  }

  .form-card {
    padding: 20px;
    border-radius: 16px;
  }

  h1 {
    font-size: 1.6rem;
  }

  .submit-btn {
    width: 100%;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .form-card {
    padding: 16px;
  }

  .back-btn {
    width: 100%;
    text-align: center;
  }

  .form-group input,
  .form-group textarea {
    padding: 12px;
    font-size: 0.95rem;
  }
}
</style>