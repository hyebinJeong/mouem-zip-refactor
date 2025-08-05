<template>
  <div class="checklist-page">
    <div class="wrapper">
      <div class="container">
        <h1 class="main-title">
          계약 전에서 놓치는 부분이 없도록
          <span class="blue-text">체크리스트</span>를 통해 도와드릴게요.
        </h1>

        <div class="content-box">
          <div class="slider-wrapper">
            <button class="arrow left" @click="prevSlide">
              <svg viewBox="0 0 24 24" width="50" height="50" fill="#888">
                <polygon points="15,5 7,12 15,19" />
              </svg>
            </button>
            <div class="slider">
              <img
                  :src="images[currentIndex]"
                  class="slider-img"
                  @click="openModal"
              />
            </div>
            <button class="arrow right" @click="nextSlide">
              <svg viewBox="0 0 24 24" width="50" height="50" fill="#888">
                <polygon points="9,5 17,12 9,19" />
              </svg>
            </button>
          </div>


          <div class="checklist-box">
            <ul class="checklist">
              <li v-for="(item, index) in checklistItems" :key="index">
                <label :for="`todo-${index}`" class="item">
                  <input
                      type="checkbox"
                      :id="`todo-${index}`"
                      class="hidden"
                      v-model="checked[index]"
                  />
                  <label :for="`todo-${index}`" class="cbx">
                    <svg width="14px" height="12px" viewBox="0 0 14 12">
                      <polyline points="1 7.6 5 11 13 1"></polyline>
                    </svg>
                  </label>
                  <label :for="`todo-${index}`" class="cbx-lbl">
                    {{ item.text }}
                    <span v-if="!checked[index]" v-html="item.span"></span>
                  </label>
                </label>
              </li>
            </ul>
          </div>

          <button class="next-button-fixed" @click="goNext">확인 완료</button>
        </div>
      </div>

      <Buddy @open-dictionary="openDictionaryModal" />

      <div v-if="showModal" class="image-modal" @click.self="closeModal">
        <img :src="images[currentIndex]" class="modal-img" />
      </div>

      <TermViewModal v-if="showDictionaryModal" @close="closeDictionaryModal" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import Buddy from '@/components/BuddyHelper.vue'
import checklistItems from '@/stores/checklistStore2.js'
import TermViewModal from "@/components/TermViewModal.vue";
import { useRoute } from 'vue-router'

const route = useRoute()
const userId = Number(route.query.userId)
//const userId = 1
const registryId = Number(route.query.registryId)

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

const goNext = async () => {
  // registryId가 없는 경우: 매물 없이 체크리스트만 본 사용자
  if (!registryId) {
    router.push('/')
    return
  }

  const payload = {
    userId,
    registryId,
    checked: checked.value
  }

  try {
    const res = await fetch('http://localhost:8080/api/checklist', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    })

    if (!res.ok) throw new Error('전송 실패')
    alert('체크리스트 저장 완료!')
    router.push({
      path: '/final-report',
      query: {
        userId,
        registryId
      }
    })

  } catch (e) {
    console.error(e)
    alert('저장에 실패했습니다.')
  }
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

<style lang="scss">
$main: #1A80E5;

.checklist-page {
  .wrapper {
    width: 100%;
    height: calc(100vh - 130px);
    display: flex;
    justify-content: center;
    position: relative;
  }

  .container {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .main-title {
    font-size: 26px;
    font-weight: bold;
    color: #222;
    text-align: center;
    margin: 20px 0 10px 0;
  }

  .blue-text {
    color: #1A80E5;
    margin-left: 10px;
  }

  .content-box {
    display: flex;
    flex-wrap: wrap;
    gap: 40px;
    justify-content: center;
    width: 100%;
    height: 90%;
    align-items: flex-start;
    position: relative;
  }

  .slider-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 30px;
    height: 100%;
  }

  .slider {
    width: 420px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .slider-img {
    height: 100%;
    width: auto;
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
    background-color: #F7F9FC;
    padding: 10px;
    border-radius: 16px;
    flex: 1 1 400px;
    max-width: 650px;
    height: 510px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    flex-direction: column;
    justify-content: space-between;
    margin-top: 15px;
  }

  .checklist {
    margin-top: 10px;
    line-height: 2;
    width: 100%;
    flex-grow: 1;
    margin-bottom: 12px;
  }

  .checklist li {
    margin-bottom: 16px;
    display: flex;
    align-items: flex-start;
    font-size: 17px;
    gap: 12px;
    line-height: 2.2;
  }

  .item {
    display: flex;
    align-items: center;
    user-select: none;
    transform: translateZ(0);
  }

  .cbx {
    position: relative;
    top: 1px;
    display: inline-block;
    width: 18px;
    height: 18px;
    margin-right: 6px;
    border: 1px solid #C8CCD4;
    border-radius: 3px;
    cursor: pointer;

    svg {
      position: relative;
      top: -10px;
      transform: scale(0);
      fill: none;
      stroke-linecap: round;
      stroke-linejoin: round;

      polyline {
        stroke-width: 3;
        stroke: $main;
      }
    }

    &:before {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      margin: -10px 0px 0 -10px;
      width: 20px;
      height: 20px;
      border-radius: 100%;
      background: $main;
      transform: scale(0);
    }

    &:after {
      content: '';
      position: absolute;
      top: 5px;
      left: 5px;
      width: 2px;
      height: 2px;
      border-radius: 2px;
      box-shadow: 0 -18px 0 $main, 12px -12px 0 $main, 18px 0 0 $main,
      12px 12px 0 $main, 0 18px 0 $main, -12px 12px 0 $main,
      -18px 0 0 $main, -12px -12px 0 $main;
      transform: scale(0);
    }
  }

  .cbx-lbl {
    position: relative;
    cursor: pointer;
    transition: color 0.3s ease;
    margin-left: 10px;

    &:after {
      content: '';
      position: absolute;
      top: 50%;
      left: 0;
      width: 0;
      height: 1px;
      background: #9098A9;
    }
  }

  input[type='checkbox'] {
    display: none;

    &:checked + .cbx {
      border-color: transparent;

      svg {
        transform: scale(1.5);
        transition: all 0.4s ease;
        transition-delay: 0.1s;
      }

      &:before {
        transform: scale(1);
        opacity: 0;
        transition: all 0.3s ease;
      }

      &:after {
        transform: scale(1);
        opacity: 0;
        transition: all 0.6s ease;
      }
    }

    &:checked ~ .cbx-lbl {
      color: #9098A9;

      &:after {
        width: 100%;
        transition: all 0.4s ease;
      }
    }
  }

  .next-button-fixed {
    position: absolute;
    right: 50px;
    bottom: 20px;
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

  .next-button-fixed:hover {
    background-color: #2563eb;
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
}
</style>
