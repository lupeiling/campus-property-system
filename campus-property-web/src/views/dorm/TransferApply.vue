<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">申请调宿</h1><p class="apple-caption">申请更换宿舍房间</p></div>
    <el-form :model="form" label-position="top" style="max-width:500px">
      <el-form-item label="当前宿舍楼">
        <el-select v-model="form.currentBuildingId" placeholder="请选择当前宿舍楼" filterable style="width:100%" @change="onCurrentBuildingChange">
          <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="当前房间">
        <el-select v-model="form.currentRoomId" placeholder="请选择当前房间" filterable style="width:100%" :disabled="!form.currentBuildingId">
          <el-option v-for="r in currentRooms" :key="r.id" :label="r.roomNo" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="期望宿舍楼">
        <el-select v-model="form.targetBuildingId" placeholder="请选择期望宿舍楼" filterable style="width:100%" @change="onTargetBuildingChange">
          <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="期望房间">
        <el-select v-model="form.targetRoomId" placeholder="请选择期望房间" filterable style="width:100%" :disabled="!form.targetBuildingId">
          <el-option v-for="r in targetRooms" :key="r.id" :label="r.roomNo + ' (余' + (r.capacity - r.currentCount) + '床)'" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请理由"><el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请说明调宿原因" /></el-form-item>
      <el-form-item><el-button type="primary" @click="handleSubmit">提交申请</el-button></el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { getBuildingList, getRoomList } from '@/api/dorm'
export default {
  name: 'DormTransfer',
  data() {
    return { buildings: [], rooms: [], form: { currentBuildingId: null, currentRoomId: null, targetBuildingId: null, targetRoomId: null, reason: '' } }
  },
  computed: {
    currentRooms() { return this.rooms.filter(r => r.buildingId === this.form.currentBuildingId) },
    targetRooms() { return this.rooms.filter(r => r.buildingId === this.form.targetBuildingId) }
  },
  created() { this.loadAuxData() },
  methods: {
    async loadAuxData() {
      const [bRes, rRes] = await Promise.all([getBuildingList({ current: 1, size: 200 }), getRoomList({ current: 1, size: 200 })])
      this.buildings = bRes.data.records; this.rooms = rRes.data.records
    },
    onCurrentBuildingChange() { this.form.currentRoomId = null },
    onTargetBuildingChange() { this.form.targetRoomId = null },
    handleSubmit() { this.$message.success('调宿申请已提交，请等待审核'); this.form = { currentBuildingId: null, currentRoomId: null, targetBuildingId: null, targetRoomId: null, reason: '' } }
  }
}
</script>

<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
