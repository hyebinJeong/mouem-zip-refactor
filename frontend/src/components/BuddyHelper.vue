<template>
  <div class="buddy-fixed" @click="handleClick">
    <img src="@/assets/buddygreen.png" alt="버디" class="buddy-img-out" />
    <div class="balloon-out" v-if="showBalloon">
      어려운 단어가 있다면<br />
      여기를 클릭하세요!
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['open-dictionary'])

const showBalloon = ref(true)
const handleClick = () => {
  // 말풍선 토글
  showBalloon.value = !showBalloon.value
  // 모달열기알람
  emit('open-dictionary')
}

</script>

<style scoped>
.buddy-fixed {
  position: fixed;
  top: 220px;
  right: 140px;
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
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.02); }
}

.balloon-out::before,
.balloon-out::after {
  content: "";
  position: absolute;
  border-width: 10px;
  border-style: solid;
  right: 20px;
}

.balloon-out::before {
  bottom: -21px;
  border-color: rgba(0, 0, 0, 0.1) transparent transparent transparent;
}

.balloon-out::after {
  bottom: -20px;
  border-color: #fff transparent transparent transparent;
}
</style>
