<script setup>
defineProps({
  show: Boolean, // 부모 컴포넌트로부터 표시 여부
});

const gradeList = [
  {
    title: '안전 등급',
    colorClass: 'text-primary',
    items: [
      '등기분석: 권리 침해 사항이 하나도 없는 경우',
      '전세가율: -5% 이하',
      '체크리스트: 모두 체크',
    ],
  },
  {
    title: '보통 등급',
    colorClass: 'text-success',
    items: [
      '등기분석: 최근 2년 이내에 권리침해 사항이 있는 경우',
      '전세가율: +0~4%',
      '체크리스트: 입주 전 수리에 대해 확인, 임차 주택의 사용/관리 수선 확인 미체크 시',
    ],
  },
  {
    title: '주의 등급',
    colorClass: 'text-warning',
    items: [
      '등기분석: 최근 1년 이내에 권리침해 사항이 있는 경우',
      '전세가율: 해당 지역 평균보다 +5% 이상',
      '체크리스트: 불리한 특약 확인 미체크 시',
    ],
  },
  {
    title: '위험 등급',
    colorClass: 'text-danger',
    items: [
      '등기분석: 현재 기준 권리침해 사항이 하나라도 있는 경우',
      '전세가율: 해당지역 평균보다 +10% 이상',
      '체크리스트: 임대인의 미납 세금 확인, 확정일자, 등기부등본상 소유주 일치 여부, 선순위 세입자 항목, 주소일치 여부, 전입신고 가능 여부 중 하나라도 미체크 시',
    ],
  },
  {
    title: '판단 보류 등급',
    colorClass: 'text-muted',
    items: [
      '등기분석: 최근 2년 이내에 권리침해 사항이 있는 경우',
      '전세가율: +0~4%',
      '체크리스트: 입주 전 수리에 대해 확인, 임차 주택의 사용/관리 수선 확인 미체크 시',
    ],
  },
];
</script>

<template>
  <div
    v-if="show"
    class="DiagnosisModalPage d-flex justify-content-center align-items-center bg-dark bg-opacity-50 position-fixed top-0 start-0 w-100"
  >
    <div
      class="diagnosis-box bg-white rounded-4 shadow-sm p-4 d-flex flex-column"
    >
      <h1 class="fs-4 fw-bold text-center mb-3 my-2">진단 등급 판정 기준</h1>

      <div class="diagnosis-content flex-grow-1 overflow-auto px-1 py-2">
        <div
          v-for="(grade, index) in gradeList"
          :key="index"
          class="mb-4 text-start"
        >
          <div :class="[grade.colorClass, 'fw-bold', 'mb-2']">
            {{ grade.title }}
          </div>
          <div class="bg-light rounded-3 p-3">
            <div v-for="(item, i) in grade.items" :key="i" class="mb-1">
              <p class="mb-1">{{ item }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="text-center mt-3">
        <button
          class="btn btn-primary px-4 py-2 rounded-3"
          @click="$emit('close')"
        >
          확인 완료
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.DiagnosisModalPage {
  overflow: hidden;
  height: 100dvh;
  z-index: 1050;
}

.diagnosis-box {
  width: 95%;
  max-width: 720px;
  height: calc(100dvh - 130px);
}

@media (max-width: 576px) {
  .diagnosis-box {
    width: 100%;
    height: 95vh;
    border-radius: 0;
  }
}
</style>
