<!-- // views/category/CategoryManager.vue -->
<template>
  <div class="p-4" style="background-color: #f7f9fc; min-height: 70vh">
    <div class="d-flex align-items-center mb-3">
      <button
        class="btn btn-link me-2 p-0 arrow-button"
        @click="$router.back()"
      >
        <i class="fas fa-arrow-left"></i>
      </button>
      <h5 class="fw-bold mb-0">카테고리 관리</h5>
    </div>
    <p class="text-muted">
      선택한 단어에 대해 확인하고 수정 및 관리할 수 있는 페이지입니다.
    </p>

    <div class="mb-3 d-flex justify-content-end">
      <div class="input-group" style="width: 25%">
        <input
          type="text"
          class="form-control rounded-pill"
          placeholder="검색할 용어를 입력하세요"
          v-model="search"
        />
      </div>
    </div>

    <table class="table align-middle">
      <thead>
        <tr>
          <th>카테고리</th>
          <th>카테고리 설명</th>
          <th>카테고리 색상</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in filteredCategories"
          :key="item.id"
          @click="goToEdit(item.id)"
          style="cursor: pointer"
        >
          <td class="text-body">{{ item.name }}</td>
          <td>{{ item.description }}</td>
          <td>
            <span
              class="d-inline-block rounded-circle"
              :style="{
                backgroundColor: item.color,
                width: '20px',
                height: '20px',
              }"
            ></span>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="text-end">
      <router-link to="/category/add" class="btn btn-primary">추가</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const search = ref('');
const categories = ref([
  {
    id: 1,
    name: '전세계약과 돈 관련',
    description:
      '전세 계약에서 오는 보증금, 계약금, 관리비 등 금전 거래 용어를 다룹니다.',
    color: '#DFF6DD',
  },
  {
    id: 2,
    name: '전세 권리 보호',
    description:
      '계약 전 꼭 확인해야 할 등기부 권리관계, 담보, 중개사 보장 등 보호장치 용어를 포함합니다.',
    color: '#DDF1FC',
  },
  {
    id: 3,
    name: '전세 계약 기간 및 갱신',
    description:
      '보증금을 지키기 위한 전입신고, 확정일자, 우선변제권 등 법적 권리를 정리합니다.',
    color: '#E5E4FD',
  },
  {
    id: 4,
    name: '전세권리/안전장치',
    description:
      '전세의 계약 기간, 갱신 조건, 해지 요건 등 기간 관련 용어를 안내합니다.',
    color: '#F9FFC1',
  },
  // 추가해야 할 점, db연결할때 , 화면을 벗어나 많아지면, 스크롤 영역이 되도록
  // 수정 페이지에 들어갔을때 카테고리 정보가 들어가있도록,
]);

const filteredCategories = computed(() => {
  return categories.value.filter(
    (c) => c.name.includes(search.value) || c.description.includes(search.value)
  );
});

const goToEdit = (id) => {
  router.push(`/category/edit/${id}`);
};
</script>
