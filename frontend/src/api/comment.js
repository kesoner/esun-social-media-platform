import { http } from '@/utils/request'

export const commentApi = {
  // 建立留言
  createComment: (postId, data) => {
    return http.post(`/comments/posts/${postId}`, data)
  },

  // 根據 ID 獲取留言
  getCommentById: (id) => {
    return http.get(`/comments/${id}`)
  },

  // 根據發文 ID 獲取留言
  getCommentsByPostId: (postId) => {
    return http.get(`/comments/posts/${postId}`)
  },

  // 根據發文 ID 獲取留言（分頁）
  getCommentsByPostIdPaged: (postId, params = {}) => {
    return http.get(`/comments/posts/${postId}/page`, { params })
  },

  // 根據作者獲取留言
  getCommentsByAuthor: (authorId, params = {}) => {
    return http.get(`/comments/author/${authorId}`, { params })
  },

  // 刪除留言
  deleteComment: (id) => {
    return http.delete(`/comments/${id}`)
  },

  // 搜尋留言
  searchComments: (params = {}) => {
    return http.get('/comments/search', { params })
  },

  // 獲取最新留言
  getLatestComments: (params = {}) => {
    return http.get('/comments/latest', { params })
  },

  // 統計發文留言數
  getCommentCount: (postId) => {
    return http.get(`/comments/posts/${postId}/count`)
  }
}
