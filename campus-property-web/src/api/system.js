import request from '@/utils/request'

export function getRoleList() {
  return request({ url: '/system/role/list', method: 'get' })
}

export function getLogList(params) {
  return request({ url: '/system/log/list', method: 'get', params })
}
