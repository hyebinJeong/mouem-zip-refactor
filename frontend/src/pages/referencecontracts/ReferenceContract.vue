<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';

const router = useRouter();
const auth = useAuthStore();

// 폼 데이터
const contractName = ref('');
const lessor = ref('');
const lessee = ref('');
const address = ref('');
const contractAmount = ref('');
const deposit = ref('');
const rent = ref('');
const structure = ref('');
const maintenanceFee = ref('');
const startDate = ref('');
const endDate = ref('');
const special = ref(['']);
const landCategory = ref('');
const landArea = ref('');
const buildingArea = ref('');
const leasePart = ref('');
const leaseArea = ref('');

// 세션스토리지 저장/복원
function saveContractToSession() {
  const contractData = {
    contractName: contractName.value,
    lessor: lessor.value,
    lessee: lessee.value,
    address: address.value,
    contractAmount: contractAmount.value,
    deposit: deposit.value,
    rent: rent.value,
    structure: structure.value,
    maintenanceFee: maintenanceFee.value,
    startDate: startDate.value,
    endDate: endDate.value,
    special: special.value,
    landCategory: landCategory.value,
    landArea: landArea.value,
    buildingArea: buildingArea.value,
    leasePart: leasePart.value,
    leaseArea: leaseArea.value,
  };
  sessionStorage.setItem('contractData', JSON.stringify(contractData));
}

// 새로고침 감지
const isHardReload = () => {
  const navType = performance.getEntriesByType('navigation')[0]?.type;
  return navType === 'reload' || navType === 'navigate';
};

onMounted(() => {
  const fromSpecialPage = sessionStorage.getItem('fromSpecialPage') === 'true';
  const contractData = sessionStorage.getItem('contractData');

  if (isHardReload() && !fromSpecialPage) {
    // 초기화
    contractName.value = '';
    lessor.value = '';
    lessee.value = '';
    address.value = '';
    contractAmount.value = '';
    deposit.value = '';
    rent.value = '';
    structure.value = '';
    maintenanceFee.value = '';
    startDate.value = '';
    endDate.value = '';
    special.value = [''];
    landCategory.value = '';
    landArea.value = '';
    buildingArea.value = '';
    leasePart.value = '';
    leaseArea.value = '';
    sessionStorage.removeItem('contractData');
    sessionStorage.removeItem('selectedClauses');
  } else if (contractData) {
    const data = JSON.parse(contractData);
    contractName.value = data.contractName || '';
    lessor.value = data.lessor || '';
    lessee.value = data.lessee || '';
    address.value = data.address || '';
    contractAmount.value = data.contractAmount || '';
    deposit.value = data.deposit || '';
    rent.value = data.rent || '';
    structure.value = data.structure || '';
    maintenanceFee.value = data.maintenanceFee || '';
    startDate.value = data.startDate || '';
    endDate.value = data.endDate || '';
    special.value =
      Array.isArray(data.special) && data.special.length > 0
        ? data.special
        : [''];
    landCategory.value = data.landCategory || '';
    landArea.value = data.landArea || '';
    buildingArea.value = data.buildingArea || '';
    leasePart.value = data.leasePart || '';
    leaseArea.value = data.leaseArea || '';
  }

  // 특약 선택 합치기
  const selected = JSON.parse(
    sessionStorage.getItem('selectedClauses') || '[]'
  );
  const newClauses = selected.map((clause) => clause.text).filter(Boolean);
  newClauses.forEach((clause) => {
    if (!special.value.includes(clause)) {
      special.value.unshift(clause);
    }
  });

  sessionStorage.removeItem('fromSpecialPage');
});

// 입력 감시해서 세션에 저장
watch(
  [
    contractName,
    lessor,
    lessee,
    address,
    contractAmount,
    deposit,
    rent,
    structure,
    maintenanceFee,
    startDate,
    endDate,
    special,
    landCategory,
    landArea,
    buildingArea,
    leasePart,
    leaseArea,
  ],
  saveContractToSession,
  { deep: true }
);

// 특약 입력 제어
const addSpecialTerm = () => special.value.push('');
const removeSpecialTerm = (index) => {
  if (special.value.length > 1) special.value.splice(index, 1);
  else special.value = [''];
};

// ✅ 제출 로직
const onSubmit = async () => {
  // 필수 입력 검증
  const requiredFields = [
    contractName.value,
    lessor.value,
    lessee.value,
    address.value,
    contractAmount.value,
    deposit.value,
    rent.value,
    structure.value,
    maintenanceFee.value,
    startDate.value,
    endDate.value,
    landCategory.value,
    landArea.value,
    buildingArea.value,
    leasePart.value,
    leaseArea.value,
  ];
  if (requiredFields.some((field) => !field || field.trim() === '')) {
    alert('모든 필수 항목을 입력해주세요.');
    return;
  }

  // 특약 최소 1개
  const validSpecials = special.value.filter(
    (term) => term && term.trim() !== ''
  );
  if (validSpecials.length === 0) {
    alert('특약사항을 최소 1개 이상 입력하거나 선택해주세요.');
    return;
  }

  try {
    // ✅ 백엔드 DTO와 맞춘 payload
    const payload = {
      contractName: contractName.value,
      lessorName: lessor.value,
      lesseeName: lessee.value,
      address: address.value,
      landCategory: landCategory.value,
      landArea: parseFloat(landArea.value),
      buildingUsage: structure.value,
      buildingArea: parseFloat(buildingArea.value),
      leasedPart: leasePart.value,
      leasedArea: parseFloat(leaseArea.value),
      deposit: parseInt(deposit.value),
      downPayment: parseInt(contractAmount.value),
      balance: parseInt(rent.value),
      maintenanceCost: parseInt(maintenanceFee.value),
      leaseStart: startDate.value,
      leaseEnd: endDate.value,
      specialClauses: validSpecials, // ✅ JSON 배열
    };

    const res = await axios.post('/api/contract', payload, {
      headers: { Authorization: `Bearer ${auth.token}` },
    });

    alert('계약서가 저장되었습니다.');
    const contractId = res.data; // 백엔드에서 생성된 contractId 반환
    router.push({
      name: 'ReferenceContractSuccess',
      query: { id: contractId, from: 'list' },
    });
  } catch (err) {
    console.error('계약서 저장 중 오류 발생:', err);
    alert('저장에 실패했습니다.');
  }

  saveContractToSession();
};

// 특약 예시 페이지 이동
const goToSpecialPage = () => {
  sessionStorage.setItem('fromSpecialPage', 'true');
  router.push({ name: 'SpecialContractsRecommendation' });
};
</script>

<template>
  <div class="wrapper">
    <div class="contract-box">
      <h1 class="title">계약서 작성을 위해 필요한 정보를 입력해주세요.</h1>
      <form class="form-grid" @submit.prevent="onSubmit">
        <!-- ✅ 계약서 이름 맨 위 -->
        <div class="form-row full">
          <label>계약서 이름</label>
          <input v-model="contractName" type="text" placeholder="성명" />
        </div>
        <div class="form-row">
          <label>임대인(집주인)</label>
          <input v-model="lessor" type="text" placeholder="성명" />
        </div>
        <div class="form-row">
          <label>임차인(세입자)</label>
          <input v-model="lessee" type="text" placeholder="성명" />
        </div>
        <div class="form-row full">
          <label>소재지</label>
          <input
            v-model="address"
            type="text"
            placeholder="도로명 주소를 입력해주세요"
          />
        </div>
        <div class="form-row">
          <label>토지 지목</label>
          <input v-model="landCategory" type="text" placeholder="대" />
        </div>
        <div class="form-row">
          <label>토지 면적</label>
          <input v-model="landArea" type="text" placeholder="m²" />
        </div>
        <div class="form-row">
          <label>건물 구조·용도</label>
          <input v-model="structure" type="text" placeholder="다세대 주택" />
        </div>
        <div class="form-row">
          <label>건물 면적</label>
          <input v-model="buildingArea" type="text" placeholder="m²" />
        </div>
        <div class="form-row">
          <label>임차할 부분</label>
          <input v-model="leasePart" type="text" placeholder="동·호수" />
        </div>
        <div class="form-row">
          <label>임차할 면적</label>
          <input v-model="leaseArea" type="text" placeholder="m²" />
        </div>
        <div class="form-row">
          <label>보증금</label>
          <input v-model="deposit" type="text" placeholder="원" />
        </div>
        <div class="form-row">
          <label>계약금</label>
          <input v-model="contractAmount" type="text" placeholder="원" />
        </div>
        <div class="form-row">
          <label>잔금</label>
          <input v-model="rent" type="text" placeholder="원" />
        </div>
        <div class="form-row">
          <label>관리비</label>
          <input v-model="maintenanceFee" type="text" placeholder="원" />
        </div>
        <div class="form-row full date-range">
          <label>임대차 기간</label>
          <div class="date-inputs">
            <input v-model="startDate" type="date" />
            <span>부터</span>
            <input v-model="endDate" type="date" />
            <span>까지</span>
          </div>
        </div>

        <!-- 특약사항 -->
        <div class="form-row full special-terms">
          <label>특약 사항</label>
          <div class="special-input-wrapper">
            <div class="special-list">
              <div
                class="special-input"
                v-for="(term, index) in special"
                :key="index"
              >
                <textarea
                  v-model="special[index]"
                  placeholder="특약 사항을 입력하세요"
                  rows="3"
                ></textarea>
                <div class="btn-group">
                  <button
                    v-if="index === special.length - 1"
                    type="button"
                    class="btn-small add"
                    @click="addSpecialTerm"
                  >
                    ➕
                  </button>
                  <button
                    type="button"
                    class="btn-small remove"
                    @click="removeSpecialTerm(index)"
                  >
                    ➖
                  </button>
                </div>
              </div>
            </div>
            <div class="side-controls">
              <button
                type="button"
                class="btn-template"
                @click="goToSpecialPage"
              >
                특약 예시에서 선택하기
              </button>
              <p class="tip">특약사항을 추가해드릴게요.</p>
            </div>
          </div>
        </div>

        <div class="button-group full">
          <button
            type="button"
            class="btn-back"
            @click="router.push({ name: 'home' })"
          >
            뒤로 가기
          </button>
          <button type="submit" class="btn-submit">작성 완료</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.wrapper {
  display: flex;
  justify-content: center;
  padding: 40px;
  background-color: white;
}
.contract-box {
  background-color: #f5f7fa;
  border-radius: 16px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.06);
  padding: 40px;
  width: 100%;
  max-width: 920px;
}
.title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 32px;
}
.form-grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.form-row {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 16px;
}
.form-row.full {
  flex-wrap: wrap;
}
label {
  width: 160px;
  font-weight: 700;
  text-align: left;
  flex-shrink: 0;
  font-size: 15px;
}
input[type='text'],
input[type='date'] {
  flex-grow: 1;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
}
.date-range .date-inputs {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-grow: 1;
}
.date-range label {
  width: 160px;
}

.special-terms .special-input-wrapper {
  display: flex;
  gap: 16px;
  flex-grow: 1;
  align-items: flex-start;
}
.special-list {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.special-input {
  display: flex;
  align-items: center;
  gap: 8px;
}
.special-input textarea {
  width: 100%;
  flex-grow: 1;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 14px;
  resize: vertical;
}
.btn-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.btn-small {
  padding: 6px 12px;
  border-radius: 6px;
  border: none;
  background-color: #ddd;
  cursor: pointer;
  white-space: nowrap;
}
.btn-small.add {
  background-color: skyblue;
}
.btn-small.remove {
  background-color: palevioletred;
}
.side-controls {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding-top: 6px;
}
.btn-template {
  background-color: #eef5ff;
  color: #1e60d1;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
}
.tip {
  font-size: 13px;
  color: #888;
  margin: 0;
}
.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 40px;
}
.btn-back {
  background-color: #f0f0f0;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: bold;
  border: none;
  cursor: pointer;
}
.btn-submit {
  background-color: #007bff;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: bold;
  border: none;
  cursor: pointer;
}
</style>
