import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

export const getFinalReport = async (reportId) => {
  const auth = useAuthStore();
  const res = await axios.get(`/api/reports/${reportId}`, {
    headers: { Authorization: `Bearer ${auth.token}` },
  });
  return res.data;
};

export const getFinalReportByUserAndRegistry = async (userId, registryId) => {
  const auth = useAuthStore();
  const res = await axios.get('/api/reports', {
    params: { userId, registryId },
    headers: { Authorization: `Bearer ${auth.token}` },
  });
  return res.data;
};
