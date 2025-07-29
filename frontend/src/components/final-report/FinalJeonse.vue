<script setup>
import BarChart from './charts/BarChart.vue';

const mockData = {
  jeonseRatio: 77.8,
  regionAvgJeonseRatio: 75.0,
};
</script>

<template>
  <div class="FinalJeonse">
    <!-- <h2>전세가율 컴포넌트</h2> -->
    <BarChart
      :jeonseRatio="mockData.jeonseRatio"
      :regionAvgJeonseRatio="mockData.regionAvgJeonseRatio"
    />
  </div>
</template>

<style scoped>
/* .FinalJeonse {
} */
</style>
