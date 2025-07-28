<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const contract = ref({
  lessor: '',
  lessee: '',
  address: '',
  contractAmount: '',
  deposit: '',
  rent: '',
  structure: '',
  maintenanceFee: '',
  startDate: '',
  endDate: '',
  special: '',
});

// 기본 특약 3개
const defaultSpecialTerms = [
  '임대인은 임차인의 대항력 및 확정일자 확보 이전에 해당 부동산에 제3자와의 담보권 설정, 매도, 제3자 점유를 하지 않는다.',
  '임대인은 계약 체결 당시 해당 주택에 존재하는 모든 선순위 권리와 보증금 정보(선순위 세입자 포함)를 정확히 고지하였으며, 향후 변동 시 즉시 통보한다.',
  '본 계약은 공인중개사를 통하지 않고 당사자 간 직거래로 체결되었으며, 이에 따라 발생할 수 있는 권리분쟁은 민법 및 주택임대차보호법에 따르기로 한다.',
];

// 전체 특약 목록: 기본 + 사용자 입력
const mergedSpecialTerms = computed(() => {
  const userSpecials = Array.isArray(contract.value.special)
    ? contract.value.special
    : contract.value.special
    ? [contract.value.special]
    : [];
  return [...defaultSpecialTerms, ...userSpecials];
});

onMounted(() => {
  const stored = sessionStorage.getItem('contractData');
  if (stored) {
    contract.value = JSON.parse(stored);
  } else {
    router.replace({ name: 'reference-contract' });
  }
});
</script>

<template>
  <div class="page-wrapper">
    <div class="container">
      <h1><span class="highlight">계약서</span>가 완성되었어요.</h1>
      <p class="sub">계약서는 마이페이지에서 다운로드할 수 있어요.</p>

      <!-- 큰 매물명 제목 -->
      <h2 class="property-title">힐스테이트 팔교엘포레A3BL</h2>
      <hr class="divider" />

      <!-- 표 형태 정보 표시 -->
      <div class="table-box">
        <table class="info-table">
          <tr>
            <td><strong>임대인 :</strong> {{ contract.lessor }}</td>
            <td><strong>임차인 :</strong> {{ contract.lessee }}</td>
          </tr>
        </table>
        <hr class="divider full" />

        <table class="info-table">
          <tr>
            <td><strong>소재지 :</strong> {{ contract.address }}</td>
            <td><strong>월세 :</strong> {{ contract.rent }}</td>
          </tr>
          <tr>
            <td><strong>계약금 :</strong> {{ contract.contractAmount }}</td>
            <td><strong>보증금 :</strong> {{ contract.deposit }}</td>
          </tr>
          <tr>
            <td colspan="2">
              <strong>임대차 기간 :</strong> {{ contract.startDate }} ~
              {{ contract.endDate }}
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <strong>관리비 :</strong> {{ contract.maintenanceFee }}
            </td>
          </tr>
        </table>
      </div>

      <hr class="divider" />

      <!-- 특약사항 -->
      <div class="special-section">
        <h3>특약 사항</h3>

        <p
          v-for="(item, index) in mergedSpecialTerms"
          :key="index"
          class="custom"
        >
          {{ index + 1 }}. {{ item }}
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-wrapper {
  display: flex;
  justify-content: center;
  padding: 40px 16px;
  background-color: #f5f7fa;
}

.container {
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  max-width: 800px;
  width: 100%;
  padding: 40px 32px;
  box-sizing: border-box;
}

h1 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
}

.highlight {
  color: #2563eb;
}

.sub {
  font-size: 14px;
  color: #444;
  margin-bottom: 24px;
}

.property-title {
  font-size: 22px;
  font-weight: 700;
  margin: 20px 0;
  color: #111827;
  text-align: center;
}

.table-box {
  margin-bottom: 24px;
  font-size: 14px;
  color: #222;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
}

.info-table td {
  padding: 10px;
  border: none;
  vertical-align: top;
}

.line {
  border: none;
  border-top: 2px solid #000;
  margin: 16px 0;
}

.divider {
  border: none;
  border-top: 1px solid #ccc;
  margin: 24px 0;
}

.special-section {
  margin-top: 16px;
}

.special-section h3 {
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 12px;
  color: #111;
}

.special-section p {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin-bottom: 10px;
}

.special-section strong {
  font-weight: 600;
  color: #111827;
}

.special-section .custom {
  margin-top: 16px;
  background-color: #f0f4ff;
  padding: 12px;
  border-radius: 8px;
  color: #1e3a8a;
  font-weight: 500;
}
</style>
