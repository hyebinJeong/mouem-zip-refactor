<template>
  <div class="wrapper">
    <div class="container">
      <h1 class="main-title">
        계약 전에서 놓치는 부분이 없도록
        <span class="blue-text">체크리스트</span>를 통해 도와드릴게요.
      </h1>

      <div class="content-box">
        <div class="slider-wrapper">
          <button class="arrow left" @click="prevSlide">‹</button>
          <div class="slider">
            <img
                :src="images[currentIndex]"
                class="slider-img"
                @click="openModal"
            />
          </div>
          <button class="arrow right" @click="nextSlide">›</button>
        </div>

        <div class="checklist-box">
          <ul class="checklist">
            <li
                v-for="(item, index) in checklistItems"
                :key="index"
            >
              <input type="checkbox" v-model="checked[index]" />
              <span v-html="`${index + 1}. ${item}`"></span>
            </li>
          </ul>
        </div>
      </div>

      <div class="bottom-right">
        <button class="next-button" @click="goNext">확인 완료</button>
      </div>
    </div>

    <Buddy @open-dictionary="openDictionaryModal" />

    <div v-if="showModal" class="image-modal" @click.self="closeModal">
      <img :src="images[currentIndex]" class="modal-img" />
    </div>
    <TermViewModal v-if="showDictionaryModal" @close="closeDictionaryModal" />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import Buddy from '@/components/BuddyHelper.vue'
import checklistItems from '@/stores/checklistStore.js'
import TermViewModal from "@/components/TermViewModal.vue";

// 용어 모달 표시 상태
const showDictionaryModal = ref(false)

// 용어모달 열기/닫기 함수
const openDictionaryModal = () => {
  showDictionaryModal.value = true
}
const closeDictionaryModal = () => {
  showDictionaryModal.value = false
}

const router = useRouter()

const images = [
  new URL('@/assets/표준계약서1.png', import.meta.url).href,
  new URL('@/assets/표준계약서2.png', import.meta.url).href,
  new URL('@/assets/표준계약서3.png', import.meta.url).href
]

const currentIndex = ref(0)

const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % images.length
}
const prevSlide = () => {
  currentIndex.value = (currentIndex.value - 1 + images.length) % images.length
}

const checked = ref(Array(checklistItems.length).fill(false))

const goNext = () => {
  router.push('/next-page')
}

// 이미지 모달
const showModal = ref(false)
const openModal = () => { showModal.value = true }
const closeModal = () => { showModal.value = false }

const onEscPress = (e) => {
  if (e.key === 'Escape') closeModal()
}

onMounted(() => window.addEventListener('keydown', onEscPress))
onBeforeUnmount(() => window.removeEventListener('keydown', onEscPress))
</script>

<style scoped>
.wrapper {
  width: 100vw;
  display: flex;
  justify-content: center;
  padding: 5px 20px;
  position: relative;
}

.container {
  width: 100%;
  max-width: 1100px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.main-title {
  font-size: 26px;
  font-weight: bold;
  color: #222;
  text-align: center;
  margin-bottom: 24px;
}

.blue-text {
  color: #1A80E5;
  margin-left: 10px;
}

.content-box {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 40px;
  justify-content: center;
  width: 100%;
  margin-bottom: 10px;
  align-items: center;
}

.slider-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  height: 550px;
}

.slider {
  width: 420px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.slider-img {
  max-height: 100%;
  width: auto;
  max-width: 100%;
  cursor: zoom-in;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.arrow {
  font-size: 50px;
  background: white;
  border: none;
  padding: 6px 12px;
  cursor: pointer;
  border-radius: 50%;
}

.checklist-box {
  background-color: #f7fafd;
  padding: 24px;
  border-radius: 16px;
  flex: 1 1 400px;
  max-width: 550px;
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.checklist {
  padding: 0;
  margin: 0;
  line-height: 2;
  width: 100%;
}

.checklist li {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
  font-size: 15px;
  gap: 12px;
  line-height: 2;
}

.checklist input[type="checkbox"] {
  width: 20px;
  height: 20px;
  margin-top: 7px;
  accent-color: #1A80E5;
}

.bottom-right {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.next-button {
  background-color: #1A80E5;
  color: white;
  font-weight: 600;
  font-size: 14px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.next-button:hover {
  background-color: #2563eb;
}

.buddy-fixed {
  position: fixed;
  top:220px;
  right: 100px;
  z-index: 50;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.buddy-img-out {
  width: 70px;
  cursor: pointer;
}

.balloon-out {
  position: absolute;
  bottom: 80px;
  right: 0;
  background-color: #fff;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
}

.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-img {
  max-width: 90%;
  max-height: 90%;
  border-radius: 10px;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.5);
  cursor: zoom-out;
}
</style>
