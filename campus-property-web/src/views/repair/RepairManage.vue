<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">维修管理</h1><p class="apple-caption">维修工单提交与调度</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>维修工单管理</span><div style="display:flex;gap:8px"><el-dropdown v-if="isStaff" @command="handleExport"><el-button size="small">导出<i class="el-icon-arrow-down el-icon--right"></i></el-button><el-dropdown-menu slot="dropdown"><el-dropdown-item command="excel">导出 Excel</el-dropdown-item><el-dropdown-item command="pdf">导出 PDF</el-dropdown-item></el-dropdown-menu></el-dropdown><el-button type="primary" size="small" @click="showAddDialog">提交工单</el-button></div></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="loadData">
        <el-option label="待审核" :value="0" /><el-option label="已审核" :value="1" /><el-option label="已派工" :value="2" />
        <el-option label="维修中" :value="3" /><el-option label="已完成" :value="4" /><el-option label="已拒绝" :value="5" />
      </el-select>
      <el-select v-model="sourceFilter" placeholder="来源类型" clearable @change="loadData">
        <el-option label="宿舍" :value="1" /><el-option label="设施" :value="2" />
      </el-select>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="orderNo" label="工单编号" width="180" />
      <el-table-column prop="title" label="标题" width="150" />
      <el-table-column prop="sourceType" label="来源" width="80">
        <template slot-scope="{row}">{{ row.sourceType === 1 ? '宿舍' : '设施' }}</template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="80" />
      <el-table-column prop="urgency" label="紧急程度" width="100">
        <template slot-scope="{row}"><el-tag :type="['','warning','danger'][row.urgency]">{{ ['','普通','紧急','非常紧急'][row.urgency] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template slot-scope="{row}"><el-tag :type="['warning','success','primary','primary','success','danger'][row.status]">{{ ['待审核','已审核','已派工','维修中','已完成','已拒绝'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="200">
        <template slot-scope="{row}">
          <el-button v-if="isStaff && row.status === 0" size="small" type="success" @click="handleApprove(row, 1)">审核</el-button>
          <el-button v-if="isStaff && row.status === 0" size="small" type="danger" @click="handleApprove(row, 5)">拒绝</el-button>
          <el-button v-if="isStaff && row.status === 1" size="small" type="primary" @click="showAssignDialog(row)">派工</el-button>
          <el-button v-if="isStaff && row.status === 2" size="small" type="primary" @click="handleStart(row)">开始维修</el-button>
          <el-button v-if="isStaff && row.status === 3" size="small" type="success" @click="showCompleteDialog(row)">完成</el-button>
          <el-button v-if="row.status === 4" size="small" @click="showEvalDialog(row)">评价</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog title="提交工单" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
        <el-form-item label="来源类型"><el-select v-model="editForm.sourceType" style="width:100%"><el-option label="宿舍" :value="1" /><el-option label="设施" :value="2" /></el-select></el-form-item>
        <el-form-item label="来源ID"><el-input v-model="editForm.sourceId" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="editForm.category" /></el-form-item>
        <el-form-item label="紧急程度"><el-select v-model="editForm.urgency" style="width:100%"><el-option label="普通" :value="1" /><el-option label="紧急" :value="2" /><el-option label="非常紧急" :value="3" /></el-select></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSubmit">提交</el-button></span>
    </el-dialog>
    <el-dialog title="派工" :visible.sync="showAssign" width="400px">
      <el-form :model="assignForm" label-position="top">
        <el-form-item label="维修人员ID"><el-input v-model="assignForm.repairPersonId" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showAssign = false">取消</el-button><el-button type="primary" @click="handleAssign">确定</el-button></span>
    </el-dialog>
    <el-dialog title="完成维修" :visible.sync="showComplete" width="500px">
      <el-form :model="completeForm" label-position="top">
        <el-form-item label="维修结果"><el-input v-model="completeForm.repairResult" type="textarea" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showComplete = false">取消</el-button><el-button type="primary" @click="handleComplete">确定</el-button></span>
    </el-dialog>
    <el-dialog title="评价" :visible.sync="showEval" width="400px">
      <el-form :model="evalForm" label-position="top">
        <el-form-item label="评分"><el-rate v-model="evalForm.score" /></el-form-item>
        <el-form-item label="评价内容"><el-input v-model="evalForm.content" type="textarea" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showEval = false">取消</el-button><el-button type="primary" @click="handleEval">提交</el-button></span>
    </el-dialog>
  </el-card>
</template>
<script>
import { getRepairList, submitRepair, approveRepair, assignRepair, startRepair, completeRepair, addEvaluation } from '@/api/repair'
import { exportRepairOrder } from '@/api/export'
export default {
  name: 'RepairManage',
  data() {
    return {
      tableData: [], current: 1, size: 10, total: 0, statusFilter: null, sourceFilter: null,
      showDialog: false,
      editForm: { title: '', description: '', sourceType: 1, sourceId: null, category: '', urgency: 1 },
      showAssign: false, currentRow: null, assignForm: { repairPersonId: null },
      showComplete: false, completeForm: { repairResult: '' },
      showEval: false, evalForm: { score: 5, content: '' }
    }
  },
  computed: { isStaff() { return this.$store.getters.isStaff } },
  created() { this.loadData() },
  methods: {
    async loadData() {
      const params = { current: this.current, size: this.size, status: this.statusFilter, sourceType: this.sourceFilter }
      if (!this.isStaff) {
        const userId = this.$store.getters.user ? this.$store.getters.user.id : null
        if (userId) params.applicantId = userId
      }
      const res = await getRepairList(params)
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.editForm = { title: '', description: '', sourceType: 1, sourceId: null, category: '', urgency: 1 }; this.showDialog = true },
    async handleSubmit() { try { await submitRepair(this.editForm); this.$message.success('提交成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async handleApprove(row, status) { try { await approveRepair(row.id, { status }); this.$message.success('操作成功'); this.loadData() } catch (e) {} },
    showAssignDialog(row) { this.currentRow = row; this.assignForm = { repairPersonId: null }; this.showAssign = true },
    async handleAssign() { try { await assignRepair(this.currentRow.id, this.assignForm); this.$message.success('派工成功'); this.showAssign = false; this.loadData() } catch (e) {} },
    async handleStart(row) { try { await startRepair(row.id); this.$message.success('已开始维修'); this.loadData() } catch (e) {} },
    showCompleteDialog(row) { this.currentRow = row; this.completeForm = { repairResult: '' }; this.showComplete = true },
    async handleComplete() { try { await completeRepair(this.currentRow.id, this.completeForm); this.$message.success('维修完成'); this.showComplete = false; this.loadData() } catch (e) {} },
    showEvalDialog(row) { this.currentRow = row; this.evalForm = { score: 5, content: '' }; this.showEval = true },
    async handleEval() {
      try {
        await addEvaluation({ orderId: this.currentRow.id, userId: this.$store.getters.user.id, score: this.evalForm.score, content: this.evalForm.content })
        this.$message.success('评价成功'); this.showEval = false
      } catch (e) {}
    },
    async handleExport(format) {
      try {
        const res = await exportRepairOrder(format)
        const blob = new Blob([res], { type: format === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '维修工单统计表.' + (format === 'pdf' ? 'pdf' : 'xlsx')
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (e) { this.$message.error('导出失败') }
    }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
