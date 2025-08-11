<template>
  <div class="container">
    <div class="top-bar">
      <div class="logo-wrapper" @click="goHome">
        <img class="logo" :src="logo" alt="HomeBuddy Logo" />
      </div>
      <div class="menu-container" :style="{ color: fontColor }">
        <div class="menu-item" @click="goSafetyCheck">매물 안전성 진단</div>
        <div class="menu-item" @click="goChecklist">체크리스트</div>

        <div
            class="menu-item dropdown"
            @mouseenter="showContractGuide = true"
            @mouseleave="showContractGuide = false"
        >
          계약 가이드
          <ul class="dropdown-list" v-show="showContractGuide">
            <li @click.stop="goReferenceContract">참고계약서 작성</li>
            <li @click.stop="goReferenceGuidebook">참고 가이드북</li>
          </ul>
        </div>

        <div class="menu-item" @click="goGlossary">용어해설집</div>

        <button
            v-if="userId"
            class="mypage"
            :style="{ color: fontColor }"
            @click="goMyPage"
        >
          마이페이지
        </button>
        <button
            v-else
            class="mypage"
            :style="{ color: fontColor }"
            @click="goLogin"
        >
          로그인
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import logo from '@/assets/homebuddylogo.png';

const props = defineProps({
  fontColor: String,
});

const router = useRouter();
const showContractGuide = ref(false);

const auth = useAuthStore();
const userId = computed(() => auth.userId);

const goHome = () => router.push('/');
const goSafetyCheck = () => router.push('/agreement');
const goChecklist = () => router.push('/checklist');
const goReferenceContract = () => router.push('/referencecontracts');
const goReferenceGuidebook = () => router.push('/reference-guidebook');
const goGlossary = () => router.push('/glossary');
const goMyPage = () => router.push('/my');
const goLogin = () => router.push('/login');
</script>

<style scoped>
.container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  border-bottom: 1.5px solid #eaeaea;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 95%;
  height: 130px;
  box-sizing: border-box;
}

.logo-wrapper {
  width: 250px;
  display: flex;
  height: 120px;
  justify-content: flex-start;
  align-items: center;
  cursor: pointer;
}

.logo-wrapper:hover {
  transform: scale(1.03);
  transition: transform 0.3s ease;
}

.logo {
  width: 200px;
  height: auto;
}

.menu-container {
  width: 75%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-left: 50px;
  color: inherit;
}

.menu-item {
  position: relative;
  font-weight: bolder;
  font-size: 20px;
  font-family: Regular;
  height: 2.5em;
  display: flex;
  align-items: center;
  cursor: pointer;
  z-index: 2;
  user-select: none;
  margin-right: 80px;
}

.dropdown-list {
  background-color: #ececec;
  font-weight: bolder;
  font-size: 18px;
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  width: 170px;
  margin: 0;
  padding: 0;
  list-style: none;
  z-index: 10;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 5px;
  overflow: hidden;
}

.dropdown-list li {
  height: 3em;
  display: flex;
  align-items: center;
  justify-content: center;
  color: black;
  background-color: transparent;
  cursor: pointer;
}

.dropdown-list li:hover {
  background-color: white;
}

.mypage {
  background-color: transparent;
  border: none;
  font-weight: bolder;
  font-size: 20px;
  cursor: pointer;
  z-index: 3;
  margin-left: auto;
}

.mypage:hover {
  color: #007bff;
  transition: all 0.1s ease-in-out;
}

.menu-item:hover {
  color: #007bff;
  border-bottom: 2px solid #007bff;
  transition: all 0.1s ease-in-out;
}
</style>
