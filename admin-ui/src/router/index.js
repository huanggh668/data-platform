import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/desensitize',
    name: 'Desensitize',
    component: () => import('../views/Desensitize.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/desensitize/rules',
    name: 'DesensitizeRules',
    component: () => import('../views/desensitization/RuleManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/desensitize/jobs',
    name: 'DesensitizeJobs',
    component: () => import('../views/desensitization/JobManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/desensitize/datasources',
    name: 'DesensitizeDatasources',
    component: () => import('../views/desensitization/DataSource.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/watermark',
    name: 'Watermark',
    component: () => import('../views/Watermark.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/watermark/factors',
    name: 'WatermarkFactors',
    component: () => import('../views/watermark/FactorManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/watermark/jobs',
    name: 'WatermarkJobs',
    component: () => import('../views/watermark/JobManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/watermark/traceability',
    name: 'WatermarkTraceability',
    component: () => import('../views/watermark/Traceability.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/encryption',
    name: 'Encryption',
    component: () => import('../views/Encryption.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/encryption/algorithms',
    name: 'EncryptionAlgorithms',
    component: () => import('../views/encryption/AlgorithmManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/encryption/jobs',
    name: 'EncryptionJobs',
    component: () => import('../views/encryption/JobManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('../views/Users.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/dashboard')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
