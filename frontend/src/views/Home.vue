<template>
  <div class="home-container">
    <div class="container">
      <div class="home-layout">
        <!-- 左側邊欄 -->
        <aside class="sidebar" v-if="!isMobile">
          <div class="sidebar-content">
            <!-- 使用者資訊卡片 -->
            <div class="user-card">
              <el-avatar :size="60" :src="userInfo.avatar">
                <el-icon size="30"><User /></el-icon>
              </el-avatar>
              <h3 class="username">{{ userInfo.username }}</h3>
              <p class="user-stats">
                <span>{{ userInfo.postCount || 0 }} 發文</span>
                <span>{{ userInfo.commentCount || 0 }} 留言</span>
              </p>
              <el-button
                type="primary"
                size="small"
                @click="$router.push('/profile')"
              >
                編輯資料
              </el-button>
            </div>

            <!-- 快速導航 -->
            <div class="quick-nav">
              <h4>快速導航</h4>
              <ul class="nav-list">
                <li>
                  <router-link to="/" class="nav-item active">
                    <el-icon><House /></el-icon>
                    首頁
                  </router-link>
                </li>
                <li>
                  <router-link to="/search" class="nav-item">
                    <el-icon><Search /></el-icon>
                    搜尋
                  </router-link>
                </li>
                <li>
                  <router-link to="/notifications" class="nav-item">
                    <el-icon><Bell /></el-icon>
                    通知
                  </router-link>
                </li>
                <li>
                  <router-link to="/settings" class="nav-item">
                    <el-icon><Setting /></el-icon>
                    設定
                  </router-link>
                </li>
              </ul>
            </div>
          </div>
        </aside>

        <!-- 主要內容區域 -->
        <main class="main-content">
          <!-- 發文區域 -->
          <div class="post-composer">
            <div class="composer-header">
              <el-avatar :size="40" :src="userInfo.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <el-button
                class="compose-button"
                @click="$router.push('/create-post')"
              >
                分享您的想法...
              </el-button>
            </div>
          </div>

          <!-- 發文篩選 -->
          <div class="post-filter">
            <el-radio-group v-model="activeFilter" @change="handleFilterChange">
              <el-radio-button label="latest">最新</el-radio-button>
              <el-radio-button label="popular">熱門</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 發文列表 -->
          <div class="posts-container">
            <div v-if="loading" class="loading-container">
              <el-skeleton :rows="3" animated />
              <el-skeleton :rows="3" animated />
              <el-skeleton :rows="3" animated />
            </div>

            <div v-else-if="posts.length === 0" class="empty-state">
              <el-empty description="還沒有發文，快來分享第一篇吧！">
                <el-button type="primary" @click="$router.push('/create-post')">
                  建立發文
                </el-button>
              </el-empty>
            </div>

            <div v-else>
              <PostCard
                v-for="post in posts"
                :key="post.id"
                :post="post"
                @comment="handleComment"
                @delete="handleDeletePost"
              />

              <!-- 載入更多 -->
              <div class="load-more" v-if="hasMore">
                <el-button
                  @click="loadMorePosts"
                  :loading="loadingMore"
                  size="large"
                >
                  載入更多
                </el-button>
              </div>
            </div>
          </div>
        </main>

        <!-- 右側邊欄 -->
        <aside class="right-sidebar" v-if="!isMobile">
          <div class="sidebar-content">
            <!-- 熱門話題 -->
            <div class="trending-topics">
              <h4>熱門話題</h4>
              <ul class="topic-list">
                <li v-for="topic in trendingTopics" :key="topic.id">
                  <a href="#" class="topic-item">
                    #{{ topic.name }}
                    <span class="topic-count">{{ topic.count }}</span>
                  </a>
                </li>
              </ul>
            </div>

            <!-- 推薦使用者 -->
            <div class="suggested-users">
              <h4>推薦關注</h4>
              <div class="user-list">
                <div
                  v-for="user in suggestedUsers"
                  :key="user.id"
                  class="user-item"
                >
                  <el-avatar :size="32" :src="user.avatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <div class="user-info">
                    <p class="user-name">{{ user.username }}</p>
                    <p class="user-bio">{{ user.biography }}</p>
                  </div>
                  <el-button size="small" type="primary">關注</el-button>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { postApi } from '@/api/post'
import PostCard from '@/components/post/PostCard.vue'
import {
  User,
  House,
  Search,
  Bell,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

// 響應式資料
const posts = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const activeFilter = ref('latest')
const currentPage = ref(0)
const hasMore = ref(true)
const trendingTopics = ref([
  { id: 1, name: '玉山銀行', count: 128 },
  { id: 2, name: '金融科技', count: 95 },
  { id: 3, name: '數位轉型', count: 67 }
])
const suggestedUsers = ref([
  { id: 1, username: 'alice', biography: '熱愛分享生活點滴' },
  { id: 2, username: 'bob', biography: '科技愛好者' },
  { id: 3, username: 'charlie', biography: '攝影師' }
])

// 計算屬性
const userInfo = computed(() => authStore.userInfo)
const isMobile = computed(() => appStore.deviceType === 'mobile')

// 方法
const loadPosts = async (reset = false) => {
  if (reset) {
    currentPage.value = 0
    hasMore.value = true
    posts.value = []
  }

  loading.value = reset
  loadingMore.value = !reset

  try {
    const params = {
      page: currentPage.value,
      size: 10
    }

    let response
    if (activeFilter.value === 'popular') {
      response = await postApi.getPopularPosts(params)
    } else {
      response = await postApi.getPosts(params)
    }

    const newPosts = response.data.content || []
    
    if (reset) {
      posts.value = newPosts
    } else {
      posts.value.push(...newPosts)
    }

    hasMore.value = !response.data.last
    currentPage.value++
  } catch (error) {
    console.error('載入發文失敗:', error)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMorePosts = () => {
  if (!loadingMore.value && hasMore.value) {
    loadPosts(false)
  }
}

const handleFilterChange = () => {
  loadPosts(true)
}

const handleComment = (postId) => {
  // 跳轉到發文詳情頁面
  router.push(`/post/${postId}`)
}

const handleDeletePost = (postId) => {
  // 從列表中移除已刪除的發文
  posts.value = posts.value.filter(post => post.id !== postId)
}

// 生命週期
onMounted(() => {
  loadPosts(true)
})
</script>

<style scoped>
.home-container {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.home-layout {
  display: grid;
  grid-template-columns: 280px 1fr 280px;
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.sidebar,
.right-sidebar {
  position: sticky;
  top: 80px;
  height: fit-content;
}

.sidebar-content {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 20px;
  box-shadow: var(--el-box-shadow-light);
}

.user-card {
  text-align: center;
  margin-bottom: 24px;
}

.username {
  margin: 12px 0 8px;
  font-size: 18px;
  font-weight: 600;
}

.user-stats {
  margin: 8px 0 16px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.user-stats span {
  margin: 0 8px;
}

.quick-nav h4 {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  text-decoration: none;
  color: var(--el-text-color-primary);
  transition: background-color 0.3s;
}

.nav-item:hover,
.nav-item.active {
  background-color: var(--el-fill-color-light);
}

.nav-item .el-icon {
  margin-right: 12px;
}

.main-content {
  min-height: 100vh;
}

.post-composer {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: var(--el-box-shadow-light);
}

.composer-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.compose-button {
  flex: 1;
  height: 40px;
  background: var(--el-fill-color-lighter);
  border: none;
  color: var(--el-text-color-placeholder);
  text-align: left;
  padding-left: 16px;
}

.post-filter {
  margin-bottom: 20px;
}

.posts-container {
  space-y: 16px;
}

.loading-container {
  space-y: 16px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}

/* 右側邊欄樣式 */
.trending-topics,
.suggested-users {
  margin-bottom: 24px;
}

.trending-topics h4,
.suggested-users h4 {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
}

.topic-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.topic-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  text-decoration: none;
  color: var(--el-text-color-primary);
}

.topic-count {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.user-list {
  space-y: 12px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  flex: 1;
}

.user-name {
  margin: 0 0 4px;
  font-weight: 500;
  font-size: 14px;
}

.user-bio {
  margin: 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

/* 響應式設計 */
@media (max-width: 1024px) {
  .home-layout {
    grid-template-columns: 1fr;
    padding: 16px;
  }
  
  .sidebar,
  .right-sidebar {
    display: none;
  }
}
</style>
