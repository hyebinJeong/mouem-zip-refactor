<script setup>
import { Chart, ArcElement } from 'chart.js';
import { Doughnut } from 'vue-chartjs';
import { ref, watch } from 'vue';

Chart.register(ArcElement);

const props = defineProps({
  grade: { type: String, default: '' },
  color: { type: String, default: '#ddd' },
  label: { type: String, default: '' },
});

const chartData = ref(null);
const chartOptions = ref(null);

watch(
  () => [props.grade, props.color],
  () => {
    if (!props.grade || !props.color) {
      chartData.value = null;
      chartOptions.value = null;
      return;
    }

    chartData.value = {
      labels: [],
      datasets: [
        {
          data: [100],
          backgroundColor: [props.color],
          borderWidth: 0,
          cutout: '90%',
        },
      ],
    };

    chartOptions.value = {
      responsive: false,
      plugins: {
        legend: { display: false },
        tooltip: { enabled: false },
        datalabels: { display: false },
      },
    };
  },
  { immediate: true }
);
</script>

<template>
  <div class="DonutChart" v-if="chartData && chartOptions">
    <div class="canvas-container">
      <Doughnut :data="chartData" :options="chartOptions" />
      <div class="donut-center-text" :style="{ color: props.color }">
        {{ props.grade }}
      </div>
    </div>
    <div class="donut-label">{{ props.label }}</div>
  </div>
</template>

<style scoped>
.DonutChart {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto 2rem auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

.donut-center-text {
  position: absolute;
  top: 70%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.5rem;
  font-weight: bold;
}

.donut-label {
  text-align: center;
  margin-top: 0.75rem;
  font-size: 1.6rem;
  font-weight: 700;
  color: #000;
}
</style>
