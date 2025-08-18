import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import router from '@/router';

const api = axios.create({
  baseURL: '/',
});

const PUBLIC_ENDPOINTS = [/^\/api\/oauth\//];

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
      // 디버깅용(필요시 켜기)
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
    if (status === 401) {
      const auth = useAuthStore();
      auth.logout();
      if (router.currentRoute.value.path !== '/login') {
        alert('다시 로그인이 필요합니다.');
        router.push('/login');
      }
    } else if (status === 403) {
      alert('접근 권한이 없습니다.');
      router.push('/');
    }
    return Promise.reject(error);
  }
);

export default api;
