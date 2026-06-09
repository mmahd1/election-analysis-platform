const BASE_URL = import.meta.env.VITE_API_BASE_URL

if (!BASE_URL) {
    throw new Error('VITE_API_BASE_URL is not configured')
}

export type ForumPost = {
    id: number
    title: string
    author: string
    description: string
    createdAt: string
}

export type ForumComment = {
    id: number
    postId: number
    author: string
    description: string
    createdAt: string
}

// get forum post without id, because it has none yet
// author is set by the backend using the authenticated user
export type CreatePost = Omit<ForumPost, 'id' | 'createdAt' | 'author'>

export const fetchForumPosts = (page = 0, size = 10) =>
    fetch(`${BASE_URL}/forum-api/posts-api?page=${page}&size=${size}`)
        .then(res => res.json())
        .then((data) => {
            if (Array.isArray(data)) return data
            return data?.content ?? []
        })

export const createForumPost = (post: CreatePost) => {
    const token = localStorage.getItem('token')

    if (!token) {
        throw new Error('Login required')
    }

    return fetch(`${BASE_URL}/forum-api/posts-api`, {
        // makes new db item and send json items
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
        },
        // turns object into json string
        body: JSON.stringify(post)
    }).then(async (res) => {
        const responseText = await res.text()
        if (!res.ok) {
            console.error('Server response:', res.status, responseText)
            throw new Error(`Server error ${res.status}: ${responseText}`)
        }

        try {
            return JSON.parse(responseText)
        } catch (e) {
            return responseText
        }
    })
}

export const fetchMyForumPosts = () => {
    const token = localStorage.getItem('token')
    if (!token) {
        throw new Error('Login required')
    }

    return fetch(`${BASE_URL}/forum-api/posts-api/mine`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    }).then(async (res) => {
        if (!res.ok) {
            throw new Error(`Server gaf status ${res.status}`)
        }
        return res.json()
    })
}

export const updateForumPost = (
    id: number,
    post: { title: string; author: string; description: string }
) =>
{
    const token = localStorage.getItem('token')

    return fetch(`${BASE_URL}/forum-api/posts-api/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(post)
    }).then(async (res) => {
        if (!res.ok) {
            throw new Error(`Server gaf status ${res.status}`)
        }

        return res.json()
    })
}

export const fetchComments = (postId: number) =>
    fetch(`${BASE_URL}/forum-api/posts/${postId}/comments`)
        .then(res => res.json())

export const createComment = (
    postId: number,
    comment: { description: string }
) =>
{
    const token = localStorage.getItem('token')
    if (!token) {
        throw new Error('Login required')
    }

    return fetch(`${BASE_URL}/forum-api/posts/${postId}/comments`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(comment)
    }).then(async (res) => {
        if (!res.ok) {
            throw new Error(`Server gaf status ${res.status}`)
        }
        return res.json()
    })
}

// E
export const deleteComment = (postId: number, commentId: number) => {
    const token = localStorage.getItem('token')
    if (!token) throw new Error('Login required')

    return fetch(`${BASE_URL}/forum-api/posts/${postId}/comments/${commentId}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`
        }
    }).then(async (res) => {
        const text = await res.text()
        if (!res.ok) {
            console.error('Delete comment failed:', res.status, text)
            throw new Error(`Server error ${res.status}: ${text}`)
        }
    })
}
// E

export const deleteForumPost = (postId: number) => {
    const token = localStorage.getItem('token')
    if (!token) {
        throw new Error('Login required')
    }

    return fetch(`${BASE_URL}/forum-api/posts-api/${postId}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`
        }
    }).then(async (res) => {
        if (!res.ok) {
            throw new Error(`Server gaf status ${res.status}`)
        }
    })
}
