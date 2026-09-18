<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">退宿申请</h1><p class="apple-caption">申请退宿离校</p></div>
    <el-form :model="form" label-position="top" style="max-width:500px">
      <el-form-item label="宿舍楼">
        <el-select v-model="form.buildingId" placeholder="请选择宿舍楼" filterable style="width:100%" @change="onBuildingChange">
          <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="房间">
        <el-select v-model="form.roomId" placeholder="请选择房间" filterable style="width:100%" :disabled="!form.buildingId">
          <el-option v-for="r in filteredRooms" :key="r.id" :label="r.roomNo" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="退宿日期"><el-date-picker v-model="form.checkoutDate" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
      <el-form-item label="退宿原因"><el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请说明退宿原因" /></el-form-item>
      <el-form-item><el-button type="primary" @click="handleSubmit">提交申请</el-button></el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { getBuildingList, getRoomList } from '@/api/dorm'
export default {
  name: 'DormCheckout',
  data() {
    return { buildings: [], rooms: [], form: { buildingId: null, roomId: null, checkoutDate: '', reason: '' } }
  },
  computed: { filteredRooms() { return this.rooms.filter(r => r.buildingId === this.form.buildingId) } },
  created() { this.loadAuxData() },
  methods: {
    async loadAuxData() {
      const [bRes, rRes] = await Promise.all([getBuildingList({ current: 1, size: 200 }), getRoomList({ current: 1, size: 200 })])
      this.buildings = bRes.data.records; this.rooms = rRes.data.records
    },
    onBuildingChange() { this.form.roomId = null },
    handleSubmit() { this.$message.success('退宿申请已提交，请等待审核'); this.form = { buildingId: null, roomId: null, checkoutDate: '', reason: '' } }
  }
}
</script>

<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
