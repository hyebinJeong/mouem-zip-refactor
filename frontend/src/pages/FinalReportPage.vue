<script setup>
import { ref, onMounted } from 'vue';
import DiagnosisGradeInfoModal from '@/components/final-report/DiagnosisGradeInfoModal.vue';
import FinalGrade from '@/components/final-report/FinalGrade.vue';
import FinalJeonse from '@/components/final-report/FinalJeonse.vue';
import FinalRegistry from '@/components/final-report/FinalRegistry.vue';
import FinalChecklist from '@/components/final-report/FinalChecklist.vue';
import { useNavigation } from '@/composables/final-report/useNavigation';
import { getFinalReport } from '@/api/finalReport';
import FinalJeonseCard from '@/components/final-report/FinalJeonseCard.vue';

const showModal = ref(false);

const openModal = () => {
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const { goToHome, goToMyPage } = useNavigation();

// 임시 목 데이터
const reportId = 1;
const reportData = ref({
  registryRating: '보통',
  jeonseRatioRating: '위험',
  checklistRating: '안전',
  jeonseRatio: 80,
  regionAvgJeonseRatio: 75.0,
  jeonseDeposit: 28000, // 만원 단위
  salePrice: 36000,
  username: '버디',
  checked: [true, true, false, true, false, true, true, true, false],
});

// reportData.value = null;
// onMounted(async () => {
//   const res = await getFinalReport(reportId);
//   // 안전하게 받아온 데이터가 없으면 기본값 할당
//   reportData.value = {
//     registryRating: res?.registryRating ?? '',
//     jeonseRatioRating: res?.jeonseRatioRating ?? '',
//     checklistRating: res?.checklistRating ?? '',
//     jeonseRatio: res?.jeonseRatio ?? 0,
//     regionAvgJeonseRatio: res?.regionAvgJeonseRatio ?? 0,
//     salePrice: res?.salePrice ?? 0,
//     jeonseDeposit: res?.jeonseDeposit ?? 0,
//     username: res?.username ?? '사용자',
//     checked: res?.checked ?? []
//   };
// });
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
    <!-- v-if로 reportData 존재 확인 후 렌더링하여 undefined 방지 -->

    <!-- 등급 -->
    <div class="final-grade-wrap">
      <FinalGrade
        v-if="reportData"
        :registry="reportData.registryRating"
        :jeonse="reportData.jeonseRatioRating"
        :checklist="reportData.checklistRating"
      />
    </div>

    <hr class="my-4 border-top border-secondary-subtle w-75 mx-auto" />

    <!-- 전세가율 -->
    <div class="final-jeonse-wrap mt-5 mb-4">
      <div v-if="reportData.jeonseRatioRating !== '판단보류'">
        <h2 style="margin-bottom: 2rem">
          {{ reportData.username }}님의 전세가율을 분석했어요.
        </h2>
        <div
          class="d-flex flex-column flex-md-row justify-content-center align-items-end"
          style="max-width: 960px; margin: 0 auto; height: auto"
        >
          <div class="final-jeonse-col" style="width: 100%; max-width: 480px">
            <FinalJeonse
              v-if="reportData"
              :jeonseRatio="reportData.jeonseRatio"
              :regionAvgJeonseRatio="reportData.regionAvgJeonseRatio"
            />
          </div>
          <div class="final-jeonse-col">
            <FinalJeonseCard
              v-if="reportData"
              :salePrice="reportData.salePrice"
              :jeonseDeposit="reportData.jeonseDeposit"
              :jeonseRatio="reportData.jeonseRatio"
              :jeonseRatioRating="reportData.jeonseRatioRating"
              :username="reportData.username"
            />
          </div>
        </div>
      </div>
      <div v-else>
        <h2>
          {{ reportData.username }}님의 전세가율은 판단보류 등급으로, 분석이
          어려워요.
        </h2>
      </div>
    </div>

    <hr class="my-4 border-top border-secondary-subtle w-75 mx-auto" />

    <!-- 등기부등본 -->
    <div class="final-registry-wrap mt-5 mb-4">
      <h2>{{ reportData.username }}님의 등기부등본을 분석했어요.</h2>
      <div style="margin-bottom: 5rem">
        <FinalRegistry />
      </div>
    </div>

    <hr class="my-4 border-top border-secondary-subtle w-75 mx-auto" />

    <!-- 체크리스트 -->
    <div class="final-checklist-wrap mt-5 mb-3" style="margin: 6rem 0">
      <h2>{{ reportData.username }}님이 체크하지 않은 항목이에요.</h2>
      <p class="mb-4">향후 불이익을 방지하려면 지금 확인하는 것이 좋아요.</p>
      <FinalChecklist :checked="reportData.checked" />
    </div>
    <div class="final-btn-wrap d-flex justify-content-center">
      <button
        class="btn btn-primary px-4 py-2 rounded-3 background-main"
        @click="goToHome"
      >
        홈으로 이동
      </button>
      <button
        class="btn btn-primary px-4 py-2 rounded-3 background-main"
        @click="goToMyPage"
      >
        마이페이지로 이동
      </button>
    </div>
  </div>
</template>

<style scoped>
.final-jeonse-col {
  width: 100%;
  max-width: 480px;
}
.background-main {
  background: #1a80e5;
  color: #fff;
}

.final-btn-wrap {
  column-gap: 14rem;
}

@media (max-width: 768px) {
  .final-jeonse-col {
    width: 100% !important;
  }
}
</style>
