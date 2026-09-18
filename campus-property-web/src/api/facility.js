import request from '@/utils/request'

export function getFacilityList(params) {
  return request({ url: '/facility/list', method: 'get', params })
}

export function addFacility(data) {
  return request({ url: '/facility', method: 'post', data })
}

export function updateFacility(data) {
  return request({ url: '/facility', method: 'put', data })
}

export function deleteFacility(id) {
  return request({ url: '/facility/' + id, method: 'delete' })
}

export function getFacilityRepairList(params) {
  return request({ url: '/facility/repair/list', method: 'get', params })
}

export function addFacilityRepair(data) {
  return request({ url: '/facility/repair', method: 'post', data })
}

export function handleFacilityRepair(id, data) {
  return request({ url: '/facility/repair/' + id, method: 'put', data })
}
