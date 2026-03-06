const STATUS_CONFIG = {
  Quarantine: { label: 'Quarantine', cls: 'badge-quarantine' },
  Accepted:   { label: 'Accepted',   cls: 'badge-accepted'   },
  Rejected:   { label: 'Rejected',   cls: 'badge-rejected'   },
  Depleted:   { label: 'Depleted',   cls: 'badge-depleted'   },
};

export default function StatusBadge({ status }) {
  const cfg = STATUS_CONFIG[status] ?? { label: status, cls: 'badge-depleted' };
  return (
    <span className={`badge ${cfg.cls}`}>
      <span className="badge-dot" />
      {cfg.label}
    </span>
  );
}
