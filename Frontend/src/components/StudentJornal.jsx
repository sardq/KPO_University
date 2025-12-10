import React, { useEffect, useState, useContext } from "react";
import { AuthContent } from "./AuthContent";

import * as exerciseActions from "./service/exerciseActions";
import * as userActions from "./service/userActions";
import * as gradeActions from "./service/gradeActions";
import * as disciplineActions from "./service/disciplineActions";
import * as groupActions from "./service/groupActions";
import MyToast from "./MyToast";

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

  useEffect(() => {
    const loadData = async () => {
      try {
        const u = await userActions.getMe();
        setUserCurrent(u);
        const groups = await groupActions.getAllGroups(0);

        const group = groups.find(g =>
          g.studentIds && g.studentIds.includes(u.id)
        );

        setUserGroup(group || null);
      } catch (err) {
            showToast("Ошибка загрузки пользователя/группы", "danger");
      }

      setLoading(false);
    };

    loadData();
  }, []);
  const showToast = (message, type = "danger") => {
  setToast({ show: true, message, type });

  setTimeout(() => {
    setToast({ show: false, message: "", type: "" });
  }, 3000);
};
  useEffect(() => {
    if (!userGroup) return;

    const loadDisciplines = async () => {
      try {
        const res = await disciplineActions.GetDisciplinesByGroup(userGroup.id);
        setDisciplines(res);
      } catch (err) {
        showToast("Ошибка загрузки дисциплин", "danger");
      }
    };

    loadDisciplines();
  }, [userGroup]);

  useEffect(() => {
    if (!selectedDiscipline || !userGroup) return;

    const load = async () => {
      try {
        const ex = await exerciseActions.getByDisciplineAndGroup(
          selectedDiscipline,
          userGroup.id
        );
        const exSorted = ex.sort(
          (a, b) => new Date(a.date) - new Date(b.date)
        );
        setExercises(exSorted);

        const st = await groupActions.getGroupStudents(userGroup.id);
        setStudents(
          st.sort((a, b) => a.firstName.localeCompare(b.lastName))
        );

        const gr = await gradeActions.getByGroupAndDiscipline(
          userGroup.id,
          selectedDiscipline
        );
        setGrades(gr);
      } catch (err) {
        showToast("Ошибка загрузки данных журнала", "danger");
      }
    };

    load();
  }, [selectedDiscipline, userGroup]);

  const getStudentGrade = (studentId, exerciseId) => {
    const g = grades.find(
      (gr) => gr.studentId === studentId && gr.exerciseId === exerciseId
    );
    return g ? g.value : "-";
  };

  if (loading) return <p className="text-white">Загрузка...</p>;
  <MyToast
  show={toast.show}
  message={toast.message}
  type={toast.type}
/>

  return (
    <div className="container mt-4">
      <h1 className="text-white mb-4">Электронный журнал студента</h1>

      <div className="card bg-dark text-white mb-3">
        <div className="card-body">
          <h4>Вы вошли как: {email}</h4>
          {userGroup ? (
            <>
            <p>
              Ваши данные: <strong>{userCurrent.firstName} {userCurrent.lastName}</strong>
            </p>
            <p>
              Ваша группа: <strong>{userGroup.name}</strong>
            </p>
            </>
          ) : (
            <p className="text-warning">Вы не прикреплены к группе</p>
          )}
        </div>
      </div>

      <div className="card bg-dark text-white mb-3">
        <div className="card-body">
          <h4>Выбор дисциплины</h4>
          <select
            className="form-select bg-secondary text-white"
            value={selectedDiscipline}
            onChange={(e) => setSelectedDiscipline(e.target.value)}
          >
            <option value="">-- Выберите дисциплину --</option>
            {disciplines.map((d) => (
              <option key={d.id} value={d.id}>
                {d.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {selectedDiscipline && (
        <div className="card bg-dark text-white mb-4">
          <div className="card-body">
            <h4>Журнал оценок группы</h4>

            <div
  className="mt-3"
  style={{
    maxHeight: "70vh",       
    overflowX: "auto",       
    overflowY: "auto",       
    border: "1px solid #444",
    borderRadius: "6px"
  }}
>
  <table className="table table-dark table-bordered mb-0">
                <thead>
                  <tr>
                    <th>ФИО</th>
                    {exercises.map((ex) => (
                      <th key={ex.id}>
                        {new Date(ex.date).toLocaleDateString()}
                        <br />
                        <small>
                          {new Date(ex.date).toLocaleTimeString([], {
                            hour: "2-digit",
                            minute: "2-digit",
                          })}
                        </small>
                      </th>
                    ))}
                  </tr>
                </thead>

                <tbody>
                  {students.map((st) => (
                    <tr key={st.id}>
                      <td>
                         {st.firstName} {st.lastName}
                      </td>

                      {exercises.map((ex) => (
                        <td key={ex.id} style={{ textAlign: "center" }}>
                          {getStudentGrade(st.id, ex.id)}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

          </div>
        </div>
      )}
    </div>
  );
};

export default StudentJournal;
