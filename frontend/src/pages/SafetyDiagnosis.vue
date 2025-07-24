<script setup>
import { ref } from 'vue';

const selectedFile = ref(null);
const tableData = ref([]);
const mortgageInfos = ref([]);
const seizureInfos = ref([]);
const provisionalSeizureInfos = ref([]);
const auctionInfos = ref([]);
const provisionalRegistrationInfos = ref([]);
const injunctionInfos = ref([]);

function handleFileChange(event) {
  selectedFile.value = event.target.files[0];
}

async function uploadFile() {
  if (!selectedFile.value) {
    alert('PDF 파일을 선택해주세요.');
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    // 표 데이터 받아오기
    const tableRes = await fetch('http://localhost:8080/safety-check', {
      method: 'POST',
      body: formData,
    });

    if (tableRes.ok) {
      tableData.value = await tableRes.json();
    } else {
      console.error('표 추출 실패');
    }

    // 근저당 정보 받아오기
    const mortgageRes = await fetch(
      'http://localhost:8080/safety-check/mortgages',
      {
        method: 'POST',
        body: formData,
      }
    );

    if (mortgageRes.ok) {
      mortgageInfos.value = await mortgageRes.json();
    } else {
      console.error('근저당 정보 추출 실패');
    }

    // 압류 정보 받아오기
    const seizureRes = await fetch(
      'http://localhost:8080/safety-check/seizures',
      {
        method: 'POST',
        body: formData,
      }
    );

    if (seizureRes.ok) {
      seizureInfos.value = await seizureRes.json();
    } else {
      console.error('압류 정보 추출 실패');
    }

    // 가압류 정보 받아오기
    const provisionalSeizureRes = await fetch(
      'http://localhost:8080/safety-check/provisional',
      {
        method: 'POST',
        body: formData,
      }
    );

    if (provisionalSeizureRes.ok) {
      provisionalSeizureInfos.value = await provisionalSeizureRes.json();
    } else {
      console.error('가압류 정보 추출 실패');
    }

    // 경매 정보 받아오기
    const auctionRes = await fetch(
      'http://localhost:8080/safety-check/auction',
      {
        method: 'POST',
        body: formData,
      }
    );

    if (auctionRes.ok) {
      auctionInfos.value = await auctionRes.json();
    } else {
      console.error('경매 정보 추출 실패');
    }

    // 가등기 정보 받아오기
    const provisionalRegistrationRes = await fetch(
      'http://localhost:8080/safety-check/registration',
      {
        method: 'POST',
        body: formData,
      }
    );

    if (provisionalRegistrationRes.ok) {
      provisionalRegistrationInfos.value =
        await provisionalRegistrationRes.json();
    } else {
      console.error('가등기 정보 추출 실패');
    }

    // 가등기 정보 받아오기
    const injunctionRes = await fetch(
      'http://localhost:8080/safety-check/injunction',
      {
        method: 'POST',
        body: formData,
      }
    );

    if (injunctionRes.ok) {
      injunctionInfos.value = await injunctionRes.json();
    } else {
      console.error('가처분 정보 추출 실패');
    }
  } catch (error) {
    console.error('업로드 중 오류 발생:', error);
  }
}
</script>

<template>
  <!-- 업로드 UI -->
  <div v-if="!mortgageInfos.length">
    <input type="file" @change="handleFileChange" accept="application/pdf" />
    <button
      @click="uploadFile"
      class="mt-2 bg-blue-500 text-white px-4 py-2 rounded"
    >
      다음
    </button>
  </div>

  <!-- 표 추출 결과 보기 -->
  <div v-if="tableData.length > 0" class="mt-6">
    <h3 class="text-lg font-semibold mb-2">표 추출 결과</h3>
    <table class="border-collapse border border-gray-400 w-full">
      <tbody>
        <tr v-for="(row, rowIndex) in tableData" :key="rowIndex">
          <td
            v-for="(cell, cellIndex) in row"
            :key="cellIndex"
            class="border border-gray-300 px-2 py-1"
          >
            {{ cell }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 근저당 정보 표 -->
  <div v-if="mortgageInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">근저당권 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">순위</th>
          <th class="border px-2 py-1">등기목적</th>
          <th class="border px-2 py-1">등기원인</th>
          <th class="border px-2 py-1">채권최고액</th>
          <th class="border px-2 py-1">근저당권자</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in mortgageInfos" :key="index">
          <td class="border px-2 py-1">{{ item.rank }}</td>
          <td class="border px-2 py-1">{{ item.registrationPurpose }}</td>
          <td class="border px-2 py-1">{{ item.registrationCause }}</td>
          <td class="border px-2 py-1">{{ item.maxClaimAmount }}</td>
          <td class="border px-2 py-1">{{ item.mortgageHolder }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 압류 정보 표 -->
  <div v-if="seizureInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">압류 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">순위</th>
          <th class="border px-2 py-1">등기목적</th>
          <th class="border px-2 py-1">등기원인</th>
          <th class="border px-2 py-1">권리자</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in seizureInfos" :key="index">
          <td class="border px-2 py-1">{{ item.rank }}</td>
          <td class="border px-2 py-1">{{ item.registrationPurpose }}</td>
          <td class="border px-2 py-1">{{ item.registrationCause }}</td>
          <td class="border px-2 py-1">{{ item.rightHolder }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 가압류 정보 표 -->
  <div v-if="provisionalSeizureInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">가압류 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">순위</th>
          <th class="border px-2 py-1">등기목적</th>
          <th class="border px-2 py-1">등기원인</th>
          <th class="border px-2 py-1">청구금액</th>
          <th class="border px-2 py-1">채권자</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in provisionalSeizureInfos" :key="index">
          <td class="border px-2 py-1">{{ item.rank }}</td>
          <td class="border px-2 py-1">{{ item.registrationPurpose }}</td>
          <td class="border px-2 py-1">{{ item.registrationCause }}</td>
          <td class="border px-2 py-1">{{ item.maxClaimAmount }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 경매 정보 표 -->
  <div v-if="auctionInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">경매 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">순위</th>
          <th class="border px-2 py-1">등기목적</th>
          <th class="border px-2 py-1">등기원인</th>
          <th class="border px-2 py-1">채권자</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in auctionInfos" :key="index">
          <td class="border px-2 py-1">{{ item.rank }}</td>
          <td class="border px-2 py-1">{{ item.registrationPurpose }}</td>
          <td class="border px-2 py-1">{{ item.registrationCause }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 가등기 정보 표 -->
  <div v-if="provisionalRegistrationInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">가등기 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">순위</th>
          <th class="border px-2 py-1">등기목적</th>
          <th class="border px-2 py-1">등기원인</th>
          <th class="border px-2 py-1">가등기권자</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in provisionalRegistrationInfos" :key="index">
          <td class="border px-2 py-1">{{ item.rank }}</td>
          <td class="border px-2 py-1">{{ item.registrationPurpose }}</td>
          <td class="border px-2 py-1">{{ item.registrationCause }}</td>
          <td class="border px-2 py-1">{{ item.registeredRightHolder }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 가처분 정보 표 -->
  <div v-if="injunctionInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">가처분 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">순위</th>
          <th class="border px-2 py-1">등기목적</th>
          <th class="border px-2 py-1">등기원인</th>
          <th class="border px-2 py-1">채권자(또는 권리자)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in injunctionInfos" :key="index">
          <td class="border px-2 py-1">{{ item.rank }}</td>
          <td class="border px-2 py-1">{{ item.registrationPurpose }}</td>
          <td class="border px-2 py-1">{{ item.registrationCause }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
