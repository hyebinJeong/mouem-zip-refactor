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
  판단보류: '#FBC02D',
};

const registry = ref('');
const jeonse = ref('');
const checklist = ref('');

// 테스트용 리포트 id (추후 삭제 예정)
const reportId = 1;

const fetchGrades = async () => {
  try {
    // 백엔드 작업하고 주석 해제에서 실제 데이터 연동 예정
    // const {data} = await axios.get(`/api/final-report/${reportId}`);
    // registry.value = data.registryRating;
    // jeonse.value = data.jeonseRatioRating;
    // checklist.value = data.checklistRating;

    // 테스트용 데이터
    registry.value = '보통';
    jeonse.value = '판단보류';
    checklist.value = '안전';
  } catch {
    console.error('등급 데이터 가져오기 실패', err);
  }
};

onMounted(fetchGrades);
</script>

<template>
  <div class="FinalGrade mb-5">
    <div class="donut-wrap">
      <DonutChart
        v-if="registry"
        :grade="registry"
        :color="gradeColor[registry]"
        label="등기부등본"
      />
      <DonutChart
        v-if="jeonse"
        :grade="jeonse"
        :color="gradeColor[jeonse]"
        label="전세가율"
      />
      <DonutChart
        v-if="checklist"
        :grade="checklist"
        :color="gradeColor[checklist]"
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
}

.donut-wrap {
  display: flex;
  justify-content: center;
  gap: 8rem;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .donut-wrap {
    column-gap: 4rem;
    row-gap: 1.5rem;
  }
}
</style>
