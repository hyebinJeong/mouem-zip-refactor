<script setup>
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import api from '@/api/index.js';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();

// 카카오 인가코드 받아오기
const code = route.query.code;

if (code) {
  // 백엔드에 인가코드를 보내서 JWT 요청
  api
    .post('/api/oauth/kakao/login', { access_token: code })
    .then((res) => {
      // Access 토큰 저장
      auth.login(res.data.accessToken);
      const access = res.data.accessToken;
      const payload = JSON.parse(atob(access.split('.')[1]));
      if (payload.role === 'ROLE_ADMIN') {
        router.replace('/category'); // 관리자면 바로 /category 이동
      } else {
        router.replace('/');
      }
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
