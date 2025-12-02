import './App.css';
import React, { useState } from 'react';
import classNames from 'classnames';
import MyToast from './MyToast';
import { Link } from 'react-router-dom'; 

const LoginForm = ({ onLogin, showToast, setShowToast }) => {

  const [formData, setFormData] = useState({
    login: '',
    password: ''
  });
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    setErrors(prev => ({
      ...prev,
      [name]: ''
    }));
  };

  const validate = () => {
    const newErrors = {};

    if (!formData.login.trim()) {
      newErrors.login = 'Введите логин';
    } else if (!/^[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)*$/.test(formData.login)) {
      newErrors.login = 'Неверный формат login';
    }

    if (!formData.password.trim()) {
      newErrors.password = 'Введите пароль';
    } else if (formData.password.length < 5) {
      newErrors.password = 'Пароль должен быть не менее 5 символов';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleLoginSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    onLogin(e, formData.login, formData.password);
  };

  return (
    <div className="row justify-content-center text-white mt-5">
      <div className="col-4">
        <div style={{ display: showToast ? 'block' : 'none' }}>
          <MyToast
            show={showToast}
            header={'Ошибка'}
            message={'Пользователь с такими данными не найден.'}
            type={'danger'}
          />
        </div>
        <div className="text-center">
          <h1 >Авторизация</h1>
        </div>
        <form onSubmit={handleLoginSubmit}>
          <div className="form-outline mb-4">
            <label className="form-label" htmlFor="loginLogin">Логин</label>
            <input
              type="login"
              id="loginLogin"
              name="login"
              className={classNames("form-control", { "is-invalid": errors.login })}
              value={formData.login}
              onChange={handleChange}
            />
            {errors.login && <div className="invalid-feedback">{errors.login}</div>}
          </div>

          <div className="form-outline mb-4">
            <label className="form-label" htmlFor="loginPassword">Пароль</label>
            <input
              type="password"
              id="loginPassword"
              name="password"
              className={classNames("form-control", { "is-invalid": errors.password })}
              value={formData.password}
              onChange={handleChange}
            />
            {errors.password && <div className="invalid-feedback">{errors.password}</div>}
          </div>

        <div className="text-center">
            <button type="submit" className="btn btn-secondary btn-lg btn-block mb-4">
              Войти
            </button>
          </div>

          <div className="text-center">
            <Link to="/resetPassword" className="btn btn-link d-block mb-2 " style={{ fontSize: '18px' }}>
              Забыли пароль?
            </Link>
          </div>
          </form>
      </div>
    </div>
  );
};

export default LoginForm;
