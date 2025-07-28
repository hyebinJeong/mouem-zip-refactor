<script setup>
import { ref } from 'vue';
import DiagnosisGradeInfoModal from '@/components/final-report/DiagnosisGradeInfoModal.vue';

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
  </div>
</template>

<style scoped>
/* .FinalReportPage {
} */
</style>
