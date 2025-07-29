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
