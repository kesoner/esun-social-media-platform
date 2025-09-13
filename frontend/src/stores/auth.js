import { defineStore } from 'pinia'
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'
import { authApi } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // 狀態
  const user = ref(null)
  const accessToken = ref(null)
  const refreshToken = ref(null)
  const loading = ref(false)

  // 計算屬性
  const isAuthenticated = computed(() => {
    const hasToken = !!accessToken.value
    const hasUser = !!user.value
    const result = hasToken && hasUser

    // 調試日誌
    if (process.env.NODE_ENV === 'development') {
      console.log('認證狀態檢查:', {
        hasToken,
        hasUser,
        isAuthenticated: result,
        token: accessToken.value ? '存在' : '不存在',
        user: user.value ? user.value.username : '不存在'
      })
    }

    return result
  })

  const userInfo = computed(() => {
    return user.value || {}
  })

  // Token 管理
  const setTokens = (tokens) => {
    accessToken.value = tokens.accessToken
    refreshToken.value = tokens.refreshToken
    
    // 儲存到 Cookie
    Cookies.set('accessToken', tokens.accessToken, { expires: 1 }) // 1天
    Cookies.set('refreshToken', tokens.refreshToken, { expires: 7 }) // 7天
  }

  const clearTokens = () => {
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    
    // 清除 Cookie
    Cookies.remove('accessToken')
    Cookies.remove('refreshToken')
  }

  // 初始化認證狀態
  const initializeAuth = async () => {
    const savedAccessToken = Cookies.get('accessToken')
    const savedRefreshToken = Cookies.get('refreshToken')

    if (savedAccessToken && savedRefreshToken) {
      accessToken.value = savedAccessToken
      refreshToken.value = savedRefreshToken

      try {
        // 獲取使用者資訊
        await fetchUserProfile()
      } catch (error) {
        console.error('初始化認證失敗:', error)
        // 只有在非 401 錯誤時才清除 tokens，401 錯誤會由攔截器處理
        if (error.response?.status !== 401) {
          clearTokens()
        }
      }
    }
  }

  // 登入
  const login = async (credentials) => {
    loading.value = true
    try {
      const response = await authApi.login(credentials)
      const { accessToken: token, refreshToken: refresh, user: userData } = response.data

      // 設置 tokens 和用戶資訊
      setTokens({ accessToken: token, refreshToken: refresh })
      user.value = userData

      console.log('登入成功，認證狀態:', {
        hasToken: !!token,
        hasUser: !!userData,
        isAuthenticated: !!token && !!userData
      })

      ElMessage.success('登入成功')

      // 使用 nextTick 確保狀態更新後再跳轉
      await nextTick()

      // 重定向到原來要訪問的頁面或首頁
      const redirect = router.currentRoute.value.query.redirect || '/'
      console.log('準備跳轉到:', redirect)

      await router.push(redirect)

      return response.data
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '登入失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 註冊
  const register = async (userData) => {
    loading.value = true
    try {
      const response = await authApi.register(userData)
      const { accessToken: token, refreshToken: refresh, user: newUser } = response.data
      
      setTokens({ accessToken: token, refreshToken: refresh })
      user.value = newUser
      
      ElMessage.success('註冊成功')
      router.push('/')
      
      return response.data
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '註冊失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('登出 API 調用失敗:', error)
    } finally {
      clearTokens()
      ElMessage.success('已登出')
      router.push('/login')
    }
  }

  // 獲取使用者資料
  const fetchUserProfile = async () => {
    try {
      const response = await authApi.getProfile()
      user.value = response.data
      return response.data
    } catch (error) {
      console.error('獲取使用者資料失敗:', error)
      throw error
    }
  }

  // 更新使用者資料
  const updateProfile = async (profileData) => {
    loading.value = true
    try {
      const response = await authApi.updateProfile(profileData)
      user.value = response.data
      ElMessage.success('資料更新成功')
      return response.data
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '更新失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 刷新 Token
  const refreshAccessToken = async () => {
    if (!refreshToken.value) {
      throw new Error('沒有 Refresh Token')
    }
    
    try {
      const response = await authApi.refreshToken({ refreshToken: refreshToken.value })
      const { accessToken: newToken, refreshToken: newRefresh } = response.data
      
      setTokens({ accessToken: newToken, refreshToken: newRefresh })
      return newToken
    } catch (error) {
      console.error('Token 刷新失敗:', error)
      clearTokens()
      router.push('/login')
      throw error
    }
  }

  // 檢查使用者名稱可用性
  const checkUsernameAvailability = async (username) => {
    try {
      const response = await authApi.checkUsername(username)
      return response.data.available
    } catch (error) {
      console.error('檢查使用者名稱失敗:', error)
      // 當 API 調用失敗時，假設可用（避免誤報）
      return true
    }
  }

  // 檢查電子郵件可用性
  const checkEmailAvailability = async (email) => {
    try {
      const response = await authApi.checkEmail(email)
      return response.data.available
    } catch (error) {
      console.error('檢查電子郵件失敗:', error)
      // 當 API 調用失敗時，假設可用（避免誤報）
      return true
    }
  }

  return {
    // 狀態
    user: readonly(user),
    accessToken: readonly(accessToken),
    refreshToken: readonly(refreshToken),
    loading: readonly(loading),
    
    // 計算屬性
    isAuthenticated,
    userInfo,
    
    // 方法
    initializeAuth,
    login,
    register,
    logout,
    fetchUserProfile,
    updateProfile,
    refreshAccessToken,
    checkUsernameAvailability,
    checkEmailAvailability,
    setTokens,
    clearTokens
  }
})
