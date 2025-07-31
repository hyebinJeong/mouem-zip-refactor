<template>
  <div
    class="overflow-auto"
    style="padding: 1.5rem"
    v-if="analysis && analysisItems.length"
  >
    <div v-for="item in analysisItems" :key="item.key" class="mb-4">
      <div class="analysis-card">
        <div
          class="card-header"
          :class="hasInfo(item.key) ? 'has-data' : 'no-data'"
        >
          <div class="header-content">
            <h5 class="item-title">{{ item.label }}</h5>
            <div
              class="status-badge"
              :class="hasInfo(item.key) ? 'status-warning' : 'status-safe'"
            >
              <i
                class="status-icon"
                :class="hasInfo(item.key) ? 'icon-warning' : 'icon-check'"
              ></i>
              <span class="status-text">
                {{ hasInfo(item.key) ? '해당 내역 있음' : '해당 내역 없음' }}
              </span>
            </div>
          </div>
        </div>

        <!-- 상세 정보 부분 -->
        <div v-if="hasInfo(item.key)" class="card-body">
          <div
            v-for="(detail, index) in analysis[item.key]"
            :key="index"
            class="detail-item"
          >
            <!-- 경매 -->
            <template v-if="item.key == 'auctionInfos'">
              <div class="detail-grid">
                <div class="detail-field">
                  <span class="field-label">접수일자</span>
                  <span class="field-value">{{ detail.date }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">채권자</span>
                  <span class="field-value">{{ detail.creditor }}</span>
                </div>
              </div>
            </template>

            <!-- 가압류 -->
            <template v-else-if="item.key == 'provisionalSeizureInfos'">
              <div class="detail-grid">
                <div class="detail-field">
                  <span class="field-label">접수일자</span>
                  <span class="field-value">{{ detail.date }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">채권자</span>
                  <span class="field-value">{{ detail.creditor }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">청구금액</span>
                  <span class="field-value amount">{{
                    formatCurrency(detail.maxClaimAmount)
                  }}</span>
                </div>
              </div>
            </template>

            <!-- 압류 -->
            <template v-else-if="item.key == 'seizureInfos'">
              <div class="detail-grid">
                <div class="detail-field">
                  <span class="field-label">접수일자</span>
                  <span class="field-value">{{ detail.date }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">권리자</span>
                  <span class="field-value">{{ detail.rightHolder }}</span>
                </div>
              </div>
            </template>

            <!-- 가처분 -->
            <template v-else-if="item.key == 'injunctionInfos'">
              <div class="detail-grid">
                <div class="detail-field">
                  <span class="field-label">접수일자</span>
                  <span class="field-value">{{ detail.date }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">채권자</span>
                  <span class="field-value">{{ detail.creditor }}</span>
                </div>
              </div>
            </template>

            <!-- 근저당권 -->
            <template v-else-if="item.key == 'mortgageInfos'">
              <div class="detail-grid">
                <div class="detail-field">
                  <span class="field-label">접수일자</span>
                  <span class="field-value">{{ detail.date }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">근저당권자</span>
                  <span class="field-value">{{ detail.mortgageHolder }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">채권최고액</span>
                  <span class="field-value amount">{{
                    formatCurrency(detail.maxClaimAmount)
                  }}</span>
                </div>
              </div>
            </template>

            <!-- 신탁등기 -->
            <template v-else-if="item.key == 'trustInfos'">
              <div class="detail-grid">
                <div class="detail-field">
                  <span class="field-label">접수일자</span>
                  <span class="field-value">{{ detail.date }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">수탁자</span>
                  <span class="field-value">{{ detail.trustee }}</span>
                </div>
              </div>
            </template>

            <!-- 전세권설정 -->
            <template v-else-if="item.key == 'jeonseRightInfos'">
              <div class="detail-grid">
                <div class="detail-field">
                  <span class="field-label">접수일자</span>
                  <span class="field-value">{{ detail.date }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">전세권자</span>
                  <span class="field-value">{{ detail.mortgagor }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">전세금</span>
                  <span class="field-value amount">{{
                    formatCurrency(detail.deposit)
                  }}</span>
                </div>
              </div>
            </template>

            <!-- 가등기 -->
            <template v-else-if="item.key == 'provisionalRegistrationInfos'">
              <div class="detail-grid">
                <div class="detail-field">
                  <span class="field-label">접수일자</span>
                  <span class="field-value">{{ detail.date }}</span>
                </div>
                <div class="detail-field">
                  <span class="field-label">가등기권자</span>
                  <span class="field-value">{{
                    detail.registeredRightHolder
                  }}</span>
                </div>
              </div>
            </template>

            <template v-else>
              <div class="no-detail">상세 정보가 없습니다.</div>
            </template>
          </div>

          <!-- 해당 항목의 설명 섹션 추가 -->
          <div class="explanation-section" v-if="getExplanation(item.key)">
            <div class="explanation-header">
              <h6 class="explanation-title">{{ item.label }} 왜 위험할까요?</h6>
              <div class="arrow-indicator">→ 피해 예시와 함께 살펴보아요!</div>
            </div>
            <div
              class="explanation-content"
              v-html="getExplanation(item.key)"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  analysis: Object,
  analysisItems: {
    type: Array,
    required: true,
  },
});

// 내역 존재 여부 체크 함수
function hasInfo(key) {
  return props.analysis?.[key]?.length > 0;
}

// 금액 , 넣어서 출력
function formatCurrency(value) {
  if (!value) return '';
  return Number(value).toLocaleString('ko-KR') + '원';
}

// 각 항목별 설명 반환 함수
function getExplanation(key) {
  const explanations = {
    // 경매
    auctionInfos: `
      <p>임차인 A는 보증금 1억원으로 계약했지만, 집주인의 도박빚으로 집이 경매에 넘어갔습니다.</p>
      <p>낙찰가가 대출금보다 낮아 A는 2천만원만 돌려받고, 8천만원을 잃었습니다.</p>
    `,
    // 가압류
    provisionalSeizureInfos: `
      <p>임차인 A는 2025년 5월에 전입신고와 확정일자를 받았지만, 채권자 B가 한 달 앞선 4월에 가압류를 신청해 등기되었습니다.</p>
      <p>그 결과 A는 대항력과 확정일자가 있었음에도 배당에서 밀려 보증금을 거의 돌려받지 못했습니다.</p>
    `,
    // 압류
    seizureInfos: `
      <p>임차인 A는 계약 만료 후 보증금을 요구했지만, 집주인의 세금 체납으로 부동산이 압류된 상태였습니다.</p>
      <p>압류 해제까지 2년이 걸려 그동안 A는 새 집 계약조차 어려운 상황이 되었습니다.</p>
    `,
    // 가처분
    injunctionInfos: `
      <p>임차인 A는 집주인이 바뀐 사실도 모른 채 살았는데, 한 채권자가 자신이 진짜 소유자라며 가처분을 신청했습니다.</p>
      <p>소유권 분쟁이 3년간 이어지면서 A는 보증금을 받기는커녕 집에서 쫓겨날 위기에 처했습니다.</p>
    `,
    // 근저당권
    mortgageInfos: `
      <p>임차인 A는 보증금 5천만원으로 계약했지만, 은행 근저당권이 8억원이나 설정돼 있는 줄 몰랐습니다.</p>
      <p>경매에서 집값이 떨어져 근저당권도 못 갚게 되자, A는 보증금을 전혀 돌려받지 못했습니다.</p>
    `,
    // 신탁등기
    trustInfos: `
      <p>임차인 A는 개인 집주인과 계약했다고 믿었지만, 실제 소유자는 신탁회사였습니다.</p>
      <p>신탁이 종료되며 소유자가 변경되자 A는 새 소유자에게 보증금을 받기 위해 소송을 해야 했습니다.</p>
    `,
    // 전세권설정
    jeonseRightInfos: `
      <p>임차인 A는 보증금 3억원으로 계약했지만, 이미 다른 세입자가 전세권으로 4억원을 설정해두었습니다.</p>
      <p>집값이 6억원이라 전세권자가 먼저 4억원을 가져가고, A는 2억원만 받을 수 있었습니다.</p>
    `,
    // 가등기
    provisionalRegistrationInfos: `
      <p>임차인 A가 계약한 뒤, 집주인이 가등기권자에게 소유권을 넘겨야 하는 상황이 발생했습니다.</p>
      <p>가등기가 본등기로 변경되며 새 소유자가 A와의 임대차 계약을 인정하지 않았고, 보증금 반환을 거부했습니다.</p>
    `,
  };

  return explanations[key] || null;
}
</script>

<style scoped>
.analysis-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e9ecef;
  overflow: hidden;
  transition: all 0.2s ease;
}

.analysis-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.card-header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f1f3f4;
}

.card-header.has-data {
  background: linear-gradient(135deg, #fff5f5 0%, #fef2f2 100%);
  border-left: 4px solid #ef4444;
}

.card-header.no-data {
  background: linear-gradient(135deg, #f0fdf4 0%, #f7fee7 100%);
  border-left: 4px solid #22c55e;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-title {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1f2937;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
}

.status-badge.status-warning {
  background-color: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.status-badge.status-safe {
  background-color: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.status-icon {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.icon-warning {
  background-color: #dc2626;
}

.icon-check {
  background-color: #16a34a;
}

.status-text {
  white-space: nowrap;
}

.card-body {
  padding: 0;
}

.detail-item {
  padding: 1.5rem;
  border-bottom: 1px solid #f8fafc;
  background-color: #fefefe;
}

.detail-item:last-child {
  border-bottom: 1px solid #f1f3f4;
}

/* 상세 정보 */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.detail-field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.field-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.025em;
}

.field-value {
  font-size: 0.95rem;
  color: #111827;
  font-weight: 500;
  word-break: break-all;
}

.amount {
  color: #dc2626;
  font-weight: 700;
  padding: 0.5rem;
  background-color: #fef2f2;
  border-radius: 6px;
  border: 1px solid #fecaca;
}

.no-detail {
  text-align: center;
  color: #6b7280;
  font-style: italic;
  padding: 2rem;
}

/* 설명 부분 */
.explanation-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.explanation-header {
  margin-bottom: 1rem;
}

.explanation-title {
  font-size: 1rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 0.5rem 0;
}

.arrow-indicator {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

.explanation-content {
  color: #374151;
  font-size: 0.875rem;
  line-height: 1.5;
}

.explanation-content p {
  margin: 0 0 0.75rem 0;
}

.explanation-content p:last-child {
  margin-bottom: 0;
}

.explanation-content strong {
  color: #dc2626;
  font-weight: 600;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
