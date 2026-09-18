<template>
  <div class="service-hall">
    <div class="hall-header">
      <div class="hall-greeting">
        <h1 class="hall-title">{{ greeting }}，{{ userName }}</h1>
        <p class="hall-subtitle">欢迎使用高校后勤物业服务平台，请选择您需要的服务</p>
      </div>
    </div>

    <div class="hall-section" v-if="isStudent">
      <div class="section-title">宿舍服务</div>
      <div class="card-grid">
        <div class="service-card" @click="$router.push('/dorm/repair')">
          <div class="card-icon" style="background:rgba(0,113,227,0.1)"><i class="el-icon-s-order" style="color:#0071e3"></i></div>
          <div class="card-info"><div class="card-name">宿舍报修</div><div class="card-desc">提交宿舍维修申请</div></div>
        </div>
        <div class="service-card" @click="$router.push('/dorm/transfer')">
          <div class="card-icon" style="background:rgba(255,149,0,0.1)"><i class="el-icon-sort" style="color:#ff9500"></i></div>
          <div class="card-info"><div class="card-name">申请调宿</div><div class="card-desc">申请更换宿舍房间</div></div>
        </div>
        <div class="service-card" @click="$router.push('/dorm/checkout')">
          <div class="card-icon" style="background:rgba(255,59,48,0.1)"><i class="el-icon-close" style="color:#ff3b30"></i></div>
          <div class="card-info"><div class="card-name">退宿申请</div><div class="card-desc">申请退宿离校</div></div>
        </div>
      </div>
    </div>

    <div class="hall-section" v-if="isStudent">
      <div class="section-title">费用缴纳</div>
      <div class="card-grid">
        <div class="service-card" @click="$router.push('/fee')">
          <div class="card-icon" style="background:rgba(255,149,0,0.1)"><i class="el-icon-wallet" style="color:#ff9500"></i></div>
          <div class="card-info"><div class="card-name">费用事项</div><div class="card-desc">查看费用清单与缴纳</div></div>
        </div>
      </div>
    </div>

    <div class="hall-section" v-if="isTeacher">
      <div class="section-title">住房服务</div>
      <div class="card-grid">
        <div class="service-card" @click="$router.push('/dorm/repair')">
          <div class="card-icon" style="background:rgba(0,113,227,0.1)"><i class="el-icon-s-order" style="color:#0071e3"></i></div>
          <div class="card-info"><div class="card-name">住房报修</div><div class="card-desc">提交住房维修申请</div></div>
        </div>
        <div class="service-card" @click="$router.push('/teacher/housing-benefit')">
          <div class="card-icon" style="background:rgba(52,199,89,0.1)"><i class="el-icon-house" style="color:#34c759"></i></div>
          <div class="card-info"><div class="card-name">住房待遇</div><div class="card-desc">按职称等级查看住房待遇</div></div>
        </div>
        <div class="service-card" @click="$router.push('/teacher/housing-fee')">
          <div class="card-icon" style="background:rgba(255,149,0,0.1)"><i class="el-icon-coin" style="color:#ff9500"></i></div>
          <div class="card-info"><div class="card-name">住房费用</div><div class="card-desc">查看租金与物业费明细</div></div>
        </div>
      </div>
    </div>

    <div class="hall-section" v-if="!isStaff">
      <div class="section-title">公共设施</div>
      <div class="card-grid">
        <div class="service-card" @click="$router.push('/facility/status')">
          <div class="card-icon" style="background:rgba(52,199,89,0.1)"><i class="el-icon-view" style="color:#34c759"></i></div>
          <div class="card-info"><div class="card-name">设施状态</div><div class="card-desc">查看校园设施运行状态</div></div>
        </div>
        <div class="service-card" @click="$router.push('/facility/repair')">
          <div class="card-icon" style="background:rgba(175,82,222,0.1)"><i class="el-icon-s-tools" style="color:#af52de"></i></div>
          <div class="card-info"><div class="card-name">设施报修</div><div class="card-desc">提交公共设施故障</div></div>
        </div>
      </div>
    </div>

    <div class="hall-section">
      <div class="section-title">我的记录</div>
      <div class="card-grid">
        <div class="service-card" @click="$router.push('/repair-record')">
          <div class="card-icon" style="background:rgba(88,86,214,0.1)"><i class="el-icon-document" style="color:#5856d6"></i></div>
          <div class="card-info"><div class="card-name">报修记录</div><div class="card-desc">查看我的报修与评价</div></div>
        </div>
        <div class="service-card" v-if="isStudent" @click="$router.push('/canteen/consume')">
          <div class="card-icon" style="background:rgba(0,113,227,0.1)"><i class="el-icon-coin" style="color:#0071e3"></i></div>
          <div class="card-info"><div class="card-name">消费记录</div><div class="card-desc">查看餐饮消费明细</div></div>
        </div>
      </div>
    </div>

    <div class="hall-section">
      <div class="section-title">生活服务</div>
      <div class="card-grid">
        <div class="service-card" @click="$router.push('/feedback')">
          <div class="card-icon" style="background:rgba(255,59,48,0.1)"><i class="el-icon-chat-dot-round" style="color:#ff3b30"></i></div>
          <div class="card-info"><div class="card-name">我有话说</div><div class="card-desc">意见反馈与建议</div></div>
        </div>
        <div class="service-card" @click="$router.push('/profile')">
          <div class="card-icon" style="background:rgba(142,142,147,0.1)"><i class="el-icon-user" style="color:#8e8e93"></i></div>
          <div class="card-info"><div class="card-name">个人信息</div><div class="card-desc">查看与编辑个人资料</div></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ServiceHall',
  computed: {
    user() { return this.$store.getters.user },
    userName() { return this.user ? (this.user.realName || this.user.username) : '' },
    isStaff() { return this.$store.getters.isStaff },
    isStudent() { return this.$store.getters.role === 'STUDENT' },
    isTeacher() { return this.$store.getters.role === 'TEACHER' },
    greeting() {
      const h = new Date().getHours()
      if (h < 6) return '夜深了'
      if (h < 12) return '上午好'
      if (h < 14) return '中午好'
      if (h < 18) return '下午好'
      return '晚上好'
    }
  }
}
</script>

<style scoped>
.service-hall { max-width: 960px; margin: 0 auto; }
.hall-header { margin-bottom: 32px; }
.hall-title { font-family: var(--apple-font-display); font-size: 28px; font-weight: 700; letter-spacing: -0.4px; color: var(--apple-near-black); margin: 0; }
.hall-subtitle { font-size: 15px; color: rgba(0,0,0,0.48); margin-top: 4px; }
.hall-section { margin-bottom: 28px; }
.section-title { font-family: var(--apple-font-display); font-size: 20px; font-weight: 600; letter-spacing: -0.3px; color: var(--apple-near-black); margin-bottom: 14px; }
.card-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 14px; }
.service-card { background: var(--apple-white); border-radius: var(--apple-radius-xl); box-shadow: var(--apple-card-shadow); padding: 20px; display: flex; align-items: center; gap: 14px; cursor: pointer; transition: transform 0.2s ease, box-shadow 0.2s ease; }
.service-card:hover { transform: translateY(-3px); box-shadow: rgba(0,0,0,0.28) 3px 8px 36px 0px; }
.card-icon { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.card-icon i { font-size: 20px; }
.card-name { font-family: var(--apple-font-display); font-size: 15px; font-weight: 600; letter-spacing: -0.224px; color: var(--apple-near-black); }
.card-desc { font-size: 12px; color: rgba(0,0,0,0.48); margin-top: 2px; }
</style>
