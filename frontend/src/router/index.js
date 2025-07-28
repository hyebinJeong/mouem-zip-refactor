// router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/pages/HomePage.vue';
import SafetyDiagnosis from '@/pages/SafetyDiagnosis.vue';
import SafetyDiagnosis2 from '@/pages/SafetyDiagnosis2.vue'; 
import Preview from '@/pages/checklist/Preview.vue';
import ReferenceContract from '@/pages/referencecontracts/ReferenceContract.vue';
import GlossaryBook from '@/pages/GlossaryBook.vue';
import GuidebookPage from '@/pages/GuidebookPage.vue';
import MyPage from '@/pages/MyPage.vue';
import NonDiagnosis from '@/pages/checklist/ForNoneDiagnosis.vue';
import CheckList from '@/pages/checklist/Checklist.vue';
import FinalReportPage from '@/pages/FinalReportPage.vue';
import AgreementPage from "@/pages/AgreementPage.vue";

// 카테고리 및 용어 관련 추가
import CategoryAll from '@/pages/category/CategoryAll.vue';
import CategoryManager from '@/pages/category/CategoryManager.vue';
import CategoryAdd from '@/pages/category/CategoryAdd.vue';
import CategoryEdit from '@/pages/category/CategoryEdit.vue';

import TermManager from '@/pages/term/TermManager.vue';
import TermAdd from '@/pages/term/TermAdd.vue';
import TermEdit from '@/pages/term/TermEdit.vue';
// 특약사항 관련 추가
import SpecialContractsManager from '@/pages/special-contracts/SpecialContractsManager.vue';
import SpecialContractsAdd from '@/pages/special-contracts/SpecialContractsAdd.vue';
import SpecialContractsEdit from '@/pages/special-contracts/SpecialContractsEdit.vue';

const routes = [
  { path: '/', name: 'home', component: HomePage },
  { path: '/safety-check', name: 'safety-check', component: SafetyDiagnosis },
  { path: '/safetyDiagnosis2', name: 'safetyDiagnosis2', component: SafetyDiagnosis2 },
  { path: '/checklist', name: 'preview', component: Preview },
  {
    path: '/agreement', // 면책고지 경로
    name: 'AgreementPage',
    component: AgreementPage,
    meta: { hideHeader: true }
  },
  {
    path: '/reference-contract',
    name: 'reference-contract',
    component: ReferenceContract,
  },
  {
    path: '/reference-guidebook',
    name: 'guidebookPage',
    component: GuidebookPage,
  },
  { path: '/glossary', name: 'glossary', component: GlossaryBook },
  { path: '/my', name: 'my', component: MyPage },
  {
    path: '/checklist/nondiagnosis',
    name: 'nondiagnosis',
    component: NonDiagnosis,
  },
  { path: '/checklist/checklist', name: 'checklist', component: CheckList },

  // 관리자 페이지
  {
    path: '/category',
    component: CategoryAll,
    children: [
      // 카테고리 관리
      { path: '', name: 'CategoryManager', component: CategoryManager },
      { path: 'add', name: 'CategoryAdd', component: CategoryAdd },
      {
        path: 'edit/:id',
        name: 'CategoryEdit',
        component: CategoryEdit,
        props: true,
      },
      // 용어 관리
      { path: 'term', name: 'TermManager', component: TermManager },
      { path: 'term/add', name: 'TermAdd', component: TermAdd },
      {
        path: 'term/edit/:id',
        name: 'TermEdit',
        component: TermEdit,
        props: true,
      },
      // 특약사항 관리
      {
        path: 'special',
        name: 'SpecialContractsManager',
        component: SpecialContractsManager,
      },
      {
        path: 'special/add',
        name: 'SpecialContractsAdd',
        component: SpecialContractsAdd,
      },
      {
        path: 'special/edit/:id',
        name: 'SpecialContractsEdit',
        component: SpecialContractsEdit,
        props: true,
      },
    ],
  },
  {
    path: '/referencecontracts',
    children: [
      {
        path: '',
        component: () =>
          import('@/pages/referencecontracts/ReferenceContract.vue'),
      },
      {
        path: '/referencecontracts/success',
        name: 'ReferenceContractSuccess',
        component: () =>
          import('@/pages/referencecontracts/ReferenceContractSuccess.vue'),
      },
    ],
  },
  {
    path: '/recommendation',
    name: 'SpecialContractsRecommendation',
    component: () =>
      import('@/pages/special-contracts/SpecialContractsRecommendation.vue'),
  },
  {
    path: '/final-report',
    name: 'finalReportPage',
    component: FinalReportPage,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;