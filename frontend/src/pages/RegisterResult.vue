<template>
  <div v-if="result && result.analysis !== null" class="list-group">
    <div class="d-flex flex-column align-items-center mt-3 p-3">
      <div class="text-center mb-4">
        <p class="fw-bold fs-5 mb-1">
          올려주신 등기부등본에 대해서 분석했어요.
        </p>
        <p class="fw-bold fs-5">
          <span style="color: #fe5252">위험 요소</span>는 없는지,
          <span style="color: #1a80e5">꼼꼼히</span> 살펴봤어요.
        </p>
      </div>
      <!-- 등급 표시 원형 -->
      <div
        class="rounded-circle d-flex align-items-center justify-content-center fw-bold mb-3 donut-thin"
        :style="{
          borderColor: gradeColor[result.rating] || '#6c757d',
          color: gradeColor[result.rating] || '#6c757d',
        }"
        style="width: 10rem; height: 10rem; font-size: 1.5rem"
      >
        {{ result.rating }}
      </div>
      <BuddyHelper @open-dictionary="openDictionaryModal" />
      <TermViewModal v-if="showDictionaryModal" @close="closeDictionaryModal" />
      <!-- 등급 설명 -->
      <div
        class="text-center px-4 py-3 mb-4"
        style="background-color: #f0f6ff; border-radius: 1rem; max-width: 640px"
      >
        <span class="fw-bold">{{ getGradeMessage(result.rating) }}</span>
      </div>

      <!-- 좌우분할 → 반응형으로 변경 -->
      <div class="row w-100 align-items-start analysis-container">
        <!-- PDF 뷰어 섹션 -->
        <div class="col-lg-6 col-12 pdf-section">
          <p class="fw-bold fs-5 mb-2" style="color: #151fae">
            어떤 점이 위험한지 하나씩 확인해보세요.
          </p>
          <div v-if="result?.fileUrl" class="pdf-wrapper">
            <PDFView :pdfUrl="result.fileUrl" />
          </div>
        </div>

        <!-- 분석 결과 섹션 -->
        <div class="col-lg-6 col-12 analysis-section">
          <p class="fw-bold fs-5 mb-2" style="color: #151fae">기본 정보</p>
          <p class="address-info">주소: {{ result.address }}</p>
          <p class="jeonse-rate-info">
            예상 전세가율:
            <span v-if="result.jeonseRate !== -1"
              >{{ result.jeonseRate }} %</span
            >
            <span v-else style="color: gray">판단 불가</span>
          </p>
          <div class="analysis-cards-wrapper">
            <AnalysisCards
              v-if="result && result.analysis"
              :analysis="result.analysis"
              :analysisItems="analysisItems"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="button-area">
      <button @click="showModal = true" class="checklist-button">
        체크리스트로 넘어가기
      </button>
    </div>

    <!-- 모달 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>체크리스트 작성</h3>
        </div>
        <div class="modal-body">
          <p>체크리스트를 작성하러 가시겠습니까?</p>
        </div>
        <div class="modal-footer">
          <button @click="goToChecklist" class="confirm-button">예</button>
          <button @click="goToHome" class="cancel-button">아니오</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import PDFView from '@/components/PDFView.vue';
import AnalysisCards from '@/components/AnalysisCards.vue';
import BuddyHelper from '@/components/BuddyHelper.vue';
import TermViewModal from '@/components/TermViewModal.vue';
import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const router = useRouter();
const result = ref(null);
const auth = useAuthStore();

const analysisItems = [
  { label: '경매', key: 'auctionInfos' },
  { label: '가압류', key: 'provisionalSeizureInfos' },
  { label: '압류', key: 'seizureInfos' },
  { label: '가처분', key: 'injunctionInfos' },
  { label: '근저당권', key: 'mortgageInfos' },
  { label: '신탁등기', key: 'trustInfos' },
  { label: '전세권설정', key: 'jeonseRightInfos' },
  { label: '가등기', key: 'provisionalRegistrationInfos' },
];

// 등급별 색상
const gradeColor = {
  안전: '#00AEEF',
  보통: '#39B54A',
  주의: '#F7941D',
  위험: '#ED1C24',
};

// 등급별 메시지 반환 함수
const getGradeMessage = (rating) => {
  switch (rating) {
    case '안전':
      return '사용자님이 올려주신 등기부등본은 안전합니다.';
    case '보통':
      return '사용자님이 올려주신 등기부등본은 위험 요소가 현재 말소됐지만 최근 2년 안에 기재됐던 내역이 있습니다.';
    case '주의':
      return '사용자님이 올려주신 등기부등본은 위험 요소가 현재 말소됐지만 최근 1년 안에 기재됐던 내역이 있습니다.';
    case '위험':
      return '사용자님이 올려주신 등기부등본은 위험합니다.';
    default:
      return '등기부등본을 분석 중입니다...';
  }
};

// 용어 모달 표시 상태
const showDictionaryModal = ref(false);

// 체크리스트 모달 표시 상태
const showModal = ref(false);

// 용어모달 열기/닫기 함수
const openDictionaryModal = () => {
  showDictionaryModal.value = true;
};
const closeDictionaryModal = () => {
  showDictionaryModal.value = false;
};

// 체크리스트 모달 관련 함수
const closeModal = () => {
  showModal.value = false;
};

// 체크리스트 모달창 예 클릭시 이벤트 함수
const goToChecklist = () => {
  showModal.value = false;
  router.push({
    path: '/checklist/checklist',
    query: {
      userId: auth.userId,
      registryId: route.params.registerId,
    },
  });
};

// 체크리스트 모달창 아니오 클릭시 이벤트 함수
const goToHome = () => {
  showModal.value = false;
  router.push('/');
};

onMounted(async () => {
  const registerId = route.params.registerId;
  try {
    // 두 개의 API 요청을 병렬로 처리
    const [safetyRes, jeonseRes] = await Promise.all([
      axios.get(`/api/safety-check/${registerId}`),
      axios.get(`/api/diagnosis/result?registerId=${registerId}`),
    ]);

    // jeonseRate를 기존 결과에 병합
    result.value = {
      ...safetyRes.data,
      jeonseRate: jeonseRes.data.jeonseRate,
    };
  } catch (e) {
    console.error('분석 결과 가져오기 실패:', e);
  }
});
</script>

<style scoped>
.donut-thin {
  border-width: 8px;
  border-style: solid;
  transition: all 0.3s ease;
}

/* 기본 정보 스타일 */
.address-info,
.jeonse-rate-info {
  font-size: 1.25rem;
}

/* 반응형 레이아웃 스타일 */
.analysis-container {
  min-height: 80vh;
}

/* 데스크톱 (lg 이상) - 992px 이상 */
@media (min-width: 992px) {
  .pdf-section {
    padding: 1rem;
    height: 80vh;
  }

  .pdf-wrapper {
    position: sticky;
    top: 1rem;
    max-height: calc(80vh - 2rem);
    overflow-y: auto;
  }

  .analysis-section {
    height: 80vh;
    overflow-y: auto;
    padding: 1rem;
    display: flex;
    flex-direction: column;
  }

  .analysis-cards-wrapper {
    flex: 1;
    overflow-y: auto;
    margin-top: 1rem;
  }
}

/* 태블릿 (md~lg) - 768px ~ 991px */
@media (max-width: 991.98px) and (min-width: 768px) {
  .analysis-container {
    min-height: auto;
  }

  .pdf-section {
    padding: 1rem;
    margin-bottom: 2rem;
  }

  .pdf-wrapper {
    max-height: 60vh;
    overflow-y: auto;
  }

  .analysis-section {
    padding: 1rem;
  }

  .analysis-cards-wrapper {
    margin-top: 1rem;
  }

  .address-info,
  .jeonse-rate-info {
    font-size: 1.1rem !important;
  }
}

/* 모바일 (sm~md) - 576px ~ 767px */
@media (max-width: 767.98px) and (min-width: 576px) {
  .analysis-container {
    min-height: auto;
  }

  .pdf-section {
    padding: 0.75rem;
    margin-bottom: 1.5rem;
  }

  .pdf-wrapper {
    max-height: 55vh;
    overflow-y: auto;
  }

  .analysis-section {
    padding: 0.75rem;
  }

  .analysis-cards-wrapper {
    margin-top: 1rem;
  }

  .address-info,
  .jeonse-rate-info {
    font-size: 1.05rem !important;
  }

  .checklist-button {
    padding: 15px 28px;
    font-size: 17px;
    min-width: 190px;
  }

  .modal-content {
    min-width: 350px;
    margin: 15px;
  }
}

/* 작은 모바일 (xs) - 576px 미만 */
@media (max-width: 575.98px) {
  .analysis-container {
    min-height: auto;
  }

  .pdf-section,
  .analysis-section {
    padding: 0.5rem;
  }

  .pdf-section {
    margin-bottom: 1rem;
  }

  .pdf-wrapper {
    max-height: 50vh;
  }

  .analysis-cards-wrapper {
    margin-top: 0.75rem;
  }

  .fw-bold.fs-5 {
    font-size: 1.1rem !important;
  }

  .address-info,
  .jeonse-rate-info {
    font-size: 1rem !important;
  }

  /* 버튼 및 모달 반응형 */
  .checklist-button {
    padding: 14px 24px;
    font-size: 16px;
    min-width: 180px;
  }

  .modal-content {
    min-width: 320px;
    margin: 20px;
  }

  .modal-footer {
    flex-direction: column;
  }

  .confirm-button,
  .cancel-button {
    width: 100%;
  }
}

/* 버튼 영역 - 중앙 정렬 */
.button-area {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  margin-top: 30px;
}

/* 메인 버튼 스타일 */
.checklist-button {
  background-color: #1a80e5;
  color: white;
  border: none;
  padding: 16px 32px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(26, 128, 229, 0.3);
  min-width: 200px;
}

.checklist-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(26, 128, 229, 0.4);
  background-color: #1570cc;
}

.checklist-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
}

/* 모달 오버레이 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(2px);
}

/* 모달 콘텐츠 */
.modal-content {
  background: white;
  border-radius: 16px;
  padding: 0;
  min-width: 400px;
  max-width: 500px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 모달 헤더 */
.modal-header {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  text-align: center;
}

/* 모달 바디 */
.modal-body {
  padding: 24px;
  text-align: center;
}

.modal-body p {
  margin: 0;
  font-size: 16px;
  color: #4b5563;
  line-height: 1.6;
}

/* 모달 푸터 */
.modal-footer {
  padding: 16px 24px 24px;
  display: flex;
  justify-content: center;
  gap: 16px;
}

/* 확인 버튼 */
.confirm-button {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
}

.confirm-button:hover {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

/* 취소 버튼 */
.cancel-button {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
}

.cancel-button:hover {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.confirm-button,
.cancel-button {
  width: 100%;
}
</style>
