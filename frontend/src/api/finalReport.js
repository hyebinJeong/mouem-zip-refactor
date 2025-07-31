import axios from 'axios';

export const getFinalReport = async (reportId) => {
  const res = await axios.get(`/api/reports/${reportId}`);
  return res.data;
};
