import React, { useEffect, useState } from "react";
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

    const [studentAvg, setStudentAvg] = useState(null);
    const [disciplineAvg, setDisciplineAvg] = useState(null);
    const [groupAvg, setGroupAvg] = useState(null);

    const api = axios.create({
        baseURL: "http://localhost:8080/api",
        headers: { "Content-Type": "application/json" }
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

    // Загрузка учителя
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

    // Загрузка дисциплин учителя
    useEffect(() => {
        if (!teacherId) return;
        const loadDisciplines = async () => {
            const data = await disciplineActions.getDisciplinesTeacher(teacherId);
            setDisciplines(data);
        };
        loadDisciplines();
    }, [teacherId]);

    // Загрузка групп и студентов
    useEffect(() => {
        const loadGroups = async () => {
            if (!disciplineIdGr) {
                setGroups([]);
                setGroupId("");
                return;
            }
            const data = await disciplineActions.getDisciplineGroups(disciplineIdGr);
            setGroups(Array.isArray(data) ? data : Object.values(data));
            setGroupId("");
        };
        loadGroups();
    }, [disciplineIdGr]);

    useEffect(() => {
        const loadStudents = async () => {
            if (!disciplineIdSt) {
                setStudents([]);
                setStudentId("");
                return;
            }
            const groups = await disciplineActions.getDisciplineGroups(disciplineIdSt);
            const studentsByGroup = await Promise.all(
                groups.map(async (group) => {
                    const students = await groupActions.getGroupStudents(group.id);
                    return Array.isArray(students) ? students : Object.values(students);
                })
            );
            setStudents(studentsByGroup.flat());
            setStudentId("");
        };
        loadStudents();
    }, [disciplineIdSt]);

    useEffect(() => {
        const loadGroupsRp = async () => {
            if (!disciplineIdRp) {
                setGroupsRp([]);
                setGroupIdRp("");
                return;
            }
            const data = await disciplineActions.getDisciplineGroups(disciplineIdRp);
            setGroupsRp(Array.isArray(data) ? data : Object.values(data));
            setGroupIdRp("");
        };
        loadGroupsRp();
    }, [disciplineIdRp]);

    useEffect(() => {
        const loadStudentsRp = async () => {
            if (!groupIdRp) {
                setStudentsRp([]);
                return;
            }
            const data = await groupActions.getGroupStudents(groupIdRp);
            setStudentsRp(Array.isArray(data) ? data : Object.values(data));
        };
        loadStudentsRp();
    }, [groupIdRp]);

    // Функции получения средних
    const fetchStudentAvg = async () => {
        try {
            const res = await api.get(`/grades/avg/student/${studentId}/${disciplineIdSt}`);
            setStudentAvg(res.data ?? 0);
        } catch (err) {
            setStudentAvg(0);
        }
    };

    const fetchDisciplineAvg = async () => {
        try {
            const res = await api.get(`/grades/avg/discipline/${disciplineIdDsc}`);
            setDisciplineAvg(res.data ?? 0);
        } catch (err) {
            setDisciplineAvg(0);
        }
    };

    const fetchStudentsAverages = async () => {
        try {
            const res = await api.get(`/grades/avg/students/${groupId}/${disciplineIdGr}`);
            setGroupAvg(res.data ?? 0);
        } catch (err) {
            setGroupAvg(0);
        }
    };

    // Отчёт PDF
    const downloadReport = async () => {
        if (!groupIdRp || !disciplineIdRp) {
            alert("Выберите дисциплину и группу");
            return;
        }

        try {
            const teacher = await userActions.getMe();
            const exercisesRes = await api.get(`/exercises/by-discipline-group/${disciplineIdRp}/${groupIdRp}`);
            const lessonDats = exercisesRes.data;

            const sortedLessonDates = [...lessonDats].sort((a, b) => new Date(a.date) - new Date(b.date));

            const studentRows = await Promise.all(
            studentsRp.map(async (student) => {
                const gradesRes = await api.get(`/grades/student/${student.id}/${disciplineIdRp}`);
                const avgRes = await api.get(`/grades/avg/student/${student.id}/${disciplineIdRp}`);
                const gradesRaw = Array.isArray(gradesRes.data) ? gradesRes.data : [];
                const grades = sortedLessonDates.map(date => {
                const grade = gradesRaw.find(g => g.exerciseId === date.id);
                return grade ? grade.value : "-";
                });

                return {
                studentId: student.id,
                studentName: `${student.lastName ?? ""} ${student.firstName ?? ""}`.trim(),
                grades,
                average: avgRes.data ? Number(avgRes.data) : 0
                };
            })
            );
            const lessonDates = sortedLessonDates.map(ld => ld.date);
            console.log(lessonDates);
            const groupAvgRes = await api.get(`/grades/avg/students/${groupIdRp}/${disciplineIdRp}`);
            const dto = {
                groupName: groupsRp.find(g => g.id === groupIdRp)?.name ?? "Неизвестная группа",
                disciplineName: disciplines.find(d => d.id === disciplineIdRp)?.name ?? "Неизвестная дисциплина",
                teacherName: `${teacher.lastName ?? ""} ${teacher.firstName ?? ""}`.trim(),
                lessonDates,
                students: studentRows,
                groupAverage: groupAvgRes.data ?? 0
            };

            const idRes = await api.post('/protocol/pdf', dto);
            const pdfId = idRes.data;
            const pdfRes = await api.get(`/protocol/pdf/${pdfId}`, { responseType: 'blob' });
            const url = window.URL.createObjectURL(new Blob([pdfRes.data], { type: 'application/pdf' }));
            const link = document.createElement('a');
            link.href = url;
            link.download = `report_group_${groupIdRp}_discipline_${disciplineIdRp}.pdf`;
            link.click();

        } catch (err) {
            alert("Ошибка при формировании отчета");
        }
    };

    return (
        <div className="container py-4" style={{ height: "85vh", overflowY: "auto" }}>
            <Card className="bg-dark text-white shadow rounded">
                <Card.Header className="bg-secondary d-flex align-items-center">
                    <i className="bi bi-bar-chart fs-3 me-2"></i>
                    <h3 className="m-0">Статистика и отчёты</h3>
                </Card.Header>

                <Card.Body>
                    <Nav variant="tabs" activeKey={tab} onSelect={setTab}>
                        <Nav.Item><Nav.Link eventKey="stats">Статистика</Nav.Link></Nav.Item>
                        <Nav.Item><Nav.Link eventKey="report">Отчёт</Nav.Link></Nav.Item>
                    </Nav>

                    {tab === "stats" && (
                        <div className="mt-4">
                            {/* Средний балл студента */}
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
                                        <option value={s.id} key={s.id} className="text-dark">{s.name}</option>
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
                                        <option value={s.id} key={s.id} className="text-dark">{s.lastName} {s.firstName}</option>
                                    ))}
                                </Form.Select>

                                <Button variant="success" className="mt-3" onClick={fetchStudentAvg} disabled={!studentId}>Получить</Button>
                                {studentAvg !== null && <p className="fs-4 mt-2">{Number(studentAvg).toFixed(2)}</p>}
                            </Card>

                            {/* Средний балл по дисциплине */}
                            <Card className="bg-secondary text-white p-3 mb-3 shadow">
                                <h5><i className="bi bi-book-half me-2"></i>Средний балл по дисциплине</h5>
                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={disciplineIdDsc}
                                    onChange={e => setDisciplineIdDsc(e.target.value)}
                                >
                                    <option value="">— Выберите дисциплину —</option>
                                    {disciplines.map(d => (
                                        <option key={d.id} value={d.id} className="text-dark">{d.name}</option>
                                    ))}
                                </Form.Select>

                                <Button variant="info" className="mt-3" onClick={fetchDisciplineAvg} disabled={!disciplineIdDsc}>Получить</Button>
                                {disciplineAvg !== null && <p className="fs-4 mt-2">{Number(disciplineAvg).toFixed(2)}</p>}
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
                                        <option key={d.id} value={d.id} className="text-dark">{d.name}</option>
                                    ))}
                                </Form.Select>
                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={groupId}
                                    onChange={e => setGroupId(e.target.value)}
                                >
                                    <option value="">— Выберите группу —</option>
                                    {groups.map(g => (
                                        <option key={g.id} value={g.id} className="text-dark">{g.name}</option>
                                    ))}
                                </Form.Select>

                                <Button variant="warning" className="mt-3" onClick={fetchStudentsAverages} disabled={!groupId || !disciplineIdGr}>Получить</Button>
                                {groupAvg !== null && <p className="fs-4 mt-2">{Number(groupAvg).toFixed(2)}</p>}
                            </Card>
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
                                        <option key={g.id} value={g.id} className="text-dark">{g.name}</option>
                                    ))}
                                </Form.Select>
                                <Form.Select
                                    className="bg-dark text-white border-secondary mt-2"
                                    value={groupIdRp}
                                    onChange={e => setGroupIdRp(Number(e.target.value))}
                                >
                                    <option value="">— Выберите группу —</option>
                                    {groupsRp.map(g => (
                                        <option key={g.id} value={g.id} className="text-dark">{g.name}</option>
                                    ))}
                                </Form.Select>

                                <Button variant="primary" className="mt-3" onClick={downloadReport} disabled={!groupIdRp || !disciplineIdRp}>Сформировать</Button>
                            </Card>
                        </div>
                    )}
                </Card.Body>
            </Card>
        </div>
    );
}
