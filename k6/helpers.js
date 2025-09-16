// k6/helpers.js
import http from 'k6/http';

// BASE URL: 환경변수(BASE_URL) 우선, 없으면 기본값
export const BASE = __ENV.BASE_URL || 'http://localhost:8080';

// 각 VU(가상유저)마다 독립적인 런타임이므로, 아래 변수들은 VU별로 분리됩니다.
let accessToken = null;

// JWT를 발급 (GET /api/auth/test-token 사용)
export function refreshToken() {
    const res = http.get(`${BASE}/api/auth/test-token`);
    if (res.status === 200) {
        accessToken = JSON.parse(res.body).accessToken;
        // 초기 디버깅에만 로그. 시끄러우면 주석 처리하세요.
        console.log('🔑 새 토큰 발급:', accessToken.substring(0, 20) + '...');
    } else {
        console.error('❌ 토큰 발급 실패:', res.status, res.body);
    }
}

// 토큰이 없으면 발급
export function ensureToken() {
    if (!accessToken) refreshToken();
}

// 인증 헤더 생성(추가 헤더 병합 가능)
export function authHeaders(extra = {}) {
    return {
        headers: {
            Authorization: `Bearer ${accessToken}`,
            ...extra,
        },
    };
}

// 401이면 1회 자동 갱신 후 재시도하는 범용 요청 함수
export function requestWithAuth(method, url, body = null, extraHeaders = {}) {
    ensureToken();

    let params = authHeaders(extraHeaders);
    let res = send(method, url, body, params);

    if (res.status === 401) {
        console.log('⚠️ 401 감지 → 토큰 갱신 후 재시도:', url);
        refreshToken();
        params = authHeaders(extraHeaders);
        res = send(method, url, body, params);
    }
    return res;
}

// 편의 래퍼들
export const getWithAuth    = (url, extra = {}) => requestWithAuth('GET',    url, null, extra);
export const delWithAuth    = (url, extra = {}) => requestWithAuth('DELETE', url, null, extra);
export const postWithAuth   = (url, json, extra = {}) =>
    requestWithAuth('POST', url, json, { 'Content-Type': 'application/json', ...extra });
export const putWithAuth    = (url, json, extra = {}) =>
    requestWithAuth('PUT',  url, json, { 'Content-Type': 'application/json', ...extra });

// 내부 전송 유틸
function send(method, url, body, params) {
    if (method === 'GET')    return http.get(url, params);
    if (method === 'DELETE') return http.del(url, null, params);
    if (method === 'POST')   return http.post(url, body ? JSON.stringify(body) : null, params);
    if (method === 'PUT')    return http.put(url,  body ? JSON.stringify(body) : null, params);
    throw new Error(`Unsupported method: ${method}`);
}
