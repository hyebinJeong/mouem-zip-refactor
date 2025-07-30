<template>
  <div
    class="overflow-auto"
    style="padding: 1rem"
    v-if="analysis && analysisItems.length"
  >
    <div v-for="item in analysisItems" :key="item.key" class="w-100 mb-4">
      <div class="card w-100">
        <div class="card-body">
          <h5 class="card-title mb-0 d-inline-block">{{ item.label }}</h5>
          <span
            class="badge ms-2 align-middle"
            :class="hasInfo(item.key) ? 'bg-danger text-white' : 'bg-success'"
          >
            {{
              hasInfo(item.key) ? '해당 내역이 있어요' : '해당 내역이 없어요'
            }}
          </span>
        </div>
        <div v-if="hasInfo(item.key)">
          <div
            v-for="(detail, index) in analysis[item.key]"
            :key="index"
            class="mt-3 p-3 rounded mx-3 mb-3"
            style="
              background-color: #fdecec;
              width: 40%;
              margin: 0 auto;
              padding: 1rem;
              line-height: 1.6;
            "
          >
            <!-- 경매 -->
            <template v-if="item.key == 'auctionInfos'">
              <p><strong>접수일자: </strong> {{ detail.date }}</p>
              <p><strong>채권자: </strong> {{ detail.creditor }}</p>
            </template>
            <!-- 가압류 -->
            <template v-else-if="item.key == 'provisionalSeizureInfos'">
              <p><strong>접수일자: </strong> {{ detail.date }}</p>
              <p><strong>채권자: </strong> {{ detail.creditor }}</p>
              <p>
                <strong>청구금액: </strong>
                {{ formatCurrency(detail.maxClaimAmount) }}
              </p>
            </template>
            <!-- 압류 -->
            <template v-else-if="item.key == 'seizureInfos'">
              <p><strong>접수일자: </strong> {{ detail.date }}</p>
              <p><strong>권리자: </strong> {{ detail.rightHolder }}</p>
            </template>
            <!-- 가처분 -->
            <template v-else-if="item.key == 'injunctionInfos'">
              <p><strong>접수일자: </strong> {{ detail.date }}</p>
              <p><strong>채권자: </strong> {{ detail.creditor }}</p>
            </template>
            <!-- 근저당권 -->
            <template v-else-if="item.key == 'mortgageInfos'">
              <p><strong>접수일자: </strong> {{ detail.date }}</p>
              <p><strong>근저당권자: </strong> {{ detail.mortgageHolder }}</p>
              <p>
                <strong>채권최고액: </strong>
                {{ formatCurrency(detail.maxClaimAmount) }}
              </p>
            </template>
            <!-- 신탁등기 -->
            <template v-else-if="item.key == 'trustInfos'">
              <p><strong>접수일자: </strong> {{ detail.date }}</p>
              <p><strong>수탁자: </strong> {{ detail.trustee }}</p>
            </template>
            <!-- 전세권설정 -->
            <template v-else-if="item.key == 'jeonseRightInfos'">
              <p><strong>접수일자: </strong> {{ detail.date }}</p>
              <p><strong>전세권자: </strong> {{ detail.mortgagor }}</p>
              <p>
                <strong>전세금: </strong> {{ formatCurrency(detail.deposit) }}
              </p>
            </template>
            <!-- 가등기 -->
            <template v-else-if="item.key == 'provisionalRegistrationInfos'">
              <p><strong>접수일자: </strong> {{ detail.date }}</p>
              <p>
                <strong>가등기권자: </strong> {{ detail.registeredRightHolder }}
              </p>
            </template>
            <template v-else>
              <p>상세 정보가 없습니다.</p>
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
