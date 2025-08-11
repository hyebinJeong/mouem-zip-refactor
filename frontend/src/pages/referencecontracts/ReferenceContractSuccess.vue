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
    <div v-if="isLoadingPDF" class="loading-overlay d-flex justify-content-center align-items-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <div class="contract-document" id="pdf-area">
      <h1 class="document-title">부동산 임대차 계약서</h1>
      <div class="lessor-lessee-info">
        <div class="info-item">
          임대인({{ contract.lessorName }})과 임차인({{ contract.lesseeName }})은 아래와 같이 임대차 계약을 체결한다.
        </div>
      </div>
      <table class="document-table">
        <tr class="table-header-row">
          <td colspan="4">[ 임차주택의 표시 ]</td>
        </tr>
        <tr>
          <td class="table-label">소재지</td>
          <td class="table-value" colspan="3">{{ contract.address }}</td>
        </tr>
        <tr>
          <td class="table-label">토지 지목</td>
          <td class="table-value">{{ contract.landCategory }}</td>
          <td class="table-label">토지 면적</td>
          <td class="table-value">{{ formatArea(contract.landArea) }}</td>
        </tr>
        <tr>
          <td class="table-label">건물 구조·용도</td>
          <td class="table-value">{{ contract.buildingUsage }}</td>
          <td class="table-label">건물면적</td>
          <td class="table-value">{{ formatArea(contract.buildingArea) }}</td>
        </tr>
        <tr>
          <td class="table-label">임차할 부분</td>
          <td class="table-value">{{ contract.leasedPart }}</td>
          <td class="table-label">임차할 면적</td>
          <td class="table-value">{{ formatArea(contract.leasedArea) }}</td>
        </tr>
        
        <tr class="table-header-row">
          <td colspan="4">[ 계약 내용 ]</td>
        </tr>
        <tr>
          <td class="table-label">보증금</td>
          <td class="table-value">{{ formatCurrency(contract.deposit) }}</td>
          <td class="table-label">계약금</td>
          <td class="table-value">{{ formatCurrency(contract.downPayment) }}</td>
        </tr>
        <tr>
          <td class="table-label">잔금</td>
          <td class="table-value">{{ formatCurrency(contract.balance) }}</td>
          <td class="table-label">관리비</td>
          <td class="table-value">{{ formatCurrency(contract.maintenanceCost) }}</td>
        </tr>
        
        <tr class="table-header-row">
          <td colspan="4">[ 임대차 기간 ]</td>
        </tr>
        <tr>
          <td class="table-label">기간</td>
          <td class="table-value" colspan="3">
            {{ formatDate(contract.leaseStart) }} ~ {{ formatDate(contract.leaseEnd) }}
          </td>
        </tr>

        <tr class="table-header-row">
          <td colspan="4">특약사항</td>
        </tr>
        <tr v-if="mergedSpecialTerms.length">
          <td colspan="4" class="special-clauses-cell">
            <ul class="special-list">
              <li v-for="(clause, idx) in mergedSpecialTerms" :key="idx">
                {{ idx + 1 }}. {{ clause }}
              </li>
            </ul>
          </td>
        </tr>
        <tr v-else>
          <td colspan="4" class="special-clauses-cell">등록된 특약이 없습니다.</td>
        </tr>
      </table>

    </div>

    <div class="button-area exclude-pdf">
      <button v-if="route.query.from === 'myPage'" class="btn btn-primary download-btn" @click="downloadPDF" :disabled="isLoadingPDF">
        {{ isLoadingPDF ? 'PDF 생성 중...' : '다운로드' }}
      </button>
      <button class="btn btn-secondary home-btn" @click="router.push({ name: 'home' })">
        홈으로 돌아가기
      </button>
    </div>
  </div>
</template>

<style scoped>
.page-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 100px;
  background-color: #f7f9fc;
}

.contract-document {
  background-color: #ffffff;
  border: 2px solid #000;
  max-width: 800px;
  width: 100%;
  padding: 40px;
  box-sizing: border-box;
}

.document-title {
  font-family: "Malgun Gothic", "Nanum Gothic", sans-serif;
  font-size: 36px; /* 폰트 크기 확대 */
  font-weight: bold;
  text-align: center;
  margin-bottom: 30px;
}

.document-table {
  width: 100%;
  border-collapse: collapse;
  border: 2px solid #000;
}

.document-table tr {
  border: 1px solid #000;
}

.document-table td{
  border: 1px solid #000;
  padding:15px;
  font-size:16px;
}


.document-table tr.table-header-row {
  background-color: #e9ecef;
  text-align: left; /* 왼쪽 정렬 */
  font-weight: bold;
}
.document-table tr.table-header-row td {
  background-color:#e9ecef;
  text-align: left;
  font-weight: bold;
  padding: 15px 20px; /* 패딩 확대 */
  font-size: 18px; /* 폰트 크기 확대 */
  border-bottom: 2px solid #000;
}


.table-label {
  width: 140px;
  font-size: 16px; /* 폰트 크기 확대 */
  font-weight: 600;
  background-color: #f7f9fc;
  padding: 15px; /* 패딩 확대 */
  border-right: 1px solid #000;
  text-align: center;
}

.table-value {
  padding: 15px; /* 패딩 확대 */
  font-size: 16px; /* 폰트 크기 확대 */
  border-right: 1px solid #000;
}
.document-table tr:last-child td.table-value {
  border-bottom: none;
}

.special-clauses-cell {
  padding: 15px; /* 패딩 확대 */
  font-size: 16px; /* 폰트 크기 확대 */
  line-height: 1.6; /* 줄 간격 확대 */
}
.special-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}
.special-list li {
  margin-bottom: 6px; /* 마진 확대 */
  line-height: 1.6; /* 줄 간격 확대 */
}

.button-area {
  margin-top: 24px;
  display: flex;
  gap: 10px;
}

.download-btn {
  background-color: #1a80e5;
  color: white;
  border: none;
  padding: 12px 24px; /* 패딩 확대 */
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
}

.home-btn {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 12px 24px; /* 패딩 확대 */
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
}

.lessor-lessee-info {
  display: flex;
  justify-content: space-between;
  font-size: 1.2rem; /* 폰트 크기 확대 */
  font-weight: bold;
  margin-top: 20px;
  margin-bottom: 20px;
  border-top: 1px solid #000;
  border-bottom: 1px solid #000;
  padding: 12px 0; /* 패딩 확대 */
}
</style>