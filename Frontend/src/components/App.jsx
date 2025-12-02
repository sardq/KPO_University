import './App.css';
import logo from '../logo.svg';
import React, { useState, useEffect  } from 'react';
import { BrowserRouter as Router, useNavigate  } from 'react-router-dom';

import AppContent from './AppContent';
import Header from './Header';
import { AuthContent } from './AuthContent';

function App() {
  const [role, setRole] = useState(null);
  const [email, setEmail] = useState(null);
  const [checkedAdmin, setCheckedAdmin] = useState(false);
  return (
    <div className="App">
      <div className="app-wrapper">
        <AuthContent.Provider value={{ role, setRole, email, setEmail, checkedAdmin, setCheckedAdmin }}>
            <Header pageTitle="Электронный журнал" logoSrc={logo} />
            <AppContent />
        </AuthContent.Provider>
      </div>
    </div>
  );
}

export default App;
