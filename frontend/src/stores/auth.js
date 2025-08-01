import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwt') || null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userId: (state) => {
      if (!state.token) return null;
      const payload = JSON.parse(atob(state.token.split('.')[1]));
      return payload.sub;
    },
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
