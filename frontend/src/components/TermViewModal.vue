<script>
import TermCard from '@/components/TermCard.vue'
import TermModal from '@/components/TermModal.vue'
import { fetchDictionary } from '@/api/termApi.js'

export default {
  name: 'TermViewModal',
  components: { TermCard, TermModal },
  data() {
    return {
      keyword: '',
      filter: '',
      selectedTerm: null,
      terms: []
    }
  },
  computed: {
    filteredTerms() {
      return this.terms.filter(term =>
          (!this.filter || term.categoryName === this.filter) &&
          term.termName.includes(this.keyword)
      )
          .slice() // 원본 배열 수정 방지
          .sort((a, b) => a.termName.localeCompare(b.termName, 'ko')) // 가나다순 정렬
    },
    categories() {
      const allCategories = this.terms.map(term => term.categoryName)
      return [...new Set(allCategories)]
    }
  },
  methods: {
    openModal(term) {
      this.selectedTerm = term
    },
    async loadDictionary() {
      try {
        const response = await fetchDictionary()
        this.terms = response.data
      } catch (error) {
        console.error('사전 데이터 불러오기 실패', error)
      }
    }
  },
  mounted() {
    this.loadDictionary()
  }
}
</script>

<template>
  <!-- 전체 모달 오버레이 -->
  <div class="custom-modal-overlay" @click.self="$emit('close')">
    <div class="custom-modal-content">
      <!-- 닫기 버튼 -->
      <div class="d-flex justify-content-end">
        <button type="button" class="btn-close" @click="$emit('close')"></button>
      </div>

      <!-- 검색창 -->
      <div class="row justify-content-center mb-3">
        <div class="col-md-8 d-flex justify-content-center">
          <input
              type="text"
              v-model="keyword"
              class="form-control rounded-pill px-4"
              placeholder="검색할 용어를 입력하세요"
              style="width: 66%;"
          />
        </div>
      </div>

      <!-- 카테고리 필터 -->
      <div class="row mb-4">
        <div class="col text-end">
          <select v-model="filter" class="form-select w-auto d-inline-block" style="margin-right: 3.5rem;">
            <option value="">전체</option>
            <option v-for="cat in categories" :key="cat" :value="cat">
              {{ cat }}
            </option>
          </select>
        </div>
      </div>

      <!-- 카드 리스트 -->
      <div class="row justify-content-center g-4">
        <div
            class="col-12 col-sm-6 col-md-4"
            v-for="item in filteredTerms"
            :key="item.termId"
            style="max-width: 380px;"
        >
          <TermCard :term="item" @open="openModal" />
        </div>
      </div>

      <!-- 내부 용어 모달 -->
      <TermModal :term="selectedTerm" @close="selectedTerm = null" />
    </div>
  </div>
</template>

<style scoped>
.custom-modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

/* 메인 모달 컨텐츠 */
.custom-modal-content {
  background: #fff;
  border-radius: 16px;
  width: 70%;
  height: 70%;
  overflow-y: auto;
  padding: 20px;
  position: relative;
}

/* 닫기 버튼 스타일 */
.btn-close {
  position: relative;
  z-index: 2100;
}

</style>