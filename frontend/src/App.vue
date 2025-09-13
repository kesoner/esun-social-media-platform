<template>
  <div id="app">
    <el-config-provider :locale="locale">
      <!-- 導航欄 -->
      <AppHeader v-if="!isAuthPage" />
      
      <!-- 主要內容區域 -->
      <main :class="{ 'with-header': !isAuthPage }">
        <router-view />
      </main>
      
      <!-- 全域載入指示器 -->
      <div v-loading="globalLoading" class="global-loading-overlay" v-if="globalLoading"></div>

      <!-- 認證狀態調試組件 -->
      <AuthDebug />
    </el-config-provider>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import AppHeader from '@/components/layout/AppHeader.vue'
import AuthDebug from '@/components/debug/AuthDebug.vue'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const authStore = useAuthStore()
const appStore = useAppStore()

// 語言配置
const locale = zhCn

// 判斷是否為認證頁面（登入/註冊）
const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})

// 全域載入狀態
const globalLoading = computed(() => appStore.loading)

// 初始化應用程式
onMounted(() => {
  // 檢查本地存儲的認證狀態
  authStore.initializeAuth()
})
</script>

<style scoped>
#app {
  min-height: 100vh;
  background-color: var(--el-bg-color-page);
}

main {
  min-height: 100vh;
  transition: all 0.3s ease;
}

main.with-header {
  padding-top: 60px; /* 為固定導航欄留出空間 */
  min-height: calc(100vh - 60px);
}

.global-loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  pointer-events: none;
}

/* 響應式設計 */
@media (max-width: 768px) {
  main.with-header {
    padding-top: 50px;
    min-height: calc(100vh - 50px);
  }
}
</style>
