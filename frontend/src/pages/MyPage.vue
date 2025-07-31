<script setup>
import Header from '../components/Header.vue';
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';
import axios from 'axios';

const clientId = '88a530611ac6fa5a18f5747f67b6a359';
const redirectUri = 'http://localhost:8080/';
const auth = useAuthStore();
const user = ref({ name: '', email: '', kakaoId: '' });
// 임시 로그아웃 함수
function onKakaoLogout() {
  auth.logout();
  const kakaoAuthUrl2 = `https://kauth.kakao.com/oauth/logout?client_id=${clientId}&logout_redirect_uri=${redirectUri}`;
  window.location.href = kakaoAuthUrl2;
}
onMounted(async () => {
  if (auth.token) {
    const res = await axios.get('/api/user/me', {
      headers: {
        Authorization: `Bearer ${auth.token}`,
      },
    });
    user.value = res.data; // { name, email } 구조라고 가정
  }
});

async function onWithdraw() {
  const kakaoAccessToken = sessionStorage.getItem('kakaoAccessToken');
  if (!kakaoAccessToken) {
    alert('카카오 access token이 없습니다.');
    return;
  }

  if (confirm('정말로 회원 탈퇴하시겠습니까?')) {
    try {
      await axios.post(
        '/api/oauth/kakao/unlink',
        {
          kakaoAccessToken,
        },
        {
          headers: {
            Authorization: `Bearer ${auth.token}`,
          },
        }
      );

      alert('회원 탈퇴가 완료되었습니다.');
      auth.logout();
      sessionStorage.removeItem('kakaoAccessToken');
      window.location.href = '/';
    } catch (error) {
      alert('회원 탈퇴에 실패했습니다.');
      console.error(error);
    }
  }
}
</script>

<template>
  <div class="mypage-container">
    <h1 class="mypage-title">My Page</h1>

    <div class="user-info">
      <p class="user-name">{{ user.name }}</p>
      <p class="user-email">{{ user.email }}</p>
      <button @click="onKakaoLogout" class="btn">임시 로그아웃</button>
      <p class="user-note">
        모든 리포트는 생성일로부터 50일이 지나면 자동 만료됩니다.<br />
        필요한 경우 <span class="pdf-highlight">PDF로 저장해 보관</span>하시는
        것을 권장드립니다.
      </p>
    </div>

    <section class="report-section section-with-button">
      <h2>계약 리스크 분석 리포트 목록</h2>
      <button class="more-btn" @click="goToReportPage">더보기</button>
      <!-- table -->
    </section>

    <section class="contract-section section-with-button">
      <h2>참고용 계약서 목록</h2>
      <button class="more-btn" @click="goToContractPage">더보기</button>
      <!-- table -->
    </section>

    <button class="withdraw-btn" @click="onWithdraw">회원 탈퇴</button>
  </div>
</template>

<style scoped>
.mypage-container {
  padding: 40px 5vw;
  max-width: 1000px;
  margin: 0 auto;
  font-family: 'Pretendard', sans-serif;
}

.mypage-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 24px;
}

.user-info {
  margin-bottom: 32px;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
}

.user-email {
  font-size: 14px;
  color: #888;
  margin-top: 4px;
}

.user-note {
  font-size: 13px;
  color: #555;
  margin-top: 10px;
  line-height: 1.5;
}

.pdf-highlight {
  color: #1a73e8;
  font-weight: 500;
}

.section-with-button {
  position: relative;
  margin-bottom: 32px;
}

.section-with-button h2 {
  font-size: 16px;
  font-weight: 600;
}

.section-with-button .more-btn {
  position: absolute;
  top: 0;
  right: 0;
  font-size: 13px;
  padding: 4px 12px;
  background-color: #1a73e8;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

/* 테이블 */
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

thead {
  background-color: #f8f8f8;
}

th,
td {
  padding: 12px;
  border-bottom: 1px solid #ddd;
  text-align: left;
}

.withdraw-btn {
  margin-top: 40px;
  background: none;
  border: 1px solid #ccc;
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 6px;
  cursor: pointer;
}

/* 반응형 대응 */
@media (max-width: 768px) {
  .mypage-title {
    font-size: 22px;
  }

  .user-name {
    font-size: 18px;
  }

  .user-note {
    font-size: 12px;
  }

  table {
    font-size: 12px;
  }

  .more-btn {
    font-size: 12px;
    padding: 4px 8px;
  }
}
</style>
