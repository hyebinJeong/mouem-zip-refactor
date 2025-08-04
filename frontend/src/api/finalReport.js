import axios from 'axios';

export const getFinalReport = async (reportId) => {
  const res = await axios.get(`/api/reports/${reportId}`);
  return res.data;
};

export const getFinalReportByUserAndRegistry = async (userId, registryId) => {
  const res = await axios.get('/api/reports', {
    params: { userId, registryId },
  });
  return res.data;
};
