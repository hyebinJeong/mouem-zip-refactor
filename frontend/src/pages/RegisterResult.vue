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
        class="rounded-circle border border-4 d-flex align-items-center justify-content-center fw-bold mb-3"
        :class="{
          'border-success text-success': result && result.rating === '보통',
          'border-danger text-danger': result && result.rating === '위험',
          'border-primary text-primary': result && result.rating === '양호',
        }"
        style="width: 10rem; height: 10rem; font-size: 1.5rem"
      >
        {{ result ? result.rating : '로딩중' }}
      </div>
      <BuddyHelper @open-dictionary="openDictionaryModal" />
      <TermViewModal v-if="showDictionaryModal" @close="closeDictionaryModal" />
      <!-- 등급 설명 -->
      <div
        class="text-center px-4 py-3 mb-4"
        style="background-color: #f0f6ff; border-radius: 1rem; max-width: 640px"
      >
        <span class="fw-bold">사용자님이 올려주신 등기부등본은 </span>
        <span
          :class="{
            'text-success fw-bold': result.rating === '보통',
            'text-danger fw-bold': result.rating === '위험',
            'text-primary fw-bold': result.rating === '양호',
          }"
        >
          {{ result.rating }} 등급
        </span>
        <span class="fw-bold">입니다.</span>
      </div>
      <!-- 좌우분할 -->
      <div class="row w-100 align-items-start" style="height: 80vh">
        <div class="col-6" style="padding: 1rem">
          <!-- 위험 문구 문단 (상단 고정) -->
          <p class="fw-bold fs-5 mb-2" style="color: #151fae">
            어떤 점이 위험한지 하나씩 확인해보세요.
          </p>
          <div
            v-if="result?.fileUrl"
            style="
              position: sticky;
              top: 1rem;
              max-height: calc(80vh - 2rem);
              overflow-y: auto;
            "
          >
            <PDFView :pdfUrl="result.fileUrl" />
          </div>
        </div>
        <!-- 오른쪽 분석: 스크롤 가능 -->
        <div
          class="col-6"
          style="
            height: 80vh;
            overflow-y: auto;
            padding: 1rem;
            display: flex;
            flex-direction: column;
          "
        >
          <p class="fw-bold fs-5 mb-2" style="color: #151fae">기본 정보</p>
          <p style="font-size: 1.25rem">주소: {{ result.address }}</p>
          <p style="font-size: 1.25rem">
            예상 전세가율:
            <span v-if="result.jeonseRate !== -1"
              >{{ result.jeonseRate }} %</span
            >
            <span v-else style="color: gray">판단 불가</span>
          </p>
          <div style="flex: 1; overflow-y: auto; margin-top: 1rem">
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
          <p>체크리스트를 이어서 작성하시겠습니까?</p>
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

/* 반응형 디자인 */
@media (max-width: 480px) {
  .modal-content {
    min-width: 320px;
    margin: 20px;
  }

  .checklist-button {
    padding: 14px 24px;
    font-size: 16px;
    min-width: 180px;
  }

  .modal-footer {
    flex-direction: column;
  }

  .confirm-button,
  .cancel-button {
    width: 100%;
  }
}
</style>
