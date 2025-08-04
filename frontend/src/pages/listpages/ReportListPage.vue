<script setup>
import Header from '../../components/Header.vue';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import fileIcon from '@/assets/fileicon.png';
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';

const auth = useAuthStore();
const router = useRouter();

const reports = ref([]);

onMounted(async () => {
  if (!auth.isLoggedIn) return;
  const res = await axios.get('/api/reports/list', {
    params: { userId: auth.userId },
  });
  reports.value = res.data.map((item) => ({
    reportId: item.reportId,
    title: `${item.registryName}_리포트`,
    date: new Date(item.createdAt).toISOString().split('T')[0],
  }));
});

function onContractClick(item) {
  router.push({
    name: 'finalReportPage',
    query: { reportId: item.reportId, from: 'myPage' },
  });
}
</script>

<template>
  <div class="contract-page">
    <main class="content">
      <h2 class="page-title">계약 리스크 분석 리포트 목록</h2>
      <p class="page-desc">
        등기부등본 분석, 전세가율 계산, 체크리스트 점수를 반영한 최종 분석
        리포트 목록이에요.
      </p>
      <h2 class="page-subtilte">저장된 리포트 목록</h2>

      <ul class="contract-list">
        <li
          v-for="(item, idx) in reports"
          :key="idx"
          class="contract-item clickable"
          @click="onContractClick(item)"
        >
          <div class="icon-box">
            <img :src="fileIcon" alt="file icon" class="file-icon" />
          </div>
          <div class="contract-info">
            <p class="contract-title">{{ item.title }}</p>
            <p class="contract-sub" v-if="item.date">{{ item.date }}</p>
          </div>
        </li>
      </ul>
    </main>
  </div>
</template>

<style scoped>
.contract-page {
  font-family: 'Pretendard', sans-serif;
}

.content {
  max-width: 900px;
  margin: 40px auto;
  padding: 0 5vw;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.page-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 27px;
  margin-top: 8px;
}

.page-subtilte {
  font-size: 18px;
  font-weight: 700;
}

.contract-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.contract-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.contract-item.clickable {
  cursor: pointer;
  transition: background 0.2s;
  margin-left: -13px;
  margin-right: -13px;
  padding-left: 12px;
  padding-right: 12px;
  border-radius: 7px;
}
.contract-item.clickable:hover {
  background: #f1f1f1;
}

.icon-box {
  margin-right: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 45px;
  height: 45px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f8f9fa;
}

.file-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.contract-info {
  display: flex;
  flex-direction: column;
}

.contract-title {
  font-size: 16px;
  font-weight: 600;
  position: relative;
  top: 17px;
}

.contract-sub {
  font-size: 13px;
  color: #888;
  position: relative;
  top: 1px;
}
</style>
