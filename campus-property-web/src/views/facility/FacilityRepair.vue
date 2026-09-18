<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">设施报修</h1><p class="apple-caption">校园设施报修与跟踪</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
      <span>设施报修</span>
      <el-button v-if="!isStaff" type="primary" size="small" @click="showAddDialog">报修申请</el-button>
    </div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-select v-if="!isStaffOnly" v-model="statusFilter" placeholder="状态筛选" clearable @change="loadData">
        <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已拒绝" :value="3" />
      </el-select>
      <el-select v-if="isStaffOnly" v-model="statusFilter" placeholder="状态筛选" clearable @change="loadData">
        <el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" />
      </el-select>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" width="150" />
      <el-table-column label="设施" min-width="120">
        <template slot-scope="{row}">{{ getFacilityName(row.facilityId) }}</template>
      </el-table-column>
      <el-table-column prop="urgency" label="紧急程度" width="100">
        <template slot-scope="{row}"><el-tag :type="['','warning','danger'][row.urgency]">{{ ['','普通','紧急','非常紧急'][row.urgency] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template slot-scope="{row}"><el-tag :type="['warning','primary','success','danger'][row.status]">{{ ['待处理','处理中','已完成','已拒绝'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column label="维修人员" width="100">
        <template slot-scope="{row}">{{ row.repairPerson || '未指派' }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="报修时间" width="160" />
      <el-table-column label="操作" width="200">
        <template slot-scope="{row}">
          <el-button size="small" type="primary" @click="showDetailDialog(row)">查看详情</el-button>
          <el-button v-if="isAdmin && row.status === 0" size="small" type="warning" @click="showAssignDialog(row)">派单</el-button>
          <el-button v-if="isStaffOnly && row.status === 1" size="small" type="success" @click="showHandleDialog(row)">处理</el-button>
          <el-button v-if="!isStaff && row.status === 2 && !row.evaluated" size="small" type="warning" @click="showEvalDialog(row)">评价</el-button>
          <el-tag v-if="!isStaff && row.status === 2 && row.evaluated" size="small" type="success">已评价</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />

    <el-dialog title="报修申请" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="选择设施">
          <el-select v-model="editForm.facilityId" placeholder="请选择设施" filterable style="width:100%">
            <el-option v-for="f in facilities" :key="f.id" :label="f.facilityName + ' (' + f.location + ')'" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="紧急程度"><el-select v-model="editForm.urgency" style="width:100%"><el-option label="普通" :value="1" /><el-option label="紧急" :value="2" /><el-option label="非常紧急" :value="3" /></el-select></el-form-item>
        <el-form-item label="故障图片">
          <el-upload action="" :http-request="handleUpload" :show-file-list="false" accept="image/*">
            <el-button size="small" type="primary">上传图片</el-button>
          </el-upload>
          <div v-if="editForm.images" style="margin-top:8px"><el-image :src="editForm.images" style="width:120px;height:80px" fit="cover" /><el-button type="text" style="color:#ff3b30;margin-left:8px" @click="editForm.images = ''">删除</el-button></div>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSubmit">提交</el-button></span>
    </el-dialog>

    <el-dialog title="派单" :visible.sync="assignDialogVisible" width="440px">
      <el-form :model="assignForm" label-position="top">
        <el-form-item label="指派维修人员">
          <el-select v-model="assignForm.repairPerson" placeholder="请选择维修人员" filterable style="width:100%">
            <el-option v-for="s in staffUsers" :key="s.id" :label="s.realName || s.username" :value="s.realName || s.username" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="assignDialogVisible = false">取消</el-button><el-button type="primary" @click="handleAssign">确认派单</el-button></span>
    </el-dialog>

    <el-dialog title="处理报修" :visible.sync="handleDialogVisible" width="500px">
      <el-form :model="handleForm" label-position="top">
        <el-form-item label="状态">
          <el-select v-model="handleForm.status" style="width:100%">
            <el-option label="已完成" :value="2" /><el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果"><el-input v-model="handleForm.repairResult" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="handleDialogVisible = false">取消</el-button><el-button type="primary" @click="handleHandleSubmit">确定</el-button></span>
    </el-dialog>

    <el-dialog title="报修详情" :visible.sync="detailDialogVisible" width="560px">
      <div v-if="detailRow">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="标题">{{ detailRow.title }}</el-descriptions-item>
          <el-descriptions-item label="设施">{{ getFacilityName(detailRow.facilityId) }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">
            <el-tag :type="['','warning','danger'][detailRow.urgency]">{{ ['','普通','紧急','非常紧急'][detailRow.urgency] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="['warning','primary','success','danger'][detailRow.status]">{{ ['待处理','处理中','已完成','已拒绝'][detailRow.status] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="故障描述">{{ detailRow.description || '无' }}</el-descriptions-item>
          <el-descriptions-item label="维修人员">{{ detailRow.repairPerson || '未指派' }}</el-descriptions-item>
          <el-descriptions-item label="处理结果">{{ detailRow.repairResult || '无' }}</el-descriptions-item>
          <el-descriptions-item label="报修时间">{{ detailRow.createTime }}</el-descriptions-item>
          <el-descriptions-item v-if="isStaff" label="申请人">
            {{ getApplicantInfo(detailRow.userId) }}
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="detailRow.images" style="margin-top:16px">
          <p style="font-weight:600;margin-bottom:8px">故障图片</p>
          <el-image :src="detailRow.images" style="max-width:100%;max-height:400px;border-radius:8px" fit="contain" :preview-src-list="[detailRow.images]" />
        </div>
      </div>
    </el-dialog>

    <el-dialog title="评价维修" :visible.sync="evalDialogVisible" width="440px">
      <el-form :model="evalForm" label-position="top">
        <el-form-item label="评分"><el-rate v-model="evalForm.score" show-text :texts="['很差','较差','一般','满意','非常满意']" /></el-form-item>
        <el-form-item label="评价内容"><el-input v-model="evalForm.content" type="textarea" :rows="3" placeholder="请输入您的评价" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="evalDialogVisible = false">取消</el-button><el-button type="primary" @click="submitEval">提交评价</el-button></span>
    </el-dialog>
  </el-card>
</template>

<script>
import { getFacilityRepairList, addFacilityRepair, handleFacilityRepair, getFacilityList } from '@/api/facility'
import { uploadImage } from '@/api/upload'
import { getUserList } from '@/api/user'
import request from '@/utils/request'

export default {
  name: 'FacilityRepairView',
  data() {
    return {
      tableData: [], facilities: [], staffUsers: [], allUsers: [],
      current: 1, size: 10, total: 0, statusFilter: null,
      showDialog: false, editForm: { facilityId: null, title: '', description: '', urgency: 1, images: '' },
      assignDialogVisible: false, currentRow: null, assignForm: { repairPerson: '' },
      handleDialogVisible: false, handleForm: { status: 2, repairResult: '' },
      detailDialogVisible: false, detailRow: null,
      evalDialogVisible: false, evalForm: { score: 5, content: '' }
    }
  },
  computed: {
    isAdmin() { return this.$store.getters.role === 'ADMIN' },
    isStaff() { return this.$store.getters.isStaff },
    isStaffOnly() { return this.$store.getters.role === 'STAFF' }
  },
  created() {
    this.loadFacilities(); this.loadData()
    if (this.isAdmin) { this.loadUsers() }
  },
  methods: {
    async loadFacilities() { const res = await getFacilityList({ current: 1, size: 200 }); this.facilities = res.data.records },
    async loadUsers() {
      try {
        const res = await getUserList({ current: 1, size: 200 })
        this.allUsers = res.data.records
        this.staffUsers = res.data.records.filter(u => u.role === 'STAFF')
      } catch (e) {}
    },
    getFacilityName(id) { const f = this.facilities.find(item => item.id === id); return f ? f.facilityName : id },
    getApplicantInfo(userId) {
      const u = this.allUsers.find(item => item.id === userId)
      if (!u) return userId
      return (u.realName || u.username) + ' (' + u.username + ', ' + { ADMIN: '管理员', STAFF: '物业人员', TEACHER: '教师', STUDENT: '学生' }[u.role] + ')'
    },
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (this.isStaffOnly) {
        const user = this.$store.getters.user
        if (user) params.repairPerson = user.realName || user.username
        if (!this.statusFilter) {
          params.statusList = '1,2'
        } else {
          params.status = this.statusFilter
        }
      } else {
        if (this.statusFilter !== null && this.statusFilter !== '') params.status = this.statusFilter
        const userId = this.$store.getters.user ? this.$store.getters.user.id : null
        if (userId && !this.isStaff) params.userId = userId
      }
      const res = await getFacilityRepairList(params)
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.editForm = { facilityId: null, title: '', description: '', urgency: 1, images: '' }; this.showDialog = true },
    async handleUpload(opt) { try { const res = await uploadImage(opt.file); this.editForm.images = res.data; this.$message.success('上传成功') } catch (e) { this.$message.error('上传失败') } },
    async handleSubmit() { try { await addFacilityRepair(this.editForm); this.$message.success('提交成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    showAssignDialog(row) { this.currentRow = row; this.assignForm = { repairPerson: '' }; this.assignDialogVisible = true },
    async handleAssign() {
      try {
        await handleFacilityRepair(this.currentRow.id, { status: 1, repairPerson: this.assignForm.repairPerson, repairResult: '' })
        this.$message.success('派单成功'); this.assignDialogVisible = false; this.loadData()
      } catch (e) {}
    },
    showHandleDialog(row) { this.currentRow = row; this.handleForm = { status: 2, repairResult: '' }; this.handleDialogVisible = true },
    async handleHandleSubmit() {
      try {
        await handleFacilityRepair(this.currentRow.id, this.handleForm)
        this.$message.success('处理成功'); this.handleDialogVisible = false; this.loadData()
      } catch (e) {}
    },
    showDetailDialog(row) { this.detailRow = row; this.detailDialogVisible = true },
    showEvalDialog(row) { this.currentRow = row; this.evalForm = { score: 5, content: '' }; this.evalDialogVisible = true },
    async submitEval() {
      if (!this.currentRow) return
      try {
        await request({ url: '/facility/repair/' + this.currentRow.id + '/evaluate', method: 'put' })
        this.$message.success('评价提交成功！')
        this.currentRow.evaluated = 1
        this.evalDialogVisible = false
      } catch (e) {}
    }
  }
}
</script>

<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
