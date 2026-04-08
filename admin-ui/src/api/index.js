import axios from 'axios'

const userService = axios.create({
  baseURL: 'http://192.168.70.197:8080',
  timeout: 10000
})

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

setupInterceptors(userService)
setupInterceptors(desensitizeService)
setupInterceptors(watermarkService)
setupInterceptors(encryptionService)

const user = {
  login: (username, password) => userService.post('/api/v1/users/login', { username, password }),
  getProfile: () => userService.get('/api/v1/users/profile'),
  register: (username, password, email) => userService.post('/api/v1/users/register', { username, password, email }),
  getUsers: () => userService.get('/api/v1/users'),
  toggleUser: (userId, enabled) => userService.put(`/api/v1/users/${userId}`, { enabled })
}

const desensitize = {
  desensitize: (data) => desensitizeService.post('/api/v1/desensitize', data),
  autoDesensitize: (data) => desensitizeService.post('/api/v1/desensitize/auto', data),
  batchDesensitize: (formData) => desensitizeService.post('/api/v1/desensitize/batch', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

const watermark = {
  embedText: (data) => watermarkService.post('/api/v1/watermark/embed/text', data),
  embedImage: (formData) => watermarkService.post('/api/v1/watermark/embed/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  verify: (data) => watermarkService.post('/api/v1/watermark/verify', data)
}

const encryption = {
  encrypt: (data) => encryptionService.post('/api/v1/encrypt', data),
  decrypt: (data) => encryptionService.post('/api/v1/decrypt', data),
  generateKey: (data) => encryptionService.post('/api/v1/keys/generate', data),
  getKeys: () => encryptionService.get('/api/v1/keys'),
  sign: (data) => encryptionService.post('/api/v1/sign', data),
  verify: (data) => encryptionService.post('/api/v1/verify', data)
}

export default {
  user,
  desensitize,
  watermark,
  encryption
}
