<template>
  <div class="chart-card">
    <div class="card-title">各高校深造去向分布</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getFurtherEducation } from '../../api'
import { useFilters } from '../../composables/useFilters'

const { filterParams } = useFilters()
const chartOption = ref(null)

const categories = [
  { key: '升学', name: '升学', color: '#3949ab' },
  { key: '出国留学', name: '出国留学', color: '#e53935' },
  { key: '考公', name: '考公', color: '#43a047' },
  { key: '高校/科研', name: '高校/科研', color: '#fb8c00' }
]

async function fetchData() {
  try {
    const data = await getFurtherEducation(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    const universities = data.map(d => d.university_name)

    const series = categories.map(cat => ({
      name: cat.name,
      type: 'bar',
      stack: 'total',
      emphasis: { focus: 'series' },
      itemStyle: { color: cat.color },
      data: data.map(d => d[cat.key] || 0)
    }))

    chartOption.value = {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      legend: { bottom: 0 },
      grid: { left: 140, right: 20, top: 10, bottom: 40 },
      xAxis: { type: 'value' },
      yAxis: {
        type: 'category',
        data: universities,
        axisLabel: { fontSize: 11, width: 120, overflow: 'truncate' }
      },
      series
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
