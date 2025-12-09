import { useNavigate } from 'react-router-dom';



const TeacherWelcome = () => {
  const navigate = useNavigate();

  return (
    <div className="container mt-4">
      <h1 className="text-white mb-4">Главная преподавателя</h1>

      <div className="row">
        <div className="col-12 mb-4">
          <div className="card bg-dark text-white border-light">
            <div className="card-body text-center">
              <h2 className="card-title">Добро пожаловать, преподаватель!</h2>
              <p className="card-text">Управление электронным журналом</p>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">

        <div className="col-md-6 col-lg-6">
          <div className="card bg-dark text-white h-100 shadow">
            <div className="card-body text-center d-flex flex-column">
              <div className="mb-3">
                <i className="bi bi-journal-bookmark fs-1"></i>
              </div>
              <h4 className="card-title">Электронный журнал</h4>
              <p className="card-text flex-grow-1">
                Работа с электронным журналом
              </p>
              <button 
                className="btn btn-warning w-100 mt-3" 
                onClick={() => navigate('/journalPanel')}
              >
                <i className="bi bi-arrow-right-circle me-2"></i>
                Перейти
              </button>
            </div>
          </div>
        </div>

        <div className="col-md-6 col-lg-6">
          <div className="card bg-dark text-white h-100 shadow">
            <div className="card-body text-center d-flex flex-column">
              <div className="mb-3">
                <i className="bi bi-bar-chart-line fs-1"></i>
              </div>
              <h4 className="card-title">Статистика</h4>
              <p className="card-text flex-grow-1">
                Получение статистики и отчета
              </p>
              <button 
                className="btn btn-warning w-100 mt-3" 
                onClick={() => navigate('/statisticPanel')}
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

export default TeacherWelcome;
