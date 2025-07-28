<template>
  <div class="p-4" style="background-color: #f7f9fc; min-height: 70vh">
    <div class="d-flex align-items-center mb-3">
      <i
        class="fas fa-arrow-left me-2"
        style="cursor: pointer"
        @click="goBack"
      ></i>
      <h5 class="fw-bold mb-0">특약사항 추가</h5>
    </div>
    <p class="text-muted">
      선택한 특약사항에 대해 확인하고 추가할 수 있는 페이지입니다.
    </p>

    <form @submit.prevent="submitForm" class="mt-4">
      <div class="mb-3">
        <label class="form-label fw-semibold">특약분류</label>
        <input
          v-model="type"
          type="text"
          class="form-control"
          placeholder="예: 안전 장치 조항"
        />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">중요도</label>
        <select v-model="importance" class="form-select">
          <option disabled value="">중요도 선택</option>
          <option value="높음">높음</option>
          <option value="보통">보통</option>
          <option value="낮음">낮음</option>
        </select>
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">설명</label>
        <textarea
          v-model="description"
          rows="4"
          class="form-control"
          placeholder="설명을 입력해주세요."
        ></textarea>
      </div>

      <div class="text-end mt-4">
        <button type="button" class="btn btn-secondary me-2" @click="goBack">
          취소
        </button>
        <button type="submit" class="btn btn-primary">추가</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const type = ref('');
const importance = ref('');
const description = ref('');

const goBack = () => {
  router.back();
};

const submitForm = () => {
  console.log({
    type: type.value,
    importance: importance.value,
    description: description.value,
  });
  goBack();
};
</script>
