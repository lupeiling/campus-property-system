<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">卫生管理</h1>
      <p class="apple-caption">区域分配、打扫记录与卫生检查</p>
    </div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="区域分配" name="assignment">
        <div style="margin-bottom:12px;display:flex;gap:10px">
          <el-select v-model="searchBuilding" placeholder="按宿舍楼筛选" clearable style="width:160px" @change="loadAssignments">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
          <el-button type="primary" icon="el-icon-plus" @click="handleAddAssignment">新增分配</el-button>
        </div>
        <el-table :data="assignments" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="cleanerName" label="清洁人员" width="110" />
          <el-table-column label="宿舍楼" width="120">
            <template slot-scope="{row}">{{ getBuildingName(row.buildingId) }}</template>
          </el-table-column>
          <el-table-column prop="weekStart" label="周开始" width="110" />
          <el-table-column prop="weekEnd" label="周结束" width="110" />
          <el-table-column label="状态" width="90">
            <template slot-scope="{row}">
              <el-tag :type="row.status === 1 ? 'warning' : 'success'" size="small">{{ row.status === 1 ? '进行中' : '已完成' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="{row}">
              <el-button size="mini" @click="handleEditAssignment(row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDeleteAssignment(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="打扫记录" name="record">
        <div style="margin-bottom:12px">
          <el-button type="primary" icon="el-icon-plus" @click="handleAddRecord">新增记录</el-button>
        </div>
        <el-table :data="records" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="cleanerName" label="清洁人员" width="110" />
          <el-table-column label="宿舍楼" width="120">
            <template slot-scope="{row}">{{ getBuildingName(row.buildingId) }}</template>
          </el-table-column>
          <el-table-column prop="cleanDate" label="打扫日期" width="120" />
          <el-table-column prop="score" label="评分" width="80" />
          <el-table-column prop="issues" label="问题" min-width="150" />
          <el-table-column label="操作" width="120">
            <template slot-scope="{row}">
              <el-button size="mini" @click="handleEditRecord(row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDeleteRecord(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :title="assignmentDialogTitle" :visible.sync="assignmentDialogVisible" width="500px">
      <el-form :model="assignmentForm" label-width="90px">
        <el-form-item label="清洁人员"><el-input v-model="assignmentForm.cleanerName" /></el-form-item>
        <el-form-item label="宿舍楼">
          <el-select v-model="assignmentForm.buildingId" style="width:100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="周开始"><el-date-picker v-model="assignmentForm.weekStart" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
        <el-form-item label="周结束"><el-date-picker v-model="assignmentForm.weekEnd" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="assignmentForm.status" style="width:100%">
            <el-option :value="1" label="进行中" /><el-option :value="0" label="已完成" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="assignmentDialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmitAssignment">确定</el-button></span>
    </el-dialog>
    <el-dialog :title="recordDialogTitle" :visible.sync="recordDialogVisible" width="500px">
      <el-form :model="recordForm" label-width="90px">
        <el-form-item label="清洁人员"><el-input v-model="recordForm.cleanerName" /></el-form-item>
        <el-form-item label="宿舍楼">
          <el-select v-model="recordForm.buildingId" style="width:100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="打扫日期"><el-date-picker v-model="recordForm.cleanDate" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
        <el-form-item label="评分"><el-input-number v-model="recordForm.score" :min="0" :max="100" /></el-form-item>
        <el-form-item label="问题"><el-input v-model="recordForm.issues" type="textarea" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="recordDialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmitRecord">确定</el-button></span>
    </el-dialog>
  </div>
</template>
<script>
import { getCleaningAssignmentList, addCleaningAssignment, updateCleaningAssignment, deleteCleaningAssignment, getCleaningRecordList, addCleaningRecord, updateCleaningRecord, deleteCleaningRecord } from '@/api/newModules'
import { getBuildingList } from '@/api/dorm'
export default {
  name: 'CleaningManage',
  data() {
    return {
      activeTab: 'assignment', buildings: [], searchBuilding: null,
      assignments: [], assignmentCurrent: 1, assignmentSize: 10, assignmentTotal: 0,
      records: [], recordCurrent: 1, recordSize: 10, recordTotal: 0,
      assignmentDialogVisible: false, assignmentDialogTitle: '新增分配',
      assignmentForm: { id: null, cleanerName: '', buildingId: null, weekStart: '', weekEnd: '', status: 1 },
      recordDialogVisible: false, recordDialogTitle: '新增记录',
      recordForm: { id: null, assignmentId: null, cleanerName: '', buildingId: null, cleanDate: '', score: null, issues: '' }
    }
  },
  created() { this.loadBuildings(); this.loadAssignments(); this.loadRecords() },
  methods: {
    async loadBuildings() {
      try { const res = await getBuildingList({ current: 1, size: 200 }); this.buildings = res.data.records } catch (e) {}
    },
    getBuildingName(id) { const b = this.buildings.find(item => item.id === id); return b ? b.buildingName : id },
    async loadAssignments() {
      const params = { current: this.assignmentCurrent, size: this.assignmentSize }
      if (this.searchBuilding) params.buildingId = this.searchBuilding
      try { const res = await getCleaningAssignmentList(params); this.assignments = res.data.records; this.assignmentTotal = res.data.total } catch (e) {}
    },
    async loadRecords() {
      try { const res = await getCleaningRecordList({ current: this.recordCurrent, size: this.recordSize }); this.records = res.data.records; this.recordTotal = res.data.total } catch (e) {}
    },
    handleAddAssignment() {
      this.assignmentForm = { id: null, cleanerName: '', buildingId: null, weekStart: '', weekEnd: '', status: 1 }
      this.assignmentDialogTitle = '新增分配'; this.assignmentDialogVisible = true
    },
    handleEditAssignment(row) { this.assignmentForm = { ...row }; this.assignmentDialogTitle = '编辑分配'; this.assignmentDialogVisible = true },
    async handleDeleteAssignment(row) {
      try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteCleaningAssignment(row.id); this.$message.success('删除成功'); this.loadAssignments() } catch (e) {}
    },
    async handleSubmitAssignment() {
      try {
        if (this.assignmentForm.id) { await updateCleaningAssignment(this.assignmentForm) } else { await addCleaningAssignment(this.assignmentForm) }
        this.$message.success('操作成功'); this.assignmentDialogVisible = false; this.loadAssignments()
      } catch (e) { this.$message.error('操作失败') }
    },
    handleAddRecord() {
      this.recordForm = { id: null, assignmentId: null, cleanerName: '', buildingId: null, cleanDate: '', score: null, issues: '' }
      this.recordDialogTitle = '新增记录'; this.recordDialogVisible = true
    },
    handleEditRecord(row) { this.recordForm = { ...row }; this.recordDialogTitle = '编辑记录'; this.recordDialogVisible = true },
    async handleDeleteRecord(row) {
      try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteCleaningRecord(row.id); this.$message.success('删除成功'); this.loadRecords() } catch (e) {}
    },
    async handleSubmitRecord() {
      try {
        if (this.recordForm.id) { await updateCleaningRecord(this.recordForm) } else { await addCleaningRecord(this.recordForm) }
        this.$message.success('操作成功'); this.recordDialogVisible = false; this.loadRecords()
      } catch (e) { this.$message.error('操作失败') }
    }
  }
}
</script>
<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
