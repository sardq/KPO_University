import './App.css';
import React, { useContext } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { AuthContent } from './AuthContent';

export default function Header({ pageTitle, logoSrc }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { role, email, setRole, setEmail, setIs2FAVerified } = useContext(AuthContent);

  const hideUserMenuPages = ['/login', '/resetPassword'];
  const hideUserMenu = hideUserMenuPages.includes(location.pathname);

  const isAdminOn2FAPage = role === 'ADMIN' && (location.pathname === '/emailAuth' || location.pathname === '/authSelection');
  const showUserMenu = !isAdminOn2FAPage && !hideUserMenu && localStorage.getItem("token");

  const handleLogout = () => {
    localStorage.removeItem('token');

    if (setRole && setEmail) {
      setRole(null);
      setEmail(null);
    }
  if (setIs2FAVerified) setIs2FAVerified(false);
    navigate('/login');
  };

  const handleNavigate = (path) => {
    navigate(path);
  };
  if (localStorage.getItem("token") && !role) return null;
  return (
    <header className="App-header d-flex justify-content-between align-items-center px-4">
      <div className="header-container d-flex justify-content-between align-items-center w-100">
        <div className="d-flex align-items-center">
          <img src={logoSrc} className="App-logo" alt="logo" />
          <h1 className="App-title ml-3">{pageTitle}</h1>
        </div>

        {showUserMenu && (
          <div className="user-menu d-flex align-items-center">
            <div className="user-info me-3">
              <span className="text-light">{email}</span>
            </div>
            {role === 'STUDENT' && (
              <div className="admin-nav d-flex me-2">
                <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/userHome')}>Главная</button>
                <button className="btn btn-outline-light btn-sm" onClick={() => handleNavigate('/studentJournalPanel')}>Журнал</button>
              </div>
            )}
            {role === 'ADMIN' && (
              <div className="admin-nav d-flex me-2">
                <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/adminHome')}>Главная</button>
                <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/userPanel')}>Пользователи</button>
                <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/groupPanel')}>Группы</button>
                <button className="btn btn-outline-light btn-sm" onClick={() => handleNavigate('/disciplinePanel')}>Дисциплины</button>
              </div>
            )}

            {role === 'TEACHER' && (
              <div className="teacher-nav d-flex me-2">
                <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/teacherHome')}>Главная</button>
                <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/journalPanel')}>Журнал</button>
                <button className="btn btn-outline-light btn-sm" onClick={() => handleNavigate('/statisticPanel')}>Статистика</button>
              </div>
            )}
            <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/profile')}>Профиль</button>
            <button 
              className="btn btn-outline-danger btn-sm"
              onClick={handleLogout}
              title="Выйти из системы"
            >
              <i className="bi bi-box-arrow-right"></i> Выход
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
