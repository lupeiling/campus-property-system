<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">报修记录</h1><p class="apple-caption">已完成的报修记录与评价</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>报修记录</span></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-select v-model="typeFilter" placeholder="类型筛选" clearable @change="loadData">
        <el-option label="宿舍报修" value="dorm" /><el-option label="设施报修" value="facility" />
      </el-select>
    </div>
    <el-table :data="allRecords" stripe>
      <el-table-column prop="type" label="类型" width="90">
        <template slot-scope="{row}"><el-tag :type="row.type === 'dorm' ? 'primary' : 'warning'" size="small">{{ row.type === 'dorm' ? '宿舍' : '设施' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="150" />
      <el-table-column label="位置" min-width="130">
        <template slot-scope="{row}">{{ row.locationName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template><el-tag type="success">已完成</el-tag></template>
      </el-table-column>
      <el-table-column label="维修人员" width="100">
        <template slot-scope="{row}">{{ row.repairPerson || '未指派' }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="报修时间" width="160" />
      <el-table-column label="操作" width="200">
        <template slot-scope="{row}">
          <el-button size="small" type="primary" @click="showDetail(row)">详情</el-button>
          <el-tag v-if="row.evaluated" size="small" type="success">已评价</el-tag>
          <el-tag v-if="!row.evaluated" size="small" type="info">未评价</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />

    <el-dialog title="报修详情" :visible.sync="detailVisible" width="520px">
      <div v-if="detailRow">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="类型">{{ detailRow.type === 'dorm' ? '宿舍报修' : '设施报修' }}</el-descriptions-item>
          <el-descriptions-item label="标题">{{ detailRow.title }}</el-descriptions-item>
          <el-descriptions-item label="位置">{{ detailRow.locationName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ detailRow.description || '无' }}</el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag type="success">已完成</el-tag></el-descriptions-item>
          <el-descriptions-item label="维修人员">{{ detailRow.repairPerson || '未指派' }}</el-descriptions-item>
          <el-descriptions-item label="处理结果">{{ detailRow.repairResult || '无' }}</el-descriptions-item>
          <el-descriptions-item label="报修时间">{{ detailRow.createTime }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detailRow.images" style="margin-top:16px">
          <p style="font-weight:600;margin-bottom:8px">图片</p>
          <el-image :src="detailRow.images" style="max-width:100%;max-height:300px;border-radius:8px" fit="contain" :preview-src-list="[detailRow.images]" />
        </div>
      </div>
    </el-dialog>
  </el-card>
</template>
<script>
import { getDormRepairList, getRoomList, getBuildingList } from '@/api/dorm'
import { getFacilityRepairList, getFacilityList } from '@/api/facility'

export default {
  name: 'RepairRecord',
  data() {
    return {
      allRecords: [], current: 1, size: 20, total: 0, typeFilter: null,
      buildings: [], rooms: [], facilities: [],
      detailVisible: false, detailRow: null
    }
  },
  computed: {
    userId() { return this.$store.getters.user ? this.$store.getters.user.id : null }
  },
  created() { this.loadAuxData(); this.loadData() },
  methods: {
    async loadAuxData() {
      try {
        const [bRes, rRes, fRes] = await Promise.all([
          getBuildingList({ current: 1, size: 200 }),
          getRoomList({ current: 1, size: 200 }),
          getFacilityList({ current: 1, size: 200 })
        ])
        this.buildings = bRes.data.records
        this.rooms = rRes.data.records
        this.facilities = fRes.data.records
      } catch (e) {}
    },
    getBuildingName(id) { const b = this.buildings.find(item => item.id === id); return b ? b.buildingName : '' },
    getRoomFullName(roomId) {
      const r = this.rooms.find(item => item.id === roomId)
      if (!r) return ''
      return this.getBuildingName(r.buildingId) + ' - ' + r.roomNo
    },
    getFacilityName(id) { const f = this.facilities.find(item => item.id === id); return f ? f.facilityName + ' (' + f.location + ')' : '' },
    async loadData() {
      const userId = this.userId
      if (!userId) return
      try {
        const [dRes, fRes] = await Promise.all([
          getDormRepairList({ current: 1, size: 200, userId, status: 2 }),
          getFacilityRepairList({ current: 1, size: 200, userId, status: 2 })
        ])
        const dormRecords = (dRes.data.records || []).map(r => ({
          ...r, type: 'dorm', locationName: this.getRoomFullName(r.roomId)
        }))
        const facilityRecords = (fRes.data.records || []).map(r => ({
          ...r, type: 'facility', locationName: this.getFacilityName(r.facilityId)
        }))
        let all = [...dormRecords, ...facilityRecords]
        if (this.typeFilter) all = all.filter(r => r.type === this.typeFilter)
        all.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        this.total = all.length
        const start = (this.current - 1) * this.size
        this.allRecords = all.slice(start, start + this.size)
      } catch (e) { console.error(e) }
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    showDetail(row) { this.detailRow = row; this.detailVisible = true }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
