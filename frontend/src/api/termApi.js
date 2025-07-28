import axios from 'axios';

// 백엔드 API 주소 (개발 중)
const API_BASE_URL = 'http://localhost:8080/api';

export const fetchDictionary = (params) => {
    return axios.get(`${API_BASE_URL}/dictionary`, { params });
};

export const fetchTermById = (id) => {
    return axios.get(`${API_BASE_URL}/dictionary/${id}`);
};