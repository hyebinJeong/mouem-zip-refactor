<script setup>
import Header from '../components/Header.vue';
import { useAuthStore } from '@/stores/auth';

const clientId = '88a530611ac6fa5a18f5747f67b6a359';
const redirectUri = 'http://localhost:8080/';

// 임시 로그아웃 함수
const auth = useAuthStore();
function onKakaoLogout() {
  auth.logout();
  const kakaoAuthUrl2 = `https://kauth.kakao.com/oauth/logout?client_id=${clientId}&logout_redirect_uri=${redirectUri}`;
  window.location.href = kakaoAuthUrl2;
}
</script>

<template>
  <h1>마이페이지</h1>
  <button @click="onKakaoLogout" class="btn">임시 로그아웃</button>
</template>
