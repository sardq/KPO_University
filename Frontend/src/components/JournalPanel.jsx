import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from 'react-router-dom';
import { AuthContent } from "./AuthContent";
import { Card, Table, Button, Form, Modal, Alert } from "react-bootstrap";
import * as disciplineActions from "./service/disciplineActions";
import * as groupActions from "./service/groupActions";
import * as exerciseActions from "./service/exerciseActions";

const JournalPanel = () => {
  const { role } = useContext(AuthContent);
  const navigate = useNavigate();

  const [disciplines, setDisciplines] = useState([]);
  const [groups, setGroups] = useState([]);
  const [students, setStudents] = useState([]);
  const [exercises, setExercises] = useState([]);

  const [selectedDiscipline, setSelectedDiscipline] = useState("");
  const [selectedGroup, setSelectedGroup] = useState("");
  const [currentExercise, setCurrentExercise] = useState(null);

  const [showModal, setShowModal] = useState(false);
  const [exerciseForm, setExerciseForm] = useState({ date: "", description: "" });
  const [formError, setFormError] = useState("");

  // Загрузка дисциплин
  useEffect(() => {
    if (role !== "TEACHER") navigate("/teacherHome");

    const loadDisciplines = async () => {
      const data = await disciplineActions.getAllDisciplines();
      setDisciplines(data);
    };
    loadDisciplines();
  }, [role, navigate]);

  // Загрузка групп при выборе дисциплины
  useEffect(() => {
    if (!selectedDiscipline) {
      setGroups([]);
      setSelectedGroup("");
      setStudents([]);
      setExercises([]);
      return;
    }

    const loadGroups = async () => {
      const data = await disciplineActions.getDisciplineGroups(selectedDiscipline);
      setGroups(Array.isArray(data) ? data : Object.values(data));
      setSelectedGroup("");
      setStudents([]);
      setExercises([]);
    };
    loadGroups();
  }, [selectedDiscipline]);

  // Загрузка студентов и занятий при выборе группы
  useEffect(() => {
    if (!selectedGroup || !selectedDiscipline) {
      setStudents([]);
      setExercises([]);
      return;
    }

    const loadStudents = async () => {
      const data = await groupActions.getGroupStudents(selectedGroup);
      setStudents(data.sort((a, b) => a.lastName.localeCompare(b.lastName)));
    };

    const loadExercises = async () => {
      const data = await exerciseActions.getByDisciplineAndGroup(selectedDiscipline, selectedGroup);
      // Сортировка по дате и времени
      const sorted = data.sort((a, b) => new Date(a.date) - new Date(b.date));
      setExercises(sorted);
    };

    loadStudents();
    loadExercises();
  }, [selectedGroup, selectedDiscipline]);

  // Открытие модалки для создания нового занятия
  const handleCreateExercise = () => {
    setCurrentExercise(null);
    setExerciseForm({ date: "", description: "" });
    setFormError("");
    setShowModal(true);
  };

  // Открытие модалки для редактирования существующего занятия
  const openModal = (exercise) => {
    setCurrentExercise(exercise);
    setExerciseForm({
      id: exercise.id,
      date: exercise.date.slice(0,16), // для input datetime-local
      description: exercise.description,
    });
    setFormError("");
    setShowModal(true);
  };

  // Сохранение или обновление занятия
  const handleSaveExercise = async () => {
    setFormError("");

    if (!exerciseForm.date) {
      setFormError("Укажите дату и время занятия");
      return;
    }

    const date = new Date(exerciseForm.date);
    const now = new Date();

    if (isNaN(date.getTime())) {
      setFormError("Неверный формат даты/времени");
      return;
    }

    if (date > now) {
      setFormError("Нельзя выбрать дату и время позже текущего");
      return;
    }

    const hours = date.getHours();
    const day = date.getDay();
    if (day === 0) {
      setFormError("Нельзя создавать занятие в воскресенье");
      return;
    }
    if (hours < 8 || hours > 20) {
      setFormError("Время занятия должно быть с 08:00 до 20:00");
      return;
    }

    try {
      let updatedExercise;
      if (currentExercise) {
  updatedExercise = await exerciseActions.updateExercise(currentExercise.id, {
    ...exerciseForm,
    disciplineId: currentExercise.disciplineId || selectedDiscipline,
    groupId: currentExercise.groupId || selectedGroup,
  });
  setExercises(exercises
    .map(e => e.id === updatedExercise.id ? updatedExercise : e)
    .sort((a, b) => new Date(a.date) - new Date(b.date))
  );
} else {
  updatedExercise = await exerciseActions.saveExercise({
    ...exerciseForm,
    disciplineId: selectedDiscipline,
    groupId: selectedGroup,
  });
  setExercises([...exercises, updatedExercise]
    .sort((a, b) => new Date(a.date) - new Date(b.date))
  );
}
      setShowModal(false);
    } catch (err) {
      setFormError("Ошибка при сохранении занятия");
      console.error(err);
    }
  };

  // Удаление занятия
  const handleDeleteExercise = async () => {
    if (!currentExercise) return;

    try {
      await exerciseActions.deleteExercise(currentExercise.id);
      setExercises(exercises.filter(e => e.id !== currentExercise.id));
      setShowModal(false);
    } catch (err) {
      setFormError("Ошибка при удалении занятия");
      console.error(err);
    }
  };

  return (
    <div className="main-content">
      <Card className="border border-dark bg-dark text-white">
        <Card.Header className="bg-secondary text-white">
          <h4>Журнал занятий</h4>
        </Card.Header>
        <Card.Body>
          <div className="row mb-3">
            <div className="col-6">
              <Form.Select
                className="bg-dark text-white"
                value={selectedDiscipline}
                onChange={e => setSelectedDiscipline(e.target.value)}
              >
                <option value="">Выберите дисциплину</option>
                {disciplines.map(d => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </Form.Select>
            </div>
            <div className="col-6">
              <Form.Select
                className="bg-dark text-white"
                value={selectedGroup}
                onChange={e => setSelectedGroup(e.target.value)}
                disabled={!selectedDiscipline}
              >
                <option value="">Выберите группу</option>
                {groups.map(g => (
                  <option key={g.id} value={g.id}>{g.name}</option>
                ))}
              </Form.Select>
            </div>
          </div>

          {selectedDiscipline && selectedGroup && students.length > 0 && (
            <>
              <Button className="mb-3" variant="success" onClick={handleCreateExercise}>
                Создать занятие
              </Button>

              <Table bordered hover striped variant="dark">
                <thead>
                  <tr>
                    <th>ФИО</th>
                    {exercises.map(e => (
                      <th key={e.id} style={{ cursor: "pointer" }} onClick={() => openModal(e)}>
                        {new Date(e.date).toLocaleDateString()}
                        <br />
                        <small>{new Date(e.date).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</small>
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {students.map(st => (
                    <tr key={st.id}>
                      <td>{st.lastName} {st.firstName}</td>
                      {exercises.map(ex => (
                        <td key={ex.id}>-</td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </Table>
            </>
          )}
        </Card.Body>
      </Card>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{currentExercise ? "Редактировать занятие" : "Создать занятие"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {formError && <Alert variant="danger">{formError}</Alert>}
          <Form.Group className="mb-3">
            <Form.Label>Дата и время</Form.Label>
            <Form.Control
              type="datetime-local"
              value={exerciseForm.date}
              onChange={e => setExerciseForm({ ...exerciseForm, date: e.target.value })}
            />
          </Form.Group>
          <Form.Group>
            <Form.Label>Описание</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              value={exerciseForm.description}
              onChange={e => setExerciseForm({ ...exerciseForm, description: e.target.value })}
            />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          {currentExercise && (
            <Button variant="danger" onClick={handleDeleteExercise}>
              Удалить
            </Button>
          )}
          <Button variant="secondary" onClick={() => setShowModal(false)}>Отмена</Button>
          <Button variant="primary" onClick={handleSaveExercise}>Сохранить</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default JournalPanel;
