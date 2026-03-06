import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// ─── Materials ────────────────────────────────────────────────────────────────
export const materialApi = {
  getAll: (params = {}) => api.get('/materials', { params }),
  getById: (id) => api.get(`/materials/${id}`),
  create: (data) => api.post('/materials', data),
  update: (id, data) => api.put(`/materials/${id}`, data),
  delete: (id) => api.delete(`/materials/${id}`),
};

// ─── Inventory Lots ───────────────────────────────────────────────────────────
export const lotApi = {
  getAll: (params = {}) => api.get('/lots', { params }),
  getById: (id) => api.get(`/lots/${id}`),
  receive: (data) => api.post('/lots/receive', data),
  updateStatus: (id, status, performedBy = '') =>
    api.patch(`/lots/${id}/status`, { status, performedBy }),
  getTransactions: (id) => api.get(`/lots/${id}/transactions`),
};

export default api;
