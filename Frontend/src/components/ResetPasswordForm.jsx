import React, { useState } from 'react';
import { request } from '../helpers/axios_helper';
import MyToast from './MyToast';
import { useNavigate } from 'react-router-dom';  

const ResetPasswordForm = () => {
  const [email, setEmail] = useState('');
  const [errors, setErrors] = useState({});
  const [isSent, setIsSent] = useState(false);
  const navigate = useNavigate();
  const handleChange = (e) => {
    setEmail(e.target.value);
    setErrors({});
  };

  const validate = () => {
    const newErrors = {};

    if (!email.trim()) {
      newErrors.email = 'Введите email для восстановления пароля';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = 'Неверный формат email';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleResetSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    request('POST', '/reset-password', {email})
      .then(() => {
        setIsSent(true);
        navigate('/login');
      })
      .catch(() => {
        console.log(email);
        setErrors({ email: 'Ошибка при отправке запроса.' });
      });
  };

  return (
    <div className="row justify-content-center text-white mt-5">
      <div className="col-4">
        <h3 className="text-center">Восстановление пароля</h3>

        {isSent && (
          <MyToast
            show={true}
            message="Новый пароль отправлен на вашу почту."
            onClose={() => setIsSent(false)}
          />
        )}

        <form onSubmit={handleResetSubmit}>
          <div className="form-outline mb-4">
            <label className="form-label" htmlFor="resetPasswordEmail">
              Электронная почта
            </label>
            <input
              type="email"
              id="resetPasswordEmail"
              name="email"
              className={`form-control ${errors.email ? 'is-invalid' : ''}`}
              value={email}
              onChange={handleChange}
            />
            {errors.email && <div className="invalid-feedback">{errors.email}</div>}
          </div>

          <div className="d-flex justify-content-between">
            <button type="submit" className="btn btn-primary btn-lg btn-block mb-4">
              Отправить ссылку
            </button>

            <button
              className="btn btn-primary btn-lg btn-block mb-4"
              onClick={() => navigate('/login')} 
            >
              Назад к логину
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ResetPasswordForm;
