import axios from 'axios'

const desensitizeService = axios.create({
  baseURL: 'http://192.168.70.197:8081',
  timeout: 10000
})

const watermarkService = axios.create({
  baseURL: 'http://192.168.70.197:8082',
  timeout: 10000
})

const encryptionService = axios.create({
  baseURL: 'http://192.168.70.197:8083',
  timeout: 10000
})

function setupInterceptors(axiosInstance) {
  axiosInstance.interceptors.request.use(
    config => {
      const token = localStorage.getItem('token')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    error => Promise.reject(error)
  )
  axiosInstance.interceptors.response.use(
    response => response.data,
    error => {
      const message = error.response?.data?.message || error.message || 'Request failed'
      return Promise.reject(new Error(message))
    }
  )
}

setupInterceptors(desensitizeService)
setupInterceptors(watermarkService)
setupInterceptors(encryptionService)

// Algorithm APIs
export const algorithm = {
  list: () => encryptionService.get('/api/v1/encryption/algorithms'),
  get: (id) => encryptionService.get(`/api/v1/encryption/algorithms/${id}`),
  create: (data) => encryptionService.post('/api/v1/encryption/algorithms', data),
  update: (id, data) => encryptionService.put(`/api/v1/encryption/algorithms/${id}`, data),
  delete: (id) => encryptionService.delete(`/api/v1/encryption/algorithms/${id}`)
}

// Job APIs
export const job = {
  list: () => encryptionService.get('/api/v1/encryption/jobs'),
  get: (id) => encryptionService.get(`/api/v1/encryption/jobs/${id}`),
  create: (data) => encryptionService.post('/api/v1/encryption/jobs', data),
  update: (id, data) => encryptionService.put(`/api/v1/encryption/jobs/${id}`, data),
  delete: (id) => encryptionService.delete(`/api/v1/encryption/jobs/${id}`),
  execute: (id) => encryptionService.post(`/api/v1/encryption/jobs/${id}/execute`),
  /** 查询任务进度 — 返回 { progress, status, errorMessage } */
  getProgress: (id) => encryptionService.get(`/api/v1/encryption/jobs/${id}/progress`)
}

export default {
  algorithm,
  job
}
