import React, { useContext, useState, useEffect } from 'react';
import { request, setAuthHeader } from '../helpers/axios_helper';
import { jwtDecode } from 'jwt-decode';
import { Routes, Route, useNavigate, Navigate } from 'react-router-dom';

import { AuthContent } from './AuthContent';
import LoginForm from './LoginForm';
import ProtectedRoute from "./ProtectedRoute";
import AuthSelection from './AuthSelection';
import AdminHome from './AdminHome';
import EmailAuth from './EmailAuth';
import StudentHome from './StudentHome';
import TeacherHome from './TeacherHome';
import ResetPasswordForm from './ResetPasswordForm';
import DisciplinePanel from './DisciplinePanel';
import GroupPanel from './GroupPanel';
import UserPanel from './UserPanel';

const decodeToken = (token) => {
  try {
    return jwtDecode(token);
  } catch (error) {
    console.error('Ошибка декодирования токена:', error);
    return null;
  }
};

const getRoleFromToken = (token) => {
  const decoded = decodeToken(token);
  if (decoded && decoded.role) {
    return decoded.role;
  }
  return null;
};

const getLoginFromToken = (token) => {
  const decoded = decodeToken(token);
  if (decoded && decoded.sub) {
    return decoded.sub;
  }
  return null;
};

export default function AppContent() {
  const { role, setRole, setEmail } = useContext(AuthContent);
  const [showToast, setShowToast] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    console.log('AppContent mounted');
    const token = localStorage.getItem('token');
    
    if (token) {
      setAuthHeader(token);
      
      const roleFromToken = getRoleFromToken(token);
      const loginFromToken = getLoginFromToken(token); 
      
      console.log('Данные из токена:', { roleFromToken, loginFromToken });
      
      if (roleFromToken && loginFromToken) {
        setRole(roleFromToken);
        setIsLoading(false);
        
         if (roleFromToken === 'STUDENT') {
          navigate('/studentHome');
        } else if (roleFromToken === 'TEACHER') {
          navigate('/teacherHome');
        }
      } else {
        request('GET', '/api/user/me')
          .then(response => {
            const userData = response.data;
            console.log('User data loaded from server:', userData);
            setRole(userData.role);
            setEmail(userData.email); 
            setIsLoading(false);
          })
          .catch(error => {
            console.error('Failed to load user data:', error);
            localStorage.removeItem('token');
            setAuthHeader(null);
            setRole(null);
            setEmail(null);
            setIsLoading(false);
          });
      }
    } else {
      setIsLoading(false);
    }
  }, [navigate, setRole, setEmail]);

  const onLogin = (e, login, password) => {
    e.preventDefault();
    console.log('Login attempt for:', login);
    
    request('POST', '/login', { login, password })
      .then((response) => {
        const data = response.data;
        console.log('Login successful:', data);
        
        const token = data.token;
        localStorage.setItem('token', token);
        setAuthHeader(token);
        
        const roleFromToken = getRoleFromToken(token);
        
        setRole(roleFromToken || data.role);
        setEmail(data.email);
        
        const finalRole = roleFromToken || data.role;
        
        if (finalRole === 'ADMIN') {
          navigate('/authSelection'); 

        } else if (finalRole === 'STUDENT') {
          navigate('/studentHome');
        } else if (finalRole === 'TEACHER') {
          navigate('/teacherHome');
        }
      })
      .catch((error) => {
        console.error('Login failed:', error);
        setShowToast(true);
        setTimeout(() => setShowToast(false), 3000);
        setAuthHeader(null);
      });
  };

  const handleAdminVerified = () => {
    navigate('/adminHome');
  };

  if (isLoading) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Загрузка...</span>
        </div>
      </div>
    );
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={<LoginForm onLogin={onLogin} showToast={showToast} setShowToast={setShowToast} />}
      />
      <Route path="/resetPassword" element={<ResetPasswordForm />} />
      
      <Route
        path="/emailAuth"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]}>
            <EmailAuth onVerified={handleAdminVerified} />
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/adminHome"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]}>
            <AdminHome />
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/authSelection"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]}>
            <AuthSelection />
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/studentHome"
        element={
          <ProtectedRoute role={role} allowed={["STUDENT"]}>
            <StudentHome />
          </ProtectedRoute>
        }
      />
      <Route
        path="/teacherHome"
        element={
          <ProtectedRoute role={role} allowed={["TEACHER"]}>
            <TeacherHome />
          </ProtectedRoute>
        }
      />
      <Route
        path="/disciplinePanel"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]}>
            <DisciplinePanel />
          </ProtectedRoute>
        }
      />
      <Route
        path="/groupPanel"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]}>
            <GroupPanel />
          </ProtectedRoute>
        }
      />
      <Route
        path="/userPanel"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]}>
            <UserPanel />
          </ProtectedRoute>
        }
      />
      
      <Route
        path="*"
        element={
          !localStorage.getItem('token') ? (
            <Navigate to="/login" replace /> 
          ) : role === "ADMIN" ? (
            <Navigate to="/authSelect" replace /> 
          ) : role === "STUDENT" ? (
            <Navigate to="/studentHome" replace /> 
          ) : role === "TEACHER" ? (
            <Navigate to="/teacherHome" replace /> 
          ) : (
            <Navigate to="/login" replace /> 
          )
        }
      />
    </Routes>
  );
}