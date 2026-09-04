<template>
  <div class="chart-card">
    <div class="card-title">城市就业人数 Top 15</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getCity } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getCity(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    const sorted = [...data]
      .sort((a, b) => b.count - a.count)
      .slice(0, 15)
      .reverse()

    chartOption.value = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params) => {
          const p = params[0]
          const item = sorted[sorted.length - 1 - p.dataIndex] || sorted[p.dataIndex]
          const province = item ? item.province || '' : ''
          return `${p.name}${province ? ' (' + province + ')' : ''}<br/>就业人数: ${p.value}`
        }
      },
      grid: { left: 90, right: 30, top: 10, bottom: 20 },
      xAxis: { type: 'value' },
      yAxis: {
        type: 'category',
        data: sorted.map(d => d.city),
        axisLabel: { fontSize: 11 }
      },
      series: [{
        type: 'bar',
        data: sorted.map((d, i) => ({
          value: d.count,
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
