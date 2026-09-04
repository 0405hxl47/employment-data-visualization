<template>
  <div class="chart-card full-width">
    <div class="card-title">行业薪资分布箱线图</div>
    <BaseChart :option="chartOption" height="480px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getSalaryBox } from '../../api'
import { useFilters } from '../../composables/useFilters'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getSalaryBox({ ...filterParams.value, group_by: 'industry' })
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    const categories = data.map(d => d.name || d.industry)
    const boxData = data.map(d => [d.min, d.q1, d.median, d.q3, d.max])

    chartOption.value = {
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          const d = data[params.dataIndex]
          if (!d) return ''
          return `<strong>${d.name || d.industry}</strong><br/>
            最大值: ¥${d.max}<br/>
            Q3: ¥${d.q3}<br/>
            中位数: ¥${d.median}<br/>
            Q1: ¥${d.q1}<br/>
            最小值: ¥${d.min}<br/>
            样本数: ${d.count || '-'}`
        }
      },
      grid: { left: 140, right: 30, top: 10, bottom: 30 },
      xAxis: {
        type: 'value',
        axisLabel: { formatter: '¥{value}' }
      },
      yAxis: {
        type: 'category',
        data: categories,
        axisLabel: { fontSize: 11, width: 120, overflow: 'truncate' }
      },
      series: [{
        type: 'boxplot',
        data: boxData,
        itemStyle: {
          color: '#e8eaf6',
          borderColor: '#3949ab',
          borderWidth: 2
        }
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
