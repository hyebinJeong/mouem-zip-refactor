<template>
  <div class="container">
    <div class="content-box">
      <!-- 제목 및 피해규모 바 -->
      <div class="header-section">
        <div class="title-text">
          <div class="main-title">
            <button
              class="back-icon-btn"
              @click="$router.push({ name: 'reference-contract' })"
              title="뒤로가기"
            >
              ←
            </button>
            계약서에 이런 특약을 넣으면 좋아요.
          </div>
          <div class="sub-title">한눈에 보기 좋게 정리했어요.</div>
          <div class="note">실제 피해 사례 기반으로 정리했어요.</div>
        </div>
        <div class="scale-bar">
          <div class="scale-label">피해 예상 규모</div>
          <div class="scale-bar-graph">
            <div class="bar-fill"></div>
          </div>
          <div class="scale-result">중요</div>
        </div>
      </div>

      <!-- 특약 항목 리스트 -->
      <div class="scroll-wrapper">
        <div class="grid-box">
          <div
            v-for="(item, index) in specialItems"
            :key="index"
            :class="['item', item.color]"
            @click="openModal(item)"
          >
            <span :class="['dot', item.color + '-dot']"></span>
            {{ item.title }}
          </div>
        </div>
      </div>

      <!-- 캐릭터 이미지 (스크롤 관계 없이 content-box 내부 고정) -->
      <div class="character-box">
        <img src="@/assets/skybuddy.png" alt="버디 캐릭터" />
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
            :key="idx"
            class="clause"
          >
            <div class="clause-number">{{ idx + 1 }}</div>
            <div class="clause-text">{{ clause }}</div>
            <button class="select-btn">선택하기</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SpecialContractsRecommendation',
  data() {
    const generateItems = () => {
      const colors = ['red', 'green', 'yellow', 'white'];
      const titles = [
        '안전 장치 조항',
        '보증금 및 임대료 반환 관련',
        '하자 보수 및 수리 책임',
        '중도 퇴거 관련 조건',
        '시설물 유지 및 원상복구 범위',
        '관리비 및 공과금 주체 명시',
        '계약 갱신 거절 통보 조건',
        '전입신고 및 확정일자 허용',
        '주택 사용 목적 명시',
        '소음 방지 및 분쟁방지 조항',
        '애완동물 관련 사용 조건',
        '화재 보험 가입 조건',
        '사고 발생 시 책임 범위',
        '시설 점검 의무 조항',
        '위험물 반입 금지 조건',
        '무단 점유 시 처리 절차',
        '계약해지 사유 및 방법',
        '입주 전 청소 상태 확인',
        '전기·수도·가스 명의 변경 책임',
        '입주자 대표와의 소통 방식',
      ];
      return titles.map((title, i) => ({
        title,
        color: colors[i % colors.length],
        clauses: [`${title}에 대한 조항 예시 1`, `${title}에 대한 조항 예시 2`],
      }));
    };

    return {
      selectedItem: null,
      specialItems: generateItems(),
    };
  },
  methods: {
    openModal(item) {
      this.selectedItem = item;
    },
    closeModal() {
      this.selectedItem = null;
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
  position: relative; /* 캐릭터 위치 기준 */
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

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

/* 스크롤 영역 및 항목 리스트 */
.scroll-wrapper {
  max-height: 460px;
  overflow-y: auto;
  position: relative;
  padding-bottom: 80px;
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

/* 캐릭터 - content-box 내부 고정 */
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

.back-icon-btn {
  background: none;
  border: none;
  font-size: 30px;
  font-weight: 1000px;
  cursor: pointer;
  color: #374151;
  padding-right: 8px;
  line-height: 1;
  transition: color 0.2s ease;
}

.back-icon-btn:hover {
  color: #1f2937;
}
</style>
