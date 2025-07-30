<script setup>
import { onMounted } from 'vue';
import DonutChart from './charts/DonutChart.vue';
import axios from 'axios';
import { ref } from 'vue';

const gradeColor = {
  안전: '#00AEEF',
  보통: '#39B54A',
  주의: '#F7941D',
  위험: '#ED1C24',
  판단보류: '#6C757D',
};

const props = defineProps({
  registry: { type: String, default: '' },
  jeonse: { type: String, default: '' },
  checklist: { type: String, default: '' },
});
</script>

<template>
  <div class="FinalGrade mb-5">
    <div class="donut-wrap">
      <DonutChart
        v-if="registry"
        :grade="registry"
        :color="gradeColor[registry] || '#ddd'"
        label="등기부등본"
      />
      <DonutChart
        v-if="jeonse"
        :grade="jeonse"
        :color="gradeColor[jeonse] || '#ddd'"
        label="전세가율"
      />
      <DonutChart
        v-if="checklist"
        :grade="checklist"
        :color="gradeColor[checklist] || '#ddd'"
        label="체크리스트"
      />
    </div>
  </div>
</template>

<style scoped>
.FinalGrade {
  display: flex;
  justify-content: center;
  padding: 1rem;
  margin-bottom: 3rem;
}

.donut-wrap {
  display: flex;
  justify-content: center;
  gap: 10rem;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .donut-wrap {
    gap: 12rem;
    margin-top: 1rem;
    margin-bottom: 1rem;
  }
}
</style>
