<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 반응형 필드들
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

// 특약사항 배열
const special = ref(['']);

// 특약사항 추가 버튼
const addSpecialTerm = () => {
  special.value.push('');
};

// 작성 완료 시 sessionStorage 저장
const onSubmit = () => {
  const contractData = {
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
    special: special.value.filter((s) => s.trim() !== ''),
  };

  sessionStorage.setItem('contractData', JSON.stringify(contractData));
  router.push({ name: 'ReferenceContractSuccess' });
};
</script>

<template>
  <div class="wrapper">
    <div class="contract-box">
      <h1 class="title">계약서 작성을 위해 필요한 정보를 입력해주세요.</h1>

      <form class="form-grid" @submit.prevent="onSubmit">
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
          <input type="text" placeholder="대" />
        </div>
        <div class="form-row">
          <label>토지 면적</label>
          <input type="text" placeholder="m²" />
        </div>

        <div class="form-row">
          <label>건물 구조·용도</label>
          <input v-model="structure" type="text" placeholder="다세대 주택" />
        </div>
        <div class="form-row">
          <label>건물 면적</label>
          <input type="text" placeholder="m²" />
        </div>

        <div class="form-row">
          <label>임차할 부분</label>
          <input type="text" placeholder="동·호수" />
        </div>
        <div class="form-row">
          <label>임차할 면적</label>
          <input type="text" placeholder="m²" />
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

        <!-- 특약사항 영역 -->
        <div class="form-row full special-terms">
          <label>특약 사항</label>
          <div
            class="special-input"
            v-for="(term, index) in special"
            :key="index"
          >
            <input
              v-model="special[index]"
              type="text"
              placeholder="특약 사항"
            />
            <button type="button" class="btn-small" @click="addSpecialTerm">
              ＋
            </button>
          </div>

          <button
            type="button"
            class="btn-template"
            @click="router.push({ name: 'SpecialContractsRecommendation' })"
          >
            특약 예시에서 선택하기
          </button>

          <p class="tip">특약사항을 추가해드릴게요.</p>
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
  font-size: 20px;
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
.special-terms .special-input {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.btn-small {
  padding: 6px 12px;
  border-radius: 6px;
  border: none;
  background-color: #ddd;
  cursor: pointer;
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
  margin-top: 6px;
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
