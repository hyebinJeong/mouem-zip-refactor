<script setup>
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();

// 카카오 인가코드 받아오기
const code = route.query.code;

if (code) {
  // 백엔드에 인가코드를 보내서 JWT 요청
  axios
    .post('http://localhost:8080/api/oauth/kakao/login', {
      access_token: code,
    })
    .then((res) => {
      auth.login(res.data.jwt); // JWT 저장
      sessionStorage.setItem('kakaoAccessToken', res.data.kakaoAccessToken); // sessionStorage에 카카오 액세스 토큰 저장 -> 회원탈퇴 시 사용
      router.replace('/'); // 홈으로 리다이렉트
    })
    .catch((err) => {
      console.error(err);
      router.push('/login');
    });
}
</script>

<template>
  <p>로그인 처리 중입니다...</p>
</template>
