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
        const res = await axios.get('/api/category-manager'); // 변경된 API 경로
        this.categories = res.data;
      } catch (err) {
        this.error = err;
      } finally {
        this.loading = false;
      }
    },
    async addCategory(newCategory) {
      try {
        const res = await axios.post('/api/category-manager', newCategory);
        this.categories.push(res.data);
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
    async updateCategory(id, updatedCategory) {
      try {
        await axios.put(`/api/category-manager/${id}`, updatedCategory);
        const index = this.categories.findIndex((c) => c.categoryId === id);
        if (index !== -1) {
          this.categories[index] = { categoryId: id, ...updatedCategory };
        }
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
    async deleteCategory(id) {
      try {
        await axios.delete(`/api/category-manager/${id}`);
        this.categories = this.categories.filter((c) => c.categoryId !== id);
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
    getCategoryById(id) {
      return this.categories.find((c) => c.categoryId === id);
    },
  },
});
