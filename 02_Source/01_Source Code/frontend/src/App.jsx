import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import MaterialsPage from './pages/MaterialsPage';
import LotsPage from './pages/LotsPage';

export default function App() {
  return (
    <BrowserRouter>
      <div className="app-layout">
        <Sidebar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Navigate to="/materials" replace />} />
            <Route path="/materials" element={<MaterialsPage />} />
            <Route path="/lots" element={<LotsPage />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}
