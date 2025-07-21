<template>
  <div class="container">
    <div class="top-bar">
      <div class="logo-wrapper" @click="goHome">
        <div class="logo"></div>
      </div>
      <div class="menu-container" :style="{ color: fontColor }">
        <div class="recomendation">
          여행지 예약
          <ul class="recomendation-list">
            <li @click="goReservation">글로플러 찾기</li>
            <li @click="goChat">채팅 목록</li>
          </ul>
        </div>
        <div class="recomendation">
          여행지 추천
          <ul class="recomendation-list">
            <li @click.stop="goMbti">MBTI 기반 추천</li>
            <li @click.stop="goPersonal">키워드 추천</li>
            <li @click.stop="goSimilarity">사용자 기반 추천</li>
          </ul>
        </div>
        <button class="community" :style="{ color: fontColor }" @click="goRoute">루트 탐색</button>
        <button class="community" :style="{ color: fontColor }" @click="goReview">커뮤니티</button>
        <button class="community" :style="{ color: fontColor }" @click="goChatBot">챗봇</button>
        <button class="mypage" :style="{ color: fontColor }" @click="goMyPage">마이페이지</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { defineProps } from 'vue'

const props = defineProps({
  fontColor: String
})

const router = useRouter()

const goHome = () => router.push('/')
const goReservation = () => router.push('/reservation')
const goReview = () => router.push('/review')
const goRoute = () => router.push('/route')
const goMbti = () => router.push('/recomendation/mbti')
const goPersonal = () => router.push('/recomendation/personal')
const goSimilarity = () => router.push('/recomendation/similarity')
const goMyPage = () => router.push('/my')
const goChatBot = () => router.push('/chatbot')
const goChat = () => router.push('/chat')
</script>

<style scoped>
.container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  background-color: purple;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 1500px;
  height: 60px;
}

.logo-wrapper {
  width: 25%;
  display: flex;
  justify-content: center;
  cursor: pointer;
}

.logo {
  background-image: url("/GlopleLogo.png");
  background-size: cover;
  background-position: center;
  width: 300px;
  height: 80px;
  margin-bottom: 1.5em;
}

.menu-container {
  width: 56%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-left: 14%;
}

.recomendation {
  position: relative;
  font-weight: bolder;
  font-size: 14px;
  height: 2.5em;
  font-family: Regular;
  display: flex;
  align-items: center;
  cursor: pointer;
  z-index: 2;
}

.recomendation-list {
  background-color: #d9d9d9;
  font-weight: bolder;
  font-size: 14px;
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  width: 140px;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.8s;
  z-index: 10;
}

.recomendation:hover .recomendation-list {
  opacity: 1;
  pointer-events: auto;
}

.recomendation-list li {
  text-align: center;
  height: 3em;
  display: flex;
  align-items: center;
  justify-content: center;
  color: black;
  background-color: transparent;
}

.recomendation-list li:hover {
  background-color: white;
}

.community,
.mypage {
  background-color: transparent;
  border: none;
  font-weight: bolder;
  font-size: 14px;
  cursor: pointer;
  z-index: 3;
}
</style>
