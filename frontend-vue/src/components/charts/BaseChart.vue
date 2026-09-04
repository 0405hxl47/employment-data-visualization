<template>
  <div ref="chartRef" :style="{ height: height, width: '100%' }">
    <div v-if="!option" class="empty-tip">暂无数据</div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useChart } from '../../composables/useChart'

const props = defineProps({
  option: { type: Object, default: null },
  height: { type: String, default: '380px' }
})

const chartRef = ref(null)
const { setOption } = useChart(chartRef)

watch(
  () => props.option,
  (opt) => {
    if (opt) {
      setOption(opt)
    }
  },
  { deep: true }
)
</script>

<style scoped>
.empty-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-size: 14px;
}
</style>
