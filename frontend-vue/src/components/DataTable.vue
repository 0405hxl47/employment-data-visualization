<template>
  <div class="data-table-section">
    <div class="section-header">
      <h3 class="page-title">就业数据明细</h3>
    </div>
    <el-table
      :data="tableData"
      stripe
      border
      :row-class-name="rowClassName"
      v-loading="loading"
      style="width: 100%"
      max-height="480"
    >
      <el-table-column prop="university_name" label="高校" min-width="130" />
      <el-table-column prop="university_type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTagType(row.university_type)" size="small">
            {{ row.university_type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="graduation_year" label="年份" width="70" />
      <el-table-column prop="major_name" label="专业" min-width="120" />
      <el-table-column prop="degree" label="学历" width="70" />
      <el-table-column prop="employment_rate" label="就业率" width="85">
        <template #default="{ row }">
          <span :class="{ 'rate-low': row.employment_rate < 60 }">
            {{ row.employment_rate != null ? row.employment_rate + '%' : '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="employment_type" label="就业类型" min-width="100" />
      <el-table-column prop="industry" label="行业" min-width="120" />
      <el-table-column prop="province" label="省份" width="80" />
      <el-table-column prop="city" label="城市" width="80" />
      <el-table-column label="期望薪资" width="120">
        <template #default="{ row }">
          {{ row.salary_min != null && row.salary_max != null ? '¥' + row.salary_min + '-' + row.salary_max : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="salary_true" label="实际薪资" width="90">
        <template #default="{ row }">
          {{ row.salary_true != null ? '¥' + row.salary_true : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="达成率" width="80">
        <template #default="{ row }">
          {{ row.achievement_rate != null ? row.achievement_rate + '%' : '-' }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getEmployment } from '../api'
import { useFilters } from '../composables/useFilters'
import { UNI_TYPE_TAG } from '../utils/constants'

const { filterParams } = useFilters()
const tableData = ref([])
const loading = ref(false)

function getTagType(type) {
  return UNI_TYPE_TAG[type] || ''
}

function rowClassName({ row }) {
  if (row.employment_rate != null && row.employment_rate < 60) {
    return 'warning-row'
  }
  return ''
}

async function fetchData() {
  loading.value = true
  try {
    const data = await getEmployment(filterParams.value)
    tableData.value = Array.isArray(data) ? data : []
  } catch (e) {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>

<style scoped>
.data-table-section {
  margin-top: 24px;
}

.section-header {
  margin-bottom: 12px;
}

.rate-low {
  color: #e53935;
  font-weight: 600;
}

:deep(.warning-row) {
  background-color: #fff0f0 !important;
}

:deep(.warning-row:hover > td) {
  background-color: #ffe0e0 !important;
}
</style>
