<template>
  <div class="user-profile-container">
    <div class="container">
      <div class="user-profile-layout">
        <!-- 返回按鈕 -->
        <div class="page-header">
          <el-button
            @click="$router.back()"
            circle
            size="large"
          >
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <h1 class="page-title">使用者資料</h1>
        </div>

        <!-- 載入狀態 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="4" animated />
        </div>

        <!-- 使用者不存在 -->
        <div v-else-if="!user" class="not-found">
          <el-empty description="使用者不存在">
            <el-button type="primary" @click="$router.push('/')">
              回到首頁
            </el-button>
          </el-empty>
        </div>

        <!-- 使用者資料 -->
        <div v-else class="user-profile-content">
          <!-- 使用者資訊卡片 -->
          <div class="user-card">
            <div class="user-header">
              <div class="avatar-section">
                <el-avatar :size="120" :src="user.avatar">
                  <el-icon size="60"><User /></el-icon>
                </el-avatar>
                <div class="user-actions" v-if="!isCurrentUser">
                  <el-button
                    type="primary"
                    @click="handleFollow"
                    :loading="following"
                  >
                    {{ isFollowing ? '取消關注' : '關注' }}
                  </el-button>
                  <el-button @click="handleMessage">
                    發送訊息
                  </el-button>
                </div>
              </div>
              
              <div class="user-info">
                <h1 class="username">{{ user.username }}</h1>
                <p class="email">{{ user.email }}</p>
                <p class="biography">{{ user.biography || '這個人很懶，什麼都沒有留下。' }}</p>
                
                <div class="stats">
                  <div class="stat-item">
                    <span class="stat-number">{{ user.postCount || 0 }}</span>
                    <span class="stat-label">發文</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-number">{{ user.commentCount || 0 }}</span>
                    <span class="stat-label">留言</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-number">{{ user.followerCount || 0 }}</span>
                    <span class="stat-label">關注者</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-number">{{ user.followingCount || 0 }}</span>
                    <span class="stat-label">關注中</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-number">{{ formatDate(user.createdAt) }}</span>
                    <span class="stat-label">加入時間</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 發文列表 -->
          <div class="posts-section">
            <div class="section-header">
              <h2>{{ user.username }} 的發文</h2>
            </div>
            
            <div v-if="loadingPosts" class="loading-container">
              <el-skeleton :rows="3" animated />
              <el-skeleton :rows="3" animated />
            </div>
            
            <div v-else-if="posts.length === 0" class="empty-state">
              <el-empty description="還沒有發文" />
            </div>
            
            <div v-else class="posts-list">
              <PostCard
                v-for="post in posts"
                :key="post.id"
                :post="post"
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
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api/user'
import { postApi } from '@/api/post'
import PostCard from '@/components/post/PostCard.vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { ArrowLeft, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 響應式資料
const user = ref(null)
const posts = ref([])
const loading = ref(true)
const loadingPosts = ref(false)
const loadingMore = ref(false)
const following = ref(false)
const isFollowing = ref(false)
const hasMore = ref(true)
const currentPage = ref(0)

// 計算屬性
const currentUser = computed(() => authStore.userInfo)
const userId = computed(() => route.params.id)
const isCurrentUser = computed(() => {
  return currentUser.value.id === parseInt(userId.value)
})

// 方法
const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月')
}

const loadUser = async () => {
  try {
    loading.value = true
    const response = await userApi.getUserById(userId.value)
    user.value = response.data
  } catch (error) {
    console.error('載入使用者資料失敗:', error)
    user.value = null
  } finally {
    loading.value = false
  }
}

const loadPosts = async (reset = false) => {
  if (reset) {
    currentPage.value = 0
    hasMore.value = true
    posts.value = []
  }

  loadingPosts.value = reset
  loadingMore.value = !reset

  try {
    const response = await postApi.getPostsByAuthor(userId.value, {
      page: currentPage.value,
      size: 10
    })

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
    loadingPosts.value = false
    loadingMore.value = false
  }
}

const loadMorePosts = () => {
  if (!loadingMore.value && hasMore.value) {
    loadPosts(false)
  }
}

const handleFollow = async () => {
  following.value = true
  try {
    // TODO: 實作關注/取消關注 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    isFollowing.value = !isFollowing.value
    
    if (isFollowing.value) {
      ElMessage.success(`已關注 ${user.value.username}`)
      user.value.followerCount = (user.value.followerCount || 0) + 1
    } else {
      ElMessage.success(`已取消關注 ${user.value.username}`)
      user.value.followerCount = Math.max((user.value.followerCount || 1) - 1, 0)
    }
  } catch (error) {
    console.error('關注操作失敗:', error)
    ElMessage.error('操作失敗，請重試')
  } finally {
    following.value = false
  }
}

const handleMessage = () => {
  // TODO: 實作私訊功能
  ElMessage.info('私訊功能開發中')
}

const handleDeletePost = (postId) => {
  posts.value = posts.value.filter(post => post.id !== postId)
  if (user.value) {
    user.value.postCount = Math.max((user.value.postCount || 1) - 1, 0)
  }
}

// 生命週期
onMounted(async () => {
  await loadUser()
  if (user.value) {
    await loadPosts(true)
  }
})
</script>

<style scoped>
.user-profile-container {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.user-profile-layout {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.loading-container {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--el-box-shadow-light);
}

.not-found {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 60px 24px;
  text-align: center;
  box-shadow: var(--el-box-shadow-light);
}

.user-profile-content {
  space-y: 24px;
}

.user-card {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 32px;
  box-shadow: var(--el-box-shadow-light);
}

.user-header {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.user-actions {
  display: flex;
  gap: 12px;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  color: var(--el-text-color-primary);
}

.email {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  margin: 0 0 16px;
}

.biography {
  font-size: 16px;
  line-height: 1.6;
  color: var(--el-text-color-primary);
  margin: 0 0 24px;
}

.stats {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.stat-number {
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.posts-section {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--el-box-shadow-light);
  margin-top: 24px;
}

.section-header {
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.posts-list {
  space-y: 16px;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .user-profile-layout {
    padding: 16px;
  }
  
  .user-card {
    padding: 24px;
  }
  
  .user-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 24px;
  }
  
  .user-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .username {
    font-size: 24px;
  }
  
  .stats {
    justify-content: center;
    gap: 24px;
  }
  
  .posts-section {
    padding: 20px;
  }
}
</style>
