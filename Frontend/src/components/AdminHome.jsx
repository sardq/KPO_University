import { useNavigate } from 'react-router-dom';

const downloadLogFile = async () => {
  try {
    const token = localStorage.getItem("token");

    if (!token) {
      alert("Пользователь не авторизован");
      return;
    }

    const response = await fetch("http://localhost:8080/api/log/download", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error("Ошибка при загрузке лога");
    }

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "application.log";
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    alert("Не удалось загрузить лог");
  }
};

const AdminPanel = () => {
  const navigate = useNavigate();

  return (
    <div className="container mt-4">
      <h1 className="text-white mb-4">Панель администратора</h1>
      
      <div className="row">
        <div className="col-12 mb-4">
          <div className="card bg-dark text-white border-light">
            <div className="card-body text-center">
              <h2 className="card-title">Добро пожаловать, администратор!</h2>
              <p className="card-text">Управление учебной системой</p>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        <div className="col-md-6 col-lg-3">
          <div className="card bg-dark text-white h-100">
            <div className="card-body text-center d-flex flex-column">
              <div className="mb-3">
                <i className="bi bi-person-badge fs-1"></i>
              </div>
              <h4 className="card-title">Пользователи</h4>
              <p className="card-text flex-grow-1">
                Создание, редактирование и управление пользователями системы
              </p>
              <button 
                className="btn btn-warning w-100 mt-3" 
                onClick={() => navigate('/userPanel')}
              >
                <i className="bi bi-people me-2"></i>Перейти
              </button>
            </div>
          </div>
        </div>

        <div className="col-md-6 col-lg-3">
          <div className="card bg-dark text-white h-100">
            <div className="card-body text-center d-flex flex-column">
              <div className="mb-3">
                <i className="bi bi-journal-text fs-1 "></i>
              </div>
              <h4 className="card-title">Дисциплины</h4>
              <p className="card-text flex-grow-1">
                Управление учебными дисциплинами и привязка групп
              </p>
              <button 
                className="btn btn-warning w-100 mt-3" 
                onClick={() => navigate('/disciplinePanel')}
              >
                <i className="bi bi-journal-bookmark me-2"></i>Перейти
              </button>
            </div>
          </div>
        </div>

        <div className="col-md-6 col-lg-3">
          <div className="card bg-dark text-white h-100">
            <div className="card-body text-center d-flex flex-column">
              <div className="mb-3">
                <i className="bi bi-people-fill fs-1"></i>
              </div>
              <h4 className="card-title">Группы</h4>
              <p className="card-text flex-grow-1">
                Управление учебными группами и привязка студентов
              </p>
              <button 
                className="btn btn-warning w-100 mt-3" 
                onClick={() => navigate('/groupPanel')}
              >
                <i className="bi bi-person-plus me-2"></i>Перейти
              </button>
            </div>
          </div>
        </div>

        <div className="col-md-6 col-lg-3">
          <div className="card bg-dark text-white h-100">
            <div className="card-body text-center d-flex flex-column">
              <div className="mb-3">
                <i className="bi bi-file-earmark-text fs-1"></i>
              </div>
              <h4 className="card-title">Логи системы</h4>
              <p className="card-text flex-grow-1">
                Скачивание файла логов для анализа и отладки
              </p>
              <button 
                className="btn btn-warning w-100 mt-3" 
                onClick={downloadLogFile}
              >
                <i className="bi bi-download me-2"></i>Скачать логи
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminPanel;