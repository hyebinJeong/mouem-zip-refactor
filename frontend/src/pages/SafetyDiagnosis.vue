<template>
  <div class="wrapper">
    <h2 class="headline">
      매물 안전성 분석, <strong>딱 <span class="blue">네 가지</span></strong
      >만 해요.
    </h2>
    <p class="sub">보증금, 주소를 입력하고 등기부등본만 올리면 끝!</p>

    <div class="input-group">
      <label>1. 전세 보증금을 입력해주세요. (단위: 만원)</label>
      <input
        v-model.number="jeonsePrice"
        type="number"
        min="0"
        placeholder="금액을 입력해주세요. 예시) 30000 (3억)"
        class="input-box"
      />
    </div>

    <div class="input-group">
      <label>2. 주소를 입력해주세요.</label>
      <div class="address-box">
        <input
          v-model="selectedAddress"
          placeholder="도로명 또는 지번 주소"
          readonly
          class="input-box"
        />
        <button @click="openPostcode">주소 검색</button>
      </div>
    </div>

    <div class="input-group">
      <label>3. 등기부등본을 업로드해주세요.</label>
      <div class="upload-box" @click="$refs.fileInput.click()">
        <input
          type="file"
          ref="fileInput"
          @change="handleFileUpload"
          style="display: none"
        />
        <span>{{
          file?.name ||
          '여기를 눌러 파일을 선택하거나 첨부할 파일을 끌어다 놓으세요'
        }}</span>
      </div>
    </div>

    <div class="input-group">
      <label>4. 매물 이름</label>
      <p class="tip">
        설정한 이름은 체크리스트 진단 매물 선택 시, 마이페이지에서 최종 리포트와
        계약서 조회 시 사용됩니다.
      </p>
      <input
        v-model="registryName"
        placeholder="매물 이름을 설정해주세요."
        class="input-box"
      />
    </div>

    <button class="submit-btn" @click="submitForm">분석 시작하기</button>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const auth = useAuthStore();

const selectedAddress = ref('');
const roadAddress = ref('');
const jibunAddress = ref('');
const jeonsePrice = ref('');
const file = ref(null);
const registryName = ref('');

const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: function (data) {
      const userType = data.userSelectedType;
      if (userType === 'R' && data.roadAddress) {
        roadAddress.value = data.roadAddress;
        selectedAddress.value = data.roadAddress;
      } else if (userType === 'J' && data.jibunAddress) {
        jibunAddress.value = data.jibunAddress;
        selectedAddress.value = data.jibunAddress;
      } else {
        selectedAddress.value = '';
      }
    },
  }).open();
};

const handleFileUpload = (event) => {
  file.value = event.target.files[0];
};

const submitForm = async () => {
  if (
    !file.value ||
    !selectedAddress.value ||
    !jeonsePrice.value ||
    !registryName.value
  ) {
    alert('모든 항목을 입력해주세요.');
    return;
  }

  if (jeonsePrice.value < 1000) {
    alert('보증금은 최소 1,000만원 이상 입력해주세요.');
    return;
  }

  const formData = new FormData();
  formData.append('userId', auth.userId);
  formData.append('file', file.value);
  formData.append('address', selectedAddress.value);
  formData.append('jeonsePrice', jeonsePrice.value);
  formData.append('registryName', registryName.value);

  try {
    // 1단계: 등기부등본 분석
    const response = await fetch('http://localhost:8080/api/safety-check', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      const err = await response.text();
      throw new Error(err);
    }

    const registerId = await response.json();

    // 2단계: 전세가율 분석
    const leaseRes = await fetch('/api/diagnosis/leasePer', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        registerId: registerId,
        address: selectedAddress.value,
        jeonsePrice: jeonsePrice.value,
      }),
    });
    if (!leaseRes.ok) {
      throw new Error(await leaseRes.text());
    }

    // 3단계: 모든 분석 완료 후 결과 페이지로 이동
    router.push(`/safety-check/${registerId}`);
  } catch (e) {
    alert('분석 실패: ' + e.message);
  }
};

onMounted(() => {
  const script = document.createElement('script');
  script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
  document.body.appendChild(script);
});
</script>

<style scoped>
.wrapper {
  max-width: 520px;
  margin: 0 auto;
  font-family: sans-serif;
  padding: 30px 20px;
  text-align: left;
}
.headline {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  line-height: 1.4;
  margin-bottom: 8px;
}
.sub {
  text-align: center;
  color: #424242;
  margin-bottom: 30px;
  font-weight: bold;
}
.blue {
  color: #1a80e5;
}
.input-group {
  margin-bottom: 25px;
}
label {
  font-weight: bold;
  display: block;
  margin-bottom: 8px;
}
.input-box::placeholder {
  color: #a0aab8;
}
.input-box:focus {
  outline: none;
  border-color: #1a80e5;
  background-color: #fff;
}
.address-box {
  display: flex;
  gap: 10px;
  width: 100%;
}
.address-box input {
  flex: 1;
}
.tip {
  font-size: 12px;
  color: #888;
  margin-bottom: 8px;
  white-space: nowrap;
}
.input-box {
  width: 100%;
  padding: 18px 20px;
  border: 1px solid #d1dbe8;
  background-color: #f9fbfd;
  font-size: 15px;
  color: #333;
  border-radius: 12px !important; /* !important로 강제 적용 */
  transition: border-color 0.2s ease;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: textfield;
  box-sizing: border-box;
  outline: none;
}
.upload-box {
  border: 2px dashed #ccc;
  padding: 20px;
  text-align: center;
  border-radius: 12px !important; /* !important로 강제 적용 */
  cursor: pointer;
  color: #888;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-sizing: border-box;
  width: 100%;
}
.upload-box span {
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
}
.address-box button {
  flex-shrink: 0;
  padding: 10px 14px;
  background-color: #2a7be4;
  color: white;
  border: none;
  border-radius: 12px !important; /* !important로 강제 적용 */
  cursor: pointer;
  box-sizing: border-box;
  outline: none;
}
.submit-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  background-color: #1a80e5;
  color: white;
  border: none;
  border-radius: 12px !important; /* !important로 강제 적용 */
  cursor: pointer;
  margin-top: 20px;
  box-sizing: border-box;
  outline: none;
}
</style>
