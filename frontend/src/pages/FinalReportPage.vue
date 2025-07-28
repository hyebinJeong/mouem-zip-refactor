<script setup>
import { ref } from 'vue';
import DiagnosisGradeInfoModal from '@/components/final-report/DiagnosisGradeInfoModal.vue';
import FinalGrade from '@/components/final-report/FinalGrade.vue';
import FinalJeonse from '@/components/final-report/FinalJeonse.vue';
import FinalRegistry from '@/components/final-report/FinalRegistry.vue';
import FinalChecklist from '@/components/final-report/FinalChecklist.vue';

const showModal = ref(false);

const openModal = () => {
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};
</script>

<template>
  <div class="FinalReportPage container py-5 text-center">
    <h1>최종 분석이 완료되었어요.</h1>
    <p class="text-muted">
      모든 등급은
      <span
        class="text-primary text-decoration-underline"
        role="button"
        style="cursor: pointer"
        @click="openModal"
        >판정기준</span
      >에 의해 설정된 등급입니다.
    </p>
    <DiagnosisGradeInfoModal :show="showModal" @close="closeModal" />
    <FinalGrade />
    <hr class="my-4 border-top border-secondary-subtle w-75 mx-auto" />
    <h2>username님의 전세가율을 분석했어요.</h2>
    <FinalJeonse />
    <hr class="my-4 border-top border-secondary-subtle w-75 mx-auto" />
    <h2>username님의 등기부등본을 분석했어요.</h2>
    <FinalRegistry />
    <hr class="my-4 border-top border-secondary-subtle w-75 mx-auto" />
    <h2>username님이 체크하지 않은 항목이에요.</h2>
    <p>향후 불이익을 방지하려면 지금 확인하는 것이 좋아요.</p>
    <FinalChecklist />
    <div class="final-btn-wrap d-flex justify-content-center gap-5">
      <button class="btn btn-primary px-4 py-2 rounded-3 background-main">
        홈으로 이동
      </button>
      <button class="btn btn-primary px-4 py-2 rounded-3 background-main">
        마이페이지로 이동
      </button>
    </div>
  </div>
</template>

<style scoped>
/* .FinalReportPage {
} */

.background-main {
  background: #1a80e5;
  color: #fff;
}
</style>
