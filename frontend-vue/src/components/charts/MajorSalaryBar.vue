<template>
  <div class="chart-card">
    <div class="card-title">专业平均薪资排名</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getMajorAnalysis } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getMajorAnalysis(filterParams.value)
    const stats = data && data.salary_stats
    if (!stats || stats.length === 0) {
      chartOption.value = null
      return
    }
    const sorted = [...stats].sort((a, b) => a.avg_salary - b.avg_salary)

    chartOption.value = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params) => {
          const p = params[0]
          const item = sorted[p.dataIndex]
          return `<strong>${p.name}</strong><br/>
            平均薪资: ¥${item.avg_salary}<br/>
            最低薪资: ¥${item.min_salary || '-'}<br/>
            最高薪资: ¥${item.max_salary || '-'}<br/>
            样本数: ${item.count || '-'}`
        }
      },
      grid: { left: 120, right: 30, top: 10, bottom: 20 },
      xAxis: {
        type: 'value',
        axisLabel: { formatter: '¥{value}' }
      },
      yAxis: {
        type: 'category',
        data: sorted.map(d => d.major_name),
        axisLabel: { fontSize: 11, width: 100, overflow: 'truncate' }
      },
      series: [{
        type: 'bar',
        data: sorted.map((d, i) => ({
          value: d.avg_salary,
          itemStyle: { color: COLORS[i % COLORS.length] }
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
