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
  <div class="FinalGrade">
    <div class="donut-wrap">
      <div class="donut-item" v-if="registry">
        <DonutChart
          :grade="registry"
          :color="gradeColor[registry] || '#ddd'"
          label="등기부등본"
        />
      </div>
      <div class="donut-item" v-if="jeonse">
        <DonutChart
          :grade="jeonse"
          :color="gradeColor[jeonse] || '#ddd'"
          label="전세가율"
        />
      </div>
      <div class="donut-item" v-if="checklist">
        <DonutChart
          :grade="checklist"
          :color="gradeColor[checklist] || '#ddd'"
          label="체크리스트"
        />
      </div>
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
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  justify-items: center;
  gap: 4rem 6rem;
  margin: 1rem 0;
}

.donut-item {
  width: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

@media (max-width: 768px) {
  .donut-wrap {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
}
</style>
