<template>
  <div class="login-container">
    <img src="@/assets/homebuddylogo.png" class="logo" />
    <p>간단한 <span class="highlight">내 집 진단</span> 시작해보세요!</p>
    <button @click="onKakaoLogin" class="kakao-btn">
      <img src="@/assets/kakao_login.png" alt="kakao" />
    </button>
    <a @click="goToMain" class="skip">안 받을래요</a>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();
const clientId = '88a530611ac6fa5a18f5747f67b6a359';
const redirectUri = 'http://localhost:8080/oauth/callback/kakao';

function onKakaoLogin() {
  const kakaoAuthUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=code`;
  window.location.href = kakaoAuthUrl;
}

function goToMain() {
  router.push('/'); // 메인 페이지로 이동
}
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  font-family: 'Pretendard', sans-serif;
  padding: 20px;
  max-width: 100%;
}
.logo {
  width: 646px;
  max-width: 90vw;
  height: auto;
  margin-bottom: 20px;
}
.login-container p {
  font-size: 26px;
  max-width: 1042px;
  text-align: center;
  margin-bottom: 30px;
}
.highlight {
  color: #007bff;
  font-weight: bold;
}
.kakao-btn {
  background-color: #fee500;
  border: none;
  padding: 0;
  width: 453px;
  height: 86px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.kakao-btn img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.skip {
  margin-top: 10px;
  color: gray;
  font-size: 12px;
}

/* 반응형 처리 */
@media (max-width: 768px) {
  .logo {
    width: 60vw;
  }
  .login-container p {
    font-size: 18px;
    max-width: 90vw;
  }
  .kakao-btn {
    width: 80vw;
    height: 60px;
  }
}
</style>
