<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">房间管理</h1><p class="apple-caption">管理宿舍房间信息</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>房间列表</span><el-button v-if="isAdmin" type="primary" size="small" @click="showAddDialog">新增房间</el-button></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-select v-model="buildingId" placeholder="选择宿舍楼" clearable @change="loadData">
        <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
      </el-select>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="roomNo" label="房间号" min-width="120" />
      <el-table-column prop="floor" label="楼层" width="80" />
      <el-table-column prop="capacity" label="容量" width="80" />
      <el-table-column prop="currentCount" label="已住" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="{row}"><el-tag :type="row.currentCount >= row.capacity ? 'danger' : 'success'">{{ row.currentCount >= row.capacity ? '不可用' : '可用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="{row}">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="isAdmin" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog :title="dialogTitle" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="宿舍楼"><el-select v-model="editForm.buildingId" style="width:100%"><el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" /></el-select></el-form-item>
        <el-form-item label="房间号"><el-input v-model="editForm.roomNo" /></el-form-item>
        <el-form-item label="楼层"><el-input-number v-model="editForm.floor" :min="1" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="editForm.capacity" :min="1" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSave">确定</el-button></span>
    </el-dialog>
  </el-card>
</template>
<script>
import { getRoomList, addRoom, updateRoom, deleteRoom, getBuildingList } from '@/api/dorm'
export default {
  name: 'RoomManage',
  computed: { isAdmin() { return this.$store.getters.isAdmin } },
  data() {
    return {
      tableData: [], buildings: [], buildingId: null,
      current: 1, size: 10, total: 0,
      showDialog: false, dialogTitle: '新增房间',
      editForm: { buildingId: null, roomNo: '', floor: 1, capacity: 4, status: 1 }
    }
  },
  created() { this.loadBuildings(); this.loadData() },
  methods: {
    async loadBuildings() { const res = await getBuildingList({ current: 1, size: 100 }); this.buildings = res.data.records },
    async loadData() { const res = await getRoomList({ current: this.current, size: this.size, buildingId: this.buildingId }); this.tableData = res.data.records; this.total = res.data.total },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.dialogTitle = '新增房间'; this.editForm = { buildingId: null, roomNo: '', floor: 1, capacity: 4, status: 1 }; this.showDialog = true },
    handleEdit(row) { this.dialogTitle = '编辑房间'; this.editForm = { ...row }; this.showDialog = true },
    async handleSave() { try { if (this.dialogTitle === '新增房间') await addRoom(this.editForm); else await updateRoom(this.editForm); this.$message.success('操作成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async handleDelete(row) { try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteRoom(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {} }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
