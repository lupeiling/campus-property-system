import request from '@/utils/request'

export function getOverview() {
  return request({ url: '/statistics/overview', method: 'get' })
}

export function getRepairStatus() {
  return request({ url: '/statistics/repair-status', method: 'get' })
}

export function getRepairMonthly() {
  return request({ url: '/statistics/repair-monthly', method: 'get' })
}

export function getRepairTrend() {
  return request({ url: '/statistics/repair-trend', method: 'get' })
}

export function getCategoryDistribution() {
  return request({ url: '/statistics/category-distribution', method: 'get' })
}

export function getFacilityStatus() {
  return request({ url: '/statistics/facility-status', method: 'get' })
}

export function getMaterialOverview() {
  return request({ url: '/statistics/material-overview', method: 'get' })
}
