import './App.css'
import React, { useState, useEffect, useCallback, useContext } from "react";
import MyToast from "./MyToast";
import * as exerciseActions from "./service/exerciseActions";
import * as gradeActions from "./service/gradeActions";
import * as disciplineActions from "./service/disciplineActions";
import * as groupActions from "./service/groupActions";
import * as userActions from "./service/userActions";
import { AuthContent } from "./AuthContent";
import { Card, Table, ButtonGroup, Button, Modal, Form } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStepBackward, faFastBackward, faStepForward, faFastForward } from "@fortawesome/free-solid-svg-icons";

const EXERCISES_PER_PAGE = 5;

const StudentJournal = () => {
  const { email } = useContext(AuthContent);

  const [toast, setToast] = useState({ show: false, message: "", type: "" });
  const [userCurrent, setUserCurrent] = useState(null);
  const [userGroup, setUserGroup] = useState(null);
  const [disciplines, setDisciplines] = useState([]);
  const [selectedDiscipline, setSelectedDiscipline] = useState("");

  const [exercises, setExercises] = useState([]);
  const [grades, setGrades] = useState([]);
  const [students, setStudents] = useState([]);

  const [loading, setLoading] = useState(true);

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [showGradeModal, setShowGradeModal] = useState(false);
  const [selectedCell, setSelectedCell] = useState(null);
  const [currentExercise, setCurrentExercise] = useState(null);

  const showToastMessage = (message, type = "danger") => {
    setToast({ show: true, message, type });
    setTimeout(() => setToast({ show: false, message: "", type: "" }), 3000);
  };

  useEffect(() => {
    const loadUserData = async () => {
      try {
        const u = await userActions.getMe();
        setUserCurrent(u);
        const groups = await groupActions.getAllGroups(0);
        const group = groups.find(g => g.studentIds?.includes(u.id));
        setUserGroup(group || null);
      } catch {
        showToastMessage("Ошибка загрузки пользователя/группы");
      }
      setLoading(false);
    };
    loadUserData();
  }, []);

  useEffect(() => {
    if (!userGroup) return;
    const loadDisciplines = async () => {
      try {
        const res = await disciplineActions.GetDisciplinesByGroup(userGroup.id);
        setDisciplines(res);
      } catch {
        showToastMessage("Ошибка загрузки дисциплин");
      }
    };
    loadDisciplines();
  }, [userGroup]);

  const loadPage = useCallback(async (page) => {
  if (!selectedDiscipline || !userGroup) return;
  try {
    const data = await exerciseActions.getByDisciplineAndGroupPage(
      selectedDiscipline,
      userGroup.id,
      page - 1,
      EXERCISES_PER_PAGE
    );
    console.log(data);
    if (!data || !Array.isArray(data.content) || data.totalElements === 0) {
      setExercises([]);
      setCurrentPage(1); 
      setTotalPages(1);  
      setTotalElements(0);
      setGrades([]);
      return;
    }

    setExercises(data.content);
    setCurrentPage(data.number + 1);
    setTotalPages(data.totalPages > 0 ? data.totalPages : 1);
    setTotalElements(data.totalElements);

    const st = await groupActions.getGroupStudents(userGroup.id);
    setStudents(st.sort((a, b) => a.firstName.localeCompare(b.firstName)));

    const gr = await gradeActions.getByGroupAndDiscipline(userGroup.id, selectedDiscipline);
    setGrades(gr);

  } catch {
    showToastMessage("Ошибка загрузки данных журнала");
    setExercises([]);
    setCurrentPage(1);
    setTotalPages(1);
    setTotalElements(0);
    setStudents([]);
    setGrades([]);
  }
}, [selectedDiscipline, userGroup]);


  useEffect(() => {
    loadPage(currentPage);
  }, [currentPage, loadPage]);

  useEffect(() => {
    if (selectedDiscipline) setCurrentPage(1);
  }, [selectedDiscipline]);

  const getStudentGrade = (studentId, exerciseId) => {
    const g = grades.find(gr => gr.studentId === studentId && gr.exerciseId === exerciseId);
    return g ? g.value : "-";
  };

  const handleCellClick = (studentId, exercise) => {
    const g = grades.find(gr => gr.studentId === studentId && gr.exerciseId === exercise.id);
    setSelectedCell({
      studentId,
      grade: g ? g.value : "-",
      comment: g ? g.description : ""
    });
    setCurrentExercise(exercise);
    setShowGradeModal(true);
  };

  const handleDisciplineChange = (e) => {
    setSelectedDiscipline(e.target.value);
    setCurrentPage(1);
  };

  const paginationActions = {
    firstPage: () => loadPage(1),
    prevPage: () => loadPage(Math.max(currentPage - 1, 1)),
    nextPage: () => loadPage(Math.min(currentPage + 1, totalPages)),
    lastPage: () => loadPage(totalPages),
  };

  if (loading) return <p className="text-white">Загрузка...</p>;

  const isFirstPage = currentPage <= 1 || totalPages === 0;
  const isLastPage = currentPage >= totalPages || totalPages === 0;

  return (
    <div className="container mt-4">
      <MyToast show={toast.show} message={toast.message} type={toast.type} />

      <h1 className="text-white mb-4">Электронный журнал студента</h1>

      <Card className="bg-dark text-white mb-3">
        <Card.Body>
          <h4>Вы вошли как: {email}</h4>
          {userGroup ? (
            <>
              <p>Ваши данные: <strong>{userCurrent.firstName} {userCurrent.lastName}</strong></p>
              <p>Ваша группа: <strong>{userGroup.name}</strong></p>
            </>
          ) : (
            <p className="text-warning">Вы не прикреплены к группе</p>
          )}
        </Card.Body>
      </Card>

      <Card className="bg-dark text-white mb-3">
        <Card.Body>
          <h4>Выбор дисциплины</h4>
          <Form.Select value={selectedDiscipline} onChange={handleDisciplineChange} className="bg-secondary text-white">
            <option value="">-- Выберите дисциплину --</option>
            {disciplines.map(d => (
              <option key={d.id} value={d.id}>{d.name}</option>
            ))}
          </Form.Select>
        </Card.Body>
      </Card>

      {selectedDiscipline && (
        <Card className="bg-dark text-white mb-4">
          <Card.Body>
            <h4>Журнал оценок группы</h4>
            <div style={{ maxHeight: "70vh", overflow: "auto", border: "1px solid #444", borderRadius: "6px" }}>
              <Table bordered hover striped variant="dark">
                <thead>
                  <tr>
                    <th>ФИО</th>
                    {exercises.map(ex => (
                      <th key={ex.id}>
                        {new Date(ex.date).toLocaleDateString()}<br/>
                        <small>{new Date(ex.date).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}</small>
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {students.map(st => (
                    <tr key={st.id}>
                      <td>{st.firstName} {st.lastName}</td>
                      {exercises.map(ex => (
                        <td key={ex.id} style={{ textAlign: "center", cursor: "pointer" }}
                            onClick={() => handleCellClick(st.id, ex)}>
                          {getStudentGrade(st.id, ex.id)}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>

            <div className="d-flex justify-content-between mt-2">
              <ButtonGroup size="sm">
                <Button variant="outline-info" disabled={isFirstPage} onClick={paginationActions.firstPage}>
                  <FontAwesomeIcon icon={faFastBackward} />
                </Button>
                <Button variant="outline-info" disabled={isFirstPage} onClick={paginationActions.prevPage}>
                  <FontAwesomeIcon icon={faStepBackward} />
                </Button>
                <Button variant="outline-info" disabled={isLastPage} onClick={paginationActions.nextPage}>
                  <FontAwesomeIcon icon={faStepForward} />
                </Button>
                <Button variant="outline-info" disabled={isLastPage} onClick={paginationActions.lastPage}>
                  <FontAwesomeIcon icon={faFastForward} />
                </Button>
              </ButtonGroup>
              <div className="text-white ms-2">
                {totalElements > 0
                  ? `Страница ${currentPage} из ${totalPages} (Всего: ${totalElements})`
                  : "Нет занятий"}
              </div>
            </div>
          </Card.Body>
        </Card>
      )}

      <Modal show={showGradeModal} onHide={() => setShowGradeModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Подробности занятия</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {currentExercise && (
            <>
              <p><strong>Дата:</strong> {new Date(currentExercise.date).toLocaleString()}</p>
              <p><strong>Описание:</strong> {currentExercise.description}</p>
              {selectedCell && (
                <>
                  <p><strong>Оценка:</strong> {selectedCell.grade}</p>
                  {selectedCell.comment && <p><strong>Комментарий:</strong> {selectedCell.comment}</p>}
                </>
              )}
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowGradeModal(false)}>Закрыть</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default StudentJournal;
