<template>
  <header class="app-header">
    <div class="header-container">
      <!-- Logo 和標題 -->
      <div class="header-left">
        <router-link to="/" class="logo-link">
          <el-icon class="logo-icon" size="28">
            <ChatDotRound />
          </el-icon>
          <span class="logo-text">玉山社群</span>
        </router-link>
      </div>

      <!-- 搜尋框 -->
      <div class="header-center" v-if="!isMobile">
        <el-input
          v-model="searchKeyword"
          placeholder="搜尋發文或使用者..."
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <!-- 右側功能區 -->
      <div class="header-right">
        <!-- 行動版搜尋按鈕 -->
        <el-button
          v-if="isMobile"
          circle
          @click="showMobileSearch = true"
        >
          <el-icon><Search /></el-icon>
        </el-button>

        <!-- 建立發文按鈕 -->
        <el-button
          type="primary"
          @click="$router.push('/create-post')"
          :icon="EditPen"
        >
          <span v-if="!isMobile">發文</span>
        </el-button>

        <!-- 通知按鈕 -->
        <el-badge :value="notificationCount" :hidden="notificationCount === 0">
          <el-button
            circle
            @click="$router.push('/notifications')"
          >
            <el-icon><Bell /></el-icon>
          </el-button>
        </el-badge>

        <!-- 主題切換 -->
        <el-button
          circle
          @click="toggleTheme"
        >
          <el-icon>
            <Sunny v-if="theme === 'dark'" />
            <Moon v-else />
          </el-icon>
        </el-button>

        <!-- 使用者選單 -->
        <el-dropdown @command="handleUserMenuCommand">
          <div class="user-avatar">
            <el-avatar :size="32" :src="userInfo.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <span v-if="!isMobile" class="username">{{ userInfo.username }}</span>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                個人資料
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                設定
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                登出
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 行動版搜尋對話框 -->
    <el-dialog
      v-model="showMobileSearch"
      title="搜尋"
      width="90%"
      :show-close="false"
    >
      <el-input
        v-model="searchKeyword"
        placeholder="搜尋發文或使用者..."
        @keyup.enter="handleMobileSearch"
        autofocus
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <template #footer>
        <el-button @click="showMobileSearch = false">取消</el-button>
        <el-button type="primary" @click="handleMobileSearch">搜尋</el-button>
      </template>
    </el-dialog>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import {
  ChatDotRound,
  Search,
  EditPen,
  Bell,
  Sunny,
  Moon,
  User,
  ArrowDown,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

// 響應式資料
const searchKeyword = ref('')
const showMobileSearch = ref(false)
const notificationCount = ref(0) // TODO: 從 API 獲取通知數量

// 計算屬性
const userInfo = computed(() => authStore.userInfo)
const theme = computed(() => appStore.theme)
const isMobile = computed(() => appStore.deviceType === 'mobile')

// 方法
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      name: 'Search',
      query: { q: searchKeyword.value.trim() }
    })
  }
}

const handleMobileSearch = () => {
  handleSearch()
  showMobileSearch.value = false
}

const toggleTheme = () => {
  appStore.toggleTheme()
}

const handleUserMenuCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      authStore.logout()
      break
  }
}
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
  box-shadow: var(--el-box-shadow-light);
  z-index: 1000;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: var(--el-text-color-primary);
  font-weight: 600;
  font-size: 18px;
}

.logo-icon {
  margin-right: 8px;
  color: var(--el-color-primary);
}

.logo-text {
  display: none;
}

.header-center {
  flex: 1;
  max-width: 400px;
  margin: 0 20px;
}

.search-input {
  width: 100%;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-avatar:hover {
  background-color: var(--el-fill-color-light);
}

.username {
  margin: 0 8px;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.dropdown-icon {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

/* 響應式設計 */
@media (min-width: 768px) {
  .app-header {
    height: 60px;
  }
  
  .logo-text {
    display: inline;
  }
}

@media (max-width: 767px) {
  .app-header {
    height: 50px;
  }
  
  .header-container {
    padding: 0 12px;
  }
  
  .header-right {
    gap: 8px;
  }
}
</style>
