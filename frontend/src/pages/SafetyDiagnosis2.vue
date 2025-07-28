<script setup>
import { ref, onMounted } from 'vue';

const selectedAddress = ref('');
const roadAddress = ref('');
const jibunAddress = ref('');
const jeonsePrice = ref('');
const file = ref(null);
const itemName = ref('');

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

const callBackend = async () => {
  console.log(selectedAddress);
  if (!jeonsePrice.value || (!roadAddress.value && !jibunAddress.value)) {
    alert('보증금과 주소를 모두 입력해주세요.');
    return;
  }
  if (jeonsePrice.value < 1000) {
    // 보증금 최소값 경고 추가
    alert('보증금은 최소 1,000만원 이상 입력해주세요.');
    return;
  }
  try {
    const response = await fetch('/api/diagnosis/leasePer', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        address: selectedAddress.value,
        jeonsePrice: jeonsePrice.value,
      }),
    });
    const contentType = response.headers.get('content-type');
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText);
    }
    if (contentType && contentType.includes('application/json')) {
      const data = await response.json();
      alert(
        `전세가율: ${data.jeonseRate.toFixed(2)}%\n예상 매매가: ${
          data.expectedSalePrice
        }만원`
      );
    } else {
      const text = await response.text();
      throw new Error(text);
    }
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

<template>
  <div class="wrapper">
    <h2 class="headline">
      매물 안전성 분석, <strong>딱 <span class="blue">세 가지</span></strong>만 해요.
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
        <span>{{ file?.name || 'Click or drag file to this area to upload' }}</span>
      </div>
    </div>

    <div class="input-group">
      <label>4. 매물 이름</label>
      <p class="tip">
        설정한 이름은 체크리스트 진단 매물 선택 시, 마이페이지에서 최종 리포트와
        계약서 조회 시 사용됩니다.
      </p>
      <input v-model="itemName" placeholder="매물 이름을 설정해주세요." class="input-box" />
    </div>

    <button class="submit-btn" @click="callBackend">분석 시작하기</button>
  </div>
</template>

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
  color: #555;
  margin-bottom: 30px;
}
.blue {
  color: #1A80E5;
}
.input-group {
  margin-bottom: 25px;
}
label {
  font-weight: bold;
  display: block;
  margin-bottom: 8px;
}
.input-box {
  width: 100%;
  padding: 18px 20px;
  border: 1px solid #D1DBE8;
  background-color: #F9FBFD;
  font-size: 15px;
  color: #333;
  transition: border-color 0.2s ease;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: textfield;
}
.input-box::placeholder {
  color: #A0AAB8;
}
.input-box:focus {
  outline: none;
  border-color: #1A80E5;
  background-color: #fff;
}
.address-box {
  display: flex;
  gap: 10px;
}
.address-box input {
  flex: 1;
}
.address-box button {
  padding: 10px 14px;
  background-color: #2a7be4;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  
}
.upload-box {
  border: 2px dashed #ccc;
  padding: 40px;
  text-align: center;
  border-radius: 12px;
  cursor: pointer;
  color: #888;
}
.tip {
  font-size: 12px;
  color: #888;
  margin-bottom: 8px;
}
.submit-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  background-color: #1A80E5;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 20px;
}
</style>
