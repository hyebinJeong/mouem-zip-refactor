<template>
  <div class="p-4" style="background-color: #f7f9fc; min-height: 70vh">
    <!-- 뒤로가기 -->
    <div class="d-flex align-items-center mb-3">
      <button class="btn btn-link me-2 p-0" @click="goBack" title="뒤로가기">
        <i class="fas fa-arrow-left text-secondary"></i>
      </button>
      <h5 class="fw-bold mb-0">특약사항 관리</h5>
    </div>

    <p class="text-muted">
      선택한 단어에 대해 확인하고 수정 및 관리할 수 있는 페이지입니다.
    </p>

    <!-- 검색창 -->
    <div class="mb-3 d-flex justify-content-end">
      <div class="input-group" style="width: 25%">
        <input
          type="text"
          class="form-control rounded-pill"
          placeholder="검색할 특약사항을 입력하세요"
          v-model="search"
        />
      </div>
    </div>

    <!-- 테이블 -->
    <table class="table align-middle">
      <thead>
        <tr>
          <th style="width: 20%">특약사항 구분</th>
          <th style="width: 60%">특약사항 세부내용</th>
          <th style="width: 20%">중요도</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="contract in paginatedContracts"
          :key="contract.id"
          @click="goToEdit(contract.id)"
          style="cursor: pointer"
        >
          <td class="text-body">{{ contract.title }}</td>
          <td>{{ truncateText(contract.detail, 30) }}</td>
          <td class="d-flex align-items-center gap-2">
            <span :class="importanceDot(contract.importance)"></span>
            <span>{{ importanceLabel(contract.importance) }}</span>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 페이지네이션 -->
    <div class="d-flex justify-content-center mt-4" v-if="totalPages > 1">
      <nav>
        <ul class="pagination">
          <!-- 맨 앞으로 -->
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="changePage(1)" :disabled="currentPage === 1">
              «
            </button>
          </li>

          <!-- 페이지 번호 -->
          <li
            v-for="page in totalPages"
            :key="page"
            class="page-item"
            :class="{ active: currentPage === page }"
          >
            <button class="page-link" @click="changePage(page)">
              {{ page }}
            </button>
          </li>

          <!-- 맨 뒤로 -->
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="changePage(totalPages)" :disabled="currentPage === totalPages">
              »
            </button>
          </li>
        </ul>
      </nav>
    </div>

    <!-- 추가 버튼 -->
    <div class="text-end mt-4">
      <router-link to="/category/special/add" class="btn btn-primary">
        추가
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const search = ref('');
const currentPage = ref(1);
const itemsPerPage = 15;

const contracts = ref([
  {
    id: 1,
    title: '안전 장치 조항',
    detail: '임대인은 임차인의 대학력 및 확정일자 확보 이전에 해당 부동산에 제3자와의 담보권 설정, 매도, 제3자 점유를 하지 않는다.',
    importance: 'high',
  },
 {
    id: 2,
    title: '보증금 반환 조항',
    detail: '세부내용2',
    importance: 'medium',
  },
  { id: 3, title: '계약 연장 조항', detail: '세부내용3', importance: 'low' },
  { id: 4, title: '분쟁 해결 조항', detail: '세부내용4', importance: 'medium' },
  { id: 5, title: '청소 의무 조항', detail: '세부내용5', importance: 'low' },
  { id: 6, title: '수리 책임 조항', detail: '세부내용6', importance: 'high' },
  { id: 7, title: '보험 가입 조항', detail: '세부내용7', importance: 'medium' },
  { id: 8, title: '방문 제한 조항', detail: '세부내용8', importance: 'low' },
  { id: 9, title: '주차 규정 조항', detail: '세부내용9', importance: 'medium' },
  { id: 10, title: '반려동물 조항', detail: '세부내용10', importance: 'low' },
  {
    id: 11,
    title: '중개 수수료 조항',
    detail: '세부내용11',
    importance: 'high',
  },
  {
    id: 12,
    title: '보안 시설 조항',
    detail: '세부내용12',
    importance: 'medium',
  },
  {
    id: 13,
    title: '입주일 조정 조항',
    detail: '세부내용13',
    importance: 'low',
  },
  { id: 14, title: '퇴거 조건 조항', detail: '세부내용14', importance: 'high' },
  {
    id: 15,
    title: '임대료 조정 조항',
    detail: '세부내용15',
    importance: 'medium',
  },
  { id: 16, title: '열쇠 관리 조항', detail: '세부내용16', importance: 'low' },
  {
    id: 17,
    title: '공용시설 이용 조항',
    detail: '세부내용17',
    importance: 'medium',
  },
  {
    id: 18,
    title: '에너지 절약 조항',
    detail: '세부내용18',
    importance: 'low',
  },
  {
    id: 19,
    title: '전입신고 조항',
    detail: '세부내용19',
    importance: 'medium',
  },
  {
    id: 20,
    title: '임차인 책임 조항',
    detail: '세부내용20',
    importance: 'high',
  },
  {
    id: 21,
    title: '재계약 우선권 조항',
    detail: '세부내용21',
    importance: 'medium',
  },
  {
    id: 22,
    title: '보일러 관리 조항',
    detail: '세부내용22',
    importance: 'low',
  },
  {
    id: 23,
    title: '계약 갱신 거절 조항',
    detail: '세부내용23',
    importance: 'high',
  },
  { id: 24, title: '화재 예방 조항', detail: '세부내용24', importance: 'high' },
  {
    id: 25,
    title: '야간 소음 제한 조항',
    detail: '세부내용25',
    importance: 'medium',
  },
  {
    id: 26,
    title: '외부인 출입 조항',
    detail: '세부내용26',
    importance: 'low',
  },
  {
    id: 27,
    title: '보안 카메라 설치 조항',
    detail: '세부내용27',
    importance: 'medium',
  },
  {
    id: 28,
    title: '임대차 해지 조항',
    detail: '세부내용28',
    importance: 'high',
  },
  {
    id: 29,
    title: '소방 안전 점검 조항',
    detail: '세부내용29',
    importance: 'high',
  },
  {
    id: 30,
    title: '하자 발생 시 처리 조항',
    detail: '세부내용30',
    importance: 'medium',
  },
  {
    id: 31,
    title: '엘리베이터 사용 조항',
    detail: '세부내용31',
    importance: 'low',
  },
  {
    id: 32,
    title: '전세금 보호 조항',
    detail: '세부내용32',
    importance: 'high',
  },
  {
    id: 33,
    title: '계약 기간 준수 조항',
    detail: '세부내용33',
    importance: 'medium',
  },
  {
    id: 34,
    title: '쓰레기 배출 규정 조항',
    detail: '세부내용34',
    importance: 'low',
  },
  { id: 35, title: '공실 책임 조항', detail: '세부내용35', importance: 'high' },
  {
    id: 36,
    title: '임대료 체납 시 조치 조항',
    detail: '세부내용36',
    importance: 'high',
  },
  {
    id: 37,
    title: '대리인 계약 금지 조항',
    detail: '세부내용37',
    importance: 'medium',
  },
  {
    id: 38,
    title: '시설 훼손 시 배상 조항',
    detail: '세부내용38',
    importance: 'high',
  },
  {
    id: 39,
    title: '임차인 전출 통보 조항',
    detail: '세부내용39',
    importance: 'medium',
  },
  {
    id: 40,
    title: '건물 외관 유지 조항',
    detail: '세부내용40',
    importance: 'low',
  },

]);

const filteredContracts = computed(() =>
  contracts.value.filter(
    (c) =>
      c.title.toLowerCase().includes(search.value.toLowerCase()) ||
      c.detail.toLowerCase().includes(search.value.toLowerCase())
  )
);

const totalPages = computed(() =>
  Math.ceil(filteredContracts.value.length / itemsPerPage)
);

const paginatedContracts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return filteredContracts.value.slice(start, start + itemsPerPage);
});

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const goToEdit = (id) => {
  router.push(`/category/special/edit/${id}`);
};

const goBack = () => {
  router.back();
};

const importanceDot = (level) => {
  return {
    high: 'dot red',
    medium: 'dot yellow',
    low: 'dot green',
  }[level];
};

const importanceLabel = (level) => {
  return {
    high: '높음',
    medium: '보통',
    low: '낮음',
  }[level] || '';
};

const truncateText = (text, maxLength) => {
  if (!text) return '';
  return text.length > maxLength ? text.slice(0, maxLength) + '...' : text;
};
</script>

<style scoped>
button.btn-link {
  text-decoration: none;
}

.dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
}
.red {
  background-color: #ff0404;
}
.yellow {
  background-color: #ffde59;
}
.green {
  background-color: #42bc00;
}

.pagination .page-item.active .page-link {
  background-color: #0d6efd;
  border-color: #0d6efd;
  color: white;
}
</style>
