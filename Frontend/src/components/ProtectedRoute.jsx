import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, role, allowed }) => {
  const token = localStorage.getItem('token');
  
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  
  if (role === null) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Загрузка...</span>
        </div>
      </div>
    );
  }
  
  if (!allowed.includes(role)) {
    if (role === 'ADMIN') {
      return <Navigate to="/authSelection" replace />;
    } else if (role === 'STUDENT') {
      return <Navigate to="/studentHome" replace />;
    } else if (role === 'TEACHER') {
      return <Navigate to="/teacherHome" replace />;
    }
    
    return <Navigate to="/login" replace />;
  }
  
  return children;
};

export default ProtectedRoute;