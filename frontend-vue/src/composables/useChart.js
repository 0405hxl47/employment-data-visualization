import { ref, onMounted, onUnmounted, shallowRef } from 'vue'
import * as echarts from 'echarts'

export function useChart(containerRef) {
  const chart = shallowRef(null)
  let resizeObserver = null

  const setOption = (option, notMerge = true) => {
    if (chart.value) {
      chart.value.setOption(option, notMerge)
    }
  }

  onMounted(() => {
    if (containerRef.value) {
      chart.value = echarts.init(containerRef.value)
      resizeObserver = new ResizeObserver(() => {
        chart.value && chart.value.resize()
      })
      resizeObserver.observe(containerRef.value)
    }
  })

  onUnmounted(() => {
    if (resizeObserver && containerRef.value) {
      resizeObserver.unobserve(containerRef.value)
      resizeObserver.disconnect()
    }
    if (chart.value) {
      chart.value.dispose()
      chart.value = null
    }
  })

  return { chart, setOption }
}
