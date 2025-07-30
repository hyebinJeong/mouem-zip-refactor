import { defineStore } from 'pinia';
import axios from 'axios';

export const useCategoryStore = defineStore('category', {
  state: () => ({
    categories: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetchCategories() {
      this.loading = true;
      this.error = null;
      try {
        const res = await axios.get('/api/categories'); // 실제 API 경로 맞게 수정하세요
        this.categories = res.data;
      } catch (err) {
        this.error = err;
      } finally {
        this.loading = false;
      }
    },
    async addCategory(newCategory) {
      try {
        const res = await axios.post('/api/categories', newCategory);
        this.categories.push(res.data);
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
    async updateCategory(id, updatedCategory) {
      try {
        await axios.put(`/api/categories/${id}`, updatedCategory);
        const index = this.categories.findIndex((c) => c.id === id);
        if (index !== -1) {
          this.categories[index] = { id, ...updatedCategory };
        }
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
    async deleteCategory(id) {
      try {
        await axios.delete(`/api/categories/${id}`);
        this.categories = this.categories.filter((c) => c.id !== id);
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
    getCategoryById(id) {
      return this.categories.find((c) => c.id === id);
    },
  },
});
