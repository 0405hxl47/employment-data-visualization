<template>
  <div class="chart-card">
    <div class="card-title">{{ title }}</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getIndustry } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const props = defineProps({
  mode: { type: String, default: 'count' } // 'count' | 'salary'
})

const { filterParams } = useFilters()
const chartOption = ref(null)

const title = computed(() =>
  props.mode === 'count' ? '行业就业人数排名' : '行业平均薪资排名'
)

async function fetchData() {
  try {
    const data = await getIndustry(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    const sorted = [...data].sort((a, b) => {
      const va = props.mode === 'count' ? a.count : a.avg_salary
      const vb = props.mode === 'count' ? b.count : b.avg_salary
      return va - vb
    })
    const categories = sorted.map(d => d.industry)
    const values = sorted.map(d => props.mode === 'count' ? d.count : d.avg_salary)

    chartOption.value = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params) => {
          const p = params[0]
          const val = props.mode === 'salary' ? `¥${p.value}` : p.value
          return `${p.name}<br/>${p.seriesName}: ${val}`
        }
      },
      grid: { left: 140, right: 30, top: 10, bottom: 20 },
      xAxis: {
        type: 'value',
        axisLabel: {
          formatter: props.mode === 'salary' ? '¥{value}' : '{value}'
        }
      },
      yAxis: {
        type: 'category',
        data: categories,
        axisLabel: {
          fontSize: 11,
          width: 120,
          overflow: 'truncate'
        }
      },
      series: [{
        name: props.mode === 'count' ? '就业人数' : '平均薪资',
        type: 'bar',
        data: values.map((v, i) => ({
          value: v,
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
