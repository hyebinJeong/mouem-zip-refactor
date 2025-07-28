<template>
  <div class="wrapper">
    <div class="container">
      <h1 class="main-title">
        계약 전에서 놓치는 부분이 없도록<br />
        <span class="blue-text">체크리스트</span>를 통해 도와드릴게요.
      </h1>

      <p class="sub-title">
        전세 계약 전 확인할 수 있는 거의 유일한 공적 자료는 등기부등본입니다. <br />
        등기부등본부터 확인해보세요.
        등기부등본을 분석하고싶으시면 매물 안전성 진단을 통해 알아보세요!
      </p>

      <div class="image-section">
        <img src="@/assets/diagnosis.png" alt="진단" class="icon-img" />
        <span class="arrow">▶</span>
        <img src="@/assets/checklist.png" alt="체크리스트" class="icon-img" />
      </div>

      <div class="highlight-box">
        <img src="@/assets/buddyblue.png" alt="버디" class="buddy-img" />
        <div class="highlight-text">
          <span>아직 매물 안전성 진단을 안 하셨다면, 꼭 진행해보시길 권장드립니다.</span><br />
          <span class="blue-text">
            계약 전 단 한 번의 확인으로, 수천만 원의 피해를 막을 수 있습니다.
          </span>
        </div>

        <div class="button-section">
          <button class="blue-button">매물 안전성 진단 받으러가기</button>
        </div>
      </div>

      <div class="bottom-right">
        <button class="next-button" @click="goNext">다음</button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goNext() {
  router.push('/checklist/checklist')
}
</script>


<style scoped>
.wrapper {
  width: 100vw;
  display: flex;
  justify-content: center;
  padding: 30px 20px;
}

.container {
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.main-title {
  font-size: 32px;
  font-weight: bold;
  color: #222;
  line-height: 1.5;
  margin-bottom: 10px;
  display: flex;
  text-align: center;
}

.blue-text {
  color: #1A80E5;
  margin: 0 0 0 10px;
}

.image-section {
  display: flex;
  align-items: center;
  gap: 100px;
  margin-bottom: 40px;
}

.icon-img {
  height: 250px;
  object-fit: contain;
}

.arrow {
  font-size: 50px;
  color: #999;
}

.sub-title {
  color: #666;
  font-size: 16px;
  text-align: center;
  line-height: 1.6;
  margin-bottom: 40px;
}

.highlight-box {
  border-radius: 12px;
  font-size: 15px;
  color: #444;
  display: flex;
  align-items: center;
  gap: 16px;
  width: 52%;
  margin-bottom: 30px;
}

.buddy-img {
  height: 80px;
  margin-top:20px;
}

.highlight-text {
  flex: 1;
}

.button-section {
  display: flex;
  justify-content: flex-end;
  margin-left: auto;
}

.blue-button {
  background-color: #1A80E5;
  color: white;
  font-weight: 600;
  font-size: 14px;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.blue-button:hover {
  background-color: #2563eb;
}

.bottom-right {
  display: flex;
  justify-content: flex-end;
  width: 95%;
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
</style>
