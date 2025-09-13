import { http } from '@/utils/request'

export const authApi = {
  // 使用者註冊
  register: (data) => {
    return http.post('/auth/register', data)
  },

  // 使用者登入
  login: (data) => {
    return http.post('/auth/login', data)
  },

  // 使用者登出
  logout: () => {
    return http.post('/auth/logout')
  },

  // 獲取個人資料
  getProfile: () => {
    return http.get('/auth/profile')
  },

  // 更新個人資料
  updateProfile: (data) => {
    return http.put('/auth/profile', data)
  },

  // 刷新 Token
  refreshToken: (data) => {
    return http.post('/auth/refresh', data)
  },

  // 檢查使用者名稱可用性
  checkUsername: (username) => {
    return http.get('/auth/check-username', {
      params: { username }
    })
  },

  // 檢查電子郵件可用性
  checkEmail: (email) => {
    return http.get('/auth/check-email', {
      params: { email }
    })
  }
}
