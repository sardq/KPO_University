import React, { useContext, useState } from 'react';
import { request, setAuthHeader } from '../helpers/axios_helper';
import axios from 'axios';
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

export default function AppContent() {
  const { role, setRole, checkedAdmin, setCheckedAdmin, setEmail } = useContext(AuthContent);
  const [showToast, setShowToast] = useState(false);
  const navigate = useNavigate();

  const onLogin = (e, login, password) => {
    e.preventDefault();
    request('POST', '/login', { login, password })
      .then((response) => {
        const data = response.data;
        const token = data.token;
        localStorage.setItem('token', token);
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        setAuthHeader(token);
        const userRole = data.role;
        setRole(userRole);
        setEmail(data.email);
        console.log(response);
        if (userRole === 'ADMIN') {
          setCheckedAdmin(false);
          navigate('/authSelection');
        } else if (userRole === 'STUDENT') {
          navigate('/studentHome');
        } else {
          navigate('/teacherHome');
        }
      })
      .catch((error) => {
        setShowToast(true);
        setTimeout(() => setShowToast(false), 3000);
        setAuthHeader(null);
      });
  };

  return (
    <Routes>
      <Route
        path="/login"
        element={<LoginForm onLogin={onLogin} showToast={showToast} setShowToast={setShowToast} />}
      />
      <Route path="/resetPassword" element={<ResetPasswordForm />} />
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
        path="/authSelection"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]} checkedAdmin={true}>
            <AuthSelection />
          </ProtectedRoute>
        }
      />
      <Route
        path="/emailAuth"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]} checkedAdmin={true}>
            <EmailAuth />
          </ProtectedRoute>
        }
      />
      <Route
        path="/adminHome"
        element={
          <ProtectedRoute role={role} allowed={["ADMIN"]} checkedAdmin={checkedAdmin}>
            <AdminHome />
          </ProtectedRoute>
        }
      />
      <Route
        path="*"
        element={
          !localStorage.getItem('token') ? (
            <Navigate to="/login" replace /> 
          ) : role === "ADMIN" && checkedAdmin ? (
            <Navigate to="/adminHome" replace /> 
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
    