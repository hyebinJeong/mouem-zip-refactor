import { defineStore } from 'pinia';
import axios from 'axios';

export const useSpecialContractsStore = defineStore('specialContracts', {
  state: () => ({
    contracts: [],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchContracts() {
      this.loading = true;
      try {
        const res = await axios.get('/specialcontract-manager');
        this.contracts = res.data.map(item => ({
          special_clause_id: item.special_clause_id,
          category: item.category,
          importance: item.importance,
          importance_color: item.importance_color,
          description: item.description,
        }));
        this.error = null;
      } catch (err) {
        console.error('특약 목록 조회 실패:', err);
        this.error = err;
      } finally {
        this.loading = false;
      }
    },

    async fetchContractById(id) {
      try {
        const res = await axios.get(`/specialcontract-manager/${id}`);
        return {
          special_clause_id: res.data.special_clause_id,
          category: res.data.category,
          importance: res.data.importance,
          importance_color: res.data.importance_color,
          description: res.data.description,
        };
      } catch (err) {
        console.error('특약 단일 조회 실패:', err);
        throw err;
      }
    },

    async addContract(contract) {
      try {
        const res = await axios.post('/specialcontract-manager', contract);
        this.contracts.push(res.data);
      } catch (err) {
        console.error('특약 추가 실패:', err);
        throw err;
      }
    },

    async updateContract(contract) {
      try {
        await axios.put(`/specialcontract-manager/${contract.special_clause_id}`, contract);
        const idx = this.contracts.findIndex(c => c.special_clause_id === contract.special_clause_id);
        if (idx !== -1) this.contracts[idx] = contract;
      } catch (err) {
        console.error('특약 수정 실패:', err);
        throw err;
      }
    },

    async deleteContractById(id) {
      try {
        await axios.delete(`/specialcontract-manager/${id}`);
        this.contracts = this.contracts.filter(c => c.special_clause_id !== id);
      } catch (err) {
        console.error('특약 삭제 실패:', err);
        throw err;
      }
    },
  },
});
