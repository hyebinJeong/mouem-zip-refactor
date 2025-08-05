<template>
  <div class="container">
    <div class="content-box">
      <!-- 상단 헤더 -->
      <div class="header-section">
        <div class="title-text">
          <div class="main-title">계약서에 이런 특약을 넣으면 좋아요.</div>
          <div class="sub-title">한눈에 보기 좋게 정리했어요.</div>
          <div class="note">실제 피해 사례 기반으로 정리했어요.</div>
        </div>

        <!-- 오른쪽: 피해 예상 규모 + 중요도 필터 -->
        <div class="right-controls">
          <div class="scale-bar">
            <div class="scale-label">피해 예상 규모</div>
            <div class="scale-bar-graph"><div class="bar-fill"></div></div>
            <div class="scale-result">중요</div>
          </div>

          <!-- ✅ 중요도 필터 드롭다운 -->
          <select v-model="filter" class="form-select w-auto d-inline-block">
            <option value="">전체</option>
            <option value="높음">높음</option>
            <option value="중간">중간</option>
            <option value="낮음">낮음</option>
          </select>
        </div>
      </div>

      <!-- 특약 리스트 -->
      <div class="scroll-wrapper">
        <div class="grid-box">
          <div
            v-for="(item, index) in filteredRecommendations"
            :key="index"
            :class="['item', item.importanceColor]"
            @click="openModal(item)"
          >
            <span :class="['dot', item.importanceColor + '-dot']"></span>
            {{ item.title }}
          </div>
        </div>
      </div>

      <!-- 하단 캐릭터 -->
      <div
        class="character-box"
        @click="$router.push({ name: 'ReferenceContract' })"
      >
        <img src="@/assets/skybuddy.png" alt="버디 캐릭터" />
        <div class="speech-bubble">
          선택을 완료하셨다면<br />저를 클릭해주세요!
        </div>
      </div>
    </div>

    <!-- 모달 -->
    <div v-if="selectedItem" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box">
        <div class="modal-header">
          <h3>{{ selectedItem.title }}</h3>
          <span class="close-btn" @click="closeModal">×</span>
        </div>
        <div class="modal-content">
          <div
            v-for="(clause, idx) in selectedItem.clauses"
            :key="clause.id"
            class="clause"
          >
            <div class="clause-number">{{ idx + 1 }}</div>
            <div class="clause-text">{{ clause.text }}</div>
            <button class="select-btn" @click="selectClause(clause)">
              선택하기
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SpecialContractsRecommendation',
  data() {
    return {
      selectedItem: null,
      recommendations: [], // 전체 데이터
      filter: '', // ✅ 선택된 중요도
    };
  },
  computed: {
    // ✅ 필터 적용된 특약
    filteredRecommendations() {
      if (!this.filter) return this.recommendations;
      return this.recommendations.filter((r) => r.importance === this.filter);
    },
  },
  created() {
    this.fetchRecommendations();
  },
  methods: {
    async fetchRecommendations() {
      try {
        const response = await axios.get('/api/recommendation');
        const data = response.data;
        const importanceMap = {
          높음: 'red',
          중간: 'yellow',
          낮음: 'green',
          기타: 'white',
        };

        // ✅ 카테고리별 그룹핑
        const grouped = {};
        data.forEach((item) => {
          const key = item.category; // 카테고리 이름
          if (!grouped[key]) {
            grouped[key] = {
              title: key,
              importance: item.importance,
              importanceColor: importanceMap[item.importance] || 'white',
              clauses: [],
            };
          }
          grouped[key].clauses.push({
            id: item.special_clause_id,
            text: item.description,
          });
        });

        this.recommendations = Object.values(grouped);
      } catch (error) {
        console.error('특약 추천 데이터를 불러오는 중 오류 발생:', error);
      }
    },
    openModal(item) {
      this.selectedItem = item;
    },
    closeModal() {
      this.selectedItem = null;
    },
    selectClause(clause) {
      const selected = JSON.parse(
        sessionStorage.getItem('selectedClauses') || '[]'
      );

      const alreadyExists = selected.some((item) => item.text === clause.text);

      if (!alreadyExists) {
        selected.push(clause);
        sessionStorage.setItem('selectedClauses', JSON.stringify(selected));
        alert('특약 사항이 추가되었습니다.');
      } else {
        alert('이미 선택된 조항입니다.');
      }

      this.closeModal();
    },
  },
};
</script>

<style scoped>
.container {
  background-color: #f3f4f6;
  min-height: 100vh;
  padding: 40px 60px;
  font-family: 'Arial', sans-serif;
}

.content-box {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  position: relative;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.right-controls {
  text-align: right;
}

.form-select {
  margin-top: 10px;
}

/* 타이틀 */
.title-text .main-title {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
}

.title-text .sub-title {
  margin-top: 4px;
  font-size: 18px;
  color: #1f2937;
  font-weight: 600;
}

.title-text .note {
  font-size: 14px;
  color: #6b7280;
  margin-top: 8px;
}

/* 피해 예상 규모 */
.scale-bar {
  text-align: right;
}

.scale-label {
  font-size: 14px;
  color: #6b7280;
}

.scale-bar-graph {
  width: 200px;
  height: 8px;
  background-color: #e5e7eb;
  border-radius: 9999px;
  margin: 8px 0;
  overflow: hidden;
}

.bar-fill {
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, #10b981, #facc15, #ef4444);
  border-radius: 9999px;
}

.scale-result {
  font-size: 14px;
  color: #dc2626;
  font-weight: 600;
}

/* 리스트 */
.scroll-wrapper {
  max-height: 460px;
  overflow-y: auto;
  position: relative;
  padding-bottom: 80px;
}
.scroll-wrapper::-webkit-scrollbar {
  width: 6px;
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}
.scroll-wrapper:hover::-webkit-scrollbar {
  opacity: 1;
}

.grid-box {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.item {
  background-color: #f9fafb;
  padding: 16px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  font-size: 15px;
  font-weight: 500;
  color: #1f2937;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
  text-align: center;
}
.item:hover {
  background-color: #e5e7eb;
}
.item.red {
  background-color: #fef2f2;
}
.item.green {
  background-color: #ecfdf5;
}
.item.yellow {
  background-color: #fffbeb;
}
.item.white {
  background-color: #f3f4f6;
}

/* 점 표시 */
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
  display: inline-block;
}
.red-dot {
  background-color: #dc2626;
}
.green-dot {
  background-color: #10b981;
}
.yellow-dot {
  background-color: #facc15;
}
.white-dot {
  background-color: #9ca3af;
}

/* 캐릭터 */
.character-box {
  position: absolute;
  bottom: 16px;
  left: 16px;
  z-index: 1;
}
.character-box img {
  width: 60px;
  height: auto;
}
.speech-bubble {
  position: absolute;
  left: 60px;
  bottom: 20px;
  background: #ffffff;
  border: 1px solid #d1d5db;
  padding: 10px 14px;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  font-size: 13px;
  line-height: 1.4;
  color: #111827;
  white-space: nowrap;
}
.speech-bubble::after {
  content: '';
  position: absolute;
  left: -8px;
  bottom: 12px;
  border-width: 6px;
  border-style: solid;
  border-color: transparent #ffffff transparent transparent;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(31, 41, 55, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-box {
  background-color: white;
  width: 640px;
  max-width: 90%;
  padding: 32px;
  border-radius: 20px;
  position: relative;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.modal-header h3 {
  font-size: 18px;
  font-weight: bold;
}
.close-btn {
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
}
.modal-content .clause {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.clause-number {
  color: #2563eb;
  font-weight: bold;
  font-size: 18px;
  width: 24px;
}
.clause-text {
  flex: 1;
  font-size: 14px;
  margin-left: 12px;
  margin-right: 8px;
}
.select-btn {
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 6px 10px;
  cursor: pointer;
  font-size: 13px;
}
</style>
