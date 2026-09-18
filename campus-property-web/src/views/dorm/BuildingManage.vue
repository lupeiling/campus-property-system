<template>
  <el-card>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">宿舍楼管理</h1>
      <p class="apple-caption">管理宿舍楼栋信息</p>
    </div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
      <span>宿舍楼列表</span>
      <el-button v-if="isAdmin" type="primary" size="small" @click="showAddDialog">新增宿舍楼</el-button>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="buildingNo" label="楼栋编号" width="120" />
      <el-table-column prop="buildingName" label="楼栋名称" width="150" />
      <el-table-column prop="floors" label="楼层数" width="80" />
      <el-table-column prop="roomsPerFloor" label="每层房间数" width="110" />
      <el-table-column prop="capacity" label="每间容量" width="90" />
      <el-table-column prop="genderType" label="类型" width="80">
        <template slot-scope="{row}">{{ ['混合', '男生', '女生'][row.genderType] }}</template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column v-if="isAdmin" label="操作" width="180">
        <template slot-scope="{row}">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />

    <el-dialog :title="dialogTitle" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="楼栋编号"><el-input v-model="editForm.buildingNo" /></el-form-item>
        <el-form-item label="楼栋名称"><el-input v-model="editForm.buildingName" /></el-form-item>
        <el-form-item label="楼层数"><el-input-number v-model="editForm.floors" :min="1" /></el-form-item>
        <el-form-item label="每层房间数"><el-input-number v-model="editForm.roomsPerFloor" :min="1" /></el-form-item>
        <el-form-item label="每间容量"><el-input-number v-model="editForm.capacity" :min="1" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.genderType" style="width:100%"><el-option label="混合" :value="0" /><el-option label="男生" :value="1" /><el-option label="女生" :value="2" /></el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSave">确定</el-button></span>
    </el-dialog>
  </el-card>
</template>

<script>
import { getBuildingList, addBuilding, updateBuilding, deleteBuilding } from '@/api/dorm'

export default {
  name: 'BuildingManage',
  computed: { isAdmin() { return this.$store.getters.isAdmin } },
  data() {
    return {
      tableData: [], current: 1, size: 10, total: 0,
      showDialog: false, dialogTitle: '新增宿舍楼',
      editForm: { buildingNo: '', buildingName: '', floors: 6, roomsPerFloor: 20, capacity: 4, genderType: 0, description: '' }
    }
  },
  created() { this.loadData() },
  methods: {
    async loadData() { const res = await getBuildingList({ current: this.current, size: this.size }); this.tableData = res.data.records; this.total = res.data.total },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.dialogTitle = '新增宿舍楼'; this.editForm = { buildingNo: '', buildingName: '', floors: 6, roomsPerFloor: 20, capacity: 4, genderType: 0, description: '' }; this.showDialog = true },
    handleEdit(row) { this.dialogTitle = '编辑宿舍楼'; this.editForm = { ...row }; this.showDialog = true },
    async handleSave() { try { if (this.dialogTitle === '新增宿舍楼') await addBuilding(this.editForm); else await updateBuilding(this.editForm); this.$message.success('操作成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async handleDelete(row) { try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteBuilding(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {} }
  }
}
</script>

<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
