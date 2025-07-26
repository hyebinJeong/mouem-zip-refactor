<script setup>
import { ref } from 'vue';

const selectedFile = ref(null);
const mortgageInfos = ref([]);
const seizureInfos = ref([]);
const provisionalSeizureInfos = ref([]);
const auctionInfos = ref([]);
const provisionalRegistrationInfos = ref([]);
const injunctionInfos = ref([]);
const jeonseRightInfos = ref([]);
const trustInfos = ref([]);

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
    // const tableRes = await fetch('http://localhost:8080/safety-check', {
    // method: 'POST',
    // body: formData,
    // });
    // if (tableRes.ok) {
    //   tableData.value = await tableRes.json();
    // } else {
    //   console.error('표 추출 실패');
    // }
    const analysisResult = await fetch('http://localhost:8080/safety-check', {
      method: 'POST',
      body: formData,
    });
    if (analysisResult.ok) {
      const result = await analysisResult.json();
      mortgageInfos.value = result.mortgageInfos || [];
      seizureInfos.value = result.seizureInfos || [];
      provisionalSeizureInfos.value = result.provisionalSeizureInfos || [];
      auctionInfos.value = result.auctionInfos || [];
      provisionalRegistrationInfos.value =
        result.provisionalRegistrationInfos || [];
      injunctionInfos.value = result.injunctionInfos || [];
      jeonseRightInfos.value = result.jeonseRightInfos || [];
      trustInfos.value = result.trustInfos || [];
    } else {
      console.error('분석 실패');
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

  <!-- 근저당 정보 표 -->
  <div v-if="mortgageInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">근저당권 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">채권최고액</th>
          <th class="border px-2 py-1">근저당권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in mortgageInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.maxClaimAmount }}</td>
          <td class="border px-2 py-1">{{ item.mortgageHolder }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
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
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">권리자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in seizureInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.rightHolder }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
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
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">청구금액</th>
          <th class="border px-2 py-1">채권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in provisionalSeizureInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.maxClaimAmount }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
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
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">채권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in auctionInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
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
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">가등기권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in provisionalRegistrationInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.registeredRightHolder }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
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
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">채권자(또는 권리자)</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in injunctionInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 전세권설정 정보 표 -->
  <div v-if="jeonseRightInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">전세권설정 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">전세금</th>
          <th class="border px-2 py-1">전세권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in jeonseRightInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.deposit }}</td>
          <td class="border px-2 py-1">{{ item.mortgagor }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 신탁등기 정보 표 -->
  <div v-if="trustInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">신탁등기 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">수탁자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in trustInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.trustee }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
