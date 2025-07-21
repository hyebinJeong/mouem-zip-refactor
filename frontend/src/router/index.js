import GuidebookPage from '@/pages/GuidebookPage.vue';
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // { path: '/', name: 'home', component: HomePage },
    { path: '/guidebook', name: 'guidebook', component: GuidebookPage },
  ],
});
export default router;
