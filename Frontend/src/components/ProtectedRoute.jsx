import React from 'react';
import { Navigate } from 'react-router-dom';

export default function ProtectedRoute({ role, allowed, checkedAdmin, children }) {
  
  if (!role) {
    return <Navigate to="/login" replace />;
  }

  if (allowed && !allowed.includes(role)) {
    return <Navigate to="/login" replace />;
  }

  if (role === 'ADMIN' && !checkedAdmin) {
    return <Navigate to="/authSelection" replace />; 
  }

  return children;
}
