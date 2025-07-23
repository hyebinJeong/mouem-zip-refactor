import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/pages/HomePage.vue';
import SafetyDiagnosis from '@/pages/SafetyDiagnosis.vue';
import Preview from '@/pages/checklist/Preview.vue';
import ReferenceContract from '@/pages/ReferenceContract.vue';
import ReferenceGuidebook from '@/pages/ReferenceGuideBook.vue';
import GlossaryBook from '@/pages/GlossaryBook.vue';
import MyPage from '@/pages/MyPage.vue';
import NonDiagnosis from '@/pages/checklist/ForNoneDiagnosis.vue'
import CheckList from '@/pages/checklist/Checklist.vue'

const routes = [
  { path: '/', name: 'home', component: HomePage },
  { path: '/safety-check', name: 'safety-check', component: SafetyDiagnosis },
  { path: '/checklist', name: 'preview', component: Preview },
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
  { path: '/checklist/nondiagnosis', name: 'nondiagnosis', component:NonDiagnosis},
  { path: '/checklist/checeklist', name: 'checklist', component:CheckList},
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
