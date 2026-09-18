import request from '@/utils/request'

export function getRepairList(params) {
  return request({ url: '/repair/list', method: 'get', params })
}

export function getRepairById(id) {
  return request({ url: '/repair/' + id, method: 'get' })
}

export function submitRepair(data) {
  return request({ url: '/repair', method: 'post', data })
}

export function approveRepair(id, data) {
  return request({ url: '/repair/' + id + '/approve', method: 'put', data })
}

export function assignRepair(id, data) {
  return request({ url: '/repair/' + id + '/assign', method: 'put', data })
}

export function startRepair(id) {
  return request({ url: '/repair/' + id + '/start', method: 'put' })
}

export function completeRepair(id, data) {
  return request({ url: '/repair/' + id + '/complete', method: 'put', data })
}

export function addEvaluation(data) {
  return request({ url: '/repair/evaluation', method: 'post', data })
}

export function getEvaluation(orderId) {
  return request({ url: '/repair/evaluation/' + orderId, method: 'get' })
}
