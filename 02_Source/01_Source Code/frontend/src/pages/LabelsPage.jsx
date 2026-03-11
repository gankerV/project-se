import { useState, useEffect, useCallback } from 'react';
import { labelApi } from '../services/api';
import Modal from '../components/Modal';

const LABEL_TYPES = ['RawMaterial', 'Sample', 'Intermediate', 'FinishedProduct', 'API', 'Status'];
const SOURCE_TYPES = ['LOT', 'BATCH'];

function TemplateForm({ initial, onSubmit, onClose, loading, error }) {
  const [form, setForm] = useState({
    templateId: '',
    templateName: '',
    labelType: 'RawMaterial',
    templateContent: '',
    width: 100,
    height: 60,
    ...initial,
  });
  const set = (k, v) => setForm((f) => ({ ...f, [k]: v }));
  const isEdit = !!initial;

  return (
    <form onSubmit={(e) => { e.preventDefault(); onSubmit(form); }}>
      <div className="modal-body">
        {error && <div className="alert alert-error">⚠ {error}</div>}
        <div className="form-grid">
          {!isEdit && (
            <div className="form-group form-full">
              <label className="form-label required">Template ID</label>
              <input
                className="form-control"
                value={form.templateId}
                onChange={(e) => set('templateId', e.target.value)}
                placeholder="VD: TMPL-001"
                required
                id="tmpl-templateId"
              />
            </div>
          )}
          <div className="form-group">
            <label className="form-label required">Tên template</label>
            <input
              className="form-control"
              value={form.templateName}
              onChange={(e) => set('templateName', e.target.value)}
              placeholder="VD: Nhãn nguyên liệu cơ bản"
              required
              id="tmpl-templateName"
            />
          </div>
          <div className="form-group">
            <label className="form-label required">Loại nhãn</label>
            <select
              className="form-control"
              value={form.labelType}
              onChange={(e) => set('labelType', e.target.value)}
              required
              id="tmpl-labelType"
            >
              {LABEL_TYPES.map((t) => <option key={t}>{t}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label required">Chiều rộng (mm)</label>
            <input
              className="form-control"
              type="number"
              min="1"
              value={form.width}
              onChange={(e) => set('width', parseInt(e.target.value))}
              required
              id="tmpl-width"
            />
          </div>
          <div className="form-group">
            <label className="form-label required">Chiều cao (mm)</label>
            <input
              className="form-control"
              type="number"
              min="1"
              value={form.height}
              onChange={(e) => set('height', parseInt(e.target.value))}
              required
              id="tmpl-height"
            />
          </div>
          <div className="form-group form-full">
            <label className="form-label required">Nội dung template (HTML)</label>
            <textarea
              className="form-control"
              value={form.templateContent}
              onChange={(e) => set('templateContent', e.target.value)}
              placeholder="<div>{{materialName}} - Lot: {{lotId}}</div>"
              required
              rows={6}
              id="tmpl-content"
              style={{ resize: 'vertical', fontFamily: 'monospace', fontSize: 12 }}
            />
          </div>
        </div>
      </div>
      <div className="modal-footer">
        <button type="button" className="btn btn-outline" onClick={onClose} disabled={loading}>Huỷ</button>
        <button type="submit" className="btn btn-primary" disabled={loading} id="btn-submit-template">
          {loading ? '⏳ Đang lưu...' : (isEdit ? '💾 Cập nhật' : '➕ Tạo template')}
        </button>
      </div>
    </form>
  );
}

export default function LabelsPage() {
  const [activeTab, setActiveTab] = useState('templates');
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [fetchError, setFetchError] = useState('');

  const [showCreate, setShowCreate] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Generate label state
  const [genForm, setGenForm] = useState({ templateId: '', sourceType: 'LOT', sourceId: '' });
  const [genResult, setGenResult] = useState(null);
  const [genLoading, setGenLoading] = useState(false);
  const [genError, setGenError] = useState('');

  const flash = (msg) => { setSuccessMsg(msg); setTimeout(() => setSuccessMsg(''), 3000); };
  const setGen = (k, v) => setGenForm((f) => ({ ...f, [k]: v }));

  const loadTemplates = useCallback(async () => {
    setLoading(true);
    setFetchError('');
    try {
      const res = await labelApi.getTemplates();
      setTemplates(res.data);
    } catch (e) {
      setFetchError(e.response?.data?.message ?? 'Không thể tải danh sách template');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadTemplates(); }, [loadTemplates]);

  const handleCreate = async (form) => {
    setFormLoading(true);
    setFormError('');
    try {
      await labelApi.createTemplate(form);
      setShowCreate(false);
      flash('✅ Đã tạo template nhãn mới');
      loadTemplates();
    } catch (e) {
      setFormError(e.response?.data?.message ?? 'Tạo template thất bại');
    } finally {
      setFormLoading(false);
    }
  };

  const handleUpdate = async (form) => {
    setFormLoading(true);
    setFormError('');
    try {
      await labelApi.updateTemplate(editTarget.templateId, form);
      setEditTarget(null);
      flash('✅ Cập nhật template thành công');
      loadTemplates();
    } catch (e) {
      setFormError(e.response?.data?.message ?? 'Cập nhật thất bại');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async () => {
    setFormLoading(true);
    try {
      await labelApi.deleteTemplate(deleteTarget.templateId);
      setDeleteTarget(null);
      flash('🗑 Đã xoá template');
      loadTemplates();
    } catch (e) {
      setFormError(e.response?.data?.message ?? 'Xoá thất bại');
      setDeleteTarget(null);
    } finally {
      setFormLoading(false);
    }
  };

  const handleGenerate = async (e) => {
    e.preventDefault();
    setGenLoading(true);
    setGenError('');
    setGenResult(null);
    try {
      const res = await labelApi.generate(genForm);
      setGenResult(res.data);
    } catch (e) {
      setGenError(e.response?.data?.message ?? 'Tạo nhãn thất bại');
    } finally {
      setGenLoading(false);
    }
  };

  return (
    <>
      <div className="page-header">
        <div className="page-header-left">
          <h1>🏷️ Nhãn (Labels)</h1>
          <p>Quản lý template nhãn và tạo nhãn in</p>
        </div>
        {activeTab === 'templates' && (
          <button
            id="btn-create-template"
            className="btn btn-primary"
            onClick={() => { setFormError(''); setShowCreate(true); }}
          >
            ➕ Tạo template
          </button>
        )}
      </div>

      <div className="page-body">
        {successMsg && <div className="alert alert-success" style={{ marginBottom: 16 }}>{successMsg}</div>}

        <div className="tabs">
          <button
            className={`tab ${activeTab === 'templates' ? 'active' : ''}`}
            onClick={() => setActiveTab('templates')}
          >📋 Templates</button>
          <button
            className={`tab ${activeTab === 'generate' ? 'active' : ''}`}
            onClick={() => setActiveTab('generate')}
          >🖨️ Tạo nhãn</button>
        </div>

        {activeTab === 'templates' && (
          <div className="card">
            {loading ? (
              <div className="loading-center"><div className="spinner" /></div>
            ) : fetchError ? (
              <div style={{ padding: 20 }}><div className="alert alert-error">{fetchError}</div></div>
            ) : templates.length === 0 ? (
              <div className="empty-state">
                <div className="empty-icon">🏷️</div>
                <p>Chưa có template nào. Nhấn <strong>Tạo template</strong> để bắt đầu.</p>
              </div>
            ) : (
              <div className="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>Template ID</th>
                      <th>Tên template</th>
                      <th>Loại nhãn</th>
                      <th>Kích thước</th>
                      <th>Ngày tạo</th>
                      <th style={{ textAlign: 'right' }}>Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                    {templates.map((t) => (
                      <tr key={t.templateId}>
                        <td className="td-mono">{t.templateId}</td>
                        <td className="td-primary">{t.templateName}</td>
                        <td><span className="type-badge">{t.labelType}</span></td>
                        <td className="text-muted">{t.width} × {t.height} mm</td>
                        <td className="text-muted">
                          {t.createdDate ? new Date(t.createdDate).toLocaleDateString('vi-VN') : '—'}
                        </td>
                        <td>
                          <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end' }}>
                            <button
                              id={`btn-edit-tmpl-${t.templateId}`}
                              className="btn btn-outline btn-sm"
                              onClick={() => { setFormError(''); setEditTarget(t); }}
                            >✏ Sửa</button>
                            <button
                              id={`btn-delete-tmpl-${t.templateId}`}
                              className="btn btn-danger btn-sm"
                              onClick={() => setDeleteTarget(t)}
                            >🗑</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {activeTab === 'generate' && (
          <div className="card">
            <div className="card-header">
              <span className="card-title">🖨️ Tạo nhãn từ template</span>
            </div>
            <div style={{ padding: 20 }}>
              <form onSubmit={handleGenerate}>
                {genError && <div className="alert alert-error" style={{ marginBottom: 16 }}>⚠ {genError}</div>}
                <div className="form-grid" style={{ maxWidth: 560 }}>
                  <div className="form-group form-full">
                    <label className="form-label required">Template nhãn</label>
                    <select
                      className="form-control"
                      value={genForm.templateId}
                      onChange={(e) => setGen('templateId', e.target.value)}
                      required
                      id="gen-templateId"
                    >
                      <option value="">-- Chọn template --</option>
                      {templates.map((t) => (
                        <option key={t.templateId} value={t.templateId}>
                          {t.templateName} ({t.labelType})
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label required">Loại nguồn</label>
                    <select
                      className="form-control"
                      value={genForm.sourceType}
                      onChange={(e) => setGen('sourceType', e.target.value)}
                      required
                      id="gen-sourceType"
                    >
                      {SOURCE_TYPES.map((s) => <option key={s}>{s}</option>)}
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label required">
                      {genForm.sourceType === 'LOT' ? 'Lot ID' : 'Batch ID'}
                    </label>
                    <input
                      className="form-control"
                      value={genForm.sourceId}
                      onChange={(e) => setGen('sourceId', e.target.value)}
                      placeholder={genForm.sourceType === 'LOT' ? 'Nhập Lot ID' : 'Nhập Batch ID'}
                      required
                      id="gen-sourceId"
                    />
                  </div>
                  <div className="form-group form-full">
                    <button
                      type="submit"
                      className="btn btn-primary"
                      disabled={genLoading}
                      id="btn-generate-label"
                      style={{ alignSelf: 'flex-start' }}
                    >
                      {genLoading ? '⏳ Đang tạo nhãn...' : '🖨️ Tạo nhãn'}
                    </button>
                  </div>
                </div>
              </form>

              {genResult && (
                <div style={{ marginTop: 24 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                    <span className="detail-panel-title" style={{ marginBottom: 0 }}>
                      Xem trước nhãn: {genResult.templateName} ({genResult.labelType})
                    </span>
                    <span className="text-muted">
                      {genResult.width} × {genResult.height} mm
                      {genResult.generatedAt && ` · ${new Date(genResult.generatedAt).toLocaleString('vi-VN')}`}
                    </span>
                  </div>
                  <div
                    className="label-preview"
                    dangerouslySetInnerHTML={{ __html: genResult.renderedContent }}
                    style={{ maxWidth: `${genResult.width * 3}px` }}
                  />
                </div>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Create Template Modal */}
      {showCreate && (
        <Modal title="➕ Tạo template nhãn mới" onClose={() => setShowCreate(false)} size="modal-lg">
          <TemplateForm
            onSubmit={handleCreate}
            onClose={() => setShowCreate(false)}
            loading={formLoading}
            error={formError}
          />
        </Modal>
      )}

      {/* Edit Template Modal */}
      {editTarget && (
        <Modal title={`✏ Sửa template: ${editTarget.templateName}`} onClose={() => setEditTarget(null)} size="modal-lg">
          <TemplateForm
            initial={editTarget}
            onSubmit={handleUpdate}
            onClose={() => setEditTarget(null)}
            loading={formLoading}
            error={formError}
          />
        </Modal>
      )}

      {/* Delete Confirm Modal */}
      {deleteTarget && (
        <Modal
          title="⚠ Xác nhận xoá template"
          onClose={() => setDeleteTarget(null)}
          footer={
            <>
              <button className="btn btn-outline" onClick={() => setDeleteTarget(null)}>Huỷ</button>
              <button
                id="btn-confirm-delete-template"
                className="btn btn-danger"
                onClick={handleDelete}
                disabled={formLoading}
              >
                {formLoading ? 'Đang xoá...' : '🗑 Xoá'}
              </button>
            </>
          }
        >
          <p>Bạn có chắc muốn xoá template <strong>{deleteTarget.templateName}</strong>?</p>
          <p className="text-muted">Thao tác này không thể hoàn tác.</p>
        </Modal>
      )}
    </>
  );
}
