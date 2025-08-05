<!-- src/pages/special-contracts/SpecialContractEdit.vue -->
<template>
  <div class="p-4" style="background-color: #f7f9fc; min-height: 70vh">
    <div class="d-flex align-items-center mb-3">
      <i
        class="fas fa-arrow-left me-2"
        style="cursor: pointer"
        @click="goBack"
      ></i>
      <h5 class="fw-bold mb-0">특약사항 수정</h5>
    </div>
    <p class="text-muted">
      선택한 특약사항에 대해 확인하고 수정 및 관리할 수 있는 페이지입니다.
    </p>

    <form @submit.prevent="submitForm" class="mt-4">
      <div class="mb-3">
        <label class="form-label fw-semibold">특약분류</label>
        <input v-model="category" type="text" class="form-control" />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">중요도</label>
        <select v-model="importance" class="form-select">
          <option value="높음">높음</option>
          <option value="중간">중간</option>
          <option value="낮음">낮음</option>
        </select>
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">설명</label>
        <textarea
          v-model="description"
          rows="4"
          class="form-control"
          placeholder="특약사항 설명을 입력하세요"
        ></textarea>
      </div>

      <div class="text-end mt-4">
        <button type="button" class="btn btn-secondary me-2" @click="goBack">
          취소
        </button>
        <button type="submit" class="btn btn-primary">수정</button>
        <button
          type="button"
          class="btn btn-danger ms-2"
          @click="deleteContract"
        >
          삭제
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useSpecialContractsStore } from '@/stores/specialContractsStore';

const store = useSpecialContractsStore();
const router = useRouter();
const route = useRoute();

const category = ref('');
const importance = ref('');
const description = ref('');

const importanceColorMap = {
  높음: '#FF0000',
  중간: '#FFA500',
  낮음: '#00FF00',
};

// ID는 라우트 params에서 받음
const id = route.params.id;

const goBack = () => {
  router.back();
};

const submitForm = async () => {
  if (!category.value || !importance.value || !description.value) {
    alert('모든 항목을 입력해주세요.');
    return;
  }

  const importance_color = importanceColorMap[importance.value] || '#000000';

  await store.updateContract(id, {
    category: category.value,
    importance: importance.value,
    importance_color,
    description: description.value,
  });

  router.push('/category/special'); // 수정 후 목록으로 이동
};

const deleteContract = async () => {
  if (confirm('정말 삭제하시겠습니까?')) {
    await store.deleteContract(id);
    router.push('/category/special');
  }
};

onMounted(async () => {
  const contract = await store.fetchContractById(id);
  if (contract) {
    category.value = contract.category;
    importance.value = contract.importance;
    description.value = contract.description;
  } else {
    alert('특약사항 데이터를 불러오지 못했습니다.');
    router.back();
  }
});
</script>
