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
        입니다.
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
          <p style="font-size: 18px">주소: {{ result.address }}</p>
          <p style="font-size: 18px">
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
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import PDFView from '@/components/PDFView.vue';
import AnalysisCards from '@/components/AnalysisCards.vue';

const route = useRoute();
const result = ref(null);

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

<style scoped></style>
