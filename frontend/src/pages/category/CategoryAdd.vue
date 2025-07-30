<template>
  <div
    class="p-4 position-relative"
    style="background-color: #f7f9fc; min-height: 70vh"
  >
    <!-- 뒤로가기 화살표 + 제목 -->
    <div class="d-flex align-items-center mb-3">
      <i
        class="fas fa-arrow-left me-2"
        style="cursor: pointer"
        @click="goBack"
      ></i>
      <h5 class="fw-bold mb-0">카테고리 추가</h5>
    </div>
    <p class="text-muted">새로운 카테고리를 생성합니다.</p>

    <!-- 카테고리 추가 폼 -->
    <form @submit.prevent="handleSubmit" class="mt-4">
      <div class="mb-3">
        <label class="form-label fw-semibold">카테고리명</label>
        <input
          v-model="category_name"
          type="text"
          class="form-control"
          placeholder="카테고리를 입력해주세요."
          required
        />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">설명</label>
        <textarea
          v-model="category_description"
          class="form-control"
          placeholder="카테고리 설명을 입력해주세요."
          required
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
        <button type="submit" class="btn btn-primary">저장</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useCategoryStore } from '@/stores/categoryStore';

const router = useRouter();
const categoryStore = useCategoryStore();

const category_name = ref('');
const category_description = ref('');
const category_color = ref('#000000');

const goBack = () => {
  router.back();
};

const handleSubmit = async () => {
  try {
    await categoryStore.addCategory({
      categoryName: category_name.value,
      description: category_description.value,
      categoryColor: category_color.value,
    });
    goBack();
  } catch (error) {
    alert('카테고리 추가 중 오류가 발생했습니다.');
  }
};
</script>
