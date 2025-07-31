<script setup>
import BarChart from './charts/BarChart.vue';

const props = defineProps({
  jeonseRatio: { type: Number, default: 0 },
  regionAvgJeonseRatio: { type: Number, default: 0 },
});
</script>

<template>
  <div class="FinalJeonse">
    <BarChart
      :jeonseRatio="jeonseRatio"
      :regionAvgJeonseRatio="regionAvgJeonseRatio"
    />
  </div>
</template>

<style scoped>
/* .FinalJeonse {
} */
</style>
