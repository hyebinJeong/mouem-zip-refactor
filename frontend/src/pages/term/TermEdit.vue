<!-- src/views/term/TermEdit.vue -->
<template>
  <div class="p-4" style="background-color: #f7f9fc; min-height: 70vh">
    <div class="d-flex align-items-center mb-3">
      <i
        class="fas fa-arrow-left me-2"
        style="cursor: pointer"
        @click="goBack"
      ></i>
      <h5 class="fw-bold mb-0">용어 수정</h5>
    </div>
    <p class="text-muted">
      선택한 단어에 대해 확인하고 수정 및 관리할 수 있는 페이지입니다.
    </p>

    <form @submit.prevent="submitForm" class="mt-4">
      <div class="mb-3">
        <label class="form-label fw-semibold">단어</label>
        <input v-model="term_name" type="text" class="form-control" />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">정의</label>
        <input v-model="term_define" type="text" class="form-control" />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">예시</label>
        <input v-model="term_example" type="text" class="form-control" />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">주의</label>
        <input v-model="term_caution" type="text" class="form-control" />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">카테고리</label>
        <select v-model="category_id" class="form-select">
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
        <button type="submit" class="btn btn-primary">수정</button>
        <button type="button" class="btn btn-danger ms-2">삭제</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const id = route.params.id;

const term_name = ref('');
const term_define = ref('');
const term_example = ref('');
const term_caution = ref('');
const category_id = ref('');
const categories = ref([]);

const goBack = () => {
  router.back();
};

const submitForm = () => {
  // 수정 API 호출
  console.log('수정됨:', {
    term_name: term_name.value,
    term_define: term_define.value,
    term_example: term_example.value,
    term_caution: term_caution.value,
    category_id: category_id.value,
  });
  goBack();
};

onMounted(() => {
  // 기존 용어 정보 로딩 예시
  term_name.value = '근저당권';
  term_define.value = '돈을 빌려줄 때 담보로 잡는 권리에요.';
  term_example.value = 'KB국민은행 근저당권 2억 설정';
  term_caution.value =
    '보증금보다 근저당이 많으면 보증금을 돌려받기 어려울 수 있습니다.';
  category_id.value = 1;

  categories.value = [
    { category_id: 1, category_name: '전세권 / 안전장치' },
    { category_id: 2, category_name: '계약' },
  ];
});
</script>
