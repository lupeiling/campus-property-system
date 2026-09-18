import request from '@/utils/request'

export function getFeedbackList(params) {
  return request({ url: '/feedback/list', method: 'get', params })
}

export function createFeedback(data) {
  return request({ url: '/feedback', method: 'post', data })
}

export function replyFeedback(id, data) {
  return request({ url: '/feedback/' + id + '/reply', method: 'put', data })
}
