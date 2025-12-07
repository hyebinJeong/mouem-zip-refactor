import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import router from '@/router';

const api = axios.create({
  baseURL: '', // 배포, 로컬 환경에서 잘 돌아가도록 수정
  withCredentials: true, // 쿠키 포함 필수
});

const PUBLIC_ENDPOINTS = [/^\/api\/oauth\//];
const REFRESH_URL = '/api/oauth/kakao/refresh';

// api url 판별
function isApiUrl(url = '') {
  const u = url.toString();
  return u.startsWith('/api') || u.startsWith('api') || u.includes('/api/');
}

api.interceptors.request.use((config) => {
  const url = (config.url || '').toString();
  const publicHit = PUBLIC_ENDPOINTS.some((re) => re.test(url));

  if (isApiUrl(url) && !publicHit) {
    const auth = useAuthStore();
    if (auth?.token) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${auth.token}`;
      console.debug('[api] attach token to', url);
    } else {
      console.debug('[api] no token for', url);
    }
  }

  return config;
});

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const status = error?.response?.status;
    const auth = useAuthStore();
    const originalRequest = error.config;

    // refresh 호출 자체에서 에러난 경우 → 바로 reject
    if (originalRequest?.url?.includes(REFRESH_URL)) {
      return Promise.reject(error);
    }

    // 401 → refresh 시도
    if (status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const res = await api.post(REFRESH_URL);
        const newAccess = res.data.accessToken;

        // 새로운 Access 저장
        auth.login(newAccess);

        // 원래 요청 재시도
        originalRequest.headers.Authorization = `Bearer ${newAccess}`;
        return api(originalRequest);
      } catch (refreshError) {
        // refresh 실패 → 로그아웃 처리
        auth.logout();
        if (router.currentRoute.value.path !== '/login') {
          alert('다시 로그인이 필요합니다.');
          router.push('/login');
        }
        return Promise.reject(refreshError);
      }
    } else if (status === 403) {
      alert('접근 권한이 없습니다.');
      router.push('/');
    }

    return Promise.reject(error);
  }
);

export default api;
