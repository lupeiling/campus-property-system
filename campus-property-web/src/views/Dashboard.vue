<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">统计分析</h1>
      <p class="apple-body-text" style="color:rgba(0,0,0,0.48);margin-top:4px">高校后勤物业管理系统数据概览与趋势分析</p>
    </div>

    <el-row :gutter="16" style="margin-top:8px">
      <el-col :span="4" v-for="card in statCards" :key="card.label">
        <div class="apple-stat-card" :style="{'border-top': '3px solid ' + card.color}">
          <div class="stat-icon" :style="{background: card.bgColor}">
            <i :class="card.icon" :style="{color: card.color}"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:20px">
      <el-col :span="16">
        <div class="chart-card">
          <div class="card-section-title">月度报修统计</div>
          <p class="card-subtitle">近12个月各类报修数量趋势</p>
          <div ref="monthlyChart" style="height:360px"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="card-section-title">报修类型分布</div>
          <p class="card-subtitle">各类型报修占比</p>
          <div ref="categoryChart" style="height:360px"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:20px">
      <el-col :span="12">
        <div class="chart-card">
          <div class="card-section-title">报修状态统计</div>
          <p class="card-subtitle">各类报修当前处理状态</p>
          <div ref="statusChart" style="height:320px"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <div class="card-section-title">近30日报修趋势</div>
          <p class="card-subtitle">每日新增报修数量</p>
          <div ref="trendChart" style="height:320px"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:20px">
      <el-col :span="8">
        <div class="chart-card">
          <div class="card-section-title">设施状态分布</div>
          <p class="card-subtitle">设施运行状态统计</p>
          <div ref="facilityChart" style="height:280px"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="card-section-title">用户角色分布</div>
          <p class="card-subtitle">系统用户角色占比</p>
          <div ref="userRoleChart" style="height:280px"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="card-section-title">物资库存TOP5</div>
          <p class="card-subtitle">库存数量最多的物资</p>
          <div ref="materialChart" style="height:280px"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getOverview, getRepairStatus, getRepairMonthly, getRepairTrend, getCategoryDistribution, getFacilityStatus, getMaterialOverview } from '@/api/statistics'

export default {
  name: 'Dashboard',
  data() {
    return {
      overview: {},
      charts: {}
    }
  },
  computed: {
    statCards() {
      const o = this.overview
      return [
        { label: '用户总数', value: o.userCount || 0, icon: 'el-icon-user', color: '#0071e3', bgColor: 'rgba(0,113,227,0.1)' },
        { label: '宿舍楼', value: o.buildingCount || 0, icon: 'el-icon-office-building', color: '#34c759', bgColor: 'rgba(52,199,89,0.1)' },
        { label: '房间总数', value: o.roomCount || 0, icon: 'el-icon-house', color: '#af52de', bgColor: 'rgba(175,82,222,0.1)' },
        { label: '设施总数', value: o.facilityCount || 0, icon: 'el-icon-monitor', color: '#ff9500', bgColor: 'rgba(255,149,0,0.1)' },
        { label: '待处理维修', value: o.pendingRepairCount || 0, icon: 'el-icon-s-tools', color: '#ff3b30', bgColor: 'rgba(255,59,48,0.1)' },
        { label: '物资种类', value: o.materialCount || 0, icon: 'el-icon-box', color: '#5856d6', bgColor: 'rgba(88,86,214,0.1)' }
      ]
    }
  },
  mounted() {
    this.loadAllData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    Object.values(this.charts).forEach(c => c && c.dispose())
  },
  methods: {
    handleResize() {
      Object.values(this.charts).forEach(c => c && c.resize())
    },
    async loadAllData() {
      try {
        const [overviewRes, monthlyRes, statusRes, trendRes, categoryRes, facilityRes, materialRes] = await Promise.all([
          getOverview(), getRepairMonthly(), getRepairStatus(), getRepairTrend(), getCategoryDistribution(), getFacilityStatus(), getMaterialOverview()
        ])
        this.overview = overviewRes.data
        this.$nextTick(() => {
          this.renderMonthlyChart(monthlyRes.data)
          this.renderStatusChart(statusRes.data.statusList)
          this.renderTrendChart(trendRes.data)
          this.renderCategoryChart(categoryRes.data)
          this.renderFacilityChart(facilityRes.data)
          this.renderUserRoleChart(categoryRes.data.userRoles)
          this.renderMaterialChart(materialRes.data.topMaterials)
        })
      } catch (e) {
        console.error('加载统计数据失败', e)
      }
    },
    getChart(refName) {
      if (!this.charts[refName]) {
        this.charts[refName] = echarts.init(this.$refs[refName])
      }
      return this.charts[refName]
    },
    renderMonthlyChart(data) {
      const chart = this.getChart('monthlyChart')
      const months = data.map(d => d.month)
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['宿舍维修', '设施报修'], top: 0, textStyle: { fontSize: 12 } },
        grid: { left: 50, right: 20, bottom: 30, top: 40 },
        xAxis: { type: 'category', data: months, axisLabel: { fontSize: 11 } },
        yAxis: { type: 'value', axisLabel: { fontSize: 11 } },
        series: [
          { name: '宿舍维修', type: 'bar', stack: 'repair', data: data.map(d => d.dormRepair), itemStyle: { color: '#0071e3', borderRadius: [0, 0, 0, 0] } },
          { name: '设施报修', type: 'bar', stack: 'repair', data: data.map(d => d.facilityRepair), itemStyle: { color: '#ff9500', borderRadius: [4, 4, 0, 0] } }
        ]
      })
    },
    renderStatusChart(data) {
      const chart = this.getChart('statusChart')
      const statuses = data.map(d => d.name)
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['宿舍维修', '设施报修'], top: 0, textStyle: { fontSize: 12 } },
        grid: { left: 60, right: 20, bottom: 30, top: 40 },
        xAxis: { type: 'value', axisLabel: { fontSize: 11 } },
        yAxis: { type: 'category', data: statuses, axisLabel: { fontSize: 12 } },
        series: [
          { name: '宿舍维修', type: 'bar', data: data.map(d => d.dormRepair), itemStyle: { color: '#0071e3', borderRadius: [0, 4, 4, 0] } },
          { name: '设施报修', type: 'bar', data: data.map(d => d.facilityRepair), itemStyle: { color: '#ff9500', borderRadius: [0, 4, 4, 0] } }
        ]
      })
    },
    renderTrendChart(data) {
      const chart = this.getChart('trendChart')
      chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, bottom: 30, top: 20 },
        xAxis: { type: 'category', data: data.map(d => d.date), axisLabel: { fontSize: 10, rotate: 45 } },
        yAxis: { type: 'value', axisLabel: { fontSize: 11 }, minInterval: 1 },
        series: [{
          type: 'line', data: data.map(d => d.count), smooth: true,
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(0,113,227,0.25)' }, { offset: 1, color: 'rgba(0,113,227,0.02)' }]) },
          lineStyle: { color: '#0071e3', width: 2 },
          itemStyle: { color: '#0071e3' }
        }]
      })
    },
    renderCategoryChart(data) {
      const chart = this.getChart('categoryChart')
      const pieData = [
        { name: '宿舍维修', value: data.dormRepairTotal },
        { name: '设施报修', value: data.facilityRepairTotal }
      ].filter(d => d.value > 0)
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'horizontal', bottom: 0, textStyle: { fontSize: 12 } },
        series: [{
          type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
          data: pieData,
          label: { formatter: '{b}\n{d}%', fontSize: 12 },
          itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
          color: ['#0071e3', '#ff9500']
        }]
      })
    },
    renderFacilityChart(data) {
      const chart = this.getChart('facilityChart')
      const validData = data.filter(d => d.value > 0)
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 0, textStyle: { fontSize: 12 } },
        series: [{
          type: 'pie', radius: ['35%', '65%'], center: ['50%', '42%'],
          data: validData,
          label: { formatter: '{b}\n{d}%', fontSize: 11 },
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
          color: ['#34c759', '#ff9500', '#8e8e93']
        }]
      })
    },
    renderUserRoleChart(data) {
      const chart = this.getChart('userRoleChart')
      const validData = data.filter(d => d.value > 0)
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 0, textStyle: { fontSize: 12 } },
        series: [{
          type: 'pie', radius: ['35%', '65%'], center: ['50%', '42%'],
          data: validData,
          label: { formatter: '{b}\n{d}%', fontSize: 11 },
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
          color: ['#ff3b30', '#0071e3', '#ff9500', '#34c759']
        }]
      })
    },
    renderMaterialChart(data) {
      const chart = this.getChart('materialChart')
      if (!data || data.length === 0) return
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: 80, right: 30, bottom: 20, top: 10 },
        xAxis: { type: 'value', axisLabel: { fontSize: 11 } },
        yAxis: { type: 'category', data: data.map(d => d.name).reverse(), axisLabel: { fontSize: 11 } },
        series: [{
          type: 'bar',
          data: data.map(d => d.quantity).reverse(),
          itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#5856d6' }, { offset: 1, color: '#af52de' }]), borderRadius: [0, 4, 4, 0] },
          barWidth: 18,
          label: { show: true, position: 'right', fontSize: 11, formatter: function(p) { return p.value + (data[data.length - 1 - p.dataIndex] ? data[data.length - 1 - p.dataIndex].unit : '') } }
        }]
      })
    }
  }
}
</script>

<style scoped>
.apple-hero-section {
  margin-bottom: 8px;
}

.apple-stat-card {
  background: var(--apple-white);
  border-radius: var(--apple-radius-xl);
  border: none;
  box-shadow: var(--apple-card-shadow);
  padding: 20px;
  display: flex;
  align-items: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.apple-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: rgba(0, 0, 0, 0.28) 3px 8px 36px 0px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.stat-icon i {
  font-size: 20px;
}

.stat-value {
  font-family: var(--apple-font-display);
  font-size: 24px;
  font-weight: 600;
  line-height: 1.14;
  letter-spacing: 0.196px;
  color: var(--apple-near-black);
}

.stat-label {
  font-family: var(--apple-font-text);
  font-size: 12px;
  font-weight: 400;
  letter-spacing: -0.224px;
  color: rgba(0, 0, 0, 0.48);
  margin-top: 2px;
}

.chart-card {
  background: var(--apple-white);
  border-radius: var(--apple-radius-xl);
  box-shadow: var(--apple-card-shadow);
  padding: 20px 24px;
}

.card-section-title {
  font-family: var(--apple-font-display);
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 0.231px;
  line-height: 1.19;
  color: var(--apple-near-black);
}

.card-subtitle {
  font-family: var(--apple-font-text);
  font-size: 12px;
  font-weight: 400;
  color: rgba(0, 0, 0, 0.48);
  margin-top: 2px;
  margin-bottom: 8px;
}
</style>
