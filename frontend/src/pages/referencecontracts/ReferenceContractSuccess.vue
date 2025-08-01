<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const contract = ref({
  contractName: '',
  lessor: '',
  lessee: '',
  address: '',
  landCategory: '',
  landArea: '',
  structure: '',
  buildingArea: '',
  leasePart: '',
  leaseArea: '',
  deposit: '',
  contractAmount: '',
  rent: '',
  maintenanceFee: '',
  startDate: '',
  endDate: '',
  special: [],
});

const mergedSpecialTerms = computed(() => {
  const userSpecials = Array.isArray(contract.value.special)
    ? contract.value.special
    : contract.value.special
    ? [contract.value.special]
    : [];
  return [...userSpecials];
});

// ✅ 모달 상태
const showModal = ref(true);

const closeModal = () => {
  showModal.value = false;
};

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

      <!-- ✅ 계약서 이름 -->
      <h2 class="property-title">
        {{ contract.contractName || '계약서 이름이 입력되지 않았습니다' }}
      </h2>

      <hr class="divider" />

      <div class="table-box">
        <table class="info-table">
          <tr>
            <td>
              <div class="label">임대인(집주인)</div>
              <div class="value">{{ contract.lessor }}</div>
            </td>
            <td>
              <div class="label">임차인(세입자)</div>
              <div class="value">{{ contract.lessee }}</div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="label">소재지</div>
              <div class="value">{{ contract.address }}</div>
            </td>
            <td>
              <div class="label">토지 지목</div>
              <div class="value">{{ contract.landCategory }}</div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="label">토지 면적</div>
              <div class="value">{{ contract.landArea }}</div>
            </td>
            <td>
              <div class="label">건물 구조·용도</div>
              <div class="value">{{ contract.structure }}</div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="label">건물 면적</div>
              <div class="value">{{ contract.buildingArea }}</div>
            </td>
            <td>
              <div class="label">임차할 부분</div>
              <div class="value">{{ contract.leasePart }}</div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="label">임차할 면적</div>
              <div class="value">{{ contract.leaseArea }}</div>
            </td>
            <td>
              <div class="label">보증금</div>
              <div class="value">{{ contract.deposit }}</div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="label">계약금</div>
              <div class="value">{{ contract.contractAmount }}</div>
            </td>
            <td>
              <div class="label">잔금</div>
              <div class="value">{{ contract.rent }}</div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="label">관리비</div>
              <div class="value">{{ contract.maintenanceFee }}</div>
            </td>
            <td colspan="2">
              <div class="label">임대차 기간</div>
              <div class="value">
                {{ contract.startDate }} ~ {{ contract.endDate }}
              </div>
            </td>
          </tr>
        </table>
      </div>

      <hr class="divider" />

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

    <!-- ✅ 계약서 자동 삭제 안내 모달 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <h2>📌 계약서 자동 삭제 안내</h2>
        <p>
          계약서는 작성일 기준 <strong>50일 후 자동 삭제</strong>됩니다.<br />
          필요 시 사전 <strong>캡쳐 또는 다운로드</strong>해 주시기 바랍니다.
        </p>
        <button class="close-btn" @click="closeModal">확인</button>
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
  padding: 12px;
  border: none;
  vertical-align: top;
}

/* ✅ 라벨 / 값 스타일 */
.label {
  font-size: 15px;
  font-weight: 600;
  color: #111;
  margin-bottom: 6px;
}

.value {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  white-space: pre-line; /* 줄바꿈 허용 */
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

.special-section .custom {
  margin-top: 16px;
  background-color: #f0f4ff;
  padding: 12px;
  border-radius: 8px;
  color: #1e3a8a;
  font-weight: 500;
}

/* ✅ 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-content {
  background: white;
  padding: 32px 24px;
  border-radius: 12px;
  max-width: 400px;
  width: 90%;
  text-align: center;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.3s ease;
}

.modal-content h2 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #1e3a8a;
}

.modal-content p {
  font-size: 15px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 20px;
}

.close-btn {
  background: #2563eb;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.close-btn:hover {
  background: #1d4ed8;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
