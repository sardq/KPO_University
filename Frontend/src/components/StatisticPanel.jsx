import React, {  useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import * as userActions from "./service/userActions";
import * as disciplineActions from "./service/disciplineActions";
import * as groupActions from "./service/groupActions";
import { Card, Button, Form, Nav } from "react-bootstrap";

export default function StatisticsPage() {
    const navigate = useNavigate();

    const [tab, setTab] = useState("stats");

    const [teacherId, setTeacherId] = useState(null);

    const [disciplineIdDsc, setDisciplineIdDsc] = useState("");
    const [disciplineIdSt, setDisciplineIdSt] = useState("");
    const [disciplineIdGr, setDisciplineIdGr] = useState("");
    const [disciplineIdRp, setDisciplineIdRp] = useState("");
    const [groupIdRp, setGroupIdRp] = useState("");
    const [groupsRp, setGroupsRp] = useState([]);
    const [studentsRp, setStudentsRp] = useState([]);

    const [groupId, setGroupId] = useState("");
    const [studentId, setStudentId] = useState("");

    const [disciplines, setDisciplines] = useState([]);
    const [groups, setGroups] = useState([]);
    const [students, setStudents] = useState([]);

    const [result, setResult] = useState(null);

    const api = axios.create({
        baseURL: "http://localhost:8080/api",
        headers: {
            "Content-Type": "application/json"
        }
    });
    
    api.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem("token");
            if (token && token !== "null" && token !== "undefined") {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    useEffect(() => {
        const load = async () => {
            const user = await userActions.getMe();
            if (!user || user.role !== "TEACHER") {
                navigate("/teacherHome");
                return;
            }
            setTeacherId(user.id);
        };
        load();
    }, [navigate]);

    useEffect(() => {
        if (!teacherId) return;

        const loadDisciplines = async () => {
            const data = await disciplineActions.getDisciplinesTeacher(teacherId);
            setDisciplines(data);
        };
        loadDisciplines();
    }, [teacherId]);

    useEffect(() => {
        if (!disciplineIdGr) {
            setGroups([]);
            setGroupId("");
            return;
        }

        const loadGroups = async () => {
            const data = await disciplineActions.getDisciplineGroups(disciplineIdGr);
            setGroups(Array.isArray(data) ? data : Object.values(data));
            setGroupId("");
        };

        loadGroups();
    }, [disciplineIdGr]);
    useEffect(() => {
        if (!disciplineIdSt) {
            setGroups([]);
            setGroupId("");
            return;
        }

        const loadStudents = async () => {
            const groups = await disciplineActions.getDisciplineGroups(disciplineIdSt);
            const studentsByGroup = await Promise.all(
            groups.map(async (group) => {
                const students = await groupActions.getGroupStudents(group.id);
                return Array.isArray(students) ? students : Object.values(students);
            })
        );
        const allStudents = studentsByGroup.flat();
            setStudents(allStudents);
            setStudentId("");
        };

        loadStudents();
    }, [disciplineIdSt]);
    useEffect(() => {
        if (!disciplineIdRp) {
            setGroupsRp([]);
            setGroupIdRp("");
            return;
        }

        const loadGroupsRp = async () => {
            const data = await disciplineActions.getDisciplineGroups(disciplineIdRp);
            setGroupsRp(Array.isArray(data) ? data : Object.values(data));
            setGroupIdRp("");
        };

        loadGroupsRp();
    }, [disciplineIdRp]);
     useEffect(() => {
        if (!groupIdRp) {
            setStudentsRp([]);
            return;
        }

        const loadStudentsRp = async () => {
            const data = await groupActions.getGroupStudents(groupIdRp);
            setStudentsRp(Array.isArray(data) ? data : Object.values(data));
        };

        loadStudentsRp();
    }, [groupIdRp]);
useEffect(() => {
        if (!disciplineIdSt) {
            setGroups([]);
            setGroupId("");
            return;
        }

        const loadStudents = async () => {
            const groups = await disciplineActions.getDisciplineGroups(disciplineIdSt);
            const studentsByGroup = await Promise.all(
            groups.map(async (group) => {
                const students = await groupActions.getGroupStudents(group.id);
                return Array.isArray(students) ? students : Object.values(students);
            })
        );
        const allStudents = studentsByGroup.flat();
            setStudents(allStudents);
            setStudentId("");
        };

        loadStudents();
    }, [disciplineIdSt]);
    

    const fetchStudentAvg = async () => {
    try {
        const res = await api.get(`/grades/avg/student/${studentId}/${disciplineIdSt}`);
        setResult({ type: "student", data: res.data ?? 0 }); 
    } catch (err) {
        console.error(err);
        setResult({ type: "student", data: 0 });
    }
};

const fetchDisciplineAvg = async () => {
    try {
        const res = await api.get(`/grades/avg/discipline/${disciplineIdDsc}`);
        setResult({ type: "discipline", data: res.data ?? 0 });
    } catch (err) {
        console.error(err);
        setResult({ type: "discipline", data: 0 });
    }
};

const fetchStudentsAverages = async () => {
    try {
        const res = await api.get(`/grades/avg/students/${groupId}/${disciplineIdGr}`);
        setResult({ type: "group", data: res.data ?? 0 });
    } catch (err) {
        setResult({ type: "group", data: 0 });
    }
};

const downloadReport = async () => {
    if (!groupIdRp || !disciplineIdRp) {
        alert("Выберите дисциплину и группу");
        return;
    }

    try {
        const teacher = await userActions.getMe();

        const exercisesRes = await api.get(`/exercises/by-discipline-group/${disciplineIdRp}/${groupIdRp}`);
        const lessonDates = exercisesRes.data?.map(e => e.date) ?? [];

        const studentRows = await Promise.all(
        studentsRp.map(async (student) => {
        const gradesRes = await api.get(`/grades/student/${student.id}/${disciplineIdRp}`);
        const avgRes = await api.get(`/grades/avg/student/${student.id}/${disciplineIdRp}`);
        const grades = Array.isArray(gradesRes.data) ? gradesRes.data.map(g => g.value) : [];
        let average = 0;

        if (avgRes.data !== null && avgRes.data !== "" && !isNaN(avgRes.data)) {
            average = Number(avgRes.data);
        }
        console.log(grades);
        console.log(average);
        console.log(student.id);
        console.log(student.lastName);
        console.log(student.firstName);

        return {
            studentId: student.id,
            studentName: `${student.lastName ?? ""} ${student.firstName ?? ""}`.trim(),
            grades,
            average
        };
    })
);
        console.log(disciplineIdRp);
        console.log(groupIdRp);
        console.log(disciplines);
        console.log(groupsRp);
        const groupAvgRes = await api.get(`/grades/avg/students/${groupIdRp}/${disciplineIdRp}`);
        console.log(groupAvgRes);
        const dto = {
            groupName: groupsRp.find(g => g.id === groupIdRp)?.name ?? "Неизвестная группа",
            disciplineName: disciplines.find(d => d.id === disciplineIdRp)?.name ?? "Неизвестная дисциплина",
            teacherName: `${teacher.lastName ?? ""} ${teacher.firstName ?? ""}`.trim(),
            lessonDates,
            students: studentRows,
            groupAverage: groupAvgRes.data ?? 0
        };
        console.log(dto);

        const idRes = await api.post('/protocol/pdf', dto);
const pdfId = idRes.data;
console.log(pdfId);
const pdfRes = await api.get(`/protocol/pdf/${pdfId}`, { responseType: 'blob' });

const url = window.URL.createObjectURL(new Blob([pdfRes.data], { type: 'application/pdf' }));
const link = document.createElement('a');
link.href = url;
link.download = `report_group_${groupIdRp}_discipline_${disciplineIdRp}.pdf`;
link.click();

    } catch (err) {
        console.error("Ошибка при формировании отчета", err);
        alert("Ошибка при формировании отчета");
    }
};


    return (
        <div className="container py-4" style={{
        height: "85vh",
        overflowY: "auto",
      }}>

            <Card className="bg-dark text-white shadow rounded">
                <Card.Header className="bg-secondary d-flex align-items-center">
                    <i className="bi bi-bar-chart fs-3 me-2"></i>
                    <h3 className="m-0">Статистика и отчёты</h3>
                </Card.Header>

                <Card.Body>
                    <Nav variant="tabs" activeKey={tab} onSelect={setTab}>
                        <Nav.Item>
                            <Nav.Link eventKey="stats">Статистика</Nav.Link>
                        </Nav.Item>
                        <Nav.Item>
                            <Nav.Link eventKey="report">Отчёт</Nav.Link>
                        </Nav.Item>
                    </Nav>

                    {tab === "stats" && (
                        <div className="mt-4">

                            <Card className="bg-secondary text-white p-3 mb-3 shadow">
                                <h5><i className="bi bi-person-fill me-2"></i>Средний балл студента</h5>
                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={disciplineIdSt}
                                    onChange={e => setDisciplineIdSt(e.target.value)}
                                    disabled={!disciplines.length}
                                >
                                    <option value="">— Выберите дисциплину —</option>
                                    {disciplines.map(s => (
                                        <option value={s.id} key={s.id} className="text-dark">
                                            {s.name}
                                        </option>
                                    ))}
                                </Form.Select>
                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={studentId}
                                    onChange={e => setStudentId(e.target.value)}
                                    disabled={!students.length}
                                >
                                    <option value="">— Выберите студента —</option>
                                    {students.map(s => (
                                        <option value={s.id} key={s.id} className="text-dark">
                                            {s.lastName} {s.firstName}
                                        </option>
                                    ))}
                                </Form.Select>

                                <Button
                                    variant="success"
                                    className="mt-3"
                                    onClick={fetchStudentAvg}
                                    disabled={!studentId}
                                >
                                    Получить
                                </Button>
                            </Card>

                            <Card className="bg-secondary text-white p-3 mb-3 shadow">
                                <h5><i className="bi bi-book-half me-2"></i>Средний балл по дисциплине</h5>

                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={disciplineIdDsc}
                                    onChange={e => setDisciplineIdDsc(e.target.value)}
                                >
                                    <option value="">— Выберите дисциплину —</option>
                                    {disciplines.map(d => (
                                        <option key={d.id} value={d.id} className="text-dark">
                                            {d.name}
                                        </option>
                                    ))}
                                </Form.Select>

                                <Button
                                    variant="info"
                                    className="mt-3"
                                    onClick={fetchDisciplineAvg}
                                    disabled={!disciplineIdDsc}
                                >
                                    Получить
                                </Button>
                            </Card>

                            {/* Средний балл группы */}
                            <Card className="bg-secondary text-white p-3 shadow">
                                <h5><i className="bi bi-people-fill me-2"></i>Средний балл группы по дисциплине</h5>

                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={disciplineIdGr}
                                    onChange={e => setDisciplineIdGr(e.target.value)}
                                >
                                    <option value="">— Выберите дисциплину —</option>
                                    {disciplines.map(d => (
                                        <option key={d.id} value={d.id} className="text-dark">
                                            {d.name}
                                        </option>
                                    ))}
                                </Form.Select>
                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={groupId}
                                    onChange={e => {
                                        setGroupId(e.target.value);
                                    }}
                                >
                                    <option value="">— Выберите группу —</option>
                                    {groups.map(g => (
                                        <option key={g.id} value={g.id} className="text-dark">
                                            {g.name}
                                        </option>
                                    ))}
                                </Form.Select>

                                <Button
                                    variant="warning"
                                    className="mt-3"
                                    onClick={fetchStudentsAverages}
                                    disabled={!groupId || !disciplineIdGr}
                                >
                                    Получить
                                </Button>
                            </Card>

                            {result && (
  <div className="mt-4">
    {result.type === "student" && (
      <Card className="bg-dark text-white p-3 shadow mb-3">
        <h5><i className="bi bi-person-fill me-2"></i>Средний балл студента</h5>
        <p className="fs-4 m-0">
          {result.data != null ? Number(result.data).toFixed(2) : "Нет данных"}
        </p>
      </Card>
    )}

    {result.type === "discipline" && (
      <Card className="bg-dark text-white p-3 shadow mb-3">
        <h5><i className="bi bi-book-half me-2"></i>Средний балл по дисциплине</h5>
        <p className="fs-4 m-0">
          {result.data != null ? Number(result.data).toFixed(2) : "Нет данных"}
        </p>
      </Card>
    )}

    {result.type === "group" && (
      <Card className="bg-dark text-white p-3 shadow mb-3">
        <h5><i className="bi bi-people-fill me-2"></i>Средний балл группы по дисциплине</h5>
        <p className="fs-4 m-0">
          {result.data != null ? Number(result.data).toFixed(2) : "Нет данных"}
        </p>
      </Card>
    )}
  </div>
)}
                        </div>
                    )}

                    {tab === "report" && (
                        <div className="mt-4">
                            <Card className="bg-secondary text-white p-3 shadow">
                                <h5><i className="bi bi-file-earmark-text me-2"></i>Сформировать отчет</h5>
                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={disciplineIdRp}
                                       onChange={e => setDisciplineIdRp(Number(e.target.value))}
                                >
                                    <option value="">— Выберите дисциплину —</option>
                                    {disciplines.map(g => (
                                        <option key={g.id} value={g.id} className="text-dark">
                                            {g.name}
                                        </option>
                                    ))}
                                </Form.Select>
                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={groupIdRp}
                                    onChange={e => setGroupIdRp(Number(e.target.value))}
                                >
                                    <option value="">— Выберите группу —</option>
                                    {groupsRp.map(g => (
                                        <option key={g.id} value={g.id} className="text-dark">
                                            {g.name}
                                        </option>
                                    ))}
                                </Form.Select>

                                <Button
                                    variant="primary"
                                    className="mt-3"
                                    onClick={downloadReport}
                                    disabled={!groupIdRp || !disciplineIdRp}
                                >
                                    Сформировать
                                </Button>
                            </Card>
                        </div>
                    )}
                </Card.Body>
            </Card>
        </div>
    );
}
