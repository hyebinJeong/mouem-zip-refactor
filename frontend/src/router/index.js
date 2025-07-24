// router/index.js
import { createRouter, createWebHistory } from 'vue-router';

import HomePage from '@/pages/HomePage.vue';
import SafetyDiagnosis from '@/pages/SafetyDiagnosis.vue';
import Preview from '@/pages/checklist/Preview.vue';
import ReferenceContract from '@/pages/ReferenceContract.vue';
import ReferenceGuidebook from '@/pages/ReferenceGuideBook.vue';
import GlossaryBook from '@/pages/GlossaryBook.vue';
import MyPage from '@/pages/MyPage.vue';
import NonDiagnosis from '@/pages/checklist/ForNoneDiagnosis.vue';
import CheckList from '@/pages/checklist/Checklist.vue';

import CategoryAll from '@/pages/category/CategoryAll.vue';
import CategoryManager from '@/pages/category/CategoryManager.vue';
import CategoryAdd from '@/pages/category/CategoryAdd.vue';
import CategoryEdit from '@/pages/category/CategoryEdit.vue';

import TermManager from '@/pages/term/TermManager.vue';
import TermAdd from '@/pages/term/TermAdd.vue';
import TermEdit from '@/pages/term/TermEdit.vue';

const routes = [
  { path: '/', name: 'home', component: HomePage },
  { path: '/safety-check', name: 'safety-check', component: SafetyDiagnosis },
  { path: '/checklist', name: 'preview', component: Preview },
  {
    path: '/checklist/nondiagnosis',
    name: 'nondiagnosis',
    component: NonDiagnosis,
  },
  { path: '/checklist/checklist', name: 'checklist', component: CheckList }, // 오타 수정

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

  // 카테고리 및 용어 관리 - 중첩 라우트
  {
    path: '/category',
    component: CategoryAll,
    children: [
      {
        path: '',
        name: 'CategoryManager',
        component: CategoryManager,
      },
      {
        path: 'add',
        name: 'CategoryAdd',
        component: CategoryAdd,
      },
      {
        path: 'edit/:id',
        name: 'CategoryEdit',
        component: CategoryEdit,
        props: true,
      },
      {
        path: 'term',
        name: 'TermManager',
        component: TermManager,
      },
      {
        path: 'term/add',
        name: 'TermAdd',
        component: TermAdd,
      },
      {
        path: 'term/edit/:id',
        name: 'TermEdit',
        component: TermEdit,
        props: true,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
