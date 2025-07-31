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
</script>

<style scoped>
/* 전체 카드 스타일 */
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

/* 헤더 스타일 */
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

/* 상태 배지 */
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

/* 본문 스타일 */
.card-body {
  padding: 0;
}

.detail-item {
  padding: 1.5rem;
  border-bottom: 1px solid #f8fafc;
}

.detail-item:last-child {
  border-bottom: none;
}

/* 상세 정보 그리드 */
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

/* 반응형 디자인 */
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
