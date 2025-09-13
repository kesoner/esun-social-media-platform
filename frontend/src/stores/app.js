import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 狀態
  const loading = ref(false)
  const theme = ref('light') // 'light' | 'dark'
  const sidebarCollapsed = ref(false)
  const deviceType = ref('desktop') // 'mobile' | 'tablet' | 'desktop'

  // 載入狀態管理
  const setLoading = (status) => {
    loading.value = status
  }

  const showLoading = () => {
    loading.value = true
  }

  const hideLoading = () => {
    loading.value = false
  }

  // 主題管理
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    updateTheme()
  }

  const setTheme = (newTheme) => {
    theme.value = newTheme
    updateTheme()
  }

  const updateTheme = () => {
    const html = document.documentElement
    if (theme.value === 'dark') {
      html.classList.add('dark')
    } else {
      html.classList.remove('dark')
    }
    
    // 儲存主題設定到本地存儲
    localStorage.setItem('theme', theme.value)
  }

  // 初始化主題
  const initializeTheme = () => {
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      theme.value = savedTheme
    } else {
      // 檢查系統偏好
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      theme.value = prefersDark ? 'dark' : 'light'
    }
    updateTheme()
  }

  // 側邊欄管理
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  const setSidebarCollapsed = (collapsed) => {
    sidebarCollapsed.value = collapsed
  }

  // 設備類型檢測
  const updateDeviceType = () => {
    const width = window.innerWidth
    if (width < 768) {
      deviceType.value = 'mobile'
    } else if (width < 1024) {
      deviceType.value = 'tablet'
    } else {
      deviceType.value = 'desktop'
    }
  }

  // 初始化設備類型檢測
  const initializeDeviceType = () => {
    updateDeviceType()
    window.addEventListener('resize', updateDeviceType)
  }

  // 清理事件監聽器
  const cleanup = () => {
    window.removeEventListener('resize', updateDeviceType)
  }

  return {
    // 狀態
    loading: readonly(loading),
    theme: readonly(theme),
    sidebarCollapsed: readonly(sidebarCollapsed),
    deviceType: readonly(deviceType),
    
    // 方法
    setLoading,
    showLoading,
    hideLoading,
    toggleTheme,
    setTheme,
    initializeTheme,
    toggleSidebar,
    setSidebarCollapsed,
    updateDeviceType,
    initializeDeviceType,
    cleanup
  }
})
