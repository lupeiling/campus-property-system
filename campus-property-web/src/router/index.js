import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { title: '注册' } },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/ServiceHall.vue'), meta: { title: '办事大厅' } },
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '统计分析', role: 'STAFF' } },
      { path: 'user', name: 'UserManage', component: () => import('@/views/user/UserManage.vue'), meta: { title: '用户管理', role: 'ADMIN' } },
      { path: 'dorm/building', name: 'DormBuilding', component: () => import('@/views/dorm/BuildingManage.vue'), meta: { title: '宿舍楼管理', role: 'STAFF' } },
      { path: 'dorm/room', name: 'DormRoom', component: () => import('@/views/dorm/RoomManage.vue'), meta: { title: '房间管理', role: 'STAFF' } },
      { path: 'dorm/allocation', name: 'DormAllocation', component: () => import('@/views/dorm/AllocationManage.vue'), meta: { title: '宿舍分配', role: 'STAFF' } },
      { path: 'dorm/repair', name: 'DormRepair', component: () => import('@/views/dorm/RepairManage.vue'), meta: { title: '宿舍报修' } },
      { path: 'dorm/transfer', name: 'DormTransfer', component: () => import('@/views/dorm/TransferApply.vue'), meta: { title: '申请调宿', role: 'STUDENT' } },
      { path: 'dorm/checkout', name: 'DormCheckout', component: () => import('@/views/dorm/CheckoutApply.vue'), meta: { title: '退宿申请', role: 'STUDENT' } },
      { path: 'canteen', name: 'CanteenManage', component: () => import('@/views/canteen/CanteenManage.vue'), meta: { title: '食堂管理', role: 'STAFF' } },
      { path: 'canteen/dish', name: 'DishManage', component: () => import('@/views/canteen/DishManage.vue'), meta: { title: '菜品管理', role: 'STAFF' } },
      { path: 'canteen/consume', name: 'ConsumeRecord', component: () => import('@/views/canteen/ConsumeRecord.vue'), meta: { title: '消费记录' } },
      { path: 'canteen/inspection', name: 'InspectionManage', component: () => import('@/views/canteen/InspectionManage.vue'), meta: { title: '卫生检查', role: 'STAFF' } },
      { path: 'facility', name: 'FacilityManage', component: () => import('@/views/facility/FacilityManage.vue'), meta: { title: '设施管理', role: 'STAFF' } },
      { path: 'facility/status', name: 'FacilityStatus', component: () => import('@/views/facility/FacilityStatus.vue'), meta: { title: '设施状态' } },
      { path: 'facility/repair', name: 'FacilityRepair', component: () => import('@/views/facility/FacilityRepair.vue'), meta: { title: '设施报修' } },
      { path: 'material', name: 'MaterialManage', component: () => import('@/views/material/MaterialManage.vue'), meta: { title: '物资管理', role: 'STAFF' } },
      { path: 'material/purchase', name: 'PurchaseManage', component: () => import('@/views/material/PurchaseManage.vue'), meta: { title: '采购管理', role: 'STAFF' } },
      { path: 'material/usage', name: 'UsageManage', component: () => import('@/views/material/UsageManage.vue'), meta: { title: '领用记录', role: 'STAFF' } },
      { path: 'repair-record', name: 'RepairRecord', component: () => import('@/views/repair/RepairRecord.vue'), meta: { title: '报修记录' } },
      { path: 'feedback', name: 'Feedback', component: () => import('@/views/Feedback.vue'), meta: { title: '我有话说' } },
      { path: 'feedback-manage', name: 'FeedbackManage', component: () => import('@/views/system/FeedbackManage.vue'), meta: { title: '意见反馈', role: 'STAFF' } },
      { path: 'system/role', name: 'RoleManage', component: () => import('@/views/system/RoleManage.vue'), meta: { title: '角色管理', role: 'ADMIN' } },
      { path: 'system/log', name: 'LogManage', component: () => import('@/views/system/LogManage.vue'), meta: { title: '系统日志', role: 'ADMIN' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人信息' } },
      { path: 'fee', name: 'StudentFee', component: () => import('@/views/fee/StudentFeeManage.vue'), meta: { title: '费用事项', role: 'STUDENT' } },
      { path: 'teacher/housing-benefit', name: 'HousingBenefit', component: () => import('@/views/teacher/HousingBenefit.vue'), meta: { title: '住房待遇', role: 'TEACHER' } },
      { path: 'teacher/housing-fee', name: 'HousingFee', component: () => import('@/views/teacher/HousingFee.vue'), meta: { title: '住房费用', role: 'TEACHER' } },
      { path: 'staff/employment', name: 'StaffEmployment', component: () => import('@/views/staff/EmploymentManage.vue'), meta: { title: '人员聘用', role: 'STAFF' } },
      { path: 'staff/housing-view', name: 'StaffHousingView', component: () => import('@/views/staff/HousingView.vue'), meta: { title: '住房情况', role: 'STAFF' } },
      { path: 'staff/dorm-audit', name: 'DormAudit', component: () => import('@/views/staff/DormAuditManage.vue'), meta: { title: '宿舍审核', role: 'STAFF' } },
      { path: 'staff/dorm-schedule', name: 'DormSchedule', component: () => import('@/views/staff/DormScheduleManage.vue'), meta: { title: '宿管排班', role: 'STAFF' } },
      { path: 'staff/cleaning', name: 'CleaningManage', component: () => import('@/views/staff/CleaningManage.vue'), meta: { title: '卫生管理', role: 'STAFF' } }
    ]
  }
]

const router = new VueRouter({ routes })

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title || '高校后勤物业管理系统') + ' - 高校后勤物业管理系统'
  const token = store.getters.token
  if (to.path === '/login' || to.path === '/register') { next(); return }
  if (!token) { next('/login'); return }
  const role = store.getters.role
  if (to.path === '/' && (role === 'ADMIN' || role === 'STAFF')) { next('/dashboard'); return }
  const requiredRole = to.meta.role
  if (requiredRole === 'ADMIN' && role !== 'ADMIN') { next('/') }
  else if (requiredRole === 'STAFF' && role !== 'ADMIN' && role !== 'STAFF') { next('/') }
  else if (requiredRole === 'TEACHER' && role !== 'TEACHER') { next('/') }
  else if (requiredRole === 'STUDENT' && role !== 'STUDENT') { next('/') }
  else { next() }
})

export default router
