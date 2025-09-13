import { http } from '@/utils/request'

export const userApi = {
  // 根據 ID 獲取使用者
  getUserById: (id) => {
    return http.get(`/users/${id}`)
  },

  // 根據使用者名稱獲取使用者
  getUserByUsername: (username) => {
    return http.get(`/users/username/${username}`)
  },

  // 搜尋使用者
  searchUsers: (params = {}) => {
    return http.get('/users/search', { params })
  }
}
