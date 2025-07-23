<script setup>
import { ref } from 'vue';

const selectedFile = ref(null);
const tableData = ref([]);
const mortgageInfos = ref([]);
const seizureInfos = ref([]);

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

    // 근저당 정보 받아오기
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
          <td class="border px-2 py-1">{{ item.seizureHolder }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
