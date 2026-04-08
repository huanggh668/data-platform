<template>
  <div v-if="isAuthenticated && route.path !== '/login'" class="app-container">
    <aside class="sidebar">
      <div class="logo">
        <h1>Data Platform</h1>
      </div>
      <el-menu
        :default-active="route.path"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="/desensitize">
          <el-icon><Hide /></el-icon>
          <span>Desensitization</span>
        </el-menu-item>
        <el-menu-item index="/watermark">
          <el-icon><Picture /></el-icon>
          <span>Watermark</span>
        </el-menu-item>
        <el-menu-item index="/encryption">
          <el-icon><Lock /></el-icon>
          <span>Encryption</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/users">
          <el-icon><User /></el-icon>
          <span>Users</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <div class="main-content">
      <header class="header">
        <div class="header-left">
          <h3>{{ pageTitle }}</h3>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ user?.username || 'User' }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">Logout</el-dropdown-item>
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { HomeFilled, Hide, Picture, Lock, User, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const isAdmin = computed(() => authStore.isAdmin)
const user = computed(() => authStore.user)

const pageTitle = computed(() => {
  const titles = {
    '/dashboard': 'Dashboard',
    '/desensitize': 'Desensitization Management',
    '/watermark': 'Watermark Management',
    '/encryption': 'Encryption Management',
    '/users': 'User Management'
  }
  return titles[route.path] || 'Data Platform'
})

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
