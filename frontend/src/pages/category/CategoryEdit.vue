<!-- src/views/category/CategoryEdit.vue -->
<template>
  <div
    class="p-4 position-relative"
    style="background-color: #f7f9fc; min-height: 70vh"
  >
    <!-- 상단 화살표와 제목 -->
    <div class="d-flex align-items-center mb-3">
      <i
        class="fas fa-arrow-left me-2"
        style="cursor: pointer"
        @click="goBack"
      ></i>
      <h5 class="fw-bold mb-0">카테고리 수정</h5>
    </div>
    <p class="text-muted">기존 카테고리 정보를 수정합니다.</p>

    <!-- 수정 폼 -->
    <form @submit.prevent="handleSubmit" class="mt-4">
      <div class="mb-3">
        <label class="form-label fw-semibold">카테고리명</label>
        <input
          v-model="category_name"
          type="text"
          class="form-control"
          placeholder="카테고리를 입력해주세요."
        />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">설명</label>
        <textarea
          v-model="category_description"
          class="form-control"
          placeholder="카테고리 설명을 입력해주세요."
        ></textarea>
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">색상</label>
        <input
          v-model="category_color"
          type="color"
          class="form-control form-control-color"
        />
      </div>

      <!-- 버튼 묶음 -->
      <div class="text-end mt-4">
        <button type="button" class="btn btn-secondary me-2" @click="goBack">
          취소
        </button>
        <button type="submit" class="btn btn-primary">수정</button>
        <button type="button" class="btn btn-danger ms-2" @click="handleDelete">
          삭제
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const id = route.params.id; // 수정할 카테고리 ID

const category_name = ref('');
const category_description = ref('');
const category_color = ref('#000000');

const goBack = () => {
  router.back();
};

const handleSubmit = () => {
  console.log('수정됨:', {
    category_name: category_name.value,
    category_description: category_description.value,
    category_color: category_color.value,
  });
  goBack();
};

const handleDelete = () => {
  console.log('삭제됨:', id);
  goBack();
};

onMounted(() => {
  // 예시로 기존 카테고리 정보 설정
  category_name.value = '전세권 / 안전장치';
  category_description.value = '보호 장치 관련 용어를 분류합니다.';
  category_color.value = '#33CC99';
});
</script>
