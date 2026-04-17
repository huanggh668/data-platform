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

// Factor APIs
export const factor = {
  list: () => watermarkService.get('/api/v1/watermark/factors'),
  get: (id) => watermarkService.get(`/api/v1/watermark/factors/${id}`),
  create: (data) => watermarkService.post('/api/v1/watermark/factors', data),
  update: (id, data) => watermarkService.put(`/api/v1/watermark/factors/${id}`, data),
  delete: (id) => watermarkService.delete(`/api/v1/watermark/factors/${id}`)
}

// Job APIs
export const job = {
  list: () => watermarkService.get('/api/v1/watermark/jobs'),
  get: (id) => watermarkService.get(`/api/v1/watermark/jobs/${id}`),
  create: (data) => watermarkService.post('/api/v1/watermark/jobs', data),
  update: (id, data) => watermarkService.put(`/api/v1/watermark/jobs/${id}`, data),
  delete: (id) => watermarkService.delete(`/api/v1/watermark/jobs/${id}`),
  execute: (id) => watermarkService.post(`/api/v1/watermark/jobs/${id}/execute`),
  /** 查询任务进度 — 返回 { progress, status, errorMessage } */
  getProgress: (id) => watermarkService.get(`/api/v1/watermark/jobs/${id}/progress`)
}

// TraceRecord APIs
export const traceRecord = {
  list: (params) => watermarkService.get('/api/v1/watermark/trace-records', { params }),
  get: (id) => watermarkService.get(`/api/v1/watermark/trace-records/${id}`)
}

export default {
  factor,
  job,
  traceRecord
}
