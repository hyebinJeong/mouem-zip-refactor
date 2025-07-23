import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/pages/HomePage.vue';
import SafetyDiagnosis from '@/pages/SafetyDiagnosis.vue';
import Checklist from '@/pages/Checklist.vue';
import ReferenceContract from '@/pages/ReferenceContract.vue';
import ReferenceGuidebook from '@/pages/ReferenceGuideBook.vue';
import GlossaryBook from '@/pages/GlossaryBook.vue';
import MyPage from '@/pages/MyPage.vue'; // 마이페이지 컴포넌트 추가 (필요하면 만드세요)

const routes = [
  { path: '/', name: 'home', component: HomePage },
  { path: '/safety-check', name: 'safety-check', component: SafetyDiagnosis },
  { path: '/checklist', name: 'checklist', component: Checklist },
  {
    path: '/reference-contract',
    name: 'reference-contract',
    component: ReferenceContract,
  },
  {
    path: '/reference-guidebook',
    name: 'reference-guidebook',
    component: ReferenceGuidebook,
  },
  { path: '/glossary', name: 'glossary', component: GlossaryBook },
  { path: '/my', name: 'my', component: MyPage },
  { path: '/guidebook', name: 'guidebook', component: GuidebookPage },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
