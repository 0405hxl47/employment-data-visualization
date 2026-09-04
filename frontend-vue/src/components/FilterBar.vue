<template>
  <div class="filter-bar">
    <el-select
      v-model="state.universityType"
      placeholder="院校层次"
      clearable
      style="width: 140px"
      @change="onTypeChange"
    >
      <el-option
        v-for="t in options.universityTypes"
        :key="t"
        :label="t"
        :value="t"
      />
    </el-select>

    <el-select
      v-model="state.universityName"
      placeholder="高校名称"
      clearable
      filterable
      style="width: 180px"
    >
      <el-option
        v-for="u in filteredUniversities"
        :key="u.name"
        :label="u.name"
        :value="u.name"
      />
    </el-select>

    <el-select
      v-model="state.degree"
      placeholder="学历层次"
      clearable
      style="width: 120px"
    >
      <el-option
        v-for="d in options.degrees"
        :key="d"
        :label="d"
        :value="d"
      />
    </el-select>

    <el-select
      v-model="state.graduationYear"
      placeholder="毕业年份"
      clearable
      style="width: 120px"
    >
      <el-option
        v-for="y in options.years"
        :key="y"
        :label="String(y)"
        :value="y"
      />
    </el-select>

    <el-input
      v-model="state.keyword"
      placeholder="搜索专业/高校名称..."
      clearable
      style="width: 200px"
    />

    <el-button type="primary" @click="handleExport">导出 Excel</el-button>
    <el-button @click="resetFilters">重置筛选</el-button>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { saveAs } from 'file-saver'
import { useFilters } from '../composables/useFilters'
import { exportExcel } from '../api'

const { state, options, filteredUniversities, resetFilters, onTypeChange } = useFilters()

async function handleExport() {
  try {
    const params = {}
    if (state.universityType) params.university_type = state.universityType
    if (state.universityName) params.university_name = state.universityName
    if (state.degree) params.degree = state.degree
    if (state.graduationYear) params.graduation_year = state.graduationYear
    if (state.keyword) params.keyword = state.keyword

    const blob = await exportExcel(params)
    saveAs(blob, '就业数据.xlsx')
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败，请稍后重试')
  }
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  margin-bottom: 20px;
  padding: 14px 18px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
</style>
