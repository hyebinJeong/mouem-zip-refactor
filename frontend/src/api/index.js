import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import router from '@/router';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

//요청 인터셉터: /api/** 중에서도 공개 엔드포인트는 제외
const PUBLIC_ENDPOINTS = [
  /^\/api\/oauth\//, // 소셜 로그인 교환 엔드포인트
];

api.interceptors.request.use((config) => {
  const rawUrl = (config.url || '').toString();
  const path = rawUrl.startsWith('http') ? new URL(rawUrl).pathname : rawUrl;

  const isApi = path.startsWith('/api');
  const isPublic = PUBLIC_ENDPOINTS.some((re) => re.test(path));

  if (isApi && !isPublic) {
    const auth = useAuthStore();
    if (auth.token) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${auth.token}`;
    }
  }
  return config;
});
<<<<<<< Updated upstream

// 응답 인터셉터 -> 401/403 공통 처리
=======
const REFRESH_URL = '/api/oauth/kakao/refresh';
>>>>>>> Stashed changes
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const status = error?.response?.status;
<<<<<<< Updated upstream
    if (status === 401) {
      // 토큰 만료 또는 미인증시 로그아웃 처리 -> 추후 리프레시 토큰 발급 시 삭제
      const auth = useAuthStore();
      auth.logout();
      if (router.currentRoute.value.path !== '/login') {
        alert('다시 로그인이 필요합니다.');
        router.push('/login');
=======
    const auth = useAuthStore();
    const originalRequest = error.config;

    if (originalRequest?.url?.includes(REFRESH_URL)) {
      return Promise.reject(error);
    }

    if (status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // refresh 요청 (쿠키에 refresh_token 있으니 body/헤더 필요 없음)
        const res = await api.post('/api/oauth/kakao/refresh');
        const newAccess = res.data.accessToken;

        // 새로운 Access 저장
        auth.login(newAccess);

        // 원래 요청 재시도
        originalRequest.headers.Authorization = `Bearer ${newAccess}`;
        return api(originalRequest);
      } catch (refreshError) {
        // refresh도 실패하면 진짜 로그아웃
        auth.logout();
        if (router.currentRoute.value.path !== '/login') {
          alert('다시 로그인이 필요합니다.');
          router.push('/login');
        }
        return Promise.reject(refreshError);
>>>>>>> Stashed changes
      }
    } else if (status === 403) {
      // 권한 부족
      alert('접근 권한이 없습니다.');
      router.push('/');
    }
    return Promise.reject(error);
  }
);

export default api;
