<template>
  <div class="pdf-viewer">
    <canvas ref="canvas"></canvas>
    <div class="controls">
      <button @click="prevPage" :disabled="pageNum <= 1">← 이전</button>
      <span>{{ pageNum }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="pageNum >= totalPages">
        다음 →
      </button>
    </div>
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
// legacy 경로에서 가져와야 내부 구조와 워커가 맞음
import {
  getDocument,
  GlobalWorkerOptions,
} from 'pdfjs-dist/legacy/build/pdf.js';
import pdfWorkerUrl from 'pdfjs-dist/legacy/build/pdf.worker.min.js?url';

GlobalWorkerOptions.workerSrc = pdfWorkerUrl;

const props = defineProps({
  pdfUrl: String,
});

const canvas = ref(null);
let pdfDoc = null;
const pageNum = ref(1);
const totalPages = ref(0);
const errorMessage = ref('');

async function renderPage(num) {
  if (!pdfDoc) {
    console.error('[renderPage] pdfDoc가 존재하지 않습니다.');
    return;
  }
  try {
    console.log(`[renderPage] 시작 - 페이지 번호: ${num}`);
    const page = await pdfDoc.getPage(num);
    console.log('[renderPage] 페이지 객체 가져오기 성공');

    const viewport = page.getViewport({ scale: 1.5 });
    console.log(
      `[renderPage] viewport 계산 - width: ${viewport.width}, height: ${viewport.height}`
    );

    const ctx = canvas.value.getContext('2d');
    if (!ctx) {
      throw new Error('Canvas 2D context를 가져오지 못함');
    }
    console.log('[renderPage] Canvas 2D 컨텍스트 가져오기 성공');

    canvas.value.height = viewport.height;
    canvas.value.width = viewport.width;
    console.log(
      `[renderPage] 캔버스 크기 설정 완료 - width: ${canvas.value.width}, height: ${canvas.value.height}`
    );

    const renderTask = page.render({ canvasContext: ctx, viewport });
    console.log('[renderPage] 렌더링 시작');

    await renderTask.promise;
    console.log(`[renderPage] 페이지 ${num} 렌더링 완료`);
  } catch (e) {
    errorMessage.value = `페이지 렌더링 실패: ${e.message}`;
    console.error('[renderPage] 에러 발생:', e);
  }
}

async function loadPdf(url) {
  errorMessage.value = '';
  try {
    const loadingTask = getDocument(url);
    pdfDoc = await loadingTask.promise;
    totalPages.value = pdfDoc.numPages;
    pageNum.value = 1;
    await renderPage(pageNum.value);
  } catch (e) {
    errorMessage.value = `PDF 로딩 실패: ${e.message}`;
    console.error(e);
  }
}

watch(
  () => props.pdfUrl,
  (newUrl) => {
    if (newUrl) loadPdf(newUrl);
  }
);

onMounted(() => {
  if (props.pdfUrl) loadPdf(props.pdfUrl);
});

function prevPage() {
  if (pageNum.value <= 1) return;
  pageNum.value--;
  renderPage(pageNum.value);
}

function nextPage() {
  if (pageNum.value >= totalPages.value) return;
  pageNum.value++;
  renderPage(pageNum.value);
}
</script>

<style scoped>
.pdf-viewer {
  text-align: center;
  margin: 1rem auto;
}
canvas {
  border: 1px solid #ccc;
  max-width: 100%;
  height: auto;
  background-color: #f9f9f9;
}
.controls {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}
.error {
  color: red;
  margin-top: 1rem;
}
</style>
