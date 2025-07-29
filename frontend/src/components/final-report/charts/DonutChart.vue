<script setup>
import { Chart, ArcElement, plugins } from 'chart.js';
import { Doughnut } from 'vue-chartjs';

Chart.register(ArcElement);

const props = defineProps({
  grade: String,
  color: String,
  label: String,
});

const chartData = {
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

const chartOptions = {
  plugins: {
    legend: { display: false },
    datalabels: {
      display: false,
    },
  },
};
</script>

<template>
  <div class="DonutChart">
    <Doughnut :data="chartData" :options="chartOptions" />
    <div class="donut-center-text" :style="{ color: props.color }">
      {{ grade }}
    </div>
    <div class="donut-label">{{ label }}</div>
  </div>
</template>

<style scoped>
.DonutChart {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 2rem;
}
.donut-center-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.5rem;
  font-weight: bold;
}

.donut-label {
  text-align: center;
  margin-top: 0.75rem;
  font-size: 1.3rem;
  font-weight: 700;
  color: #000;
}
</style>
