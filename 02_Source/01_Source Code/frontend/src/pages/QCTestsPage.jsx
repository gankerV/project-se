import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { qcTestApi, lotApi } from '../services/api';
import Modal from '../components/Modal';

const TEST_TYPES = ['Identity', 'Potency', 'Microbial', 'GrowthPromotion', 'Physical', 'Chemical'];
const RESULT_STATUSES = ['Pass', 'Fail', 'Pending'];

function ResultBadge({ status }) {
  const cls = status === 'Pass' ? 'badge-pass' : status === 'Fail' ? 'badge-fail' : 'badge-pending';
  return (
    <span className={`badge ${cls}`}>
      <span className="badge-dot" />
      {status}
    </span>
  );
}

function QCTestForm({ initial, lots, selectedLotId, onSubmit, onClose, loading, error }) {
  const today = new Date().toISOString().split('T')[0];
  const [form, setForm] = useState({
    lotId: selectedLotId || '',
    testType: 'Identity',
    testMethod: '',
    testDate: today,
    testResult: '',
    acceptanceCriteria: '',
    resultStatus: 'Pending',
    performedBy: '',
    verifiedBy: '',
    ...initial,
  });

  const set = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  return (
    <form onSubmit={(e) => { e.preventDefault(); onSubmit(form); }}>
      <div className="modal-body">
        {error && <div className="alert alert-error">⚠ {error}</div>}
        <div className="form-grid">
          {!initial && (
            <div className="form-group form-full">
              <label className="form-label required">Lô hàng</label>
              <select
                className="form-control"
                value={form.lotId}
                onChange={(e) => set('lotId', e.target.value)}
                required
                id="qc-lotId"
              >
                <option value="">-- Chọn lô hàng --</option>
                {lots.map((l) => (
                  <option key={l.lotId} value={l.lotId}>
                    {l.partNumber} — {l.materialName} ({l.manufacturerLot || 'N/A'})
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="form-group">
            <label className="form-label required">Loại kiểm nghiệm</label>
            <select
              className="form-control"
              value={form.testType}
              onChange={(e) => set('testType', e.target.value)}
              required
              id="qc-testType"
            >
              {TEST_TYPES.map((t) => <option key={t}>{t}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Phương pháp kiểm nghiệm</label>
            <input
              className="form-control"
              value={form.testMethod}
              onChange={(e) => set('testMethod', e.target.value)}
              placeholder="VD: USP <61>"
              id="qc-testMethod"
            />
          </div>
          <div className="form-group">
            <label className="form-label required">Ngày kiểm nghiệm</label>
            <input
              className="form-control"
              type="date"
              value={form.testDate}
              onChange={(e) => set('testDate', e.target.value)}
              required
              id="qc-testDate"
            />
          </div>
          <div className="form-group">
            <label className="form-label required">Kết quả</label>
            <select
              className="form-control"
              value={form.resultStatus}
              onChange={(e) => set('resultStatus', e.target.value)}
              required
              id="qc-resultStatus"
            >
              {RESULT_STATUSES.map((s) => <option key={s}>{s}</option>)}
            </select>
          </div>
          <div className="form-group form-full">
            <label className="form-label">Giá trị kết quả thực tế</label>
            <input
              className="form-control"
              value={form.testResult}
              onChange={(e) => set('testResult', e.target.value)}
              placeholder="VD: 99.5%"
              id="qc-testResult"
            />
          </div>
          <div className="form-group form-full">
            <label className="form-label">Tiêu chí chấp nhận</label>
            <input
              className="form-control"
              value={form.acceptanceCriteria}
              onChange={(e) => set('acceptanceCriteria', e.target.value)}
              placeholder="VD: NLT 98.0%"
              id="qc-acceptanceCriteria"
            />
          </div>
          <div className="form-group">
            <label className="form-label">Người thực hiện</label>
            <input
              className="form-control"
              value={form.performedBy}
              onChange={(e) => set('performedBy', e.target.value)}
              placeholder="VD: analyst01"
              id="qc-performedBy"
            />
          </div>
          <div className="form-group">
            <label className="form-label">Người xác nhận</label>
            <input
              className="form-control"
              value={form.verifiedBy}
              onChange={(e) => set('verifiedBy', e.target.value)}
              placeholder="VD: supervisor01"
              id="qc-verifiedBy"
            />
          </div>
        </div>
      </div>
      <div className="modal-footer">
        <button type="button" className="btn btn-outline" onClick={onClose} disabled={loading}>Huỷ</button>
        <button type="submit" className="btn btn-primary" disabled={loading} id="btn-submit-qc">
          {loading ? '⏳ Đang lưu...' : (initial ? '💾 Cập nhật' : '➕ Thêm kiểm nghiệm')}
        </button>
      </div>
    </form>
  );
}

export default function QCTestsPage() {
  const [searchParams] = useSearchParams();
  const [lots, setLots] = useState([]);
  const [selectedLotId, setSelectedLotId] = useState(searchParams.get('lotId') || '');
  const [tests, setTests] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(false);
  const [fetchError, setFetchError] = useState('');

  const [showCreate, setShowCreate] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  const flash = (msg) => { setSuccessMsg(msg); setTimeout(() => setSuccessMsg(''), 3000); };

  useEffect(() => {
    lotApi.getAll().then((r) => setLots(r.data.content ?? [])).catch(() => {});
  }, []);

  const loadTests = useCallback(async () => {
    if (!selectedLotId) { setTests([]); setSummary(null); return; }
    setLoading(true);
    setFetchError('');
    try {
      const [testsRes, summaryRes] = await Promise.all([
        qcTestApi.getByLot(selectedLotId),
        qcTestApi.getSummary(selectedLotId),
      ]);
      setTests(testsRes.data);
      setSummary(summaryRes.data);
    } catch (e) {
      setFetchError(e.response?.data?.message ?? 'Không thể tải dữ liệu kiểm nghiệm');
    } finally {
      setLoading(false);
    }
  }, [selectedLotId]);

  useEffect(() => { loadTests(); }, [loadTests]);

  const handleCreate = async (form) => {
    setFormLoading(true);
    setFormError('');
    try {
      await qcTestApi.create(form);
      setShowCreate(false);
      flash('✅ Đã thêm kiểm nghiệm mới');
      loadTests();
    } catch (e) {
      setFormError(e.response?.data?.message ?? 'Tạo kiểm nghiệm thất bại');
    } finally {
      setFormLoading(false);
    }
  };

  const handleUpdate = async (form) => {
    setFormLoading(true);
    setFormError('');
    try {
      await qcTestApi.update(editTarget.testId, form);
      setEditTarget(null);
      flash('✅ Cập nhật kiểm nghiệm thành công');
      loadTests();
    } catch (e) {
      setFormError(e.response?.data?.message ?? 'Cập nhật thất bại');
    } finally {
      setFormLoading(false);
    }
  };

  return (
    <>
      <div className="page-header">
        <div className="page-header-left">
          <h1>🔬 Kiểm nghiệm (QC Tests)</h1>
          <p>Quản lý kết quả kiểm nghiệm chất lượng theo lô hàng</p>
        </div>
        <button
          id="btn-add-qctest"
          className="btn btn-primary"
          onClick={() => { setFormError(''); setShowCreate(true); }}
          disabled={!selectedLotId}
        >
          ➕ Thêm kiểm nghiệm
        </button>
      </div>

      <div className="page-body">
        {successMsg && <div className="alert alert-success" style={{ marginBottom: 16 }}>{successMsg}</div>}

        {/* Lot selector */}
        <div className="card" style={{ marginBottom: 20 }}>
          <div className="card-header">
            <span className="card-title">Chọn lô hàng</span>
          </div>
          <div style={{ padding: '16px 20px' }}>
            <select
              className="form-control"
              value={selectedLotId}
              onChange={(e) => setSelectedLotId(e.target.value)}
              id="lot-selector"
              style={{ maxWidth: 480 }}
            >
              <option value="">-- Chọn lô hàng để xem kiểm nghiệm --</option>
              {lots.map((l) => (
                <option key={l.lotId} value={l.lotId}>
                  {l.partNumber} — {l.materialName} | {l.manufacturerLot || 'N/A'} [{l.status}]
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Summary stats */}
        {summary && (
          <div className="stats-row" style={{ marginBottom: 20 }}>
            <div className="stat-card">
              <span className="stat-label">Tổng kiểm nghiệm</span>
              <span className="stat-value">{summary.totalTests}</span>
            </div>
            <div className="stat-card">
              <span className="stat-label" style={{ color: 'var(--success)' }}>Pass</span>
              <span className="stat-value" style={{ color: 'var(--success)' }}>{summary.passed}</span>
            </div>
            <div className="stat-card">
              <span className="stat-label" style={{ color: 'var(--danger)' }}>Fail</span>
              <span className="stat-value" style={{ color: 'var(--danger)' }}>{summary.failed}</span>
            </div>
            <div className="stat-card">
              <span className="stat-label" style={{ color: 'var(--warning)' }}>Pending</span>
              <span className="stat-value" style={{ color: 'var(--warning)' }}>{summary.pending}</span>
            </div>
            <div className="stat-card">
              <span className="stat-label">Trạng thái lô</span>
              <span className="stat-value" style={{ fontSize: 18 }}>{summary.lotStatus || '—'}</span>
            </div>
          </div>
        )}

        {/* Tests table */}
        <div className="card">
          {!selectedLotId ? (
            <div className="empty-state">
              <div className="empty-icon">🔬</div>
              <p>Vui lòng chọn lô hàng để xem danh sách kiểm nghiệm.</p>
            </div>
          ) : loading ? (
            <div className="loading-center"><div className="spinner" /></div>
          ) : fetchError ? (
            <div style={{ padding: 20 }}><div className="alert alert-error">{fetchError}</div></div>
          ) : tests.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">🔬</div>
              <p>Chưa có kiểm nghiệm nào cho lô này. Nhấn <strong>Thêm kiểm nghiệm</strong> để bắt đầu.</p>
            </div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>Loại kiểm nghiệm</th>
                    <th>Phương pháp</th>
                    <th>Ngày kiểm</th>
                    <th>Kết quả</th>
                    <th>Tiêu chí</th>
                    <th>Trạng thái</th>
                    <th>Người thực hiện</th>
                    <th>Người xác nhận</th>
                    <th style={{ textAlign: 'right' }}>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  {tests.map((t) => (
                    <tr key={t.testId}>
                      <td className="td-primary">{t.testType}</td>
                      <td>{t.testMethod || <span className="text-muted">—</span>}</td>
                      <td className="text-muted">
                        {t.testDate ? new Date(t.testDate).toLocaleDateString('vi-VN') : '—'}
                      </td>
                      <td>{t.testResult || <span className="text-muted">—</span>}</td>
                      <td className="text-muted">{t.acceptanceCriteria || '—'}</td>
                      <td><ResultBadge status={t.resultStatus} /></td>
                      <td>{t.performedBy || <span className="text-muted">—</span>}</td>
                      <td>{t.verifiedBy || <span className="text-muted">—</span>}</td>
                      <td>
                        <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end' }}>
                          <button
                            id={`btn-edit-qc-${t.testId}`}
                            className="btn btn-outline btn-sm"
                            onClick={() => { setFormError(''); setEditTarget(t); }}
                          >✏ Sửa</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Create Modal */}
      {showCreate && (
        <Modal title="➕ Thêm kiểm nghiệm mới" onClose={() => setShowCreate(false)} size="modal-lg">
          <QCTestForm
            lots={lots}
            selectedLotId={selectedLotId}
            onSubmit={handleCreate}
            onClose={() => setShowCreate(false)}
            loading={formLoading}
            error={formError}
          />
        </Modal>
      )}

      {/* Edit Modal */}
      {editTarget && (
        <Modal title={`✏ Sửa kiểm nghiệm: ${editTarget.testType}`} onClose={() => setEditTarget(null)} size="modal-lg">
          <QCTestForm
            initial={editTarget}
            lots={lots}
            selectedLotId={selectedLotId}
            onSubmit={handleUpdate}
            onClose={() => setEditTarget(null)}
            loading={formLoading}
            error={formError}
          />
        </Modal>
      )}
    </>
  );
}
