<template>
  <div v-if="result && result.analysis !== null" class="list-group">
    <div class="d-flex flex-column align-items-center mt-3 p-3">
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
      <p v-if="result" class="text-center text-secondary mb-4">
        사용자님이 올려주신 등기부등본은
        <span
          :class="{
            'text-success': result.rating === '보통',
            'text-danger': result.rating === '위험',
            'text-primary': result.rating === '양호',
          }"
          class="fw-bold"
        >
          {{ result.rating }} 등급
        </span>
        입니다.
      </p>

      <!-- 분석 결과 카드 -->
      <!-- 위험요소 리스트 - Bootstrap 5 -->
      <div class="col-6 p-4 overflow-auto" v-if="result && result.analysis">
        <h3 class="h5 fw-bold mb-4 text-center">
          어떤 점이 위험한지 하나씩 확인해보세요.
        </h3>

        <div
          v-for="item in analysisItems"
          :key="item.key + '-details'"
          class="card mb-3"
        >
          <div class="card-body">
            <h5 class="card-title">{{ item.label }}</h5>
            <span
              class="badge"
              :class="hasInfo(item.key) ? 'bg-danger' : 'bg-success'"
            >
              {{
                hasInfo(item.key) ? '해당 내역이 있어요' : '해당 내역이 없어요'
              }}
            </span>
          </div>
        </div>
      </div>

      <!-- PDF 뷰어 -->
      <div v-if="result?.fileUrl" class="w-100 mt-4" style="max-width: 64rem">
        <PDFView :pdfUrl="result.fileUrl" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import PDFView from '@/components/PDFView.vue';

const route = useRoute();
const result = ref(null);

const analysisItems = [
  { label: '경매', key: 'auctionInfos' },
  { label: '가압류', key: 'provisionalSeizureInfos' },
  { label: '압류', key: 'seizureInfos' },
  { label: '가처분', key: 'injunctionInfos' },
  { label: '신탁등기', key: 'trustInfos' },
  { label: '근저당권', key: 'mortgageInfos' },
  { label: '전세권설정', key: 'jeonseRightInfos' },
  { label: '가등기', key: 'provisionalRegistrationInfos' },
];

// 상세 정보가 있는지 확인하는 computed
const hasAnyDetails = computed(() => {
  if (!result.value?.analysis) return false;

  return analysisItems.some((item) => result.value.analysis[item.key]?.length);
});

// 내역 존재 여부 체크 함수
function hasInfo(key) {
  return result.value?.analysis?.[key]?.length > 0;
}

onMounted(async () => {
  const registerId = route.params.registerId;
  try {
    const res = await axios.get(`/safety-check/${registerId}`);
    result.value = res.data;
    console.log(result.value);
  } catch (e) {
    console.error('분석 결과 가져오기 실패:', e);
  }
});
</script>

<style scoped>
/* 추가 스타일 필요하면 여기에 */
</style>
