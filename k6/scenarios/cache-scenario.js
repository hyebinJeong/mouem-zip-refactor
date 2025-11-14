// 회귀 테스트
// 목적: 캐시 전/후 비교, 엔드포인트별 회귀
import {check, sleep} from 'k6';
import {getWithAuth, postWithAuth, ensureToken, BASE} from "../helpers/cache-helpers1.js";
const MODE = __ENV.MODE || "cold";

// 테스트 옵션
export const options = {
    scenarios: {
        ramp: {
            executor: 'ramping-arrival-rate',
            startRate: 10,      // 시작 시 초당 50 요청
            timeUnit: '1s',     // 단위: 1초당 요청 수
            preAllocatedVUs: 50, // 초기 VU 수
            maxVUs: 100,          // 최대 동시 가상 유저 수
            stages: [
                { duration: "1m", target: 20 },   // 1분 동안 초당 20RPS
                { duration: "2m", target: 30 },   // 2분 동안 초당 30RPS 유지
                { duration: "2m", target: 40 },   // 2분 동안 초당 40RPS 유지
                { duration: "1m", target: 0 },    // 부하 제거
            ],
        },
    },
    thresholds: {
        'http_req_duration{name:registry_list}': ['p(95)<300'],
        'http_req_duration{name:reports_list}': ['p(95)<300'],
        'http_req_duration{name:reports_detail}': ['p(95)<300'],
        'http_req_duration{name:diagnosis_result}': ['p(95)<300'],
        'http_req_duration{name:safety_result}': ['p(95)<300'],
        'http_req_duration{name:contract_list}': ['p(95)<300'],
        'http_req_duration{name:contract_detail}': ['p(95)<300'],
        'http_req_duration{name:checklist_save}': ['p(95)<400'],
        http_req_duration: ['p(95)<600'],
        checks: ['rate>0.95'],
    },
};

// 간단 테스트
// export const options = {
//     scenarios: {
//         cold: { // 콜드 채우기: 짧고 약하게
//             executor: 'constant-arrival-rate',
//             rate: 10, timeUnit: '1s',
//             duration: '10s',
//             preAllocatedVUs: 10, maxVUs: 50,
//         },
//         hot: {  // 바로 핫: 같은 엔드포인트/ID 반복
//             startTime: '10s',
//             executor: 'constant-arrival-rate',
//             rate: 50, timeUnit: '1s',
//             duration: '20s',
//             preAllocatedVUs: 20, maxVUs: 100,
//         },
//     },
//     thresholds: {
//         // 핫 구간에서 p95가 충분히 내려오는지(임시 기준)
//         // 'http_req_duration{name:diagnosis_result}': ['p(95)<150'],
//         'http_req_duration{name:registry_list}': ['p(95)<300'],
//         // 전체 엄격 기준을 잠깐 완화해도 좋음
//         http_req_duration: ['p(95)<1000'],
//     },
// };

const USER_ID = Number(__ENV.USER_ID || 1);
const WARMUP_CREATE  = __ENV.WARMUP_CREATE === '1';
const WARMUP_LIMIT   = Number(__ENV.WARMUP_LIMIT || 5);

// 체크리스트 POST 빈도 제어(기본 20%) — 데이터 오염 방지용
const CHECKLIST_POST_RATE = Number(__ENV.CHECKLIST_POST_RATE || 0.2);

export function setup() {
    ensureToken();

    let reportIds = [];
    let registryIds = [];
    let contractIds = [];

    // 1) 리포트 리스트
    const rReports = getWithAuth(`${BASE}/api/reports/list?userId=${USER_ID}`, { tags: { name: 'reports_list' } });
    console.log('[DEBUG] reports_list 응답 코드:', rReports.status);
    console.log('[DEBUG] reports_list 응답 헤더:', rReports.headers['Content-Type']);
    console.log('[DEBUG] reports_list 응답 바디 (앞 200자):', String(rReports.body).slice(0, 200));
    if (rReports.headers['Content-Type']?.includes('application/json') && rReports.status === 200) {
        try {
            const arr = JSON.parse(rReports.body);
            reportIds = (Array.isArray(arr) ? arr : (arr.items || [])).map(r => r.reportId).filter(Boolean);
        } catch (e) {
            console.error('리포트 리스트 파싱 실패:', e.message, String(rReports.body).slice(0, 200));
        }
    }

    //  2) 내 등기부등본 분석 목록
    const rRegs = getWithAuth(`${BASE}/api/registry/user/${USER_ID}`);
    console.log('[DEBUG] registry 응답:', rRegs.status, rRegs.body);
    if (rRegs.headers['Content-Type']?.includes('application/json') && rRegs.status === 200) {
        try {
            const arr   = JSON.parse(rRegs.body);
            const items = Array.isArray(arr) ? arr : (arr.items || []);
            registryIds = items.map(x => x.registryId || x.id).filter(Boolean);
        } catch (e) {
            console.error('레지스트리 파싱 실패:', e.message, String(rRegs.body).slice(0, 200));
        }
    }

    // 3) 계약서 리스트
    const rcontract = getWithAuth(`${BASE}/api/contract/list`, { tags: { name: 'contract_list' } });
    if (rcontract.headers['Content-Type']?.includes('application/json') && rcontract.status === 200) {
        try {
            const arr   = JSON.parse(rcontract.body);
            const items = Array.isArray(arr) ? arr : (arr.items || []);
            contractIds = items.map(c => c.contractId || c.id).filter(Boolean);
        } catch (e) {
            console.error('계약서 파싱 실패:', e.message, String(rcontract.body).slice(0, 200));
        }
    }

    const hotRegistryIds = MODE === "hot" ? [registryIds[0]] : registryIds.slice(0, 50);
    const hotReportIds   = MODE === "hot" ? [reportIds[0]]   : reportIds.slice(0, 50);
    const hotContractIds = MODE === "hot" ? [contractIds[0]] : contractIds.slice(0, 50);


    return { hotRegistryIds, hotReportIds, hotContractIds, registryIds, reportIds, contractIds };
}



// -------------------- 액션 정의 --------------------
function getRegistryList() {
    const res = getWithAuth(`${BASE}/api/registry/user/${USER_ID}`, { tags: { name: 'registry_list' } });
    check(res, { 'GET /api/registry/user/{id} 200': r => r.status === 200 });
}

function getReportsList() {
    const res = getWithAuth(`${BASE}/api/reports/list?userId=${USER_ID}`, { tags: { name: 'reports_list' } });
    check(res, { 'GET /api/reports/list 200': r => r.status === 200 });
}

function getReportDetail(data) {
    //const ids = data.hotReportIds?.length ? data.hotReportIds : data.reportIds.slice(0, 50);
    const ids =
        data.hotReportIds.length > 0
            ? data.hotReportIds
            : (data.reportIds.length > 0 ? data.reportIds.slice(0, 50) : []);

    if (ids.length === 0) return;
    console.log('[DEBUG] 리포트 대상 ID 개수:', ids.length);
    console.log('[DEBUG] 리포트 대상 ID 샘플 (앞 5개):', ids.slice(0, 5));
    const id = pick(ids);
    const res = getWithAuth(`${BASE}/api/reports/${id}`, { tags: { name: 'reports_detail' } });
    check(res, { 'GET /api/reports/{id} 200': r => r.status === 200 });
}

function getDiagnosisResult(data) {
    //const ids = data.hotRegistryIds?.length ? data.hotRegistryIds : data.registryIds.slice(0, 50);
    const ids =
        data.hotRegistryIds.length > 0
            ? data.hotRegistryIds
            : (data.registryIds.length > 0 ? data.registryIds.slice(0, 50) : []);

    if (ids.length === 0) return;
    console.log('[DEBUG] 전세가율 대상 ID 개수:', ids.length);
    console.log('[DEBUG] 전세가율 대상 ID 샘플 (앞 5개):', ids.slice(0, 5));
    const id = pick(ids);
    const res = getWithAuth(`${BASE}/api/diagnosis/result?registerId=${id}`, { tags: { name: 'diagnosis_result' } });
    check(res, { 'GET /api/diagnosis/result 200|404|400': r => r.status === 200 || r.status === 404 || r.status === 400 });
}

function getSafetyResult(data) {
    //const ids = data.hotRegistryIds?.length ? data.hotRegistryIds : data.registryIds.slice(0, 50);
    const ids =
        data.hotRegistryIds && data.hotRegistryIds.length > 0
            ? data.hotRegistryIds
            : (data.registryIds && data.registryIds.length > 0
                ? data.registryIds.slice(0, 50)
                : []);

    if (ids.length === 0) {
        console.warn("[WARN] safety-check: 사용할 registryId가 없습니다.");
        return;
    }
    console.log('[DEBUG] 등기부 안전도 대상 ID 개수:', ids.length);
    console.log('[DEBUG] 등기부 안전도 대상 ID 샘플 (앞 5개):', ids.slice(0, 5));
    const id = pick(ids);

    const res = getWithAuth(`${BASE}/api/safety-check/${id}`, { tags: { name: 'safety_result' } });
    check(res, { 'GET /api/safety-check/{id} 200|404': r => r.status === 200 || r.status === 404 });
}

function getcontractList() {
    const res = getWithAuth(`${BASE}/api/contract/list`, { tags: { name: 'contract_list' } });
    check(res, { 'GET /api/contract/list 200': r => r.status === 200 });
}

function getContractDetail(data) {
    //const ids = data.hotContractIds?.length ? data.hotContractIds : data.contractIds.slice(0, 50);
    const ids =
        data.hotContractIds.length > 0
            ? data.hotContractIds
            : (data.contractIds.length > 0 ? data.contractIds.slice(0, 50) : []);

    if (ids.length === 0) return;
    console.log('[DEBUG] 계약서 대상 ID 개수:', ids.length);
    console.log('[DEBUG] 계약서 대상 ID 샘플 (앞 5개):', ids.slice(0, 5));
    const id = pick(ids);
    const res = getWithAuth(`${BASE}/api/contract/${id}`, { tags: { name: 'contract_detail' } });
    check(res, { 'GET /api/contract/{id} 200|404': r => r.status === 200 || r.status === 404 });
}

// 체크리스트 저장 (랜덤 9개, 과도한 POST 방지 확률 제어)
function saveChecklist(data) {
    if (!data.registryIds?.length) return;
    // 과도한 데이터 변경 방지: 일정 확률로만 POST
    if (Math.random() > CHECKLIST_POST_RATE) return;

    const id = pick(data.registryIds);
    const payload = {
        userId: USER_ID,
        registryId: id,
        checked: Array.from({ length: 9 }, () => Math.random() > 0.5)  // 배열 그대로
    };

    const res = postWithAuth(`${BASE}/api/checklist`, payload, { tags: { name: 'checklist_save' } });
    check(res, { 'POST /api/checklist 200|201': r => r.status === 200 || r.status === 201 });
    // check(res, { 'POST /api/checklist 200|201': r => {
    //         const ok = r.status === 200 || r.status === 201;
    //         if (!ok) console.error('[CHECKLIST_FAIL]', 'id=', id, 'status=', r.status, String(r.body).slice(0, 200));
    //         return ok;
    //     }});
}

// 랜덤 선택 유틸
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)]; }

// -------------------- VU 실행 루프 --------------------
export default function (data) {
    ensureToken();

    // 매 iteration마다 전부 실행
    getRegistryList();
    getReportsList();
    getReportDetail(data);
    getDiagnosisResult(data);
    getSafetyResult(data);
    saveChecklist(data);
    getcontractList();
    getContractDetail(data);

    // arrival-rate 시나리오에서는 sleep을 짧게
    sleep(0.01);
}

// ========== 결과 파일 저장(전/후 비교·회귀 추적용) ==========
export function handleSummary(summary) {
    if (summary && summary.setup_data) {
        // 토큰 가리기
        if (summary.setup_data.token) {
            summary.setup_data.token = '***masked***';
        }

        // summary에서는 전체 ID 목록 제거
        delete summary.setup_data.registryIds;
        delete summary.setup_data.reportIds;
        delete summary.setup_data.contractIds;
    }

    const label = __ENV.LABEL || 'run';
    const dir   = '../results';

    // 현재 날짜·시간
    const timestamp = new Date().toLocaleString('ko-KR', { timeZone: 'Asia/Seoul' });
    summary.testedAt = timestamp;

    return {
        stdout: JSON.stringify(summary, null, 2),
        [`${dir}/summary-cache-${label}.json`]: JSON.stringify(summary, null, 2),
    };
}
