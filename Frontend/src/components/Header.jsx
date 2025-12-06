import './App.css';
import React, { useContext } from 'react';
import { useNavigate, useLocation  } from 'react-router-dom';
import { AuthContent } from './AuthContent';

export default function Header({ pageTitle, logoSrc }) {
  const navigate = useNavigate();
   const location = useLocation();
  const { role, email, setRole, setEmail } = useContext(AuthContent);
  const isAdminOn2FAPage = role === 'ADMIN' && (location.pathname === '/emailAuth' || location.pathname === '/authSelection');
  const showUserMenu = !isAdminOn2FAPage && email;
  const handleLogout = () => {
    localStorage.removeItem('token');
    
    if (setRole && setEmail) {
      setRole(null);
      setEmail(null);
    }
    
    navigate('/login');
  };
  const handleNavigate = (path) => {
    navigate(path);
  };
  // const handleProfileClick = () => {
  //   if (role === 'ADMIN' && location.pathname === '/emailAuth') {
  //     return; 
  //   }
  //    navigate('/profile');
  // };

  return (
    <header className="App-header d-flex justify-content-between align-items-center px-4">
      <div className="header-container d-flex justify-content-between align-items-center w-100">
        <div className="d-flex align-items-center">
          <img src={logoSrc} className="App-logo" alt="logo" />
          <h1 className="App-title ml-3">{pageTitle}</h1>
        </div>
         {showUserMenu && (
        <div className="user-menu d-flex align-items-center">
            <>
              <div className="user-info me-3">
                <span className="badge bg-secondary me-2">
                  {role != null}
                </span>
                <span className="text-light">{email}</span>
              </div>
              {role === 'ADMIN' && (
          <div className="admin-nav d-flex ">
            <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/userPanel')}>
              Пользователи
            </button>
            <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/groupPanel')}>
              Группы
            </button>
            <button className="btn btn-outline-light btn-sm me-2" onClick={() => handleNavigate('/disciplinePanel')}>
              Дисциплины
            </button>
          </div>
        )}
              
              
              <button 
                className="btn btn-outline-danger btn-sm"
                onClick={handleLogout}
                title="Выйти из системы"
              >
                <i className="bi bi-box-arrow-right"></i> Выход
              </button>
            </>
          
        </div>
          )}
      </div>
    </header>
  );
}