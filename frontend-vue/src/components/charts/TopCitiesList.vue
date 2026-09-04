<template>
  <div class="chart-card full-width">
    <div class="card-title">就业热门城市 Top 10</div>
    <el-table
      :data="tableData"
      stripe
      border
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column label="排名" width="70" align="center">
        <template #default="{ $index }">
          <el-tag
            :type="$index < 3 ? 'danger' : 'info'"
            size="small"
            effect="dark"
          >
            {{ $index + 1 }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="city" label="城市" min-width="120" />
      <el-table-column prop="province" label="省份" min-width="100" />
      <el-table-column prop="count" label="就业人数" width="110" align="right">
        <template #default="{ row }">
          {{ row.count ? row.count.toLocaleString() : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="avg_salary" label="平均薪资" width="120" align="right">
        <template #default="{ row }">
          {{ row.avg_salary ? '¥' + row.avg_salary.toLocaleString() : '-' }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getMajorAnalysis } from '../../api'
import { useFilters } from '../../composables/useFilters'

const { filterParams } = useFilters()
const tableData = ref([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const data = await getMajorAnalysis(filterParams.value)
    const cities = data && data.top_cities
    if (!cities || cities.length === 0) {
      tableData.value = []
      return
    }
    tableData.value = cities.slice(0, 10)
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
