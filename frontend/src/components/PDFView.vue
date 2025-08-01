<template>
  <div class="pdf-viewer">
    <canvas ref="canvas"></canvas>
    <div class="controls-container">
      <div class="controls-segment">
        <button
          @click="prevPage"
          :disabled="pageNum <= 1"
          class="segment-button prev-button"
        >
          ← 이전
        </button>
        <div class="segment-info">{{ pageNum }} / {{ totalPages }}</div>
        <button
          @click="nextPage"
          :disabled="pageNum >= totalPages"
          class="segment-button next-button"
        >
          다음 →
        </button>
      </div>
    </div>
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
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
    return;
  }
  try {
    const page = await pdfDoc.getPage(num);

    const viewport = page.getViewport({ scale: 1.5 });

    const ctx = canvas.value.getContext('2d');
    if (!ctx) {
      throw new Error('Canvas 2D context를 가져오지 못함');
    }

    canvas.value.height = viewport.height;
    canvas.value.width = viewport.width;

    const renderTask = page.render({ canvasContext: ctx, viewport });

    await renderTask.promise;
  } catch (e) {
    errorMessage.value = `페이지 렌더링 실패: ${e.message}`;
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
.controls-container {
  display: flex;
  justify-content: center;
  width: 100%;
}
.controls-segment {
  display: inline-flex;
  background: #f2f2f7;
  border-radius: 12px;
  padding: 4px;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8ed;
}
.segment-button {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #3a3a3c;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  min-width: 80px;
}
.segment-button:hover:not(:disabled) {
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  color: #007aff;
}
.segment-button:active:not(:disabled) {
  transform: scale(0.98);
}
.segment-button:disabled {
  color: #8e8e93;
  cursor: not-allowed;
  opacity: 0.6;
}
.segment-info {
  padding: 12px 20px;
  font-weight: 600;
  color: #007aff;
  font-size: 14px;
  background: rgba(0, 122, 255, 0.1);
  border-radius: 8px;
  min-width: 60px;
  text-align: center;
}
.segment-button:focus {
  outline: 2px solid #007aff;
  outline-offset: 2px;
}
.error {
  color: red;
  margin-top: 1rem;
}
</style>
