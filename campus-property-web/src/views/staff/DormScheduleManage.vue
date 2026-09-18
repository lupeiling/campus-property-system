<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">宿管排班</h1>
      <p class="apple-caption">宿舍管理员值班安排</p>
    </div>
    <div style="margin-bottom:12px;display:flex;gap:10px">
      <el-select v-model="searchBuilding" placeholder="按宿舍楼筛选" clearable style="width:160px" @change="loadData">
        <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
      </el-select>
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增排班</el-button>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="managerName" label="宿管姓名" width="110" />
      <el-table-column label="宿舍楼" width="120">
        <template slot-scope="{row}">{{ getBuildingName(row.buildingId) }}</template>
      </el-table-column>
      <el-table-column prop="scheduleDate" label="值班日期" width="120" />
      <el-table-column prop="shift" label="班次" width="80" />
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column label="操作" width="120">
        <template slot-scope="{row}">
          <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="宿管姓名"><el-input v-model="form.managerName" /></el-form-item>
        <el-form-item label="宿舍楼">
          <el-select v-model="form.buildingId" style="width:100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="值班日期"><el-date-picker v-model="form.scheduleDate" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
        <el-form-item label="班次">
          <el-select v-model="form.shift" style="width:100%">
            <el-option label="白班" value="白班" /><el-option label="夜班" value="夜班" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { getDormScheduleList, addDormSchedule, updateDormSchedule, deleteDormSchedule } from '@/api/newModules'
import { getBuildingList } from '@/api/dorm'
export default {
  name: 'DormScheduleManage',
  data() {
    return {
      tableData: [], current: 1, size: 10, total: 0,
      buildings: [], searchBuilding: null,
      dialogVisible: false, dialogTitle: '新增排班',
      form: { id: null, managerName: '', buildingId: null, scheduleDate: '', shift: '白班', remark: '' }
    }
  },
  created() { this.loadBuildings(); this.loadData() },
  methods: {
    async loadBuildings() {
      try { const res = await getBuildingList({ current: 1, size: 200 }); this.buildings = res.data.records } catch (e) {}
    },
    getBuildingName(id) { const b = this.buildings.find(item => item.id === id); return b ? b.buildingName : id },
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (this.searchBuilding) params.buildingId = this.searchBuilding
      try {
        const res = await getDormScheduleList(params)
        this.tableData = res.data.records; this.total = res.data.total
      } catch (e) { this.$message.error('加载失败') }
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    handleAdd() {
      this.form = { id: null, managerName: '', buildingId: null, scheduleDate: '', shift: '白班', remark: '' }
      this.dialogTitle = '新增排班'; this.dialogVisible = true
    },
    handleEdit(row) { this.form = { ...row }; this.dialogTitle = '编辑排班'; this.dialogVisible = true },
    async handleDelete(row) {
      try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteDormSchedule(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {}
    },
    async handleSubmit() {
      try {
        if (this.form.id) { await updateDormSchedule(this.form) } else { await addDormSchedule(this.form) }
        this.$message.success('操作成功'); this.dialogVisible = false; this.loadData()
      } catch (e) { this.$message.error('操作失败') }
    }
  }
}
</script>
<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
