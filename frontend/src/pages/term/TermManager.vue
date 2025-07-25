<template>
  <div class="p-4" style="background-color: #f7f9fc; min-height: 70vh">
    <!-- 헤더 -->
    <div class="d-flex align-items-center mb-3">
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
          <th style="width: 20%">단어</th>
          <th style="width: 60%">단어 설명</th>
          <th style="width: 20%">카테고리</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="term in paginatedTerms"
          :key="term.term_id"
          @click="goToEdit(term.term_id)"
          style="cursor: pointer"
        >
          <td>{{ term.term_name }}</td>
          <td>{{ truncateText(term.term_define, 30) }}</td>
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

    <!-- 페이지네이션 (15개 초과일 때만 표시) -->
    <div
      class="d-flex justify-content-center mt-3"
      v-if="filteredTerms.length > itemsPerPage"
    >
      <nav>
        <ul class="pagination mb-0">
          <!-- 맨 앞으로 -->
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="goToPage(1)">«</button>
          </li>

          <!-- 현재 페이지 기준 ±2 -->
          <li
            v-for="page in visiblePages"
            :key="page"
            class="page-item"
            :class="{ active: currentPage === page }"
          >
            <button class="page-link" @click="goToPage(page)">
              {{ page }}
            </button>
          </li>

          <!-- 맨 뒤로 -->
          <li
            class="page-item"
            :class="{ disabled: currentPage === totalPages }"
          >
            <button class="page-link" @click="goToPage(totalPages)">»</button>
          </li>
        </ul>
      </nav>
    </div>

    <!-- 추가 버튼 -->
    <div class="text-end mt-4">
      <router-link to="/category/term/add" class="btn btn-primary"
        >추가</router-link
      >
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const search = ref('');
const currentPage = ref(1);
const itemsPerPage = 15;

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
  {
    term_id: 3,
    term_name: '전입신고',
    term_define: '임차인이 실제 거주함을 신고하는 절차입니다.',
    category_name: '전세 계약 기간 및 갱신',
    category_color: '#E5E4FD',
  },
  {
    term_id: 4,
    term_name: '확정일자',
    term_define: '계약서 날짜를 법적으로 인정받기 위한 절차입니다.',
    category_name: '전세 계약 기간 및 갱신',
    category_color: '#F9FFC1',
  },
  {
    term_id: 5,
    term_name: '우선변제권',
    term_define: '보증금을 우선적으로 돌려받을 수 있는 권리입니다.',
    category_name: '전세 권리 보호',
    category_color: '#DDF1FC',
  },
  {
    term_id: 6,
    term_name: '중개사 보장',
    term_define: '중개사가 책임지고 계약을 보장하는 제도입니다.',
    category_name: '전세 권리 보호',
    category_color: '#FDE2E2',
  },
]);

const filteredTerms = computed(() =>
  terms.value.filter(
    (term) =>
      term.term_name.includes(search.value) ||
      term.term_define.includes(search.value)
  )
);

const totalPages = computed(() =>
  Math.ceil(filteredTerms.value.length / itemsPerPage)
);

const paginatedTerms = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return filteredTerms.value.slice(start, start + itemsPerPage);
});

const truncateText = (text, maxLength) => {
  return text.length > maxLength ? text.slice(0, maxLength) + '...' : text;
};

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const visiblePages = computed(() => {
  const pages = [];
  const start = Math.max(1, currentPage.value - 2);
  const end = Math.min(totalPages.value, currentPage.value + 2);
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});

const goToEdit = (id) => {
  router.push(`/category/term/edit/${id}`);
};

const goBack = () => {
  router.back();
};

watch(search, () => {
  currentPage.value = 1;
});
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
.pagination .page-link {
  cursor: pointer;
}
</style>
