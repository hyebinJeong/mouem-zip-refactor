import axios from 'axios';

export const getFinalReport = async (reportId) => {
  const res = await axios.get(`/api/final-report/${reportId}`);
  return res.data;
};
