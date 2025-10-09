// k6/load-helpers2.js
import http from 'k6/http';

export const BASE = __ENV.BASE_URL || 'http://localhost:8080';

/** ── (A) setup()에서 1회만 호출할 토큰 발급 ── */
export function fetchTokenOnce() {
    const res = http.get(`${BASE}/api/auth/test-token`);
    if (res.status === 200) {
        const token = JSON.parse(res.body).accessToken;
        console.log('setup 토큰:', token.substring(0, 20) + '...');
        return token;
    }
    throw new Error(`토큰 발급 실패: ${res.status} ${String(res.body).slice(0,120)}`);
}

/** ── (B) 매 요청에 토큰을 '인자'로 주입하는 함수들 ── */
function authParamsWith(token, params = {}) {
    const headers = {
        Accept: 'application/json',
        Authorization: `Bearer ${token}`,
        ...(params.headers || {}),
    };
    return { ...params, headers };
}
export function getWithToken(url, token, params = {}) {
    return http.get(url, authParamsWith(token, params));
}
export function delWithToken(url, token, params = {}) {
    return http.del(url, null, authParamsWith(token, params));
}
export function postWithToken(url, token, json, params = {}) {
    const p = authParamsWith(token, { ...params, headers: { 'Content-Type': 'application/json', ...(params.headers||{}) } });
    return http.post(url, json ? JSON.stringify(json) : null, p);
}
export function putWithToken(url, token, json, params = {}) {
    const p = authParamsWith(token, { ...params, headers: { 'Content-Type': 'application/json', ...(params.headers||{}) } });
    return http.put(url, json ? JSON.stringify(json) : null, p);
}

/** ── (C) 기존 전역 토큰 방식(그대로 유지해도 됨) ── */
let accessToken = null;
export function refreshToken() {
    const res = http.get(`${BASE}/api/auth/test-token`);
    if (res.status === 200) {
        accessToken = JSON.parse(res.body).accessToken;
        console.log('새 토큰 발급:', accessToken.substring(0, 20) + '...');
    } else {
        console.error('토큰 발급 실패:', res.status, res.body);
    }
}
export function ensureToken() {
    if (!accessToken) refreshToken();
}
function withAuthParams(params = {}) {
    const headers = {
        Accept: 'application/json',
        Authorization: `Bearer ${accessToken}`,
        ...(params.headers || {}),
    };
    return { ...params, headers };
}
function send(method, url, body, params) {
    if (method === 'GET')    return http.get(url, params);
    if (method === 'DELETE') return http.del(url, null, params);
    if (method === 'POST')   return http.post(url, body, params);
    if (method === 'PUT')    return http.put(url, body, params);
    throw new Error(`Unsupported method: ${method}`);
}
export function requestWithAuth(method, url, body = null, params = {}) {
    ensureToken();
    let p = withAuthParams(params);
    let res = send(method, url, body, p);
    if (res.status === 401) {
        refreshToken();
        p = withAuthParams(params);
        res = send(method, url, body, p);
    }
    return res;
}
export const getWithAuth  = (url, params = {}) => requestWithAuth('GET', url, null, params);
export const delWithAuth  = (url, params = {}) => requestWithAuth('DELETE', url, null, params);
export const postWithAuth = (url, json, params = {}) =>
    requestWithAuth('POST', url, json ? JSON.stringify(json) : null,
        { ...params, headers: { 'Content-Type': 'application/json', ...(params.headers||{}) } });
export const putWithAuth  = (url, json, params = {}) =>
    requestWithAuth('PUT',  url, json ? JSON.stringify(json) : null,
        { ...params, headers: { 'Content-Type': 'application/json', ...(params.headers||{}) } });
