import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use(
  config => {
    const token = store.getters.token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    if (response.config.responseType === 'blob') {
      return response.data
    }
    const res = response.data
    if (res.code !== 200) {
      Message.error(res.message || '请求失败')
      if (res.code === 401) {
        store.dispatch('logout')
        if (router.currentRoute.path !== '/login') {
          router.push('/login')
        }
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        Message.error('登录已过期，请重新登录')
        store.dispatch('logout')
        if (router.currentRoute.path !== '/login') {
          router.push('/login')
        }
      } else if (status === 403) {
        Message.error('权限不足，无法访问')
      } else {
        Message.error(error.response.data?.message || error.message || '网络错误')
      }
    } else {
      Message.error('网络连接异常，请检查网络')
    }
    return Promise.reject(error)
  }
)

export default service
