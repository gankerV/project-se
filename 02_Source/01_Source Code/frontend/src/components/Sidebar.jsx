import { NavLink } from 'react-router-dom';

const NAV_SECTIONS = [
  {
    label: 'Material Management',
    items: [
      { to: '/materials', icon: '🧪', label: 'Vật tư (Materials)' },
      { to: '/lots',      icon: '📦', label: 'Tồn kho (Lots)' },
    ],
  },
  {
    label: 'Quality Control',
    items: [
      { to: '/qctests', icon: '🔬', label: 'Kiểm nghiệm (QC Tests)' },
    ],
  },
  {
    label: 'Production',
    items: [
      { to: '/batches', icon: '⚗️', label: 'Lô sản xuất (Batches)' },
    ],
  },
  {
    label: 'Labels & Reports',
    items: [
      { to: '/labels',    icon: '🏷️', label: 'Nhãn (Labels)' },
      { to: '/dashboard', icon: '📊', label: 'Báo cáo (Reports)' },
    ],
  },
  {
    label: 'Administration',
    items: [
      { to: '/users', icon: '👥', label: 'Người dùng (Users)' },
    ],
  },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h2>IMS</h2>
        <span>Inventory System</span>
      </div>
      <nav className="sidebar-nav">
        {NAV_SECTIONS.map((section) => (
          <div key={section.label}>
            <div className="nav-section-label">{section.label}</div>
            {section.items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              >
                <span className="nav-icon">{item.icon}</span>
                {item.label}
              </NavLink>
            ))}
          </div>
        ))}
      </nav>
    </aside>
  );
}
