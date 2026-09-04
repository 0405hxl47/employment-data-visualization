<template>
  <div class="chart-card">
    <div class="card-title">高校类型-行业-学历 旭日图</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getSunburst } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getSunburst(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    chartOption.value = {
      color: COLORS,
      tooltip: { trigger: 'item' },
      series: [{
        type: 'sunburst',
        data: data,
        radius: ['12%', '90%'],
        label: { fontSize: 10, rotate: 'radial' },
        emphasis: { focus: 'ancestor' },
        levels: [
          {},
          {
            r0: '12%', r: '40%',
            itemStyle: { borderWidth: 2 },
            label: { rotate: 'tangential', fontSize: 11 }
          },
          {
            r0: '40%', r: '68%',
            label: { align: 'right', fontSize: 10 }
          },
          {
            r0: '68%', r: '90%',
            label: { position: 'outside', fontSize: 9, silent: false },
            itemStyle: { borderWidth: 1 }
          }
        ]
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
