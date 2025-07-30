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
import { useCategoryStore } from '@/stores/categoryStore';

const router = useRouter();
const route = useRoute();
const categoryStore = useCategoryStore();

const id = Number(route.params.id);

const category_name = ref('');
const category_description = ref('');
const category_color = ref('#000000');

const goBack = () => {
  router.back();
};

const loadCategory = () => {
  const category = categoryStore.getCategoryById(id);
  if (category) {
    category_name.value = category.categoryName;
    category_description.value = category.description;
    category_color.value = category.categoryColor;
  } else {
    alert('존재하지 않는 카테고리입니다.');
    goBack();
  }
};

const handleSubmit = async () => {
  try {
    await categoryStore.updateCategory(id, {
      categoryName: category_name.value,
      description: category_description.value,
      categoryColor: category_color.value,
    });
    goBack();
  } catch (error) {
    alert('카테고리 수정 중 오류가 발생했습니다.');
  }
};

const handleDelete = async () => {
  if (confirm('정말 삭제하시겠습니까?')) {
    try {
      await categoryStore.deleteCategory(id);
      goBack();
    } catch (error) {
      alert('삭제 중 오류가 발생했습니다.');
    }
  }
};

onMounted(() => {
  if (!categoryStore.categories.length) {
    categoryStore.fetchCategories().then(loadCategory);
  } else {
    loadCategory();
  }
});
</script>
