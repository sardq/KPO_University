import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContent } from "./AuthContent";

const StudentWelcome = () => {
  const navigate = useNavigate();
  const { email, setRole } = useContext(AuthContent);

  setRole("STUDENT");

  return (
    <div className="container mt-4">
      <h1 className="text-white mb-4">Личный кабинет студента</h1>

      <div className="row">
        <div className="col-12 mb-4">
          <div className="card bg-dark text-white border-light">
            <div className="card-body text-center">
              <h2 className="card-title">Добро пожаловать, {email}!</h2>
              <p className="card-text">Ваш электронный журнал</p>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4 justify-content-center">
        <div className="col-md-6 col-lg-6">
          <div className="card bg-dark text-white h-100 shadow">
            <div className="card-body text-center d-flex flex-column">
              <div className="mb-3">
                <i className="bi bi-journal-text fs-1"></i>
              </div>
              <h4 className="card-title">Электронный журнал</h4>
              <p className="card-text flex-grow-1">
                Просмотр оценок, посещаемости и успеваемости
              </p>
              <button
                className="btn btn-warning w-100 mt-3"
                onClick={() => navigate("/studentJournalPanel")}
              >
                <i className="bi bi-arrow-right-circle me-2"></i>
                Перейти
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentWelcome;
