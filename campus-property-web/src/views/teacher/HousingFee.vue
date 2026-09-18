<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">住房费用</h1>
      <p class="apple-caption">查看住房租金与物业费明细</p>
    </div>
    <el-card v-if="myBenefit" style="margin-bottom:20px;border-left:4px solid #ff9500">
      <div style="font-size:16px;font-weight:600;margin-bottom:12px;color:#1d1d1f">我的住房费用</div>
      <el-descriptions :column="2" border size="medium">
        <el-descriptions-item label="公寓名称">{{ myBenefit.benefit.apartmentName }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ myBenefit.benefit.location }}</el-descriptions-item>
        <el-descriptions-item label="职称等级">{{ myBenefit.title }}</el-descriptions-item>
        <el-descriptions-item label="教师实际月自付（元）">
          <span style="color:#ff3b30;font-weight:600;font-size:16px">¥{{ myBenefit.benefit.selfPay }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="计算规则">
          <el-button type="text" style="color:#0071e3" @click="showCalcDetail">详情</el-button>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card>
      <div slot="header" style="font-weight:600;font-size:16px">各职称等级住房费用标准</div>
      <el-table :data="benefitList" stripe border>
        <el-table-column prop="titleLevel" label="职称等级" width="110" />
        <el-table-column label="公寓名称（位置）" min-width="180">
          <template slot-scope="{row}">{{ row.apartmentName }}（{{ row.location }}）</template>
        </el-table-column>
        <el-table-column label="教师实际月自付（元）" width="160">
          <template slot-scope="{row}">
            <span style="color:#ff3b30;font-weight:600">¥{{ row.selfPay }}</span>
          </template>
        </el-table-column>
        <el-table-column label="计算规则" width="100">
          <template slot-scope="{row}">
            <el-button type="text" style="color:#0071e3" @click="showRowDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="费用计算详情" :visible.sync="calcDialogVisible" width="520px">
      <div v-if="calcDetail">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="职称等级">{{ calcDetail.titleLevel }}</el-descriptions-item>
          <el-descriptions-item label="住房类型">{{ calcDetail.housingType }}</el-descriptions-item>
          <el-descriptions-item label="建筑面积">{{ calcDetail.area }} ㎡</el-descriptions-item>
          <el-descriptions-item label="月租金标准">{{ calcDetail.rentPerSqm }} 元/㎡</el-descriptions-item>
          <el-descriptions-item label="月物业费标准">{{ calcDetail.propertyFeePerSqm }} 元/㎡</el-descriptions-item>
          <el-descriptions-item label="月租金">{{ calcDetail.area }} × {{ calcDetail.rentPerSqm }} = {{ calcDetail.area * calcDetail.rentPerSqm }} 元</el-descriptions-item>
          <el-descriptions-item label="月物业费">{{ calcDetail.area }} × {{ calcDetail.propertyFeePerSqm }} = {{ calcDetail.area * calcDetail.propertyFeePerSqm }} 元</el-descriptions-item>
          <el-descriptions-item label="学校月补贴">{{ calcDetail.schoolSubsidy }} 元</el-descriptions-item>
          <el-descriptions-item label="计算公式">{{ calcDetail.formula }}</el-descriptions-item>
          <el-descriptions-item label="最终费用（教师月自付）">
            <span style="color:#ff3b30;font-weight:600;font-size:16px">¥{{ calcDetail.selfPay }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { getTeacherHousingBenefit, getAllHousingBenefits } from '@/api/newModules'
export default {
  name: 'TeacherHousingFee',
  data() {
    return { myBenefit: null, benefitList: [], calcDialogVisible: false, calcDetail: null }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const [benefitRes, listRes] = await Promise.all([getTeacherHousingBenefit(), getAllHousingBenefits()])
        this.myBenefit = benefitRes.data
        this.benefitList = listRes.data
      } catch (e) { this.$message.error('加载失败') }
    },
    showCalcDetail() {
      this.calcDetail = this.myBenefit.benefit
      this.calcDialogVisible = true
    },
    showRowDetail(row) {
      this.calcDetail = row
      this.calcDialogVisible = true
    }
  }
}
</script>
<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
