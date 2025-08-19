import { defineStore } from 'pinia';
import api from '@/api';

const REFRESH_URL = '/api/oauth/kakao/refresh';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null,
    loading: false,
    _refreshPromise: null, // 진행 중인 리프레시 공유용
  }),

  getters: {
    isLoggedIn: (s) => !!s.token,
    userId: (s) => {
      try {
        if (!s.token) return null;
        return JSON.parse(atob(s.token.split('.')[1])).sub;
      } catch {
        return null;
      }
    },
    userRole: (s) => {
      try {
        if (!s.token) return null;
        return JSON.parse(atob(s.token.split('.')[1])).role;
      } catch {
        return null;
      }
    },
    userNickname: (s) => {
      try {
        if (!s.token) return null;
        return JSON.parse(atob(s.token.split('.')[1])).nickname;
      } catch {
        return null;
      }
    },
  },

  actions: {
    login(token) {
      this.token = token;
    },
    logout() {
      this.token = null;
    },

    // 새로고침 시 Access 복구: 항상 '대기'해서 레이스 방지
    async tryRefresh() {
      // 이미 액세스 토큰 있으면 스킵
      if (this.token) return;

      // 이미 리프레시 진행 중이면 그 프라미스를 그대로 기다림
      if (this._refreshPromise) return this._refreshPromise;

      // 신규 리프레시 시작 (공유 프라미스 생성)
      this.loading = true;
      this._refreshPromise = (async () => {
        try {
          const res = await api.post(REFRESH_URL);
          const newAccess = res.data.accessToken;
          this.login(newAccess);
          console.debug('[authStore] refresh success');
        } catch (err) {
          console.warn('[authStore] refresh failed');
          this.logout();
        } finally {
          this.loading = false;
          this._refreshPromise = null; // 반드시 해제
        }
      })();

      return this._refreshPromise;
    },
  },
});
