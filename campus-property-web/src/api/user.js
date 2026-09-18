import request from '@/utils/request'

export function login(data) {
  return request({ url: '/auth/login', method: 'post', data })
}

export function register(data) {
  return request({ url: '/auth/register', method: 'post', data })
}

export function getCurrentUser() {
  return request({ url: '/auth/current', method: 'get' })
}

export function getUserList(params) {
  return request({ url: '/user/list', method: 'get', params })
}

export function addUser(data) {
  return request({ url: '/user', method: 'post', data })
}

export function updateUser(data) {
  return request({ url: '/user', method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: '/user/' + id, method: 'delete' })
}

export function changePassword(data) {
  return request({ url: '/user/password', method: 'put', data })
}

export function updateProfile(data) {
  return request({ url: '/user/profile', method: 'put', data })
}
