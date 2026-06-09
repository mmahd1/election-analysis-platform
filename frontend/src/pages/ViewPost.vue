<template>
    <div>
        <NavBarComponent/>
    </div>

    <div class="post-page">

        <RouterLink to="/ForumPage" class="back-btn">
            ← {{ translation.postForum.backToForm }}
        </RouterLink>

      <div v-if="isLoadingPost" class="status-banner loading">
         {{ translation.postForum.loadingPost }}
      </div>

      <div v-if="errorMessage" class="status-banner error">
         {{ translation.postForum.loadPostError }}
      </div>

        <div v-if="!isLoadingPost && !errorMessage && post" class="post-content">
        <div class="post-layout">
            <div class="post-section">
                <div class="post-card">
                    <h1>{{ post.title }}</h1>

                    <div class="post-meta">
                        <span>{{ post.author }}</span>
                        <span>•</span>
                        <span>{{ timeAgo(post.createdAt) }}</span>
                    </div>

                    <p class="post-description">
                        {{ post.description }}
                    </p>

                    <button
                        v-if="canDeletePost()"
                        class="delete-post-btn"
                        :disabled="deletingPost"
                        @click="removePost"
                    >
                        {{ deletingPost ? "Verwijderen..." : "Post verwijderen" }}
                    </button>
                </div>
            </div>

            <div class="comments-section">
                <div class="comments-card">
                    <h2>{{ translation.postForum.comments }}</h2>

                    <div v-if="isLoggedIn" class="comment-form">
                        <input
                            v-model="meEmail"
                            type="text"
                            class="comment-input"
                            disabled
                        />

                        <textarea
                            v-model="commentDescription"
                            :placeholder="translation.postForum.commentPlaceholder"
                            class="comment-textarea"
                            required
                        ></textarea>

                        <button class="comment-btn" @click="submitComment">
                            {{ translation.postForum.postComment }}
                        </button>
                    </div>

                    <p v-else class="login-to-comment">
                        {{ translation.postForum.loginToComment }}
                    </p>

                    <div class="comments-list">

                        <p v-if="loadingComments" class="no-comments">
                            {{ translation.postForum.loadingComments }}
                        </p>

                        <p v-else-if="comments.length === 0" class="no-comments">
                            {{ translation.postForum.noComments }}
                        </p>

                        <div
                            v-for="comment in comments"
                            :key="comment.id"
                            class="comment-item"
                        >
                            <div class="comment-header">
                                <strong>{{ comment.author }}</strong>
                                <span>{{ timeAgo(comment.createdAt) }}</span>

                                <button
                                    v-if="canDeleteComment(comment.author)"
                                    class="delete-btn"
                                    @click="removeComment(comment.id)"
                                >
                                    ✕
                                </button>

                            </div>

                            <p class="comment-content">
                                {{ comment.description }}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import NavBarComponent from '../components/NavBarComponent.vue'
import {useRoute} from 'vue-router'
import {onMounted, ref} from 'vue'
import {
    createComment,
    deleteComment,
    deleteForumPost,
    fetchComments,
    fetchForumPosts,
    type ForumComment,
    type ForumPost
} from '../api/forumApi'
import { getMe } from '../api/authApi'
import {useLanguage} from '../composables/LanguageButton'
import router from '../router'

const route = useRoute()
const {translation} = useLanguage()

const post = ref<ForumPost | null>(null)
const comments = ref<ForumComment[]>([])
const loadingComments = ref(true)
const isLoadingPost = ref(true)
const errorMessage = ref('')


const commentDescription = ref('')

const isLoggedIn = ref(false)
const meEmail = ref('')
const isAdmin = ref(false)
const deletingPost = ref(false)

const ADMIN_EMAIL = 'Marouan@admin.nl'

const postId = Number(route.params.id)

onMounted(async () => {
    try {
        const token = localStorage.getItem('token')
        if (token) {
            const me = await getMe()
            isLoggedIn.value = true
            meEmail.value = me.email
            isAdmin.value = me.email.toLowerCase() === ADMIN_EMAIL.toLowerCase()
        }
    } catch {
        isLoggedIn.value = false
        meEmail.value = ''
        isAdmin.value = false
    }

    try {
        const posts = await fetchForumPosts()
        post.value = posts.find(
            (p: ForumPost) => p.id === postId
        ) || null

        try {
            comments.value = await fetchComments(postId)
        } catch (e: any) {
            console.error('Kon comments niet laden', e)
            comments.value = []
        }
    } catch (e: any) {
        console.error('Kon post niet laden', e)
        errorMessage.value = 'Post laden is mislukt.'
    } finally {
        loadingComments.value = false
        isLoadingPost.value = false
    }
})

async function submitComment() {
    if (!isLoggedIn.value) {
        return
    }

    if (!commentDescription.value.trim()) {
        return
    }

    try {
        await createComment(postId, {
            description: commentDescription.value
        })
    } catch (e) {

      window.alert(translation.value.postForum.inLoginToCommentError)
        return
    }

    comments.value = await fetchComments(postId)

    commentDescription.value = ''
}


// E
async function removeComment(commentId: number) {
    if (!isLoggedIn.value) {
      window.alert(translation.value.postForum.loginRequiredDelete)

      window.alert(translation.value.postForum.deleteNotAllowed)

      window.alert(translation.value.postForum.inLoginToCommentError)
        return
    }

  const confirmed = window.confirm(
      translation.value.postForum.deleteConfirm
  )

    if (!confirmed) return

    try {
        await deleteComment(postId, commentId)
    } catch {
      window.alert(translation.value.postForum.deleteNotAllowed)
        return
    }

    comments.value = comments.value.filter(c => c.id !== commentId)
}
// E

function canDeleteComment(commentAuthor: string) {
    if (!isLoggedIn.value) return false
    if (isAdmin.value) return true
    return meEmail.value.toLowerCase() === commentAuthor.toLowerCase()
}

function canDeletePost() {
    if (!isLoggedIn.value || !post.value) return false
    return meEmail.value.toLowerCase() === post.value.author.toLowerCase()
}

async function removePost() {
    if (!post.value || !canDeletePost()) {
        return
    }

  const confirmed = window.confirm(translation.value.postForum.deletePostConfirm)
    if (!confirmed) return

    deletingPost.value = true
    try {
        await deleteForumPost(post.value.id)
        await router.push('/ForumPage')
    } catch {
      window.alert(translation.value.postForum.deletePostNotAllowed)
    } finally {
        deletingPost.value = false
    }
}

function timeAgo(dateString: string) {
  const seconds = Math.floor(
      (Date.now() - new Date(dateString).getTime()) / 1000
  )

  if (seconds < 60) {
    return translation.value.postForum.justNow
  }

  const minutes = Math.floor(seconds / 60)

  if (minutes < 60) {
    return translation.value.postForum.minutesAgo
        .replace('{count}', String(minutes))
  }

  const hours = Math.floor(minutes / 60)

  if (hours < 24) {
    return translation.value.postForum.hoursAgo
        .replace('{count}', String(hours))
  }

  const days = Math.floor(hours / 24)

  return translation.value.postForum.daysAgo
      .replace('{count}', String(days))
}
</script>

<style scoped>
.post-page {
  font-family: Arial, sans-serif;
  min-height: 100vh;
  padding: 32px;
  background: radial-gradient(circle at top left, rgba(37, 99, 235, 0.14), transparent 24%),
  linear-gradient(180deg, #f8fbff 0%, #eef3f8 100%);
  color: #0f172a;
  box-sizing: border-box;
}

.back-btn {
  display: inline-block;
  margin-bottom: 24px;
  padding: 12px 18px;
  background: #2563eb;
  color: white;
  border-radius: 10px;
  text-decoration: none;
  font-weight: bold;
}

.status-banner {
  max-width: 1200px;
  margin: 16px auto 24px;
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

.post-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.post-card,
.comments-card {
  background: white;
  border-radius: 18px;
  padding: 28px;
  border: 1px solid #dbe2ea;
}

.post-card h1 {
  margin-top: 0;
  font-size: 2rem;
}

.post-meta {
  display: flex;
  gap: 10px;
  color: #64748b;
  margin: 18px 0;
  font-size: 0.95rem;
  flex-wrap: wrap;
}

.post-description {
  line-height: 1.8;
  color: #334155;
}

.delete-post-btn {
  margin-top: 14px;
  border: none;
  background: #dc2626;
  color: #fff;
  padding: 10px 14px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
}

.delete-post-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.comments-card h2 {
  margin-top: 0;
}

.comment-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 28px;
}

.comment-input,
.comment-textarea {
  width: 100%;
  padding: 14px;
  border-radius: 10px;
  border: 1px solid #cbd5e1;
  box-sizing: border-box;
  font-family: inherit;
}

.comment-textarea {
  min-height: 120px;
  resize: vertical;
}

.delete-btn {
  background: transparent;
  border: none;
  color: #ef4444;
  cursor: pointer;
  font-size: 16px;
  margin-left: 8px;
}

.delete-btn:hover {
  color: #b91c1c;
}

.comment-btn {
  border: none;
  background: #2563eb;
  color: white;
  padding: 12px 16px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: bold;
  transition: 0.2s;
}

.comment-btn:hover {
  background: #1d4ed8;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #475569;
  font-size: 0.9rem;
  gap: 10px;
  flex-wrap: wrap;
}

.comment-content {
  margin: 0;
  line-height: 1.6;
}

.no-comments {
  color: #64748b;
  text-align: center;
  padding: 16px;
}

.login-to-comment {
  color: #64748b;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  padding: 12px 14px;
  border-radius: 10px;
  margin: 0 0 28px 0;
}

@media (max-width: 900px) {
  .post-layout {
    grid-template-columns: 1fr;
  }

  .post-page {
    padding: 20px;
  }

  .post-card,
  .comments-card {
    padding: 20px;
  }
}

@media (max-width: 600px) {
  .post-meta {
    flex-direction: column;
    align-items: flex-start;
  }

  .back-btn {
    width: 100%;
    text-align: center;
  }

  .comment-btn {
    width: 100%;
  }
}
</style>
