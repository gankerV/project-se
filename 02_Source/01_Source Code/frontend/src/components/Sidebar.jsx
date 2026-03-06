import { NavLink, useLocation } from 'react-router-dom';

const navItems = [
  { to: '/materials', icon: '🧪', label: 'Vật tư (Materials)' },
  { to: '/lots',      icon: '📦', label: 'Tồn kho (Lots)' },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h2>IMS</h2>
        <span>Inventory System</span>
      </div>
      <nav className="sidebar-nav">
        <div className="nav-section-label">Material Management</div>
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          >
            <span className="nav-icon">{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
