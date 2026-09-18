import request from '@/utils/request'

export function getCanteenList(params) {
  return request({ url: '/canteen/list', method: 'get', params })
}

export function addCanteen(data) {
  return request({ url: '/canteen', method: 'post', data })
}

export function updateCanteen(data) {
  return request({ url: '/canteen', method: 'put', data })
}

export function deleteCanteen(id) {
  return request({ url: '/canteen/' + id, method: 'delete' })
}

export function getDishList(params) {
  return request({ url: '/canteen/dish/list', method: 'get', params })
}

export function addDish(data) {
  return request({ url: '/canteen/dish', method: 'post', data })
}

export function updateDish(data) {
  return request({ url: '/canteen/dish', method: 'put', data })
}

export function deleteDish(id) {
  return request({ url: '/canteen/dish/' + id, method: 'delete' })
}

export function getConsumeList(params) {
  return request({ url: '/canteen/consume/list', method: 'get', params })
}

export function addConsume(data) {
  return request({ url: '/canteen/consume', method: 'post', data })
}

export function getInspectionList(params) {
  return request({ url: '/canteen/inspection/list', method: 'get', params })
}

export function addInspection(data) {
  return request({ url: '/canteen/inspection', method: 'post', data })
}
