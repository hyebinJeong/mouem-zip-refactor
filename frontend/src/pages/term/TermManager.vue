<template>
  <div class="p-4" style="background-color: #f7f9fc; min-height: 70vh">
    <!-- 헤더 + 뒤로가기 버튼 -->
    <div class="d-flex align-items-center mb-3">
      <!-- 뒤로가기 아이콘 -->
      <button class="btn btn-link me-2 p-0" @click="goBack" title="뒤로가기">
        <i class="fas fa-arrow-left text-secondary"></i>
      </button>
      <h5 class="fw-bold mb-0">용어 관리</h5>
    </div>

    <p class="text-muted">전체 용어 리스트를 확인하고 관리하는 페이지입니다.</p>

    <!-- 검색창 -->
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

    <!-- 테이블 -->
    <table class="table align-middle">
      <thead>
        <tr>
          <th>단어</th>
          <th>단어 설명</th>
          <th>카테고리</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="term in filteredTerms"
          :key="term.term_id"
          @click="goToEdit(term.term_id)"
          style="cursor: pointer"
        >
          <td>{{ term.term_name }}</td>
          <td>{{ term.term_define }}</td>
          <td>
            <span
              class="badge"
              :style="{ backgroundColor: term.category_color, color: '#000' }"
            >
              {{ term.category_name }}
            </span>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 추가 버튼 -->
    <div class="text-end mt-4">
      <router-link to="/category/term/add" class="btn btn-primary"
        >추가</router-link
      >
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const search = ref('');
const terms = ref([
  {
    term_id: 1,
    term_name: '가압류/가처분',
    term_define: '상대방 재산을 미리 묶어두는 조치에요.',
    category_name: '전세계약과 금융관련',
    category_color: '#B7FFAC',
  },
  {
    term_id: 2,
    term_name: '임차권',
    term_define: '임차인이 집을 일정 기간 점유할 수 있는 권리에요.',
    category_name: '전세 권리 보호',
    category_color: '#9FE4FF',
  },
]);

// 검색 필터링
const filteredTerms = computed(() =>
  terms.value.filter(
    (term) =>
      term.term_name.includes(search.value) ||
      term.term_define.includes(search.value)
  )
);

// 수정 페이지로 이동
const goToEdit = (id) => {
  router.push(`/category/term/edit/${id}`);
};

// 뒤로가기 기능 (기본: 한 단계 뒤)
const goBack = () => {
  router.back();
  // 또는 원하는 경로로 강제 이동:
  // router.push('/category')
};
</script>

<style scoped>
.badge {
  font-size: 0.9rem;
  padding: 0.4em 0.6em;
  border-radius: 0.4rem;
}
button.btn-link {
  text-decoration: none;
}
</style>
