<template>
  <div class="d-flex flex-column justify-content-center align-items-center vh-100 bg-light p-0 m-0">
    <!-- 상단 제목 -->
    <div class="w-100 py-4 text-center position-fixed top-0 start-0 custom-header">
      <h1 class="text-white fw-bold m-0">HomeBuddy 서비스 이용 전 유의사항</h1>
    </div>

    <!-- 본문 박스 -->
    <div class="agreement-box shadow d-flex flex-column justify-content-between">
      <div>
        <h2 class="h5 fw-bold mb-5">홈버디(HomeBuddy) 면책고지</h2>
        <p>
          홈버디는 사용자가 입력한 정보와 업로드한 자료를 바탕으로 분석 결과와 참고 정보를 제공하는 서비스입니다.
          제공되는 모든 리포트 및 안내 자료는 법적 효력이 있는 공식 문서가 아니며,
          공인중개사, 감정평가사, 변호사 등 전문가의 조언이나 자문을 대체하지 않습니다.
        </p>
        <p>
          부동산 계약이나 거래는 개인 재산에 중대한 영향을 미치는 중요한 사안입니다.
          따라서 실제 판단과 최종 결정은 사용자 본인의 신중한 검토와 책임 있는 판단에 따라 이루어져야 하며,
          홈버디는 그 과정에서 <strong>참고용 도구</strong>로만 활용하시기를 권장드립니다.
        </p>
        <p>
          또한, 홈버디는 사용자 제공 정보에 기반한 분석 결과를 안내할 뿐,
          이로 인해 발생할 수 있는 법적 분쟁, 금전적 손실, 권리 관계 문제 등에 대해
          <strong>일체의 법적 책임을 지지 않습니다</strong>.
        </p>
      </div>

      <!-- 동의 문구 -->
      <div class="text-center mt-4">
        <p class="fw-semibold">
          위 내용을 충분히 숙지하였으며,<br />
          홈버디가 제공하는 정보가 참고용임을 이해하고 동의하십니까?
        </p>
      </div>
    </div>

    <!-- 동의합니다 / 동의하지 않습니다 버튼 -->
    <div class="d-flex flex-column align-items-center mt-3 gap-2">
      <button class="btn custom-btn px-4 py-2 fw-bold" style="width: 200px;" @click="handleAgree">
        동의합니다
      </button>
      <button class="btn custom-btn-gray px-4 py-2 fw-bold" style="width: 200px;" @click="handleDisagree">
        동의하지 않습니다
      </button>
    </div>

    <!-- 캐릭터 -->
    <div class="character">
      <img src="../assets/homebuddycharacter.svg" alt="HomeBuddy 캐릭터" />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

// 동의 시 → safety-check
const handleAgree = () => {
  router.push({ path: '/safety-check', name: 'safety-check' })
}

// 동의하지 않을 시 → 홈으로 이동
const handleDisagree = () => {
  router.push('/')
}
</script>

<style scoped>
.agreement-box {
  background-color: #EFF5FF;
  border-radius: 20px;
  width: 900px;
  height: 500px;
  padding: 40px;
  margin-top: 100px;
}
.agreement-box p {
  font-size: 1.1rem;
  line-height: 1.6;
}

/* 상단바 */
.custom-header {
  background-color: #1A80E5;
}

/* 버튼 */
.custom-btn {
  background-color: #1A80E5;
  border: none;
  color: white;
}
.custom-btn:hover {
  background-color: #166bc0;
}

/* 동의하지 않습니다 버튼 */
.custom-btn-gray {
  background-color: #d3d3d3;
  border: none;
  color: white;
}
.custom-btn-gray:hover {
  background-color: #bfbfbf;
}

/* 좌측 하단 캐릭터 */
.character {
  position: absolute;
  left: 160px;
  bottom: 20px;
}
.character img {
  width: 200px;
  height: auto;
}

/* 반응형 */
@media (max-width: 900px) {
  .agreement-box {
    width: 90%;
    height: auto;
  }
  .character {
    display: none;
  }
}
@media (max-width: 1300px) {
  .character {
    display: none;
  }
}
</style>