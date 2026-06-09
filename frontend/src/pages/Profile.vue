<template>
    <div>
        <NavBarComponent />

        <div class="profile-container">
            <h1>Profiel</h1>

            <p v-if="loading">
                Laden...
            </p>

            <p v-else-if="error" class="error">
                {{ error }}
            </p>

            <div v-else>
                <p v-if="fullName">
                    Naam: <strong>{{ fullName }}</strong>
                </p>

                <p>
                    Ingelogd als: <strong>{{ user?.email }}</strong>
                </p>

                <section class="my-posts-section">
                    <h2>Mijn forum posts</h2>

                    <p v-if="loadingPosts">Posts laden...</p>
                    <p v-else-if="postsError" class="error">{{ postsError }}</p>
                    <p v-else-if="myPosts.length === 0">Je hebt nog geen forum posts geplaatst.</p>

                    <div v-else class="post-list">
                        <article v-for="post in myPosts" :key="post.id" class="post-item">
                            <div class="post-item-header">
                                <h3>{{ post.title }}</h3>
                                <button
                                    class="delete-post-btn"
                                    :disabled="deletingPostId === post.id"
                                    @click="removePost(post.id)"
                                >
                                    {{ deletingPostId === post.id ? "Verwijderen..." : "Verwijderen" }}
                                </button>
                            </div>

                            <p class="post-description">{{ post.description }}</p>
                        </article>
                    </div>
                </section>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import NavBarComponent from "../components/NavBarComponent.vue"
import { computed, onMounted, ref } from "vue"
import { getMe } from "../api/authApi"
import { deleteForumPost, fetchMyForumPosts, type ForumPost } from "../api/forumApi"

const user = ref<{
    email: string
    firstName?: string
    lastName?: string
} | null>(null)
const loading = ref(true)
const error = ref("")
const loadingPosts = ref(false)
const postsError = ref("")
const myPosts = ref<ForumPost[]>([])
const deletingPostId = ref<number | null>(null)

const fullName = computed(() => {
    return [user.value?.firstName, user.value?.lastName]
        .filter(Boolean)
        .join(" ")
})

onMounted(async () => {
    try {
        user.value = await getMe()
        await loadMyPosts()
    } catch (err) {
        error.value = "Kon gebruiker niet ophalen"
    } finally {
        loading.value = false
    }
})

async function loadMyPosts() {
    loadingPosts.value = true
    postsError.value = ""

    try {
        myPosts.value = await fetchMyForumPosts()
    } catch {
        postsError.value = "Kon je forum posts niet ophalen"
    } finally {
        loadingPosts.value = false
    }
}

async function removePost(postId: number) {
    const confirmed = window.confirm("Weet je zeker dat je deze post wilt verwijderen?")
    if (!confirmed) {
        return
    }

    deletingPostId.value = postId

    try {
        await deleteForumPost(postId)
        myPosts.value = myPosts.value.filter(post => post.id !== postId)
    } catch {
        window.alert("Verwijderen is mislukt. Probeer het opnieuw.")
    } finally {
        deletingPostId.value = null
    }
}
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 24px;
  background: rgba(255, 255, 255, 0.92);
  border-radius: 16px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  box-sizing: border-box;
}

h1 {
  margin: 0 0 16px;
  font-size: clamp(1.8rem, 4vw, 2.6rem);
  color: #0f172a;
}

.profile-container h1 {
    font-size: 28px;
    margin-bottom: 20px;
    color: #222;
    letter-spacing: -0.5px;
}

.profile-container p {
    font-size: 16px;
    line-height: 1.6;
    color: #444;
    margin: 10px 0;
}

.my-posts-section {
    margin-top: 28px;
    border-top: 1px solid rgba(148, 163, 184, 0.35);
    padding-top: 20px;
}

.my-posts-section h2 {
    margin: 0 0 12px;
    color: #0f172a;
}

.post-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.post-item {
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 14px;
    background: #f8fafc;
}

.post-item-header {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    align-items: center;
}

.post-item-header h3 {
    margin: 0;
    color: #0f172a;
}

.post-description {
    margin: 8px 0 0;
}

.delete-post-btn {
    border: none;
    background: #dc2626;
    color: #fff;
    padding: 8px 12px;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
}

.delete-post-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
}

/* Loading state */
.profile-container p:first-of-type {
    color: #666;
    font-style: italic;
}

/* Error styling */
.error {
  color: #b91c1c;
  background: #fee2e2;
  padding: 10px 14px;
  border-radius: 10px;
  display: inline-block;
}

@media (max-width: 640px) {
  .profile-container {
    margin: 20px 16px;
    padding: 20px;
  }
}
</style>
