import request from '@/utils/request'

export function getBuildingList(params) {
  return request({ url: '/dorm/building/list', method: 'get', params })
}

export function addBuilding(data) {
  return request({ url: '/dorm/building', method: 'post', data })
}

export function updateBuilding(data) {
  return request({ url: '/dorm/building', method: 'put', data })
}

export function deleteBuilding(id) {
  return request({ url: '/dorm/building/' + id, method: 'delete' })
}

export function getRoomList(params) {
  return request({ url: '/dorm/room/list', method: 'get', params })
}

export function addRoom(data) {
  return request({ url: '/dorm/room', method: 'post', data })
}

export function updateRoom(data) {
  return request({ url: '/dorm/room', method: 'put', data })
}

export function deleteRoom(id) {
  return request({ url: '/dorm/room/' + id, method: 'delete' })
}

export function allocateRoom(data) {
  return request({ url: '/dorm/allocation', method: 'post', data })
}

export function checkOut(id) {
  return request({ url: '/dorm/allocation/' + id + '/checkout', method: 'put' })
}

export function getAllocationList(params) {
  return request({ url: '/dorm/allocation/list', method: 'get', params })
}

export function getDormRepairList(params) {
  return request({ url: '/dorm/repair/list', method: 'get', params })
}

export function addDormRepair(data) {
  return request({ url: '/dorm/repair', method: 'post', data })
}

export function handleDormRepair(id, data) {
  return request({ url: '/dorm/repair/' + id, method: 'put', data })
}
