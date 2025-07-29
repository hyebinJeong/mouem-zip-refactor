<script setup>
import { ref, onMounted } from 'vue';

// =======================================
// 등기부 위험 요소 관련 변수
// ========================================
const selectedFile = ref(null);
const mortgageInfos = ref([]);
const seizureInfos = ref([]);
const provisionalSeizureInfos = ref([]);
const auctionInfos = ref([]);
const provisionalRegistrationInfos = ref([]);
const injunctionInfos = ref([]);
const jeonseRightInfos = ref([]);
const trustInfos = ref([]);

function handleFileChange(event) {
  selectedFile.value = event.target.files[0];
}

async function uploadFile() {
  if (!selectedFile.value) {
    alert('PDF 파일을 선택해주세요.');
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    // 표 데이터 받아오기
    // const tableRes = await fetch('http://localhost:8080/safety-check', {
    // method: 'POST',
    // body: formData,
    // });
    // if (tableRes.ok) {
    //   tableData.value = await tableRes.json();
    // } else {
    //   console.error('표 추출 실패');
    // }
    const analysisResult = await fetch('http://localhost:8080/safety-check', {
      method: 'POST',
      body: formData,
    });
    if (analysisResult.ok) {
      const result = await analysisResult.json();
      mortgageInfos.value = result.mortgageInfos || [];
      seizureInfos.value = result.seizureInfos || [];
      provisionalSeizureInfos.value = result.provisionalSeizureInfos || [];
      auctionInfos.value = result.auctionInfos || [];
      provisionalRegistrationInfos.value =
        result.provisionalRegistrationInfos || [];
      injunctionInfos.value = result.injunctionInfos || [];
      jeonseRightInfos.value = result.jeonseRightInfos || [];
      trustInfos.value = result.trustInfos || [];
    } else {
      console.error('분석 실패');
    }
  } catch (error) {
    console.error('업로드 중 오류 발생:', error);
  }
}


// =======================================
// 주소 및 보증금 관련
// ========================================
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
        `전세가율: ${data.jeonseRate}%\n예상 매매가: ${
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




  <!-- ================================================================
  등기부등본
  ================================================================ -->
   <!-- 업로드 UI -->
  <div v-if="!mortgageInfos.length">
    <input type="file" @change="handleFileChange" accept="application/pdf" />
    <button
      @click="uploadFile"
      class="mt-2 bg-blue-500 text-white px-4 py-2 rounded"
    >
      다음
    </button>
  </div>

  <!-- 근저당 정보 표 -->
  <div v-if="mortgageInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">근저당권 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">채권최고액</th>
          <th class="border px-2 py-1">근저당권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in mortgageInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.maxClaimAmount }}</td>
          <td class="border px-2 py-1">{{ item.mortgageHolder }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 압류 정보 표 -->
  <div v-if="seizureInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">압류 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">권리자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in seizureInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.rightHolder }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 가압류 정보 표 -->
  <div v-if="provisionalSeizureInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">가압류 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">청구금액</th>
          <th class="border px-2 py-1">채권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in provisionalSeizureInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.maxClaimAmount }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 경매 정보 표 -->
  <div v-if="auctionInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">경매 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">채권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in auctionInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 가등기 정보 표 -->
  <div v-if="provisionalRegistrationInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">가등기 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">가등기권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in provisionalRegistrationInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.registeredRightHolder }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 가처분 정보 표 -->
  <div v-if="injunctionInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">가처분 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">채권자(또는 권리자)</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in injunctionInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.creditor }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 전세권설정 정보 표 -->
  <div v-if="jeonseRightInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">전세권설정 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">전세금</th>
          <th class="border px-2 py-1">전세권자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in jeonseRightInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.deposit }}</td>
          <td class="border px-2 py-1">{{ item.mortgagor }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- 신탁등기 정보 표 -->
  <div v-if="trustInfos.length">
    <h3 class="text-lg font-semibold mt-6 mb-2">신탁등기 정보</h3>
    <table class="border border-gray-300 w-full">
      <thead>
        <tr>
          <th class="border px-2 py-1">접수일자</th>
          <th class="border px-2 py-1">수탁자</th>
          <th class="border px-2 py-1">말소여부</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in trustInfos" :key="index">
          <td class="border px-2 py-1">{{ item.date }}</td>
          <td class="border px-2 py-1">{{ item.trustee }}</td>
          <td class="border px-2 py-1">{{ item.canceled }}</td>
        </tr>
      </tbody>
    </table>
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
