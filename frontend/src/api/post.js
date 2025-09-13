import { http } from '@/utils/request'

export const postApi = {
  // 建立發文
  createPost: (data) => {
    return http.post('/posts', data)
  },

  // 獲取所有發文
  getPosts: (params = {}) => {
    return http.get('/posts', { params })
  },

  // 根據 ID 獲取發文
  getPostById: (id) => {
    return http.get(`/posts/${id}`)
  },

  // 更新發文
  updatePost: (id, data) => {
    return http.put(`/posts/${id}`, data)
  },

  // 刪除發文
  deletePost: (id) => {
    return http.delete(`/posts/${id}`)
  },

  // 根據作者獲取發文
  getPostsByAuthor: (authorId, params = {}) => {
    return http.get(`/posts/author/${authorId}`, { params })
  },

  // 搜尋發文
  searchPosts: (params = {}) => {
    return http.get('/posts/search', { params })
  },

  // 獲取熱門發文
  getPopularPosts: (params = {}) => {
    return http.get('/posts/popular', { params })
  },

  // 獲取最新發文
  getLatestPosts: (params = {}) => {
    return http.get('/posts/latest', { params })
  }
}
