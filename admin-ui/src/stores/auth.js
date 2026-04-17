import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const loading = ref(false)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'admin')

  async function login(username, password) {
    loading.value = true
    try {
      const response = await api.user.login(username, password)
      token.value = response.token
      // Login response now includes user object directly
      const userObj = response.user || null
      user.value = userObj
      localStorage.setItem('token', response.token)
      if (userObj) {
        localStorage.setItem('user', JSON.stringify(userObj))
      }
      return response
    } finally {
      loading.value = false
    }
  }

  async function fetchProfile() {
    if (!token.value) return null
    try {
      const response = await api.user.getProfile()
      user.value = response.user
      localStorage.setItem('user', JSON.stringify(response.user))
      return response
    } catch (error) {
      logout()
      return null
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  async function register(username, password, email) {
    loading.value = true
    try {
      const response = await api.user.register(username, password, email)
      return response
    } finally {
      loading.value = false
    }
  }

  return {
    token,
    user,
    loading,
    isAuthenticated,
    isAdmin,
    login,
    logout,
    fetchProfile,
    register
  }
})
