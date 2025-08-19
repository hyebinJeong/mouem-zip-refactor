// src/stores/termStore.js
import { defineStore } from 'pinia';
import api from '@/api/index.js';
import { useAuthStore } from '@/stores/auth';

export const useTermStore = defineStore('term', {
  state: () => ({
    terms: [],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchTerms() {
      this.loading = true;
      this.error = null;
      try {
        const res = await api.get('/api/term-manager');
        this.terms = res.data;
      } catch (err) {
        this.error = err;
      } finally {
        this.loading = false;
      }
    },

    async addTerm(newTerm) {
      try {
        const auth = useAuthStore();
        await api.post('/api/term-manager', newTerm);
        await this.fetchTerms(); // 최신 목록 반영
      } catch (err) {
        this.error = err;
        throw err;
      }
    },

    async updateTerm(id, updatedTerm) {
      try {
        const auth = useAuthStore();
        await api.put(`/api/term-manager/${id}`, updatedTerm);
        await this.fetchTerms(); // 최신 목록 반영
      } catch (err) {
        this.error = err;
        throw err;
      }
    },

    async deleteTerm(id) {
      try {
        const auth = useAuthStore();
        await api.delete(`/api/term-manager/${id}`);
        this.terms = this.terms.filter((t) => t.termId !== id);
      } catch (err) {
        this.error = err;
        throw err;
      }
    },

    getTermById(id) {
      return this.terms.find((t) => t.termId === id);
    },

    async fetchTermById(id) {
      try {
        const res = await api.get(`/api/term-manager/${id}`);
        return res.data;
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
  },
});
