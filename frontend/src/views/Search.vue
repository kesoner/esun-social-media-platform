<template>
  <div class="search-container">
    <div class="container">
      <div class="search-layout">
        <!-- 搜尋表單 -->
        <div class="search-form">
          <el-input
            v-model="searchQuery"
            placeholder="搜尋發文或使用者..."
            size="large"
            @keyup.enter="handleSearch"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch" :loading="searching">
                搜尋
              </el-button>
            </template>
          </el-input>
        </div>

        <!-- 搜尋結果 -->
        <div v-if="hasSearched" class="search-results">
          <div class="results-header">
            <h2>搜尋結果</h2>
            <span class="results-count">
              找到 {{ totalResults }} 個結果
            </span>
          </div>

          <!-- 結果篩選 -->
          <div class="result-filters">
            <el-radio-group v-model="activeTab" @change="handleTabChange">
              <el-radio-button label="posts">發文</el-radio-button>
              <el-radio-button label="users">使用者</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 發文結果 -->
          <div v-if="activeTab === 'posts'" class="posts-results">
            <div v-if="searching" class="loading-container">
              <el-skeleton :rows="3" animated />
              <el-skeleton :rows="3" animated />
            </div>
            
            <div v-else-if="posts.length === 0" class="empty-state">
              <el-empty description="沒有找到相關發文" />
            </div>
            
            <div v-else>
              <PostCard
                v-for="post in posts"
                :key="post.id"
                :post="post"
                @delete="handleDeletePost"
              />
            </div>
          </div>

          <!-- 使用者結果 -->
          <div v-if="activeTab === 'users'" class="users-results">
            <div v-if="searching" class="loading-container">
              <el-skeleton :rows="2" animated />
              <el-skeleton :rows="2" animated />
            </div>
            
            <div v-else-if="users.length === 0" class="empty-state">
              <el-empty description="沒有找到相關使用者" />
            </div>
            
            <div v-else class="users-list">
              <div
                v-for="user in users"
                :key="user.id"
                class="user-item"
              >
                <el-avatar :size="48" :src="user.avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="user-info">
                  <router-link
                    :to="`/user/${user.id}`"
                    class="user-name"
                  >
                    {{ user.username }}
                  </router-link>
                  <p class="user-bio">{{ user.biography || '這個人很懶，什麼都沒有留下。' }}</p>
                </div>
                <el-button type="primary" size="small">
                  關注
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空狀態 -->
        <div v-else class="empty-search">
          <el-empty description="輸入關鍵字開始搜尋">
            <template #image>
              <el-icon size="64" class="empty-icon">
                <Search />
              </el-icon>
            </template>
          </el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { postApi } from '@/api/post'
import { userApi } from '@/api/user'
import PostCard from '@/components/post/PostCard.vue'
import { Search, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 響應式資料
const searchQuery = ref('')
const searching = ref(false)
const hasSearched = ref(false)
const activeTab = ref('posts')
const posts = ref([])
const users = ref([])

// 計算屬性
const totalResults = computed(() => {
  return activeTab.value === 'posts' ? posts.value.length : users.value.length
})

// 方法
const handleSearch = async () => {
  if (!searchQuery.value.trim()) return
  
  searching.value = true
  hasSearched.value = true
  
  try {
    // 更新 URL
    router.push({
      name: 'Search',
      query: { q: searchQuery.value.trim() }
    })
    
    if (activeTab.value === 'posts') {
      await searchPosts()
    } else {
      await searchUsers()
    }
  } catch (error) {
    console.error('搜尋失敗:', error)
  } finally {
    searching.value = false
  }
}

const searchPosts = async () => {
  try {
    const response = await postApi.searchPosts({
      keyword: searchQuery.value.trim(),
      page: 0,
      size: 20
    })
    posts.value = response.data.content || []
  } catch (error) {
    console.error('搜尋發文失敗:', error)
    posts.value = []
  }
}

const searchUsers = async () => {
  try {
    const response = await userApi.searchUsers({
      keyword: searchQuery.value.trim(),
      page: 0,
      size: 20
    })
    users.value = response.data.content || []
  } catch (error) {
    console.error('搜尋使用者失敗:', error)
    users.value = []
  }
}

const handleTabChange = () => {
  if (hasSearched.value && searchQuery.value.trim()) {
    handleSearch()
  }
}

const handleDeletePost = (postId) => {
  posts.value = posts.value.filter(post => post.id !== postId)
}

// 生命週期
onMounted(() => {
  // 從 URL 參數獲取搜尋關鍵字
  const query = route.query.q
  if (query) {
    searchQuery.value = query
    handleSearch()
  }
})
</script>

<style scoped>
.search-container {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.search-layout {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.search-form {
  margin-bottom: 32px;
}

.search-results {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--el-box-shadow-light);
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.results-header h2 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.results-count {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.result-filters {
  margin-bottom: 24px;
}

.loading-container {
  space-y: 16px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.users-list {
  space-y: 16px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: var(--el-fill-color-extra-light);
  transition: background-color 0.3s;
}

.user-item:hover {
  background: var(--el-fill-color-light);
}

.user-info {
  flex: 1;
}

.user-name {
  display: block;
  font-weight: 600;
  color: var(--el-text-color-primary);
  text-decoration: none;
  margin-bottom: 4px;
}

.user-name:hover {
  color: var(--el-color-primary);
}

.user-bio {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
}

.empty-search {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  color: var(--el-text-color-placeholder);
}

/* 響應式設計 */
@media (max-width: 768px) {
  .search-layout {
    padding: 16px;
  }
  
  .search-results {
    padding: 20px;
  }
  
  .results-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .user-item {
    padding: 12px;
  }
}
</style>
