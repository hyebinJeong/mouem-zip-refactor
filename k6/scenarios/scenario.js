// k6/scenario.js
import { check, sleep } from 'k6';
import { BASE, ensureToken, getWithAuth, postWithAuth } from '../helpers/cache-helpers1.js';
import http from 'k6/http';

const USER_ID   = parseInt(__ENV.USER_ID || '1', 10);
const RUN_WRITE = __ENV.RUN_WRITE === '1';
const DEBUG     = __ENV.DEBUG === '1';

// 전세가율 분석용 테스트 파라미터 (ENV로 덮어쓰기 가능)
const ADDR      = __ENV.ADDR || '서울특별시 강남구 테헤란로 152';
const JEONSE    = parseInt(__ENV.JEONSE || '25000', 10);     // 만원 단위
const AREA_M2   = __ENV.AREA ? parseFloat(__ENV.AREA) : 84;  // 전용면적 m²
const BUILDING  = __ENV.BUILDING || '테스트빌딩';

export const options = {
    vus: 20,
    duration: '2m',
    thresholds: {
        checks: ['rate>0.95'],
        http_req_duration: ['p(95)<500'],
    },
};

// 디버그 로깅
function logIfBad(name, res) {
    if (!DEBUG) return;
    if (res.status >= 200 && res.status < 300) return;
    console.log(`❗ ${name} -> ${res.status} body: ${String(res.body).slice(0, 200)}...`);
}

function pick(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

/* -------------------- setup: ID 수집 + "안전한 리포트" 선별 -------------------- */
export function setup() {
    // 토큰 1회 발급(세팅용)
    const tokenRes = http.get(`${BASE}/api/auth/test-token`);
    const accessToken = JSON.parse(tokenRes.body).accessToken;
    const h = { headers: { Authorization: `Bearer ${accessToken}` } };

    let registryIds = [];
    let contractIds = [];

    // 1) 내 리포트 리스트 가져오기
    const rList = http.get(`${BASE}/api/reports/list?userId=${USER_ID}`, h);
    const safeReports = []; // ← 상세 200 보장되는 {reportId, registryId}만 담는다
    if (rList.status === 200) {
        try {
            const arr = JSON.parse(rList.body);
            for (const it of arr) {
                const reportId  = it.reportId;
                const registryId = it.registryId;
                // 상세가 실제로 200 나오는지 사전 검증
                const detail = http.get(`${BASE}/api/reports/${reportId}`, h);
                if (detail.status === 200) {
                    safeReports.push({ reportId, registryId });
                } else {
                    logIfBad('warmup GET /api/reports/{id}', detail);
                }
            }
        } catch (e) {
            logIfBad('parse /api/reports/list', { status: 500, body: e.message });
        }
    } else {
        logIfBad('GET /api/reports/list', rList);
    }

    if (DEBUG) console.log('safeReports count =', safeReports.length);

    // 2) (선택) 내 레지스트리 목록 – 쓰기/생성 시나리오용
    const rRegs = http.get(`${BASE}/api/registry/user/${USER_ID}`, h);
    if (rRegs.status === 200) {
        try {
            const arr = JSON.parse(rRegs.body);
            const items = Array.isArray(arr) ? arr : (arr.items || []);
            registryIds = items.map(x => x.registryId || x.id).filter(Boolean);
        } catch (_) {}
    }

    // 3) 계약서 리스트
    const rContracts = http.get(`${BASE}/api/contracts/list`, h);
    if (rContracts.status === 200) {
        try {
            const arr = JSON.parse(rContracts.body);
            const items = Array.isArray(arr) ? arr : (arr.items || []);
            contractIds = items.map(x => x.contractId || x.id).filter(Boolean);
        } catch (_) {}
    }

    return { safeReports, registryIds, contractIds };
}

/* -------------------- 읽기 액션 -------------------- */
function getMe() {
    const res = getWithAuth(`${BASE}/api/user/me`);
    check(res, { 'GET /api/user/me 200': r => r.status === 200 });
}

function getReportList() {
    const res = getWithAuth(`${BASE}/api/reports/list?userId=${USER_ID}`);
    check(res, { 'GET /api/reports/list 200': r => r.status === 200 });
}

function getReportDetailById(data) {
    if (!data.safeReports.length) return; // 안전한 리포트 없으면 생략
    const { reportId } = pick(data.safeReports);
    const res = getWithAuth(`${BASE}/api/reports/${reportId}`);
    // 이제는 200만 기대해도 됨(사전 필터링 완료)
    check(res, { 'GET /api/reports/{id} 200': r => r.status === 200 });
}

function getOrCreateReport(data) {
    // (주의) 이 엔드포인트는 내부에서 생성까지 하므로 사이드이펙트가 있음
    if (!data.safeReports.length) return;
    const { registryId } = pick(data.safeReports); // 안전한 registry만 사용
    const res = getWithAuth(`${BASE}/api/reports?userId=${USER_ID}&registryId=${registryId}`);
    check(res, { 'GET /api/reports?userId&registryId 200': r => r.status === 200 });
}

function getContractsList() {
    const res = getWithAuth(`${BASE}/api/contracts/list`);
    check(res, { 'GET /api/contracts/list 200': r => r.status === 200 });
}

function getContractDetail(data) {
    if (!data.contractIds.length) return;
    const id = pick(data.contractIds);
    const res = getWithAuth(`${BASE}/api/contracts/${id}`);
    check(res, { 'GET /api/contracts/{id} 200|404': r => r.status === 200 || r.status === 404 });
}

function getCategory()        { const r = getWithAuth(`${BASE}/api/category`);        check(r, { 'GET /api/category 200': r => r.status === 200 }); }
function getTerms()           { const r = getWithAuth(`${BASE}/api/term-manager`);    check(r, { 'GET /api/term-manager 200': r => r.status === 200 }); }
function getGuideChecklist()  { const r = getWithAuth(`${BASE}/api/guide/checklist`); check(r, { 'GET /api/guide/checklist 200': r => r.status === 200 }); }
function getRecommendation()  { const r = getWithAuth(`${BASE}/api/recommendation`);  check(r, { 'GET /api/recommendation 200': r => r.status === 200 }); }

/* -------------------- 쓰기 액션(옵션) -------------------- */
function postLeasePer(data) {
    if (!RUN_WRITE) return;               // 기본 OFF, -e RUN_WRITE=1 로만 실행
    if (!data.registryIds.length) return;

    const registerId = pick(data.registryIds);
    const payload = {
        userId: USER_ID,
        registerId,
        address: ADDR,
        jeonsePrice: JEONSE,
        buildingType: BUILDING,
        excluUseAr: AREA_M2,
    };

    const res = postWithAuth(`${BASE}/api/diagnosis/leasePer`, payload);
    check(res, {
        'POST /api/diagnosis/leasePer 204|200|400': r => [204, 200, 400].includes(r.status),
    });

    const got = getWithAuth(`${BASE}/api/diagnosis/result?registerId=${registerId}`);
    check(got, {
        'GET /api/diagnosis/result 200|404': r => r.status === 200 || r.status === 404,
    });
}

/* -------------------- 트래픽 믹스 -------------------- */
const actions = [
    { fn: getMe,               weight: 14 },
    { fn: getReportList,       weight: 14 },
    { fn: getReportDetailById, weight: 12 },
    { fn: getOrCreateReport,   weight: 4  }, // 사이드이펙트 낮게
    { fn: getContractsList,    weight: 12 },
    { fn: getContractDetail,   weight: 10 },
    { fn: getGuideChecklist,   weight: 10 },
    { fn: getTerms,            weight: 8  },
    { fn: getCategory,         weight: 8  },
    { fn: getRecommendation,   weight: 6  },
    { fn: postLeasePer,        weight: 2  }, // 기본 OFF
];

function pickWeighted(list) {
    const total = list.reduce((s, a) => s + a.weight, 0);
    let r = Math.random() * total;
    for (const a of list) { if ((r -= a.weight) <= 0) return a.fn; }
    return list[0].fn;
}

export default function (data) {
    ensureToken();

    const fn = pickWeighted(actions);
    if ([getReportDetailById, getOrCreateReport, getContractDetail, postLeasePer].includes(fn)) fn(data);
    else fn();

    sleep(1);
}
