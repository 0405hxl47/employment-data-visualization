<template>
  <div class="chart-card">
    <div class="card-title">各学历平均薪资对比</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getSalaryByDegree } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { DEGREE_COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

const degreeOrder = ['博士', '硕士', '本科', '专科']

async function fetchData() {
  try {
    const data = await getSalaryByDegree(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    const sorted = [...data].sort((a, b) => {
      const ia = degreeOrder.indexOf(a.degree)
      const ib = degreeOrder.indexOf(b.degree)
      return (ia === -1 ? 99 : ia) - (ib === -1 ? 99 : ib)
    })

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
            样本数: ${item.sample_count || '-'}`
        }
      },
      grid: { left: 50, right: 20, top: 20, bottom: 30 },
      xAxis: {
        type: 'category',
        data: sorted.map(d => d.degree),
        axisLabel: { fontSize: 13 }
      },
      yAxis: {
        type: 'value',
        axisLabel: { formatter: '¥{value}' }
      },
      series: [{
        type: 'bar',
        data: sorted.map(d => ({
          value: d.avg_salary,
          itemStyle: { color: DEGREE_COLORS[d.degree] || '#3949ab' }
        })),
        barMaxWidth: 50,
        itemStyle: { borderRadius: [4, 4, 0, 0] },
        label: { show: true, position: 'top', formatter: '¥{c}' }
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
