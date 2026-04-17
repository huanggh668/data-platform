<template>
  <div v-if="isAuthenticated && route.path !== '/login'" class="app-container">
    <aside class="sidebar">
      <div class="logo">
        <h1>{{ $t('login.title') }}</h1>
      </div>
      <el-menu
        :default-active="route.path"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        unique-opened
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>{{ $t('nav.dashboard') }}</span>
        </el-menu-item>

        <el-sub-menu index="/desensitize">
          <template #title>
            <el-icon><Hide /></el-icon>
            <span>{{ $t('nav.desensitization') }}</span>
          </template>
          <el-menu-item index="/desensitize">概览</el-menu-item>
          <el-menu-item index="/desensitize/rules">规则管理</el-menu-item>
          <el-menu-item index="/desensitize/jobs">任务管理</el-menu-item>
          <el-menu-item index="/desensitize/datasources">数据源</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/watermark">
          <template #title>
            <el-icon><Picture /></el-icon>
            <span>{{ $t('nav.watermark') }}</span>
          </template>
          <el-menu-item index="/watermark">概览</el-menu-item>
          <el-menu-item index="/watermark/factors">水印因子</el-menu-item>
          <el-menu-item index="/watermark/jobs">任务管理</el-menu-item>
          <el-menu-item index="/watermark/traceability">溯源记录</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/encryption">
          <template #title>
            <el-icon><Lock /></el-icon>
            <span>{{ $t('nav.encryption') }}</span>
          </template>
          <el-menu-item index="/encryption">概览</el-menu-item>
          <el-menu-item index="/encryption/algorithms">算法管理</el-menu-item>
          <el-menu-item index="/encryption/jobs">任务管理</el-menu-item>
        </el-sub-menu>

        <el-menu-item v-if="isAdmin" index="/users">
          <el-icon><User /></el-icon>
          <span>{{ $t('nav.users') }}</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <div class="main-content">
      <header class="header">
        <div class="header-left">
          <h3>{{ pageTitle }}</h3>
        </div>
        <div class="header-right">
          <!-- 语言切换按钮 -->
          <el-button
            size="small"
            text
            @click="toggleLocale"
            class="lang-btn"
            :title="$t('language.switch')"
          >
            {{ currentLocale === 'zh' ? 'EN' : '中文' }}
          </el-button>

          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ user?.username || 'User' }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">{{ $t('common.confirm') === '确认' ? '退出登录' : 'Logout' }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
  <router-view v-else />
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { useI18n } from 'vue-i18n'
import { HomeFilled, Hide, Picture, Lock, User, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t, locale } = useI18n()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const isAdmin = computed(() => authStore.isAdmin)
const user = computed(() => authStore.user)
const currentLocale = ref(locale.value)

const pageTitle = computed(() => {
  const titles = {
    '/dashboard': t('nav.dashboard'),
    '/desensitize': t('nav.desensitization'),
    '/watermark': t('nav.watermark'),
    '/encryption': t('nav.encryption'),
    '/users': t('nav.users'),
  }
  return titles[route.path] || t('login.title')
})

/**
 * 切换中英文 — 状态保存到 localStorage 供下次启动使用
 */
function toggleLocale() {
  const next = locale.value === 'zh' ? 'en' : 'zh'
  locale.value = next
  currentLocale.value = next
  localStorage.setItem('locale', next)
}

function handleCommand(command) {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.app-container {
  display: flex;
  width: 100%;
  height: 100vh;
}

.sidebar {
  width: 220px;
  background-color: #304156;
  flex-shrink: 0;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
}

.logo h1 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lang-btn {
  font-size: 13px;
  color: #606266;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 4px 10px;
  cursor: pointer;
}

.lang-btn:hover {
  color: #409eff;
  border-color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.content {
  flex: 1;
  overflow-y: auto;
  background-color: #f0f2f5;
}
</style>
