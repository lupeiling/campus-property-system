import request from '@/utils/request'

export function getFeeList(params) {
  return request({ url: '/fee/list', method: 'get', params })
}

export function addFee(data) {
  return request({ url: '/fee', method: 'post', data })
}

export function payFee(id) {
  return request({ url: `/fee/${id}/pay`, method: 'put' })
}

export function getSemesters() {
  return request({ url: '/fee/semesters', method: 'get' })
}

export function getEmploymentList(params) {
  return request({ url: '/staff/employment/list', method: 'get', params })
}

export function addEmployment(data) {
  return request({ url: '/staff/employment', method: 'post', data })
}

export function updateEmployment(data) {
  return request({ url: '/staff/employment', method: 'put', data })
}

export function deleteEmployment(id) {
  return request({ url: `/staff/employment/${id}`, method: 'delete' })
}

export function getTeacherHousingBenefit() {
  return request({ url: '/teacher/housing/benefit', method: 'get' })
}

export function getAllHousingBenefits() {
  return request({ url: '/teacher/housing/benefit/list', method: 'get' })
}

export function getTeacherHousingList(params) {
  return request({ url: '/teacher/housing/list', method: 'get', params })
}

export function addTeacherHousing(data) {
  return request({ url: '/teacher/housing', method: 'post', data })
}

export function updateTeacherHousing(data) {
  return request({ url: '/teacher/housing', method: 'put', data })
}

export function deleteTeacherHousing(id) {
  return request({ url: `/teacher/housing/${id}`, method: 'delete' })
}

export function getDormAuditList(params) {
  return request({ url: '/dorm/audit/list', method: 'get', params })
}

export function addDormAudit(data) {
  return request({ url: '/dorm/audit', method: 'post', data })
}

export function approveDormAudit(id, data) {
  return request({ url: `/dorm/audit/${id}/approve`, method: 'put', data })
}

export function rejectDormAudit(id, data) {
  return request({ url: `/dorm/audit/${id}/reject`, method: 'put', data })
}

export function getDormScheduleList(params) {
  return request({ url: '/dorm/schedule/list', method: 'get', params })
}

export function addDormSchedule(data) {
  return request({ url: '/dorm/schedule', method: 'post', data })
}

export function updateDormSchedule(data) {
  return request({ url: '/dorm/schedule', method: 'put', data })
}

export function deleteDormSchedule(id) {
  return request({ url: `/dorm/schedule/${id}`, method: 'delete' })
}

export function getCleaningAssignmentList(params) {
  return request({ url: '/cleaning/assignment/list', method: 'get', params })
}

export function addCleaningAssignment(data) {
  return request({ url: '/cleaning/assignment', method: 'post', data })
}

export function updateCleaningAssignment(data) {
  return request({ url: '/cleaning/assignment', method: 'put', data })
}

export function deleteCleaningAssignment(id) {
  return request({ url: `/cleaning/assignment/${id}`, method: 'delete' })
}

export function getCleaningRecordList(params) {
  return request({ url: '/cleaning/record/list', method: 'get', params })
}

export function addCleaningRecord(data) {
  return request({ url: '/cleaning/record', method: 'post', data })
}

export function updateCleaningRecord(data) {
  return request({ url: '/cleaning/record', method: 'put', data })
}

export function deleteCleaningRecord(id) {
  return request({ url: `/cleaning/record/${id}`, method: 'delete' })
}
