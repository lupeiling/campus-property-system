import request from '@/utils/request'

export function exportDormAllocation(format) {
  return request({
    url: `/export/dorm-allocation/${format}`,
    method: 'get',
    responseType: 'blob'
  })
}

export function exportRepairOrder(format) {
  return request({
    url: `/export/repair-order/${format}`,
    method: 'get',
    responseType: 'blob'
  })
}

export function exportMaterial(format) {
  return request({
    url: `/export/material/${format}`,
    method: 'get',
    responseType: 'blob'
  })
}

export function exportCanteenConsume(format) {
  return request({
    url: `/export/canteen-consume/${format}`,
    method: 'get',
    responseType: 'blob'
  })
}
