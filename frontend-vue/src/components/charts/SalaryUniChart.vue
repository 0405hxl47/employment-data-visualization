<template>
  <div class="chart-card">
    <div class="card-title">各高校平均薪资排名</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getSalaryByUniversity } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { UNI_TYPE_COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getSalaryByUniversity(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    const sorted = [...data].sort((a, b) => a.avg_salary - b.avg_salary)

    chartOption.value = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params) => {
          const p = params[0]
          const item = sorted[p.dataIndex]
          return `${p.name} (${item.university_type})<br/>平均薪资: ¥${p.value}`
        }
      },
      grid: { left: 160, right: 30, top: 10, bottom: 20 },
      xAxis: {
        type: 'value',
        axisLabel: { formatter: '¥{value}' }
      },
      yAxis: {
        type: 'category',
        data: sorted.map(d => d.university_name),
        axisLabel: { fontSize: 11, width: 140, overflow: 'truncate' }
      },
      series: [{
        type: 'bar',
        data: sorted.map(d => ({
          value: d.avg_salary,
          itemStyle: { color: UNI_TYPE_COLORS[d.university_type] || '#3949ab' }
        })),
        barMaxWidth: 22,
        itemStyle: { borderRadius: [0, 4, 4, 0] }
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
