<template>
  <div class="wrap">
    <h2 class="ttl">내 동네 전세가율 변화를 한눈에 쉽게 확인해보세요!</h2>

    <!-- 주소/동 선택 -->
    <div class="row">
      <label>주소</label>
      <div class="address">
        <input class="inp addr-inp" v-model="selectedAddr" placeholder="   도로명/지번" readonly />
        <button class="btn" @click="openPostcode">주소 검색</button>
      </div>
      <small v-if="!lawd" class="tip">먼저 주소를 선택하세요.</small>
      <small v-if="bname || bcode" class="tip">
        동: {{ bname }} / 법정동코드: {{ bcode }}
      </small>
    </div>

    <!-- 아파트명 선택(옵션) -->
    <div class="row">
      <label>아파트명 (선택)</label>
      <input class="inp apt-inp" v-model="aptName" placeholder=" 예: 은마, 래미안…" />
    </div>

    <!-- 조회 / 차트 유형 -->
    <div class="row controls">
      <button
          class="btn primary"
          :disabled="loading || !lawd"
          @click="onQuery"
      >
        {{ loading ? '조회 중...' : '최근 전세가율 보기' }}
      </button>

      <div class="chart-type">
        <span class="tip">차트 유형:</span>
        <button
            class="tab"
            :class="{ active: chartType === 'line' }"
            @click="setChartType('line')"
        >
          꺾은선
        </button>
        <button
            class="tab"
            :class="{ active: chartType === 'bar' }"
            @click="setChartType('bar')"
        >
          막대
        </button>
      </div>
    </div>

<!--    <small v-if="!lawd" class="tip">먼저 주소를 선택하세요.</small>-->

    <!-- 에러 / 요약 -->
    <p v-if="error" class="err">{{ error }}</p>
    <div v-if="items.length" class="tip">
      포인트: <b>{{ items.length }}</b>개 /
      평균 전세가율: <b>{{ avgRatio.toFixed(1) }}%</b>
    </div>
    <p v-else-if="queried && !error" class="tip">데이터가 없습니다.</p>

    <!-- 차트 -->
    <div v-if="items.length" class="chartWrap">
      <canvas ref="canvasEl" height="360"></canvas>
    </div>

    <!-- 표 (선택 확인용) -->
    <div v-if="items.length" class="overflow">
      <table class="tbl">
        <thead>
        <tr>
          <th>월</th>
          <th class="r">전세가율(%)</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(r, i) in items" :key="i">
          <td>{{ r.ymd }}</td>
          <td class="r">{{ r.ratio != null ? r.ratio.toFixed(2) : '-' }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Chart } from 'chart.js/auto'

// ✅ 백엔드 기본 URL (8081에서 돌고 있으면 그대로 사용)
const API_BASE = import.meta.env.VITE_BACKEND ?? 'http://localhost:8080'

// 상태
const selectedAddr = ref('')
const bname = ref('')   // 동 이름
const bcode = ref('')   // 법정동 코드(10자리)
const aptName = ref('') // 아파트명(필터용)

const chartType = ref('line') // 'line' 또는 'bar'
const items = ref([])         // { ymd: 'YYYY-MM', ratio: number }[]
const loading = ref(false)
const error = ref('')
const queried = ref(false)

// 법정동 코드 앞 5자리 -> LAWD_CD
const lawd = computed(() =>
    bcode.value ? bcode.value.slice(0, 5) : ''
)

// 평균 전세가율
const avgRatio = computed(() => {
  const arr = items.value.map(v => v.ratio).filter(v => v != null)
  if (!arr.length) return 0
  return arr.reduce((a, b) => a + b, 0) / arr.length
})

// 차트
const canvasEl = ref(null)
let chartInstance = null

function setChartType(t) {
  chartType.value = t
  if (items.value.length) {
    drawChart(items.value)
  }
}

// 카카오 주소 검색
function openPostcode () {
  if (!window.daum?.Postcode) {
    alert('주소 검색 스크립트를 불러오는 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  new window.daum.Postcode({
    oncomplete (data) {
      selectedAddr.value = data.userSelectedType === 'R'
          ? data.roadAddress
          : data.jibunAddress
      bname.value = data.bname || ''
      bcode.value = data.bcode || ''
    }
  }).open()
}

// 조회 버튼 클릭
async function onQuery () {
  if (!lawd.value) return
  error.value = ''
  items.value = []
  queried.value = true
  loading.value = true
  destroyChart()

  try {
    const qs = new URLSearchParams({
      lawd: lawd.value,
      umd: bname.value || '',
      apt: aptName.value || ''
    }).toString()

    // ✅ 백엔드: GET /api/jeonse/ratio/recent?lawd=...&umd=...&apt=...
    const res = await fetch(`${API_BASE}/api/jeonse/ratio/recent?${qs}`, {
      headers: {
        'Accept': 'application/json'
      }
    })

    // JSON 아닌 응답 방지용 간단 체크 (선택)
    const text = await res.text()
    try {
      const json = JSON.parse(text)

      if (!res.ok) {
        throw new Error(json?.message || '조회 실패')
      }

      const rows = json.rows || []
      items.value = rows.map(r => ({
        ymd: r.ymd,    // 백엔드에서 "YYYY-MM" 형태로 내려줬다고 가정
        ratio: r.ratio // %
      }))

      if (items.value.length) {
        drawChart(items.value)
      }
    } catch (e) {
      // JSON 파싱 실패 → 서버가 XML이나 HTML 등 보낸 경우
      console.error('서버 응답:', text)
      throw new Error('서버가 JSON이 아닌 응답을 보냈어요.')
    }
  } catch (e) {
    error.value = e?.message || '요청 실패'
  } finally {
    loading.value = false
  }
}

function drawChart (list) {
  if (!canvasEl.value) return
  const ctx = canvasEl.value.getContext('2d')
  if (chartInstance) chartInstance.destroy()

  const labels = list.map(v => v.ymd)
  const data = list.map(v => v.ratio)

  chartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels,
      datasets: [{
        label: '전세가율(%)',
        data,
        fill: false,
        tension: 0.25,
        pointRadius: 3
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: { beginAtZero: true, title: { display: true, text: '(%)' } },
        x: { ticks: { autoSkip: false, maxRotation: 0 } }
      },
      plugins: {
        tooltip: {
          callbacks: {
            label: (ctx) =>
                ctx.parsed.y != null ? ` ${ctx.parsed.y}%` : ' 데이터 없음'
          }
        }
      }
    }
  })
}

function destroyChart () {
  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }
}

// 카카오 우편번호 스크립트 로드
onMounted(() => {
  if (!window.daum?.Postcode) {
    const s = document.createElement('script')
    s.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    document.body.appendChild(s)
  }
})
</script>

<style scoped>
/* 전체 영역 중앙 정렬용 래퍼 */
.wrap {
  max-width: 900px;
  margin: 25px auto;
  padding: 40px;
  text-align: center;
}

.ttl {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 45px;
}

/* 한 줄(주소, 아파트명, 버튼들) 공통 */
.row {
  margin-bottom: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;   /* 🔥 라벨 + 내용 전체를 중앙으로 */
}

.row label {
  width: 100%;
  max-width: 600px;   /* ✅ 입력칸 너비와 맞추기 */
  font-weight: 550;
  text-align: left;   /* ✅ 글자는 왼쪽 정렬 */
  margin-bottom: 8px; /* 라벨과 인풋 간 간격 */
}

/* ===== 주소 입력 + 버튼 영역 ===== */
.address {
  display: flex;                 /* 🔥 다시 flex로 */
  gap: 8px;
  width: 100%;
  justify-content: center;       /* 🔥 안에 있는 input+버튼 묶음을 가운데로 */
}

/* 주소 입력칸 */
.addr-inp {
  width: 100%;
  max-width: 500px;              /* 🔥 최대 폭 제한 */
  height: 55px;
  border: 1px solid #d1dbe8;
  border-radius: 10px;
  box-sizing: border-box;
}

/* 아파트 입력칸 */
.apt-inp {
  width: 100%;
  max-width: 600px;              /* 🔥 주소 입력칸과 동일 폭 */
  height: 55px;
  border: 1px solid #d1dbe8;
  border-radius: 10px;
  box-sizing: border-box;
}

/* 주소 검색 버튼 */
.address .btn {
  height: 55px;                  /* 🔥 input 높이와 맞춤 */
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 공통 버튼 */
.btn {
  padding: 10px 14px;
  border: 0;
  background: #64748b;
  color: #fff;
  border-radius: 10px;
  cursor: pointer;
}

.btn:hover {
  background: rgba(100, 116, 139, 0.82);
}

/* 조회 버튼(가운데 크게) */
.btn.primary {
  background: #1a80e5;
  margin-top: 8px;
  width: 100%;
  max-width: 600px;              /* 🔥 가운데 큰 버튼 */
}

.btn.primary:hover {
  background: rgba(26, 128, 229, 0.88);
}

/* 안내 텍스트 / 에러 */
.tip {
  color: #6b7280;
  font-size: 13px;
  margin-top: 15px;
}
.err {
  color: #d00;
  font-size: 14px;
}

/* ===== 조회 버튼 + 차트 유형 영역 ===== */
.controls {
  display: flex;
  flex-direction: column;        /* 🔥 위에 버튼, 아래에 차트 유형 */
  align-items: center;           /* 🔥 전체 중앙 정렬 */
  gap: 10px;
}

/* 차트 유형 토글 */
.chart-type {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: center;       /* 🔥 토글 버튼들 중앙 */
  margin-top: 14px;
}

/* 탭 버튼 (꺾은선 / 막대) */
.tab {
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #d1dbe8;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
  margin-top: 15px;
}
.tab.active {
  background: #1a80e5;
  color: #fff;
  border-color: #1a80e5;
}

/* 차트 영역 */
.chartWrap {
  width: 100%;
  max-width: 900px;
  height: 420px;
  margin: 16px auto;
}

/* 표 */
.tbl {
  width: 100%;
  border-collapse: collapse;
  margin-top: 8px;
  font-size: 14px;
}
.tbl th,
.tbl td {
  border-top: 1px solid #eee;
  padding: 8px;
}
.tbl th {
  background: #f8fafc;
  text-align: left;
}
.tbl .r {
  text-align: right;
}
.overflow {
  overflow: auto;
}
</style>