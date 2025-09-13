import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

// 路由配置
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true, title: '首頁' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresGuest: true, title: '登入' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { requiresGuest: true, title: '註冊' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { requiresAuth: true, title: '個人資料' }
  },
  {
    path: '/user/:id',
    name: 'UserProfile',
    component: () => import('@/views/user/UserProfile.vue'),
    meta: { requiresAuth: true, title: '使用者資料' }
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/post/PostDetail.vue'),
    meta: { requiresAuth: true, title: '發文詳情' }
  },
  {
    path: '/create-post',
    name: 'CreatePost',
    component: () => import('@/views/post/CreatePost.vue'),
    meta: { requiresAuth: true, title: '建立發文' }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue'),
    meta: { requiresAuth: true, title: '搜尋' }
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/Notifications.vue'),
    meta: { requiresAuth: true, title: '通知' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/Settings.vue'),
    meta: { requiresAuth: true, title: '設定' }
  },
  {
    path: '/test-auth',
    name: 'TestAuth',
    component: () => import('@/views/TestAuth.vue'),
    meta: { title: '認證測試' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { title: '頁面不存在' }
  }
]

// 建立路由實例
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守衛
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // 設定頁面標題
  if (to.meta.title) {
    document.title = `${to.meta.title} - 玉山銀行社群媒體平台`
  }

  // 如果有 token 但沒有用戶資訊，嘗試獲取用戶資訊
  if (authStore.accessToken && !authStore.user) {
    try {
      await authStore.fetchUserProfile()
    } catch (error) {
      console.error('獲取用戶資訊失敗:', error)
      // 如果獲取失敗，清除認證狀態
      authStore.clearTokens()
    }
  }

  // 檢查認證狀態
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      ElMessage.warning('請先登入')
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }

  // 檢查訪客限制（已登入用戶不能訪問登入/註冊頁面）
  if (to.meta.requiresGuest) {
    if (authStore.isAuthenticated) {
      next({ name: 'Home' })
      return
    }
  }

  next()
})

// 路由錯誤處理
router.onError((error) => {
  console.error('路由錯誤:', error)
  ElMessage.error('頁面載入失敗，請重試')
})

export default router
