<!-- src/views/term/TermAdd.vue -->
<template>
  <div class="p-4" style="background-color: #f7f9fc; min-height: 70vh">
    <div class="d-flex align-items-center mb-3">
      <i
        class="fas fa-arrow-left me-2"
        style="cursor: pointer"
        @click="goBack"
      ></i>
      <h5 class="fw-bold mb-0">용어 추가</h5>
    </div>
    <p class="text-muted">새로운 단어를 추가할 수 있는 페이지입니다.</p>

    <form @submit.prevent="submitForm" class="mt-4">
      <div class="mb-3">
        <label class="form-label fw-semibold">단어</label>
        <input
          v-model="term_name"
          type="text"
          class="form-control"
          placeholder="단어를 입력해주세요."
        />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">정의</label>
        <input
          v-model="term_define"
          type="text"
          class="form-control"
          placeholder="정의를 입력해주세요."
        />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">예시</label>
        <input
          v-model="term_example"
          type="text"
          class="form-control"
          placeholder="예시를 입력해주세요."
        />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">주의</label>
        <input
          v-model="term_caution"
          type="text"
          class="form-control"
          placeholder="주의사항을 입력해주세요."
        />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">카테고리</label>
        <select v-model="category_id" class="form-select">
          <option disabled value="">카테고리를 선택하세요.</option>
          <option
            v-for="category in categories"
            :key="category.category_id"
            :value="category.category_id"
          >
            {{ category.category_name }}
          </option>
        </select>
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
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const term_name = ref('');
const term_define = ref('');
const term_example = ref('');
const term_caution = ref('');
const category_id = ref('');
const categories = ref([]);

const goBack = () => {
  router.back(); // 🔄 뒤로가기
};

const submitForm = () => {
  // 여기에 API 요청 작성
  console.log({
    term_name: term_name.value,
    term_define: term_define.value,
    term_example: term_example.value,
    term_caution: term_caution.value,
    category_id: category_id.value,
  });
  goBack();
};

onMounted(() => {
  // API로 카테고리 목록 불러오기 예시
  categories.value = [
    { category_id: 1, category_name: '전세권 / 안전장치' },
    { category_id: 2, category_name: '계약' },
  ];
});
</script>
