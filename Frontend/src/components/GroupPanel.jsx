import './App.css'
import React, { useState, useEffect, useCallback } from 'react';
import MyToast from './MyToast';
import * as groupActions from './service/groupActions';
import * as userActions from './service/userActions';
import {
  Card,
  Table,
  ButtonGroup,
  Button,
  InputGroup,
  FormControl,
  Modal,
  Badge,
  ListGroup,
  Form,
} from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faList,
  faStepBackward,
  faFastBackward,
  faStepForward,
  faFastForward,
  faSearch,
  faTimes,
  faUserPlus,
  faUserMinus,
  faUsers,
  faUser,
  faPlus,
  faEdit,
  faTrash,
} from "@fortawesome/free-solid-svg-icons";

const GroupPanel = () => {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState("success");
  
  const [showAddStudentsModal, setShowAddStudentsModal] = useState(false);
  const [showManageStudentsModal, setShowManageStudentsModal] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [addStudentsSearch, setAddStudentsSearch] = useState("");
  const [manageStudentsSearch, setManageStudentsSearch] = useState("");

  const [currentGroup, setCurrentGroup] = useState(null);
  const [students, setStudents] = useState([]);
  const [availableStudents, setAvailableStudents] = useState([]);
  const [selectedStudentIds, setSelectedStudentIds] = useState([]);
  
  const [formData, setFormData] = useState({
    name: "",
  });

  const [state, setState] = useState({
    groups: [],
    search: "",
    currentPage: 1,
    groupsPerPage: 5,
    totalPages: 0,
    totalElements: 0,
  });
  
  const getGroups = useCallback(async (page) => {
    try {
      const pageNumber = page - 1;
      const data = await groupActions.filterGroups(
        state.search,
        null,
        pageNumber,
        state.groupsPerPage
      );
      
      setState(prev => ({
        ...prev,
        groups: data.content,
        totalPages: data.totalPages,
        totalElements: data.totalElements,
        currentPage: data.number + 1,
      }));
    } catch (error) {
      console.error("Ошибка при загрузке групп:", error);
      showToastMessage("Ошибка загрузки групп", "danger");
    }
  }, [state.search, state.groupsPerPage]);

  useEffect(() => {
    getGroups(state.currentPage);
    loadAvailableStudents();
  }, [getGroups, state.currentPage]);

  const loadGroupDetails = async (groupId) => {
    try {
      const groupDetails = await groupActions.getGroupById(groupId);
      console.log(groupDetails);
      setStudents(availableStudents.filter(s => groupDetails.studentIds.includes(s.id)));
    } catch (error) {
      console.error("Ошибка загрузки студентов группы:", error);
    }
  };

  const loadAvailableStudents = async () => {
    try {
      const data = await userActions.filterUsers("", "STUDENT", 0, 1000);
      setAvailableStudents(data.content || []);
    } catch (error) {
      console.error("Ошибка загрузки студентов:", error);
    }
  };

  const handleSearchChange = (e) => {
    setState(prev => ({ ...prev, search: e.target.value }));
  };

  const handleCancelSearch = () => {
    setState(prev => ({ ...prev, search: "" }));
    getGroups(1);
  };

  const handleSearch = () => {
    getGroups(1);
  };

  const paginationActions = {
    firstPage: () => getGroups(1),
    prevPage: () => getGroups(state.currentPage - 1),
    nextPage: () => getGroups(state.currentPage + 1),
    lastPage: () => getGroups(state.totalPages),
  };

  const showToastMessage = (message, type = "success") => {
    setToastMessage(message);
    setToastType(type);
    setShowToast(true);
    setTimeout(() => setShowToast(false), 3000);
  };

  const handleOpenCreateModal = () => {
    setFormData({
      name: "",
    });
    setShowCreateModal(true);
  };

  const handleOpenEditModal = (group) => {
    setCurrentGroup(group);
    setFormData({
      name: group.name || "",
    });
    setShowEditModal(true);
  };

  const handleOpenDeleteModal = async (group) => {
  console.log('Открытие удаления для группы:', group);
  setCurrentGroup(group);
  
  try {
    await loadGroupDetails(group.id);
  } catch (error) {
    console.error('Ошибка загрузки студентов:', error);
    setStudents([]); 
  }
  
  setShowDeleteModal(true);
};

  const handleOpenAddStudentsModal = async (group) => {
    setCurrentGroup(group);
    setSelectedStudentIds([]);
    setShowAddStudentsModal(true);
  };

  const handleOpenManageStudentsModal = async (group) => {
    setCurrentGroup(group);
    await loadGroupDetails(group.id);
    setShowManageStudentsModal(true);
  };

  const handleStudentSelection = (studentId) => {
    setSelectedStudentIds(prev => {
      if (prev.includes(studentId)) {
        return prev.filter(id => id !== studentId);
      } else {
        return [...prev, studentId];
      }
    });
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleCreateGroup = async () => {
    try {
      if (!formData.name.trim()) {
        showToastMessage("Название группы обязательно", "warning");
        return;
      }

      await groupActions.saveGroup(formData);
      showToastMessage("Группа успешно создана", "success");
      setShowCreateModal(false);
      getGroups(state.currentPage);
    } catch (error) {
      console.error("Ошибка при создании группы:", error);
      showToastMessage(error.response?.data?.message || "Ошибка при создании группы", "danger");
    }
  };

  const handleUpdateGroup = async () => {
    try {
      if (!formData.name.trim()) {
        showToastMessage("Название группы обязательно", "warning");
        return;
      }

      await groupActions.updateGroup(currentGroup.id, formData);
      showToastMessage("Группа успешно обновлена", "success");
      setShowEditModal(false);
      getGroups(state.currentPage);
    } catch (error) {
      console.error("Ошибка при обновлении группы:", error);
      showToastMessage(error.response?.data?.message || "Ошибка при обновлении группы", "danger");
    }
  };

  const handleDeleteGroup = async () => {
    try {
      if (students.length > 0) {
        if (!window.confirm(`В группе есть ${students.length} студент(ов). Удалить группу вместе со студентами?`)) {
          return;
        }
      }

      await groupActions.deleteGroup(currentGroup.id);
      showToastMessage("Группа успешно удалена", "success");
      setShowDeleteModal(false);
      getGroups(state.currentPage);
    } catch (error) {
      console.error("Ошибка при удалении группы:", error);
      showToastMessage(error.response?.data?.message || "Ошибка при удалении группы", "danger");
    }
  };

  const handleAddStudents = async () => {
    try {
      if (selectedStudentIds.length > 0) {
        await groupActions.addStudentsToGroup(currentGroup.id, selectedStudentIds);
        showToastMessage(`Добавлено ${selectedStudentIds.length} студентов в группу ${currentGroup.name}`, "success");
      } else {
        showToastMessage("Выберите хотя бы одного студента", "warning");
        return;
      }
      
      setShowAddStudentsModal(false);
      getGroups(state.currentPage);
    } catch (error) {
      console.error("Ошибка при добавлении студентов:", error);
      showToastMessage("Ошибка при добавлении студентов", "danger");
    }
  };

  const handleRemoveStudent = async (studentId) => {
    try {
      await groupActions.removeStudentFromGroup(currentGroup.id, studentId);
      showToastMessage("Студент отвязан от группы", "success");
      await loadGroupDetails(currentGroup.id);
      getGroups(state.currentPage);
    } catch (error) {
      console.error("Ошибка при отвязке студента:", error);
      showToastMessage("Ошибка при отвязке студента", "danger");
    }
  };

  const getStudentFullName = (student) => {
    return `${student.firstName || ''} ${student.lastName || ''}`.trim() || student.login;
  };

  const isFirstPage = state.currentPage === 1;
  const isLastPage = state.currentPage === state.totalPages;

  return (
    <div className="main-content">
      <div style={{ display: showToast ? "block" : "none" }}>
        <MyToast
          header={toastType === "success" ? "Успех" : "Ошибка"}
          show={showToast}
          message={toastMessage}
          type={toastType}
        />
      </div>

      <Modal show={showCreateModal} onHide={() => setShowCreateModal(false)}>
        <Modal.Header className="text-white" closeButton>
          <Modal.Title>Создать новую группу</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white">
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Название группы *</Form.Label>
              <Form.Control
                type="text"
                name="name"
                value={formData.name}
                onChange={handleFormChange}
                placeholder="Введите название группы"
                required
              />
              <Form.Text className="text-white">
                Например: "Группа 101", "ПМИ-21-1"
              </Form.Text>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCreateModal(false)}>
            Отмена
          </Button>
          <Button variant="primary" onClick={handleCreateGroup}>
            Создать
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showEditModal} onHide={() => setShowEditModal(false)}>
        <Modal.Header className="text-white" closeButton>
          <Modal.Title>Редактировать группу: {currentGroup?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white">
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Название группы *</Form.Label>
              <Form.Control
                type="text"
                name="name"
                value={formData.name}
                onChange={handleFormChange}
                required
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowEditModal(false)}>
            Отмена
          </Button>
          <Button variant="primary" onClick={handleUpdateGroup}>
            Сохранить
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton className="text-white">
          <Modal.Title>Подтверждение удаления</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white">
          <p>Вы уверены, что хотите удалить группу <strong>"{currentGroup?.name}"</strong>?</p>
          {students.length > 0 && (
            <div className="alert alert-warning">
              <p className="mb-0">
                <FontAwesomeIcon icon={faUsers} className="me-2" />
                В группе находится {students.length} студент(ов).
              </p>
            </div>
          )}
          <p className="text-danger">
            <small>Внимание: Это действие нельзя отменить. Все связи с дисциплинами будут удалены.</small>
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Отмена
          </Button>
          <Button variant="danger" onClick={handleDeleteGroup}>
            Удалить
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showAddStudentsModal} onHide={() => setShowAddStudentsModal(false)} size="lg">
        <Modal.Header closeButton className="text-white">
          <Modal.Title>Добавить студентов в группу: {currentGroup?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white" style={{ maxHeight: '400px', overflowY: 'auto' }}>
          <FormControl
          placeholder="Поиск студентов..."
          size="sm"
          className="mb-2 bg-dark text-white"
          value={addStudentsSearch}
          onChange={(e) => setAddStudentsSearch(e.target.value)}
          />
          <p>Выберите студентов для добавления в группу:</p>
          <ListGroup>
            {availableStudents.filter(s => !currentGroup?.studentIds?.includes(s.id))
            .filter(s => 
            getStudentFullName(s).toLowerCase().includes(addStudentsSearch.toLowerCase()) ||
            (s.email || "").toLowerCase().includes(addStudentsSearch.toLowerCase())
            )
            .map(student => (
              <ListGroup.Item 
                key={student.id}
                action
                onClick={() => handleStudentSelection(student.id)}
                active={selectedStudentIds.includes(student.id)}
              >
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <FontAwesomeIcon icon={faUser} className="me-2" />
                    {getStudentFullName(student)} 
                    <small className="ms-2">({student.email})</small>
                  </div>
                  {selectedStudentIds.includes(student.id) && (
                    <Badge bg="success">Выбрано</Badge>
                  )}
                </div>
              </ListGroup.Item>
            ))}
          </ListGroup>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowAddStudentsModal(false)}>
            Отмена
          </Button>
          <Button variant="primary" onClick={handleAddStudents}>
            Добавить выбранных
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showManageStudentsModal} onHide={() => setShowManageStudentsModal(false)} size="lg">
        <Modal.Header className="text-white" closeButton>
          <Modal.Title>Управление студентами группы: {currentGroup?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white" style={{ maxHeight: '400px', overflowY: 'auto' }}>
          <FormControl
          placeholder="Поиск студентов..."
          size="sm"
          className="mb-2 bg-dark text-white"
          value={manageStudentsSearch}
          onChange={(e) => setManageStudentsSearch(e.target.value)}
          />
          {students.filter(s =>
          getStudentFullName(s).toLowerCase().includes(manageStudentsSearch.toLowerCase()) ||
          (s.email || "").toLowerCase().includes(manageStudentsSearch.toLowerCase())
          ).length === 0 ? (
          <p className="text-center text-white">В группе нет студентов</p>
          ) : (
            <>
              <p>Студенты в группе ({students.length}):</p>
      <ListGroup>
        {students
          .filter(s =>
            getStudentFullName(s).toLowerCase().includes(manageStudentsSearch.toLowerCase()) ||
            (s.email || "").toLowerCase().includes(manageStudentsSearch.toLowerCase())
          )
          .map(student => (
            <ListGroup.Item key={student.id}>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <FontAwesomeIcon icon={faUser} className="me-2" />
                  {getStudentFullName(student)}
                  <small className="text-muted ms-2">({student.email})</small>
                </div>
                <Button
                  size="sm"
                  variant="outline-danger"
                  onClick={() => handleRemoveStudent(student.id)}
                  title="Отвязать от группы"
                >
                  <FontAwesomeIcon icon={faUserMinus} />
                </Button>
              </div>
            </ListGroup.Item>
          ))}
      </ListGroup>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowManageStudentsModal(false)}>
            Закрыть
          </Button>
        </Modal.Footer>
      </Modal>

      <Card className={"border border-dark bg-dark text-white"} style={{display: 'flex', flexDirection: 'column', flex: '1 1 auto', minHeight: 0, overflow: 'hidden' }}>
        <Card.Header className="bg-secondary text-white">
          <div className="container-fluid">
            <div className="row g-3 align-items-center">
              <div className="col-12 col-md-4 col-lg-3">
                <FontAwesomeIcon icon={faList} /> Управление группами
              </div>
              <div className="col-12 col-md-5 col-lg-4">
                <InputGroup size="sm">
                  <FormControl
                    placeholder="Поиск по названию группы..."
                    value={state.search}
                    className="info-border bg-dark text-white"
                    onChange={handleSearchChange}
                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                  />
                  <Button variant="outline-dark" className="no-hover-effect" onClick={handleSearch}>
                    <FontAwesomeIcon icon={faSearch} />
                  </Button>
                  <Button variant="outline-dark" className="no-hover-effect" onClick={handleCancelSearch}>
                    <FontAwesomeIcon icon={faTimes} />
                  </Button>
                </InputGroup>
              </div>
              <div className="col-12 col-md-3 col-lg-2">
                <Button 
                  variant="success" 
                  size="sm" 
                  onClick={handleOpenCreateModal}
                >
                  <FontAwesomeIcon icon={faPlus} /> Создать
                </Button>
              </div>
            </div>
          </div>
        </Card.Header>

        <Card.Body style={{ overflowY: 'auto', flex: '1 1 auto', minHeight: 0 }}>
          <Table bordered hover striped variant="dark" style={{ tableLayout: "fixed", width: "100%" }}>
            <colgroup>
              <col style={{ width: "30%" }} />
              <col style={{ width: "15%" }} />
              <col style={{ width: "55%" }} />
            </colgroup>
            <thead>
              <tr>
                <th>Название группы</th>
                <th>Студентов</th>
                <th>Действия</th>
              </tr>
            </thead>
            <tbody>
              {state?.groups?.length === 0 ? (
                <tr align="center">
                  <td colSpan="3">Нет групп</td>
                </tr>
              ) : (
                state?.groups?.map((group) => (
                  <tr key={group.id}>
                    <td>
                      <strong>{group.name}</strong>
                    </td>
                    <td>
                      <Badge bg="info" className="me-1">
                        <FontAwesomeIcon icon={faUsers} /> {group.studentsCount || 0}
                      </Badge>
                    </td>
                    <td>
                      <ButtonGroup>
                        <Button
                          size="sm"
                          variant="outline-primary"
                          onClick={() => handleOpenAddStudentsModal(group)}
                          title="Добавить студентов"
                          className="me-1"
                        >
                          <FontAwesomeIcon icon={faUserPlus} />
                        </Button>
                        <Button
                          size="sm"
                          variant="outline-warning"
                          onClick={() => handleOpenManageStudentsModal(group)}
                          title="Управление студентами"
                          className="me-1"
                        >
                          <FontAwesomeIcon icon={faUserMinus} />
                        </Button>
                        <Button
                          size="sm"
                          variant="outline-info"
                          onClick={() => handleOpenEditModal(group)}
                          title="Редактировать"
                          className="me-1"
                        >
                          <FontAwesomeIcon icon={faEdit} />
                        </Button>
                        <Button
                          size="sm"
                          variant="outline-danger"
                          onClick={() => handleOpenDeleteModal(group)}
                          title="Удалить"
                        >
                          <FontAwesomeIcon icon={faTrash} />
                        </Button>
                      </ButtonGroup>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </Card.Body>

        {state?.groups?.length > 0 && (
          <Card.Footer className="d-flex justify-content-between align-items-center">
            <div>
              Страница {state.currentPage} из {state.totalPages} (Всего: {state.totalElements})
            </div>
            <div>
              <InputGroup size="sm">
                <Button
                  variant="outline-info"
                  disabled={isFirstPage}
                  onClick={paginationActions.firstPage}
                >
                  <FontAwesomeIcon icon={faFastBackward} />
                </Button>
                <Button
                  variant="outline-info"
                  disabled={isFirstPage}
                  onClick={paginationActions.prevPage}
                >
                  <FontAwesomeIcon icon={faStepBackward} />
                </Button>
                <Button
                  variant="outline-info"
                  disabled={isLastPage}
                  onClick={paginationActions.nextPage}
                >
                  <FontAwesomeIcon icon={faStepForward} />
                </Button>
                <Button
                  variant="outline-info"
                  disabled={isLastPage}
                  onClick={paginationActions.lastPage}
                >
                  <FontAwesomeIcon icon={faFastForward} />
                </Button>
              </InputGroup>
            </div>
          </Card.Footer>
        )}
      </Card>
    </div>
  );
};

export default GroupPanel;