<template>
  <el-card>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">宿舍分配</h1>
      <p class="apple-caption">管理学生宿舍分配</p>
    </div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
      <span>分配列表</span>
      <el-button type="primary" size="small" @click="showAllocateDialog">分配宿舍</el-button>
    </div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-select v-model="filterBuildingId" placeholder="选择宿舍楼" clearable @change="onFilterBuildingChange">
        <el-option v-for="b in buildings" :key="b.id" :label="b.buildingNo + ' - ' + b.buildingName" :value="b.id" />
      </el-select>
      <el-select v-model="filterRoomId" placeholder="选择房间号" clearable @change="loadData">
        <el-option v-for="r in filterRooms" :key="r.id" :label="r.roomNo" :value="r.id" />
      </el-select>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column label="宿舍楼号" width="120">
        <template slot-scope="{row}">{{ getBuildingNo(row.roomId) }}</template>
      </el-table-column>
      <el-table-column label="房间号" width="100">
        <template slot-scope="{row}">{{ getRoomNo(row.roomId) }}</template>
      </el-table-column>
      <el-table-column prop="bedNo" label="床位号" width="80" />
      <el-table-column prop="checkInDate" label="入住日期" width="120" />
      <el-table-column prop="checkOutDate" label="退宿日期" min-width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="{row}"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '在住' : '已退宿' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="{row}">
          <el-button v-if="row.status === 1" size="small" type="warning" @click="handleCheckOut(row)">退宿</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />

    <el-dialog title="分配宿舍" :visible.sync="showDialog" width="500px">
      <el-form :model="allocateForm" label-position="top">
        <el-form-item label="用户ID"><el-input v-model="allocateForm.userId" placeholder="请输入用户ID" /></el-form-item>
        <el-form-item label="选择宿舍楼">
          <el-select v-model="allocateForm.buildingId" placeholder="请选择宿舍楼" filterable style="width:100%" @change="onBuildingChange">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName + ' (' + b.buildingNo + ')'" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择房间">
          <el-select v-model="allocateForm.roomId" placeholder="请先选择宿舍楼" filterable style="width:100%" :disabled="!allocateForm.buildingId" @change="onRoomChange">
            <el-option v-for="r in allocFilteredRooms" :key="r.id" :label="r.roomNo + ' (已住:' + r.currentCount + '/' + r.capacity + ')'" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择床位">
          <el-select v-model="allocateForm.bedNo" placeholder="请先选择房间" style="width:100%" :disabled="!allocateForm.roomId">
            <el-option v-for="bed in availableBeds" :key="bed" :label="'床位 ' + bed" :value="bed" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleAllocate">确定分配</el-button></span>
    </el-dialog>
  </el-card>
</template>

<script>
import { getAllocationList, allocateRoom, checkOut, getRoomList, getBuildingList } from '@/api/dorm'

export default {
  name: 'AllocationManage',
  data() {
    return {
      tableData: [], buildings: [], rooms: [],
      current: 1, size: 10, total: 0,
      filterBuildingId: null, filterRoomId: null,
      showDialog: false,
      allocateForm: { userId: null, buildingId: null, roomId: null, bedNo: null }
    }
  },
  computed: {
    filterRooms() {
      if (!this.filterBuildingId) return this.rooms
      return this.rooms.filter(r => r.buildingId === this.filterBuildingId)
    },
    allocFilteredRooms() {
      if (!this.allocateForm.buildingId) return []
      return this.rooms.filter(r => r.buildingId === this.allocateForm.buildingId)
    },
    availableBeds() {
      if (!this.allocateForm.roomId) return []
      const room = this.rooms.find(r => r.id === this.allocateForm.roomId)
      if (!room) return []
      const beds = []
      for (let i = 1; i <= room.capacity; i++) beds.push(i)
      return beds
    }
  },
  created() { this.loadBuildings(); this.loadRooms(); this.loadData() },
  methods: {
    async loadBuildings() { const res = await getBuildingList({ current: 1, size: 200 }); this.buildings = res.data.records },
    async loadRooms() { const res = await getRoomList({ current: 1, size: 200 }); this.rooms = res.data.records },
    getRoomByRoomId(roomId) { return this.rooms.find(r => r.id === roomId) },
    getBuildingNo(roomId) {
      const room = this.getRoomByRoomId(roomId)
      if (!room) return ''
      const b = this.buildings.find(item => item.id === room.buildingId)
      return b ? b.buildingNo : ''
    },
    getRoomNo(roomId) {
      const room = this.getRoomByRoomId(roomId)
      return room ? room.roomNo : ''
    },
    onFilterBuildingChange() { this.filterRoomId = null; this.loadData() },
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (this.filterRoomId) params.roomId = this.filterRoomId
      const res = await getAllocationList(params)
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    onBuildingChange() { this.allocateForm.roomId = null; this.allocateForm.bedNo = null },
    onRoomChange() { this.allocateForm.bedNo = null },
    showAllocateDialog() { this.allocateForm = { userId: null, buildingId: null, roomId: null, bedNo: null }; this.showDialog = true },
    async handleAllocate() {
      try {
        await allocateRoom({ userId: this.allocateForm.userId, roomId: this.allocateForm.roomId, bedNo: this.allocateForm.bedNo })
        this.$message.success('分配成功')
        this.showDialog = false
        this.loadRooms()
        this.loadData()
      } catch (e) {}
    },
    async handleCheckOut(row) {
      try {
        await this.$confirm('确认退宿？', '提示', { type: 'warning' })
        await checkOut(row.id)
        this.$message.success('退宿成功')
        this.loadRooms()
        this.loadData()
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
