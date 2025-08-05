// src/stores/specialContractsStore.js
import { defineStore } from 'pinia';
import axios from 'axios';

export const useSpecialContractsStore = defineStore('specialContractsStore', {
  state: () => ({
    contracts: [],
  }),

  actions: {
    async fetchContracts() {
      try {
        const res = await axios.get('/api/specialcontracts-manager');
        this.contracts = res.data;
      } catch (error) {
        console.error('특약사항 목록 조회 오류:', error);
      }
    },

    async fetchContractById(id) {
      try {
        const res = await axios.get(`/api/specialcontracts-manager/${id}`);
        return res.data;
      } catch (error) {
        console.error('특약사항 단건 조회 오류:', error);
        return null;
      }
    },

    async addContract(payload) {
      try {
        await axios.post('/api/specialcontracts-manager', payload);
        await this.fetchContracts();
      } catch (error) {
        console.error('특약사항 추가 오류:', error);
      }
    },

    async updateContract(id, payload) {
      try {
        await axios.put(`/api/specialcontracts-manager/${id}`, payload);
        await this.fetchContracts();
      } catch (error) {
        console.error('특약사항 수정 오류:', error);
      }
    },

    async deleteContract(id) {
      try {
        await axios.delete(`/api/specialcontracts-manager/${id}`);
        await this.fetchContracts();
      } catch (error) {
        console.error('특약사항 삭제 오류:', error);
      }
    },
  },
});
