<template>
  <div
      class="modal fade show d-block"
      tabindex="-1"
      style="background-color: rgba(0, 0, 0, 0.5);"
      v-if="term"
      @click.self="$emit('close')"
  >
    <div class="modal-dialog modal-dialog-centered custom-dialog">
      <div class="modal-content custom-modal">
        <!-- 닫기 버튼 -->
        <div class="d-flex justify-content-end p-3">
          <button type="button" class="btn-close" @click="$emit('close')"></button>
        </div>

        <div class="modal-body pt-2">
          <div class="text-center">
            <h5 class="modal-title term-title">{{ term.termName }}</h5>
            <span class="badge custom-badge mb-4 " :style="{ backgroundColor: term.categoryColor }">{{ term.categoryName }}</span>
          </div>

          <!-- 콘텐츠 영역 (좌측 정렬) -->
          <div class="content-area mx-auto">
            <!-- 정의 -->
            <div class="content-row">
              <div class="content-label">정의</div>
              <div class="content-box">{{ term.termDefine }}</div>
            </div>

            <!-- 주의 -->
            <div class="content-row mt-3">
              <div class="content-label">주의</div>
              <div class="content-box">{{ term.termCaution }}</div>
            </div>

            <!-- 예시 -->
            <div class="content-row mt-3">
              <div class="content-label">예시</div>
              <div class="content-box">{{ term.termExample }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TermModal',
  props: {
    term: Object
  }
}
</script>

<style scoped>
/* 모달 크기 */
.custom-dialog {
  max-width: 800px;
  width: 100%;
}

/* 모달 본체 */
.custom-modal {
  background-color: #E8F4FF;
  border-radius: 20px;
  padding-bottom: 20px;
  min-height: 400px;
}

.modal-title{
  font-weight: 700;
}

.term-title{
  margin-top: -20px; /* 위로 10px 당김 */
  margin-bottom: 10px; /* 기존보다 아래 여백 줄임 */
}

/* 상단 배지 */
.custom-badge {
  background-color: rgba(183, 255, 172, 0.45);
  color: #1D7E52;
  font-size: 0.85rem;
  padding: 8px 15px;
  border-radius: 12px;
}

/* 콘텐츠 전체 영역 중앙에 */
.content-area {
  max-width: 600px;
}

/* 라벨 + 박스 한 줄 */
.content-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

/* 라벨 (정의/주의/예시) */
.content-label {
  width: 48px;
  font-weight: 700;
  font-size: 1rem;
  color: #000;
  padding-top: 6px;
}

/* 설명 박스 */
.content-box {
  background-color: white;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 0.95rem;
  line-height: 1.5;
  color: #333;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  flex: 1;
  word-break: break-word;
  white-space: pre-line;
}

.custom-badge {

  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 400;
  color: #000;
}

@media (max-width: 576px) {
  /* 모달 다이얼로그 */
  .custom-dialog {
    max-width: 90% !important;
    margin: 0 auto;
  }

  /* 모달 본체 */
  .custom-modal {
    padding: 16px 12px 20px 12px;
    border-radius: 16px;
    min-height: auto; /* 너무 큰 최소 높이 제거 */
  }

  /* 제목 */
  .term-title {
    font-size: 1.2rem;
    margin-top: -30px;
    margin-bottom: 8px;
  }

  /* 배지 */
  .custom-badge {
    font-size: 0.75rem;
    padding: 6px 12px;
  }

  /* 콘텐츠 전체 영역 */
  .content-area {
    max-width: 100%;
    padding: 0 4px;
  }

  /* 라벨 */
  .content-label {
    width: 40px;
    font-size: 0.9rem;
  }

  /* 설명 박스 */
  .content-box {
    font-size: 0.85rem;
    padding: 8px 10px;
  }

  /* 닫기 버튼 위치 조금 더 여유 */
  .btn-close {
    transform: scale(0.9);
  }
}
</style>