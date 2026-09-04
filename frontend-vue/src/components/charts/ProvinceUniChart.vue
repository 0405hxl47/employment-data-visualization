<template>
  <div class="chart-card full-width">
    <div class="card-title">各高校就业省份流向（堆叠柱状图）</div>
    <div class="uni-filter">
      <el-select
        v-model="selectedUni"
        placeholder="选择高校"
        clearable
        filterable
        multiple
        collapse-tags
        style="width: 360px"
      >
        <el-option
          v-for="u in uniOptions"
          :key="u"
          :label="u"
          :value="u"
        />
      </el-select>
    </div>
    <BaseChart :option="chartOption" height="420px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getProvinceByUniversity } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams, filteredUniversities } = useFilters()
const chartOption = ref(null)
const selectedUni = ref([])
const uniOptions = ref([])

watch(filteredUniversities, (list) => {
  uniOptions.value = list.map(u => u.name)
}, { immediate: true })

async function fetchData() {
  try {
    const params = { ...filterParams.value }
    if (selectedUni.value.length > 0) {
      params.university_name = selectedUni.value[0]
    }
    const data = await getProvinceByUniversity(params)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }

    const provTotals = {}
    const uniSet = new Set()
    data.forEach(d => {
      uniSet.add(d.university_name)
      provTotals[d.province] = (provTotals[d.province] || 0) + d.count
    })

    const provinces = Object.entries(provTotals)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 10)
      .map(e => e[0])
    const universities = [...uniSet]

    const uniProvMap = {}
    data.forEach(d => {
      if (!uniProvMap[d.university_name]) uniProvMap[d.university_name] = {}
      uniProvMap[d.university_name][d.province] = d.count
    })

    const series = universities.map((uni, i) => ({
      name: uni,
      type: 'bar',
      stack: 'total',
      emphasis: { focus: 'series' },
      data: provinces.map(p => (uniProvMap[uni] && uniProvMap[uni][p]) || 0),
      itemStyle: { color: COLORS[i % COLORS.length] }
    }))

    chartOption.value = {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      legend: { type: 'scroll', bottom: 0, textStyle: { fontSize: 11 } },
      grid: { left: 80, right: 20, top: 10, bottom: 50 },
      xAxis: {
        type: 'category',
        data: provinces,
        axisLabel: { rotate: 30, fontSize: 11 }
      },
      yAxis: { type: 'value' },
      series
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
watch(selectedUni, fetchData, { deep: true })
</script>

<style scoped>
.uni-filter {
  margin-bottom: 12px;
}
</style>
