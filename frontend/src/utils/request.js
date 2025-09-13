import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import router from '@/router'

// 建立 axios 實例
const request = axios.create({
  baseURL: '/api', // 透過 Vite 代理到後端
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 請求攔截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const appStore = useAppStore()
    
    // 顯示載入狀態
    appStore.showLoading()
    
    // 添加認證 Token
    if (authStore.accessToken) {
      config.headers.Authorization = `Bearer ${authStore.accessToken}`
    }
    
    return config
  },
  (error) => {
    const appStore = useAppStore()
    appStore.hideLoading()
    return Promise.reject(error)
  }
)

// 回應攔截器
request.interceptors.response.use(
  (response) => {
    const appStore = useAppStore()
    appStore.hideLoading()
    return response
  },
  async (error) => {
    const appStore = useAppStore()
    const authStore = useAuthStore()
    
    appStore.hideLoading()
    
    const { response } = error
    
    if (!response) {
      ElMessage.error('網路連接失敗，請檢查網路設定')
      return Promise.reject(error)
    }
    
    const { status, data } = response
    
    switch (status) {
      case 400:
        // 請求參數錯誤
        if (data.validationErrors) {
          // 顯示驗證錯誤
          const errors = Object.values(data.validationErrors).join('\n')
          ElMessage.error(errors)
        } else {
          ElMessage.error(data.message || '請求參數錯誤')
        }
        break
        
      case 401:
        // 未授權，嘗試刷新 Token
        if (authStore.refreshToken && !error.config._retry) {
          error.config._retry = true
          
          try {
            await authStore.refreshAccessToken()
            // 重新發送原始請求
            error.config.headers.Authorization = `Bearer ${authStore.accessToken}`
            return request(error.config)
          } catch (refreshError) {
            // Token 刷新失敗，跳轉到登入頁面
            ElMessage.error('登入已過期，請重新登入')
            authStore.clearTokens()
            router.push('/login')
            return Promise.reject(refreshError)
          }
        } else {
          ElMessage.error('請先登入')
          authStore.clearTokens()
          router.push('/login')
        }
        break
        
      case 403:
        ElMessage.error('沒有權限執行此操作')
        break
        
      case 404:
        ElMessage.error('請求的資源不存在')
        break
        
      case 409:
        ElMessage.error(data.message || '資源衝突')
        break
        
      case 422:
        ElMessage.error(data.message || '資料驗證失敗')
        break
        
      case 429:
        ElMessage.error('請求過於頻繁，請稍後再試')
        break
        
      case 500:
        ElMessage.error('伺服器內部錯誤，請稍後再試')
        break
        
      case 502:
      case 503:
      case 504:
        ElMessage.error('服務暫時不可用，請稍後再試')
        break
        
      default:
        ElMessage.error(data.message || `請求失敗 (${status})`)
    }
    
    return Promise.reject(error)
  }
)

// 請求方法封裝
export const http = {
  get: (url, config = {}) => request.get(url, config),
  post: (url, data = {}, config = {}) => request.post(url, data, config),
  put: (url, data = {}, config = {}) => request.put(url, data, config),
  patch: (url, data = {}, config = {}) => request.patch(url, data, config),
  delete: (url, config = {}) => request.delete(url, config)
}

// 檔案上傳方法
export const uploadFile = (url, file, onProgress) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: (progressEvent) => {
      if (onProgress) {
        const percentCompleted = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total
        )
        onProgress(percentCompleted)
      }
    }
  })
}

// 下載檔案方法
export const downloadFile = async (url, filename) => {
  try {
    const response = await request.get(url, {
      responseType: 'blob'
    })
    
    const blob = new Blob([response.data])
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)
  } catch (error) {
    ElMessage.error('檔案下載失敗')
    throw error
  }
}

export default request
