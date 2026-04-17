import axios from 'axios'

const desensitizeService = axios.create({
  baseURL: 'http://192.168.70.197:30281',
  timeout: 10000
})

const watermarkService = axios.create({
  baseURL: 'http://192.168.70.197:31494',
  timeout: 10000
})

const encryptionService = axios.create({
  baseURL: 'http://192.168.70.197:31154',
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

// DataSource APIs
export const dataSource = {
  list: () => desensitizeService.get('/api/v1/datasources'),
  get: (id) => desensitizeService.get(`/api/v1/datasources/${id}`),
  create: (data) => desensitizeService.post('/api/v1/datasources', data),
  update: (id, data) => desensitizeService.put(`/api/v1/datasources/${id}`, data),
  delete: (id) => desensitizeService.delete(`/api/v1/datasources/${id}`)
}

// Rule APIs
export const rule = {
  list: () => desensitizeService.get('/api/v1/desensitization/rules'),
  get: (id) => desensitizeService.get(`/api/v1/desensitization/rules/${id}`),
  create: (data) => desensitizeService.post('/api/v1/desensitization/rules', data),
  update: (id, data) => desensitizeService.put(`/api/v1/desensitization/rules/${id}`, data),
  delete: (id) => desensitizeService.delete(`/api/v1/desensitization/rules/${id}`)
}

// Job APIs
export const job = {
  list: () => desensitizeService.get('/api/v1/desensitization/jobs'),
  get: (id) => desensitizeService.get(`/api/v1/desensitization/jobs/${id}`),
  create: (data) => desensitizeService.post('/api/v1/desensitization/jobs', data),
  update: (id, data) => desensitizeService.put(`/api/v1/desensitization/jobs/${id}`, data),
  delete: (id) => desensitizeService.delete(`/api/v1/desensitization/jobs/${id}`),
  execute: (id) => desensitizeService.post(`/api/v1/desensitization/jobs/${id}/execute`),
  /** 查询任务进度 — 返回 { progress, status, errorMessage } */
  getProgress: (id) => desensitizeService.get(`/api/v1/desensitization/jobs/${id}/progress`)
}

export default {
  dataSource,
  rule,
  job
}
