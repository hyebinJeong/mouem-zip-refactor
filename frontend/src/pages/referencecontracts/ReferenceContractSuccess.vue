<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();

// 계약서 상태
const contract = ref({
  contractName: '',
  lessorName: '',
  lesseeName: '',
  address: '',
  landCategory: '',
  landArea: '',
  buildingUsage: '',
  buildingArea: '',
  leasedPart: '',
  leasedArea: '',
  deposit: '',
  downPayment: '',
  balance: '',
  maintenanceCost: '',
  leaseStart: '',
  leaseEnd: '',
  specialClauses: [],
});

// ✅ 특약 배열 (List<String>)
const mergedSpecialTerms = ref([]);

// ✅ PDF 로딩 상태
const isLoadingPDF = ref(false);

// ✅ 헬퍼 함수
function formatCurrency(value) {
  if (!value && value !== 0) return '';
  return Number(value).toLocaleString() + '원';
}
function formatArea(value) {
  if (!value && value !== 0) return '';
  return `${value}㎡`;
}
function formatDate(value) {
  if (!value) return '';
  const date = new Date(value);
  if (isNaN(date)) return value;
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
}

onMounted(async () => {
  try {
    const contractId = route.params.id || route.query.id;
    if (!contractId || !auth.isLoggedIn) return;

    const res = await axios.get(`/api/contract/${contractId}`, {
      headers: { Authorization: `Bearer ${auth.token}` },
    });

    const data = res.data;

    contract.value = {
      contractName: data.contractName,
      lessorName: data.lessorName,
      lesseeName: data.lesseeName,
      address: data.address,
      landCategory: data.landCategory,
      landArea: data.landArea,
      buildingUsage: data.buildingUsage,
      buildingArea: data.buildingArea,
      leasedPart: data.leasedPart,
      leasedArea: data.leasedArea,
      deposit: data.deposit,
      downPayment: data.downPayment,
      balance: data.balance,
      maintenanceCost: data.maintenanceCost,
      leaseStart: data.leaseStart,
      leaseEnd: data.leaseEnd,
      specialClauses: data.specialClauses || [],
    };

    // 특약 복사
    mergedSpecialTerms.value = [...(data.specialClauses || [])];
  } catch (error) {
    console.error(
      '계약서 조회 실패:',
      error.response?.status,
      error.response?.data
    );
  }
});

async function downloadPDF() {
  const pdfArea = document.getElementById('pdf-area');
  if (!pdfArea) return;

  isLoadingPDF.value = true; // ✅ 로딩 시작

  // PDF 제외 요소 숨김
  const excludes = document.querySelectorAll('.exclude-pdf');
  excludes.forEach((el) => (el.style.visibility = 'hidden'));

  try {
    const canvas = await html2canvas(pdfArea, { scale: 2 });
    const imgData = canvas.toDataURL('image/png');
    const pdf = new jsPDF('p', 'mm', 'a4');

    const pageWidth = pdf.internal.pageSize.getWidth();
    const pageHeight = pdf.internal.pageSize.getHeight();
    const imgWidth = pageWidth;
    const imgHeight = (canvas.height * imgWidth) / canvas.width;

    let heightLeft = imgHeight;
    let position = 0;

    // 첫 페이지
    pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
    heightLeft -= pageHeight;

    // 남은 부분 페이지 추가
    while (heightLeft > 0) {
      position -= pageHeight;
      pdf.addPage();
      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;
    }

    pdf.save(`${contract.value.contractName || '계약서'}.pdf`);
  } catch (e) {
    console.error('PDF 생성 오류:', e);
  } finally {
    // 캡처 후 다시 보이게
    excludes.forEach((el) => (el.style.visibility = 'visible'));
    isLoadingPDF.value = false; // ✅ 로딩 종료
  }
}
</script>

<template>
  <div class="page-wrapper">
    <!-- ✅ PDF 생성 중일 때 로딩 오버레이 -->
    <div
      v-if="isLoadingPDF"
      class="loading-overlay d-flex justify-content-center align-items-center"
    >
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <div class="container" id="pdf-area">
      <!-- 상단 헤더 -->
      <div class="header-box">
        <h2 class="header-title">
          <span class="blue-text">계약서</span>가 완성되었어요.
        </h2>
        <p class="header-sub">계약서는 마이페이지에서 다운로드할 수 있어요.</p>
      </div>
      <div class="title-with-button">
        <div class="contract-name">
          <h3>{{ contract.contractName || '계약서 이름 없음' }}</h3>
        </div>
        <!-- ✅ 마이페이지에서 들어왔을 때만 다운로드 버튼 표시 -->
        <button
          v-if="route.query.from === 'myPage'"
          class="btn btn-primary exclude-pdf"
          @click="downloadPDF"
          :disabled="isLoadingPDF"
        >
          {{ isLoadingPDF ? 'PDF 생성 중...' : '다운로드' }}
        </button>
      </div>

      <hr class="divider" />

      <!-- 계약서 정보 -->
      <div class="table-box">
        <table class="info-table">
          <tr class="row-divider">
            <td>
              <div class="label">임대인</div>
              <div class="value">{{ contract.lessorName }}</div>
            </td>
            <td>
              <div class="label">임차인</div>
              <div class="value">{{ contract.lesseeName }}</div>
            </td>
          </tr>

          <tr class="row-divider">
            <td colspan="2">
              <div class="label">소재지</div>
              <div class="value">{{ contract.address }}</div>
            </td>
          </tr>

          <tr class="row-divider">
            <td>
              <div class="label">토지 지목</div>
              <div class="value">{{ contract.landCategory }}</div>
            </td>
            <td>
              <div class="label">토지 면적</div>
              <div class="value">{{ formatArea(contract.landArea) }}</div>
            </td>
          </tr>

          <tr class="row-divider">
            <td>
              <div class="label">건물 구조·용도</div>
              <div class="value">{{ contract.buildingUsage }}</div>
            </td>
            <td>
              <div class="label">건물 면적</div>
              <div class="value">{{ formatArea(contract.buildingArea) }}</div>
            </td>
          </tr>

          <tr class="row-divider">
            <td>
              <div class="label">임차할 부분</div>
              <div class="value">{{ contract.leasedPart }}</div>
            </td>
            <td>
              <div class="label">임차할 면적</div>
              <div class="value">{{ formatArea(contract.leasedArea) }}</div>
            </td>
          </tr>

          <tr class="row-divider">
            <td>
              <div class="label">보증금</div>
              <div class="value">{{ formatCurrency(contract.deposit) }}</div>
            </td>
            <td>
              <div class="label">계약금</div>
              <div class="value">
                {{ formatCurrency(contract.downPayment) }}
              </div>
            </td>
          </tr>

          <tr class="row-divider">
            <td>
              <div class="label">잔금</div>
              <div class="value">{{ formatCurrency(contract.balance) }}</div>
            </td>
            <td>
              <div class="label">관리비</div>
              <div class="value">
                {{ formatCurrency(contract.maintenanceCost) }}
              </div>
            </td>
          </tr>

          <tr>
            <td colspan="2">
              <div class="label">임대차 기간</div>
              <div class="value">
                {{ formatDate(contract.leaseStart) }} ~
                {{ formatDate(contract.leaseEnd) }}
              </div>
            </td>
          </tr>
        </table>
      </div>

      <hr class="divider" />

      <!-- ✅ 특약사항 -->
      <div class="special-section">
        <h3>특약 사항</h3>
        <div v-if="mergedSpecialTerms.length">
          <div
            v-for="(clause, idx) in mergedSpecialTerms"
            :key="idx"
            class="clause-box"
          >
            <span class="clause-number">{{ idx + 1 }}.</span>
            <span class="clause-text">{{ clause }}</span>
          </div>
        </div>
        <p v-else>등록된 특약이 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgb(255, 255, 255);
  z-index: 3000;
}

.page-wrapper {
  display: flex;
  justify-content: center;
  padding: 60px 100px;
  background-color: #f7f9fc;
}
.container {
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  max-width: 680px;
  width: 100%;
  padding: 60px 90px;
  box-sizing: border-box;
}
.header-box {
  margin-bottom: 16px;
}
.header-title {
  font-size: 26px;
  font-weight: 700;
  color: #000000;
  margin-bottom: 20px;
  margin-left: 10px;
}
.header-title .blue-text {
  color: #1a80e5;
}

.header-sub {
  font-size: 14px;
  color: #555;
  margin-left: 10px;
}
.contract-name {
  margin: 20px 0 20px 10px;
}
.contract-name h3 {
  font-size: 20px;
  font-weight: 670;
  color: #000000;
  text-align: left;
}
.divider {
  border: none;
  border-top: 1.4px solid #a6a6a6;
  margin: 24px 0;
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
.label {
  font-size: 15px;
  font-weight: 600;
  color: #4e4e4e;
  margin-bottom: 6px;
}
.value {
  font-size: 14px;
  color: #222222;
  line-height: 1.5;
  white-space: pre-line;
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

.clause-box {
  display: flex;
  align-items: center;
  background-color: #f0f4ff;
  border-radius: 6px;
  padding: 8px 12px;
  margin-bottom: 8px;
}

.clause-number {
  font-size: 13px;
  color: #4b6cb7;
  margin-right: 8px;
  font-weight: 500;
}

.clause-text {
  font-size: 14px;
  color: #111;
  flex: 1;
}

.info-table .row-divider td {
  border-bottom: 1.3px solid #efefef;
}

.info-table td {
  padding: 12px;
  vertical-align: top;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.title-with-button {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
}

.title-with-button .btn {
  margin-left: auto;
}
</style>
