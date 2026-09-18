<template>
  <el-container style="height: 100vh">
    <el-aside v-if="showSidebar" :width="isCollapse ? '64px' : '240px'" class="apple-sidebar">
      <div class="sidebar-logo" :class="{ collapse: isCollapse }">
        <span v-if="!isCollapse" class="logo-text">{{ isAdmin ? '后勤物业' : '后勤管理' }}</span>
        <span v-else class="logo-text">后勤</span>
      </div>
      <el-menu :default-active="$route.path" :collapse="isCollapse" background-color="transparent" text-color="rgba(255,255,255,0.7)" active-text-color="#ffffff" router>
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-data"></i>
          <span slot="title">统计分析</span>
        </el-menu-item>
        <el-submenu index="user" v-if="isAdmin">
          <template slot="title"><i class="el-icon-user"></i><span>用户管理</span></template>
          <el-menu-item index="/user">用户列表</el-menu-item>
        </el-submenu>
        <el-submenu index="dorm">
          <template slot="title"><i class="el-icon-office-building"></i><span>宿舍管理</span></template>
          <el-menu-item v-if="isAdmin" index="/dorm/building">宿舍楼管理</el-menu-item>
          <el-menu-item index="/dorm/room">房间管理</el-menu-item>
          <el-menu-item v-if="isAdmin" index="/dorm/allocation">宿舍分配</el-menu-item>
          <el-menu-item index="/dorm/repair">宿舍维修</el-menu-item>
        </el-submenu>
        <el-submenu index="staff-dorm" v-if="!isAdmin">
          <template slot="title"><i class="el-icon-office-building"></i><span>宿舍审核</span></template>
          <el-menu-item index="/staff/dorm-audit">入住/退宿/调宿审核</el-menu-item>
          <el-menu-item index="/staff/dorm-schedule">宿管排班</el-menu-item>
        </el-submenu>
        <el-submenu index="canteen">
          <template slot="title"><i class="el-icon-food"></i><span>餐饮管理</span></template>
          <el-menu-item index="/canteen">食堂管理</el-menu-item>
          <el-menu-item v-if="isAdmin" index="/canteen/dish">菜品管理</el-menu-item>
          <el-menu-item v-if="isAdmin" index="/canteen/consume">消费记录</el-menu-item>
          <el-menu-item v-if="isAdmin" index="/canteen/inspection">卫生检查</el-menu-item>
        </el-submenu>
        <el-submenu index="facility">
          <template slot="title"><i class="el-icon-school"></i><span>设施管理</span></template>
          <el-menu-item index="/facility">设施信息</el-menu-item>
          <el-menu-item index="/facility/repair">设施报修</el-menu-item>
        </el-submenu>
        <el-submenu index="material">
          <template slot="title"><i class="el-icon-box"></i><span>物资库存</span></template>
          <el-menu-item index="/material">物资库存</el-menu-item>
          <el-menu-item v-if="isAdmin" index="/material/purchase">采购管理</el-menu-item>
          <el-menu-item index="/material/usage">领用记录</el-menu-item>
        </el-submenu>
        <el-submenu index="staff-employment" v-if="!isAdmin">
          <template slot="title"><i class="el-icon-s-custom"></i><span>人员聘用</span></template>
          <el-menu-item index="/staff/employment">工作人员</el-menu-item>
        </el-submenu>
        <el-submenu index="staff-housing" v-if="!isAdmin">
          <template slot="title"><i class="el-icon-house"></i><span>住房情况</span></template>
          <el-menu-item index="/staff/housing-view">教师住房</el-menu-item>
        </el-submenu>
        <el-submenu index="staff-cleaning" v-if="!isAdmin">
          <template slot="title"><i class="el-icon-brush"></i><span>卫生管理</span></template>
          <el-menu-item index="/staff/cleaning">区域分配与打扫</el-menu-item>
        </el-submenu>
        <el-submenu index="system" v-if="isAdmin">
          <template slot="title"><i class="el-icon-setting"></i><span>系统管理</span></template>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/log">系统日志</el-menu-item>
        </el-submenu>
        <el-menu-item v-if="isAdmin" index="/feedback-manage">
          <i class="el-icon-chat-dot-round"></i>
          <span slot="title">意见反馈</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="apple-glass-nav apple-topbar" height="48px">
        <div class="topbar-left">
          <i v-if="showSidebar" :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'" class="collapse-toggle" @click="isCollapse = !isCollapse"></i>
          <el-button v-else-if="$route.path !== '/'" type="text" class="back-btn" @click="$router.push('/')">
            <i class="el-icon-arrow-left"></i> 返回
          </el-button>
          <span class="topbar-title">{{ $route.meta.title || '办事大厅' }}</span>
        </div>
        <div class="topbar-right">
          <span class="topbar-user">{{ user ? user.realName || user.username : '' }}</span>
          <el-tag size="mini" :type="roleTagType" class="topbar-role">{{ roleLabel }}</el-tag>
          <el-button type="text" class="topbar-link" @click="$router.push('/profile')">个人信息</el-button>
          <el-button type="text" class="topbar-link topbar-logout" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="apple-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return { isCollapse: false }
  },
  computed: {
    user() { return this.$store.getters.user },
    isAdmin() { return this.$store.getters.isAdmin },
    isStaff() { return this.$store.getters.isStaff },
    isTeacher() { return this.$store.getters.role === 'TEACHER' },
    isStudent() { return this.$store.getters.role === 'STUDENT' },
    showSidebar() { return this.isAdmin || this.isStaff },
    roleLabel() {
      const map = { ADMIN: '管理员', STAFF: '物业人员', TEACHER: '教师', STUDENT: '学生' }
      return map[this.$store.getters.role] || '未知'
    },
    roleTagType() {
      const map = { ADMIN: 'danger', STAFF: 'warning', TEACHER: '', STUDENT: 'success' }
      return map[this.$store.getters.role] || 'info'
    }
  },
  methods: {
    handleLogout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.apple-sidebar {
  background: #1d1d1f;
  transition: width 0.3s ease;
  overflow-y: auto;
  overflow-x: hidden;
}
.apple-sidebar::-webkit-scrollbar { width: 0; }
.sidebar-logo {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.logo-text {
  font-family: var(--apple-font-display);
  font-size: 17px;
  font-weight: 600;
  color: #ffffff;
  letter-spacing: -0.374px;
  white-space: nowrap;
}
.sidebar-logo.collapse .logo-text { font-size: 20px; }
.apple-sidebar .el-menu { border-right: none; padding-top: 8px; }
.apple-sidebar .el-menu-item,
.apple-sidebar .el-submenu__title {
  height: 40px; line-height: 40px; font-size: 14px; letter-spacing: -0.224px;
  border-radius: 8px; margin: 2px 8px; padding-left: 20px !important; transition: background 0.15s ease;
}
.apple-sidebar .el-menu-item:hover,
.apple-sidebar .el-submenu__title:hover { background: rgba(255, 255, 255, 0.08) !important; }
.apple-sidebar .el-menu-item.is-active { background: rgba(0, 113, 227, 0.24) !important; color: #ffffff !important; }
.apple-sidebar .el-submenu .el-menu-item { padding-left: 40px !important; font-size: 13px; }
.apple-topbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 24px; border-bottom: 1px solid rgba(0, 0, 0, 0.06); position: sticky; top: 0; z-index: 100;
}
.topbar-left { display: flex; align-items: center; gap: 12px; }
.collapse-toggle { font-size: 18px; cursor: pointer; color: rgba(0, 0, 0, 0.48); transition: color 0.2s ease; }
.collapse-toggle:hover { color: var(--apple-near-black); }
.back-btn { font-size: 14px; color: var(--apple-link-blue); padding: 0; }
.back-btn:hover { color: var(--apple-blue); }
.back-btn i { font-weight: 600; }
.topbar-title { font-family: var(--apple-font-display); font-size: 17px; font-weight: 600; letter-spacing: -0.374px; color: var(--apple-near-black); }
.topbar-right { display: flex; align-items: center; gap: 12px; }
.topbar-user { font-size: 14px; font-weight: 400; letter-spacing: -0.224px; color: var(--apple-text-primary); }
.topbar-role { font-size: 11px; }
.topbar-link { font-size: 14px; letter-spacing: -0.224px; color: var(--apple-link-blue); padding: 0; }
.topbar-link:hover { color: var(--apple-blue); }
.topbar-logout { color: #ff3b30; }
.topbar-logout:hover { color: #d70015; }
.apple-main { background: var(--apple-light-gray); padding: 28px; min-height: calc(100vh - 48px); }
</style>
