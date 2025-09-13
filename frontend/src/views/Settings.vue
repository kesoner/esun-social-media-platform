<template>
  <div class="settings-container">
    <div class="container">
      <div class="settings-layout">
        <!-- 頁面標題 -->
        <div class="page-header">
          <h1 class="page-title">設定</h1>
        </div>

        <!-- 設定選項 -->
        <div class="settings-content">
          <!-- 外觀設定 -->
          <div class="settings-section">
            <h2 class="section-title">外觀</h2>
            <div class="settings-item">
              <div class="item-info">
                <h3 class="item-title">主題模式</h3>
                <p class="item-description">選擇您偏好的介面主題</p>
              </div>
              <div class="item-control">
                <el-radio-group v-model="themeMode" @change="handleThemeChange">
                  <el-radio label="light">淺色</el-radio>
                  <el-radio label="dark">深色</el-radio>
                  <el-radio label="auto">跟隨系統</el-radio>
                </el-radio-group>
              </div>
            </div>
          </div>

          <!-- 通知設定 -->
          <div class="settings-section">
            <h2 class="section-title">通知</h2>
            <div class="settings-item">
              <div class="item-info">
                <h3 class="item-title">推送通知</h3>
                <p class="item-description">接收新留言和互動的通知</p>
              </div>
              <div class="item-control">
                <el-switch v-model="pushNotifications" />
              </div>
            </div>
            
            <div class="settings-item">
              <div class="item-info">
                <h3 class="item-title">電子郵件通知</h3>
                <p class="item-description">透過電子郵件接收重要通知</p>
              </div>
              <div class="item-control">
                <el-switch v-model="emailNotifications" />
              </div>
            </div>
          </div>

          <!-- 隱私設定 -->
          <div class="settings-section">
            <h2 class="section-title">隱私</h2>
            <div class="settings-item">
              <div class="item-info">
                <h3 class="item-title">個人資料可見性</h3>
                <p class="item-description">控制誰可以查看您的個人資料</p>
              </div>
              <div class="item-control">
                <el-select v-model="profileVisibility" style="width: 120px">
                  <el-option label="公開" value="public" />
                  <el-option label="僅關注者" value="followers" />
                  <el-option label="私人" value="private" />
                </el-select>
              </div>
            </div>
            
            <div class="settings-item">
              <div class="item-info">
                <h3 class="item-title">搜尋可見性</h3>
                <p class="item-description">允許其他人透過搜尋找到您</p>
              </div>
              <div class="item-control">
                <el-switch v-model="searchable" />
              </div>
            </div>
          </div>

          <!-- 帳號設定 -->
          <div class="settings-section">
            <h2 class="section-title">帳號</h2>
            <div class="settings-item">
              <div class="item-info">
                <h3 class="item-title">變更密碼</h3>
                <p class="item-description">更新您的登入密碼</p>
              </div>
              <div class="item-control">
                <el-button @click="showChangePasswordDialog = true">
                  變更密碼
                </el-button>
              </div>
            </div>
            
            <div class="settings-item">
              <div class="item-info">
                <h3 class="item-title">下載資料</h3>
                <p class="item-description">下載您的帳號資料副本</p>
              </div>
              <div class="item-control">
                <el-button @click="downloadData">
                  下載資料
                </el-button>
              </div>
            </div>
            
            <div class="settings-item danger">
              <div class="item-info">
                <h3 class="item-title">刪除帳號</h3>
                <p class="item-description">永久刪除您的帳號和所有資料</p>
              </div>
              <div class="item-control">
                <el-button type="danger" @click="showDeleteAccountDialog = true">
                  刪除帳號
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 變更密碼對話框 -->
    <el-dialog
      v-model="showChangePasswordDialog"
      title="變更密碼"
      width="400px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="目前密碼" prop="currentPassword">
          <el-input
            v-model="passwordForm.currentPassword"
            type="password"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="新密碼" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="確認密碼" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showChangePasswordDialog = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleChangePassword"
          :loading="changingPassword"
        >
          確認變更
        </el-button>
      </template>
    </el-dialog>

    <!-- 刪除帳號確認對話框 -->
    <el-dialog
      v-model="showDeleteAccountDialog"
      title="刪除帳號"
      width="400px"
    >
      <div class="delete-warning">
        <el-alert
          title="警告"
          type="error"
          description="此操作無法復原，您的所有資料將被永久刪除。"
          show-icon
          :closable="false"
        />
        
        <p class="delete-confirmation">
          請輸入您的使用者名稱 <strong>{{ userInfo.username }}</strong> 以確認刪除：
        </p>
        
        <el-input
          v-model="deleteConfirmation"
          placeholder="輸入使用者名稱"
        />
      </div>
      
      <template #footer>
        <el-button @click="showDeleteAccountDialog = false">取消</el-button>
        <el-button
          type="danger"
          @click="handleDeleteAccount"
          :disabled="deleteConfirmation !== userInfo.username"
          :loading="deletingAccount"
        >
          確認刪除
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { ElMessage, ElMessageBox } from 'element-plus'

const authStore = useAuthStore()
const appStore = useAppStore()

// 響應式資料
const themeMode = ref(appStore.theme)
const pushNotifications = ref(true)
const emailNotifications = ref(true)
const profileVisibility = ref('public')
const searchable = ref(true)
const showChangePasswordDialog = ref(false)
const showDeleteAccountDialog = ref(false)
const changingPassword = ref(false)
const deletingAccount = ref(false)
const deleteConfirmation = ref('')
const passwordFormRef = ref()

// 密碼表單
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密碼驗證規則
const passwordRules = {
  currentPassword: [
    { required: true, message: '請輸入目前密碼', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '請輸入新密碼', trigger: 'blur' },
    { min: 6, message: '密碼長度至少 6 個字元', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '請確認新密碼', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('兩次輸入的密碼不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 計算屬性
const userInfo = computed(() => authStore.userInfo)

// 方法
const handleThemeChange = (value) => {
  appStore.setTheme(value)
  ElMessage.success('主題已更新')
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true
    
    // TODO: 呼叫 API 變更密碼
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模擬 API 呼叫
    
    ElMessage.success('密碼變更成功')
    showChangePasswordDialog.value = false
    
    // 重置表單
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('變更密碼失敗:', error)
  } finally {
    changingPassword.value = false
  }
}

const downloadData = async () => {
  try {
    ElMessage.info('正在準備您的資料，請稍候...')
    // TODO: 實作資料下載功能
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('資料下載完成')
  } catch (error) {
    console.error('下載資料失敗:', error)
    ElMessage.error('下載失敗，請重試')
  }
}

const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '您確定要刪除帳號嗎？此操作無法復原。',
      '最後確認',
      {
        confirmButtonText: '確認刪除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    deletingAccount.value = true
    
    // TODO: 呼叫 API 刪除帳號
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('帳號已刪除')
    authStore.logout()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('刪除帳號失敗:', error)
      ElMessage.error('刪除失敗，請重試')
    }
  } finally {
    deletingAccount.value = false
    showDeleteAccountDialog.value = false
    deleteConfirmation.value = ''
  }
}
</script>

<style scoped>
.settings-container {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.settings-layout {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.settings-content {
  space-y: 32px;
}

.settings-section {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--el-box-shadow-light);
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px;
  color: var(--el-text-color-primary);
}

.settings-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-extra-light);
}

.settings-item:last-child {
  border-bottom: none;
}

.settings-item.danger .item-title {
  color: var(--el-color-danger);
}

.item-info {
  flex: 1;
}

.item-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 4px;
  color: var(--el-text-color-primary);
}

.item-description {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 0;
  line-height: 1.4;
}

.item-control {
  flex-shrink: 0;
  margin-left: 20px;
}

.delete-warning {
  space-y: 16px;
}

.delete-confirmation {
  margin: 16px 0;
  line-height: 1.6;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .settings-layout {
    padding: 16px;
  }
  
  .settings-section {
    padding: 20px;
  }
  
  .settings-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .item-control {
    margin-left: 0;
    width: 100%;
  }
}
</style>
