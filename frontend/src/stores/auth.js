import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwt') || null,
    user: null, // 여기에 role, nickname 등 사용자 정보를 저장 가능.
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userRole: (state) => {
      if (!state.token) return null;
      const payload = JSON.parse(atob(state.token.split('.')[1]));
      return payload.role;
    },
    userNickname: (state) => {
      if (!state.token) return null;
      const payload = JSON.parse(atob(state.token.split('.')[1]));
      return payload.nickname;
    },
  },

  actions: {
    login(token) {
      this.token = token;
      localStorage.setItem('jwt', token);
    },
    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem('jwt');
    },
  },
});
