import request from '@/utils/request'

export function getMaterialList(params) {
  return request({ url: '/material/list', method: 'get', params })
}

export function addMaterial(data) {
  return request({ url: '/material', method: 'post', data })
}

export function updateMaterial(data) {
  return request({ url: '/material', method: 'put', data })
}

export function deleteMaterial(id) {
  return request({ url: '/material/' + id, method: 'delete' })
}

export function getCategoryList() {
  return request({ url: '/material/category/list', method: 'get' })
}

export function addCategory(data) {
  return request({ url: '/material/category', method: 'post', data })
}

export function applyPurchase(data) {
  return request({ url: '/material/purchase/apply', method: 'post', data })
}

export function getPurchaseList(params) {
  return request({ url: '/material/purchase/list', method: 'get', params })
}

export function getPurchaseItems(id) {
  return request({ url: '/material/purchase/' + id + '/items', method: 'get' })
}

export function approvePurchase(id, data) {
  return request({ url: '/material/purchase/' + id + '/approve', method: 'put', data })
}

export function getUsageList(params) {
  return request({ url: '/material/usage/list', method: 'get', params })
}

export function useMaterial(data) {
  return request({ url: '/material/usage', method: 'post', data })
}

export function returnMaterial(id) {
  return request({ url: '/material/usage/' + id + '/return', method: 'put' })
}
