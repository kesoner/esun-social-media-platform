<template>
  <div class="notifications-container">
    <div class="container">
      <div class="notifications-layout">
        <!-- 頁面標題 -->
        <div class="page-header">
          <h1 class="page-title">通知</h1>
          <el-button
            v-if="notifications.length > 0"
            @click="markAllAsRead"
            size="small"
          >
            全部標為已讀
          </el-button>
        </div>

        <!-- 通知篩選 -->
        <div class="notification-filters">
          <el-radio-group v-model="activeFilter" @change="handleFilterChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="unread">未讀</el-radio-button>
            <el-radio-button label="comments">留言</el-radio-button>
            <el-radio-button label="mentions">提及</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 通知列表 -->
        <div class="notifications-list">
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
            <el-skeleton :rows="3" animated />
            <el-skeleton :rows="3" animated />
          </div>

          <div v-else-if="filteredNotifications.length === 0" class="empty-state">
            <el-empty description="沒有通知">
              <template #image>
                <el-icon size="64" class="empty-icon">
                  <Bell />
                </el-icon>
              </template>
            </el-empty>
          </div>

          <div v-else>
            <div
              v-for="notification in filteredNotifications"
              :key="notification.id"
              class="notification-item"
              :class="{ 'unread': !notification.read }"
              @click="handleNotificationClick(notification)"
            >
              <div class="notification-avatar">
                <el-avatar :size="40" :src="notification.sender?.avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
              </div>

              <div class="notification-content">
                <div class="notification-text">
                  <span class="sender-name">{{ notification.sender?.username }}</span>
                  <span class="notification-message">{{ getNotificationMessage(notification) }}</span>
                </div>
                <div class="notification-time">
                  {{ formatTime(notification.createdAt) }}
                </div>
              </div>

              <div class="notification-actions">
                <el-button
                  v-if="!notification.read"
                  @click.stop="markAsRead(notification.id)"
                  circle
                  size="small"
                  class="read-button"
                >
                  <el-icon><Check /></el-icon>
                </el-button>
                
                <el-button
                  @click.stop="deleteNotification(notification.id)"
                  circle
                  size="small"
                  class="delete-button"
                >
                  <el-icon><Close /></el-icon>
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-tw'
import {
  Bell,
  User,
  Check,
  Close
} from '@element-plus/icons-vue'

dayjs.extend(relativeTime)
dayjs.locale('zh-tw')

const router = useRouter()

// 響應式資料
const loading = ref(false)
const activeFilter = ref('all')
const notifications = ref([
  // 模擬通知資料
  {
    id: 1,
    type: 'comment',
    sender: { id: 2, username: 'alice', avatar: '' },
    message: '在您的發文中留言',
    postId: 1,
    read: false,
    createdAt: new Date(Date.now() - 5 * 60 * 1000) // 5分鐘前
  },
  {
    id: 2,
    type: 'mention',
    sender: { id: 3, username: 'bob', avatar: '' },
    message: '在發文中提及了您',
    postId: 2,
    read: false,
    createdAt: new Date(Date.now() - 30 * 60 * 1000) // 30分鐘前
  },
  {
    id: 3,
    type: 'comment',
    sender: { id: 4, username: 'charlie', avatar: '' },
    message: '在您的發文中留言',
    postId: 3,
    read: true,
    createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000) // 2小時前
  }
])

// 計算屬性
const filteredNotifications = computed(() => {
  let filtered = notifications.value

  switch (activeFilter.value) {
    case 'unread':
      filtered = filtered.filter(n => !n.read)
      break
    case 'comments':
      filtered = filtered.filter(n => n.type === 'comment')
      break
    case 'mentions':
      filtered = filtered.filter(n => n.type === 'mention')
      break
  }

  return filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

// 方法
const formatTime = (time) => {
  return dayjs(time).fromNow()
}

const getNotificationMessage = (notification) => {
  switch (notification.type) {
    case 'comment':
      return '在您的發文中留言'
    case 'mention':
      return '在發文中提及了您'
    case 'like':
      return '喜歡了您的發文'
    case 'follow':
      return '開始關注您'
    default:
      return notification.message || '有新的互動'
  }
}

const handleFilterChange = () => {
  // 篩選變更時的處理邏輯
}

const handleNotificationClick = (notification) => {
  // 標記為已讀
  if (!notification.read) {
    markAsRead(notification.id)
  }

  // 跳轉到相關頁面
  if (notification.postId) {
    router.push(`/post/${notification.postId}`)
  } else if (notification.sender) {
    router.push(`/user/${notification.sender.id}`)
  }
}

const markAsRead = (notificationId) => {
  const notification = notifications.value.find(n => n.id === notificationId)
  if (notification) {
    notification.read = true
    ElMessage.success('已標記為已讀')
  }
}

const markAllAsRead = () => {
  notifications.value.forEach(notification => {
    notification.read = true
  })
  ElMessage.success('所有通知已標記為已讀')
}

const deleteNotification = (notificationId) => {
  const index = notifications.value.findIndex(n => n.id === notificationId)
  if (index > -1) {
    notifications.value.splice(index, 1)
    ElMessage.success('通知已刪除')
  }
}

// 生命週期
onMounted(() => {
  // TODO: 從 API 載入通知
  loading.value = false
})
</script>

<style scoped>
.notifications-container {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.notifications-layout {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.notification-filters {
  margin-bottom: 24px;
}

.notifications-list {
  background: var(--el-bg-color);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--el-box-shadow-light);
}

.loading-container {
  padding: 20px;
  space-y: 16px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  color: var(--el-text-color-placeholder);
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-border-color-extra-light);
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: var(--el-fill-color-extra-light);
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item.unread {
  background-color: var(--el-color-primary-light-9);
}

.notification-item.unread::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background-color: var(--el-color-primary);
}

.notification-avatar {
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-text {
  margin-bottom: 4px;
  line-height: 1.4;
}

.sender-name {
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-right: 4px;
}

.notification-message {
  color: var(--el-text-color-regular);
}

.notification-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.notification-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.notification-item:hover .notification-actions {
  opacity: 1;
}

.read-button {
  color: var(--el-color-success);
}

.delete-button {
  color: var(--el-color-danger);
}

/* 響應式設計 */
@media (max-width: 768px) {
  .notifications-layout {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .notification-item {
    padding: 12px 16px;
  }
  
  .notification-actions {
    opacity: 1;
  }
}
</style>
