<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">意见反馈</h1><p class="apple-caption">查看所有用户反馈与建议</p></div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="type" label="类型" width="80" />
      <el-table-column label="反馈人" width="120">
        <template slot-scope="{row}">{{ row.userName || row.userId }}<br><span style="font-size:11px;color:rgba(0,0,0,0.4)">{{ {ADMIN:'管理员',STAFF:'物业人员',TEACHER:'教师',STUDENT:'学生'}[row.userRole] }}</span></template>
      </el-table-column>
      <el-table-column prop="content" label="反馈内容" show-overflow-tooltip />
      <el-table-column prop="contact" label="联系方式" width="120" />
      <el-table-column label="回复" min-width="150">
        <template slot-scope="{row}">{{ row.reply || '暂无回复' }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="160" />
      <el-table-column label="操作" width="100">
        <template slot-scope="{row}">
          <el-button size="small" type="primary" @click="showReplyDialog(row)">回复</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />

    <el-dialog title="回复反馈" :visible.sync="replyVisible" width="500px">
      <el-form :model="replyForm" label-position="top">
        <el-form-item label="回复内容"><el-input v-model="replyForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="replyVisible = false">取消</el-button><el-button type="primary" @click="handleReply">提交回复</el-button></span>
    </el-dialog>
  </el-card>
</template>

<script>
import { getFeedbackList, replyFeedback } from '@/api/feedback'
export default {
  name: 'FeedbackManage',
  data() {
    return { tableData: [], current: 1, size: 10, total: 0, replyVisible: false, currentRow: null, replyForm: { reply: '' } }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      const res = await getFeedbackList({ current: this.current, size: this.size })
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    showReplyDialog(row) { this.currentRow = row; this.replyForm = { reply: row.reply || '' }; this.replyVisible = true },
    async handleReply() {
      try {
        await replyFeedback(this.currentRow.id, this.replyForm)
        this.$message.success('回复成功'); this.replyVisible = false; this.loadData()
      } catch (e) {}
    }
  }
}
</script>

<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
