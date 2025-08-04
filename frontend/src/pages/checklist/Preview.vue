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
            <th>남은 기간</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="(item, index) in properties"
              :key="index"
              @click="handleRowClick(item)"
              class="clickable-row"
          >
            <td>{{ item.registryName }}</td>
            <td>{{ item.createdAt }}</td>
            <td class="expires-text">{{ item.expiresIn }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <h2 class="main-title">사전 체크용으로 체크리스트만 확인할래요</h2>

      <p class="small-text">안전 등급 분석에 포함되지 않고, 확인만 가능해요.</p>

      <div class="button-wrapper">
        <button @click="goToChecklist" class="blue-button">
          체크리스트 확인하러가기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const properties = ref([]);

const auth = useAuthStore();
const userId = auth.userId || null; // 로그인하지 않은 경우 null
//const userId = 1;

onMounted(async () => {
  if (!userId) return; // userId 없으면 요청 안함

  try {
    const registryRes = await axios.get(`http://localhost:8080/api/registry/user/${userId}`);
    const registryList = registryRes.data;

    const today = new Date();

    properties.value = registryList.map(item => {
      const createdDate = new Date(item.analysisDate);
      const diffTime = createdDate.getTime() + 50 * 24 * 60 * 60 * 1000 - today.getTime();
      const remainingDays = Math.max(0, Math.ceil(diffTime / (1000 * 60 * 60 * 24)));

      return {
        registryId: item.registryId,
        registryName: item.registryName,
        createdAt: createdDate.toLocaleDateString('ko-KR'),
        expiresIn: remainingDays > 0 ? `${remainingDays}일 후에 만료됩니다.` : '',
        userId: userId,
      };
    });
  } catch (error) {
    console.error('데이터 불러오기 실패:', error);
  }
});

function goToChecklist() {
  router.push('/checklist/checklist');
}

function handleRowClick(item) {
  router.push({
    path: '/checklist/checklist',
    query: {
      userId: userId,
      registryId: item.registryId,
    },
  });
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
  background-color: #1a80e5;
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

.expires-text {
  color: #1a80e5; /* 파란색 */
  font-weight: 500;
}

</style>
