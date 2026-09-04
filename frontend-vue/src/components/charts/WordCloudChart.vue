<template>
  <div class="chart-card">
    <div class="card-title">岗位关键词词云</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import 'echarts-wordcloud'
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getWordcloud } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getWordcloud(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    chartOption.value = {
      tooltip: { show: true },
      series: [{
        type: 'wordCloud',
        shape: 'circle',
        sizeRange: [14, 52],
        rotationRange: [-45, 45],
        rotationStep: 15,
        gridSize: 8,
        drawOutOfBound: false,
        textStyle: {
          fontFamily: 'sans-serif',
          fontWeight: 'bold',
          color: () => COLORS[Math.floor(Math.random() * COLORS.length)]
        },
        emphasis: {
          textStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' }
        },
        data: data.map(d => ({ name: d.name, value: d.value }))
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
