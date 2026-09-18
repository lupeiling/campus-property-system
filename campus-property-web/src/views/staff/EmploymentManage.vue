<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">人员聘用</h1>
      <p class="apple-caption">后勤工作人员管理</p>
    </div>
    <div style="margin-bottom:12px;display:flex;gap:10px">
      <el-select v-model="searchPosition" placeholder="按岗位筛选" clearable style="width:160px" @change="loadData">
        <el-option label="维修人员" value="维修人员" />
        <el-option label="宿舍管理员" value="宿舍管理员" />
        <el-option label="清洁人员" value="清洁人员" />
        <el-option label="门卫" value="门卫" />
        <el-option label="巡逻保安" value="巡逻保安" />
      </el-select>
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增人员</el-button>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="position" label="岗位" width="110" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column label="薪资标准" width="110">
        <template slot-scope="{row}">¥{{ row.salary }}</template>
      </el-table-column>
      <el-table-column prop="hireDate" label="入职时间" width="120" />
      <el-table-column label="状态" width="80">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '在职' : '离职' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="120" />
      <el-table-column label="操作" width="120">
        <template slot-scope="{row}">
          <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="form.position" style="width:100%">
            <el-option label="维修人员" value="维修人员" />
            <el-option label="宿舍管理员" value="宿舍管理员" />
            <el-option label="清洁人员" value="清洁人员" />
            <el-option label="门卫" value="门卫" />
            <el-option label="巡逻保安" value="巡逻保安" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="薪资标准"><el-input v-model.number="form.salary" type="number" /></el-form-item>
        <el-form-item label="入职时间"><el-date-picker v-model="form.hireDate" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option :value="1" label="在职" /><el-option :value="0" label="离职" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { getEmploymentList, addEmployment, updateEmployment } from '@/api/newModules'
export default {
  name: 'StaffEmploymentManage',
  data() {
    return {
      tableData: [], current: 1, size: 10, total: 0,
      searchPosition: '', dialogVisible: false, dialogTitle: '新增人员',
      form: { id: null, name: '', position: '', phone: '', salary: null, hireDate: '', status: 1, remark: '' }
    }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (this.searchPosition) params.position = this.searchPosition
      try {
        const res = await getEmploymentList(params)
        this.tableData = res.data.records; this.total = res.data.total
      } catch (e) { this.$message.error('加载失败') }
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    handleAdd() {
      this.form = { id: null, name: '', position: '', phone: '', salary: null, hireDate: '', status: 1, remark: '' }
      this.dialogTitle = '新增人员'; this.dialogVisible = true
    },
    handleEdit(row) { this.form = { ...row }; this.dialogTitle = '编辑人员'; this.dialogVisible = true },
    async handleSubmit() {
      try {
        if (this.form.id) { await updateEmployment(this.form) } else { await addEmployment(this.form) }
        this.$message.success('操作成功'); this.dialogVisible = false; this.loadData()
      } catch (e) { this.$message.error('操作失败') }
    }
  }
}
</script>
<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
