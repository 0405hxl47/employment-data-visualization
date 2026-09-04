<template>
  <div class="chart-card">
    <div class="card-title">就业类型分布</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getEmploymentType } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getEmploymentType(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    chartOption.value = {
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', right: 10, top: 'center' },
      color: COLORS,
      series: [{
        type: 'pie',
        radius: ['40%', '72%'],
        center: ['40%', '50%'],
        label: { formatter: '{b}\n{d}%' },
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' }
        },
        data: data.map(d => ({ name: d.employment_type, value: d.count }))
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
