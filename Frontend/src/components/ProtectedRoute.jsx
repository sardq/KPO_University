import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContent } from './AuthContent';

const ProtectedRoute = ({ children, allowed }) => {
  const { role } = useContext(AuthContent);
  const token = localStorage.getItem('token');

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (!role) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Загрузка...</span>
        </div>
      </div>
    );
  }

  if (!allowed.includes(role)) {
    if (role === "ADMIN") return <Navigate to="/authSelection" replace />;
    if (role === "STUDENT") return <Navigate to="/studentHome" replace />;
    if (role === "TEACHER") return <Navigate to="/teacherHome" replace />;

    return <Navigate to="/login" replace />;
  }
  console.log('role in context:', role);
  return children;
};

export default ProtectedRoute;