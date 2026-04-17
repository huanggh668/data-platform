import { createI18n } from 'vue-i18n'
import zh from '../locales/zh.js'
import en from '../locales/en.js'

/**
 * 从 localStorage 读取用户语言偏好，默认中文
 * 切换语言：i18n.global.locale.value = 'en' | 'zh'
 */
const savedLocale = localStorage.getItem('locale') || 'zh'

const i18n = createI18n({
  legacy: false,        // 使用 Composition API 模式
  locale: savedLocale,
  fallbackLocale: 'zh',
  messages: { zh, en },
})

export default i18n
