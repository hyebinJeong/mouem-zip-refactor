<template>
  <div class="wrapper">
    <div class="container">
      <h1 class="main-title">
        매물 안전성 분석을 받은 매물이 있어요.<br />
        어떤 매물의 체크리스트를 확인해볼까요?
      </h1>

      <div class="table-wrapper">
        <table class="info-table">
          <thead>
          <tr>
            <th>진단 매물</th>
            <th>생성일</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="(item, index) in properties"
              :key="index"
              @click="handleRowClick(item)"
              class="clickable-row"
          >
            <td>{{ item.address }}</td>
            <td>{{ item.createdAt }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <h2 class="main-title">사전 체크용으로 체크리스트만 확인할래요</h2>

      <p class="small-text">
        안전 등급 분석에 포함되지 않고, 확인만 가능해요.
      </p>

      <div class="button-wrapper">
        <button @click="goToChecklist" class="blue-button">
          체크리스트 확인하러가기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

// 더미 매물 데이터
const properties = [
  { address: '서울시 강남구 역삼동 123-45', createdAt: '2025-07-20' },
  { address: '서울시 강남구 역삼동 678-90', createdAt: '2025-07-21' },
]

function goToChecklist() {
  router.push('/checklist/nondiagnosis')
}

function handleRowClick(item) {
  console.log('클릭한 매물:', item)
  // 나중에 여기에 상세 이동 router.push(`/property/${item.id}`) 등 연결 가능
}
</script>

<style scoped>
.wrapper {
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 100px;
}

.container {
  width: 100%;
  max-width: 800px;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  padding: 32px;
}

.main-title {
  font-size: 32px;
  font-weight: bold;
  color: #222;
  line-height: 1.5;
}

.table-wrapper {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  width: 80%;
  margin: 10px 0 60px 0;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.info-table thead {
  background-color: #f4f4f4;
  color: #555;
  text-align: left;
}

.info-table th,
.info-table td {
  padding: 12px;
  border-top: 1px solid #eee;
}

.clickable-row {
  cursor: pointer;
  transition: background-color 0.2s;
}

.clickable-row:hover {
  background-color: #f0f8ff;
}

.small-text {
  font-size: 14px;
  color: #888;
}

.button-wrapper {
  margin-top: 10px;
}

.blue-button {
  background-color: #1A80E5;
  width: 288px;
  height: 59px;
  font-size: 14px;
  color: white;
  font-weight: 600;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}

.blue-button:hover {
  background-color: #2563eb;
}
</style>
