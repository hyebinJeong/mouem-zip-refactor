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
        <input v-model="type" type="text" class="form-control" />
      </div>

      <div class="mb-3">
        <label class="form-label fw-semibold">중요도</label>
        <select v-model="importance" class="form-select">
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
          placeholder="특약사항 설명을 입력하세요"
        ></textarea>
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

// 수정할 데이터 불러오기 (예시)
onMounted(() => {
  // 실제로는 API 등을 통해 id(route.params.id) 기반 데이터 로드
  type.value = '안전 장치 조항';
  importance.value = '높음';
  description.value =
    '임대인은 임차인의 대학력 및 확정일자 확보 이전에 해당 부동산에 제3자와의 담보권 설정, 매도, 제3자 점유를 하지 않는다.';
});
</script>
