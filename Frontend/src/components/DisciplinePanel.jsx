import './App.css'
import React, { useState, useEffect, useCallback } from 'react';
import MyToast from './MyToast';
import * as disciplineActions from './service/disciplineActions';
import * as userActions from './service/userActions';
import * as groupActions from './service/groupActions';
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
  faUsers,
  faUserGroup,
  faUserFriends,
  faUserMinus,
  faPlus,
  faEdit,
  faTrash,
} from "@fortawesome/free-solid-svg-icons";

const DisciplinePanel = () => {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState("success");
  
  const [showAddGroupsModal, setShowAddGroupsModal] = useState(false);
  const [showManageGroupsModal, setShowManageGroupsModal] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [addGroupsSearch, setAddGroupsSearch] = useState("");
  const [manageGroupsSearch, setManageGroupsSearch] = useState("");
  const [currentDiscipline, setCurrentDiscipline] = useState(null);
  const [groups, setGroups] = useState([]);
  const [availableGroups, setAvailableGroups] = useState([]);
  const [selectedGroupIds, setSelectedGroupIds] = useState([]);
  const [showAddTeachersModal, setShowAddTeachersModal] = useState(false);
  const [showManageTeachersModal, setShowManageTeachersModal] = useState(false);
  const [teachers, setTeachers] = useState([]);
  const [availableTeachers, setAvailableTeachers] = useState([]);
  const [selectedTeacherIds, setSelectedTeacherIds] = useState([]);
  const [addTeachersSearch, setAddTeachersSearch] = useState("");
  const [manageTeachersSearch, setManageTeachersSearch] = useState("");
  const [formData, setFormData] = useState({
    name: "",
  });
  
  const [state, setState] = useState({
    disciplines: [],
    search: "",
    currentPage: 1,
    disciplinesPerPage: 5,
    totalPages: 0,
    totalElements: 0,
  });
  const getStudentFullName = (student) => {
    return `${student.firstName || ''} ${student.lastName || ''}`.trim() || student.login;
  };
  const loadDisciplineTeachers = async (disciplineId) => {
  try {
    const disciplineDetails = await disciplineActions.getDisciplineById(disciplineId);
    setTeachers(
      availableTeachers.filter(t => disciplineDetails.teacherIds.includes(t.id))
    );
  } catch (error) {
      showToastMessage("Ошибка загрузки преподавателей дисциплины", "danger");
  }
};
const loadAvailableTeachers = async () => {
  try {
    const data = await userActions.filterUsers("", "TEACHER", 0, 100); 
    setAvailableTeachers(data.content || []);
  } catch (error) {
  }
};
  const getDisciplines = useCallback(async (page) => {
    try {
      const pageNumber = page - 1;
      const data = await disciplineActions.filterDisciplines(
        state.search,
        null,
        pageNumber,
        state.disciplinesPerPage
      );
      
      setState(prev => ({
        ...prev,
        disciplines: data.content,
        totalPages: data.totalPages,
        totalElements: data.totalElements,
        currentPage: data.number + 1,
      }));
    } catch (error) {
      showToastMessage("Ошибка загрузки дисциплин", "danger");
    }
  }, [state.search, state.disciplinesPerPage]);

  useEffect(() => {
    getDisciplines(state.currentPage);
    loadAvailableGroups();
    loadAvailableTeachers();
  }, [getDisciplines, state.currentPage]);

  const loadDisciplineDetails = async (disciplineId) => {
    try {
      const disciplineDetails = await disciplineActions.getDisciplineById(disciplineId);
      setGroups(availableGroups.filter(g => disciplineDetails.groupIds.includes(g.id)));
    } catch (error) {
     showToastMessage("Ошибка загрузки групп дисциплины", "danger");

    }
  };
  const validateDisciplineForm = () => {
  if (formData.name.length < 2) {
    showToastMessage("Название дисциплины слишком короткое", "warning");
    return false;
  }
  if (formData.name.length > 100) {
    showToastMessage("Название дисциплины слишком длинное (макс. 100 символов)", "warning");
    return false;
  }
  return true;
};
  const loadAvailableGroups = async () => {
    try {
      const data = await groupActions.getAllGroups(0);
      setAvailableGroups(data || []);
    } catch (error) {
     showToastMessage("Ошибка загрузки групп", "danger");

    }
  };

  const handleSearchChange = (e) => {
    setState(prev => ({ ...prev, search: e.target.value }));
  };

  const handleCancelSearch = () => {
    setState(prev => ({ ...prev, search: "" }));
    getDisciplines(1);
  };

  const handleSearch = () => {
    getDisciplines(1);
  };

  const paginationActions = {
    firstPage: () => getDisciplines(1),
    prevPage: () => getDisciplines(state.currentPage - 1),
    nextPage: () => getDisciplines(state.currentPage + 1),
    lastPage: () => getDisciplines(state.totalPages),
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

  const handleOpenEditModal = (discipline) => {
    setCurrentDiscipline(discipline);
    setFormData({
      name: discipline.name || "",
    });
    setShowEditModal(true);
  };

  const handleOpenDeleteModal = (discipline) => {
    setCurrentDiscipline(discipline);
    setShowDeleteModal(true);
  };
  const handleOpenAddTeachersModal = async (discipline) => {
  setCurrentDiscipline(discipline);
  setSelectedTeacherIds([]);
  setShowAddTeachersModal(true);
};

const handleOpenManageTeachersModal = async (discipline) => {
  setCurrentDiscipline(discipline);
  await loadDisciplineTeachers(discipline.id);
  setShowManageTeachersModal(true);
};

const handleTeacherSelection = (teacherId) => {
  setSelectedTeacherIds(prev => {
    if (prev.includes(teacherId)) return prev.filter(id => id !== teacherId);
    return [...prev, teacherId];
  });
};

const handleAddTeachers = async () => {
  if (selectedTeacherIds.length === 0) {
    showToastMessage("Выберите хотя бы одного преподавателя", "warning");
    return;
  }
  try {
    await disciplineActions.addTeachersToDiscipline(currentDiscipline.id, selectedTeacherIds);
    showToastMessage(`Добавлено ${selectedTeacherIds.length} преподавателей`, "success");
    setShowAddTeachersModal(false);
    getDisciplines(state.currentPage);
  } catch (error) {
    showToastMessage("Ошибка при добавлении преподавателей", "danger");
  }
};

const handleRemoveTeacher = async (teacherId) => {
  try {
    await disciplineActions.removeTeacherFromDiscipline(currentDiscipline.id, teacherId);
    showToastMessage("Преподаватель отвязан от дисциплины", "success");
    await loadDisciplineTeachers(currentDiscipline.id);
    getDisciplines(state.currentPage);
  } catch (error) {
    showToastMessage("Ошибка при отвязке преподавателя", "danger");
  }
};
  const handleOpenAddGroupsModal = async (discipline) => {
    setCurrentDiscipline(discipline);
    setSelectedGroupIds([]);
    setShowAddGroupsModal(true);
  };

  const handleOpenManageGroupsModal = async (discipline) => {
    setCurrentDiscipline(discipline);
    await loadDisciplineDetails(discipline.id);
    setShowManageGroupsModal(true);
  };

  const handleGroupSelection = (groupId) => {
    setSelectedGroupIds(prev => {
      if (prev.includes(groupId)) {
        return prev.filter(id => id !== groupId);
      } else {
        return [...prev, groupId];
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

  const handleCreateDiscipline = async () => {
    if (!validateDisciplineForm()) return;
    try {
      if (!formData.name.trim()) {
        showToastMessage("Название дисциплины обязательно", "warning");
        return;
      }

      await disciplineActions.saveDiscipline(formData);
      showToastMessage("Дисциплина успешно создана", "success");
      setShowCreateModal(false);
      getDisciplines(state.currentPage);
    } catch (error) {
      showToastMessage("Ошибка при создании дисциплины", "danger");
    }
  };

  const handleUpdateDiscipline = async () => {
    if (!validateDisciplineForm()) return;
    try {
      if (!formData.name.trim()) {
        showToastMessage("Название дисциплины обязательно", "warning");
        return;
      }

      await disciplineActions.updateDiscipline(currentDiscipline.id, formData);
      showToastMessage("Дисциплина успешно обновлена", "success");
      setShowEditModal(false);
      getDisciplines(state.currentPage);
    } catch (error) {
      showToastMessage("Ошибка при обновлении дисциплины", "danger");
    }
  };

  const handleDeleteDiscipline = async () => {
    try {
      await disciplineActions.deleteDiscipline(currentDiscipline.id);
      showToastMessage("Дисциплина успешно удалена", "success");
      setShowDeleteModal(false);
      getDisciplines(state.currentPage);
    } catch (error) {
      showToastMessage("Ошибка при удалении дисциплины", "danger");
    }
  };

  const handleAddGroups = async () => {
    try {
      if (selectedGroupIds.length > 0) {
        await disciplineActions.addGroupsToDiscipline(currentDiscipline.id, selectedGroupIds);
        showToastMessage(`Добавлено ${selectedGroupIds.length} групп к дисциплине ${currentDiscipline.name}`, "success");
      } else {
        showToastMessage("Выберите хотя бы одну группу", "warning");
        return;
      }
      
      setShowAddGroupsModal(false);
      getDisciplines(state.currentPage);
    } catch (error) {
      showToastMessage("Ошибка при добавлении групп", "danger");
    }
  };

  const handleRemoveGroup = async (groupId) => {
    try {
      await disciplineActions.removeGroupFromDiscipline(currentDiscipline.id, groupId);
      showToastMessage("Группа отвязана от дисциплины", "success");
      await loadDisciplineDetails(currentDiscipline.id);
      getDisciplines(state.currentPage);
    } catch (error) {
      showToastMessage("Ошибка при отвязке группы", "danger");
    }
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
          <Modal.Title>Создать новую дисциплину</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white">
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Название дисциплины *</Form.Label>
              <Form.Control
                type="text"
                name="name"
                value={formData.name}
                onChange={handleFormChange}
                placeholder="Введите название дисциплины"
                required
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCreateModal(false)}>
            Отмена
          </Button>
          <Button variant="primary" onClick={handleCreateDiscipline}>
            Создать
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showEditModal} onHide={() => setShowEditModal(false)}>
        <Modal.Header className="text-white" closeButton>
          <Modal.Title>Редактировать дисциплину</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white">
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Название дисциплины *</Form.Label>
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
          <Button variant="primary" onClick={handleUpdateDiscipline}>
            Сохранить
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton className="text-white">
          <Modal.Title>Подтверждение удаления</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white">
          <p>Вы уверены, что хотите удалить дисциплину <strong>"{currentDiscipline?.name}"</strong>?</p>
          <p className="text-danger">
            <small>Внимание: Это действие нельзя отменить. Все связи с группами будут удалены.</small>
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Отмена
          </Button>
          <Button variant="danger" onClick={handleDeleteDiscipline}>
            Удалить
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showAddGroupsModal} onHide={() => setShowAddGroupsModal(false)} size="lg">
        <Modal.Header closeButton className="text-white">
          <Modal.Title>Добавить группы к дисциплине: {currentDiscipline?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white" style={{ maxHeight: '400px', overflowY: 'auto' }}>
          <FormControl
          placeholder="Поиск групп..."
          size="sm"
          className="mb-2 bg-dark text-white"
          value={addGroupsSearch}
          onChange={(e) => setAddGroupsSearch(e.target.value)}
          />
          <p>Выберите группы для добавления к дисциплине:</p>
          <ListGroup>
            {availableGroups.filter(g => !currentDiscipline?.groupIds?.includes(g.id))
            .filter(g => g.name.toLowerCase().includes(addGroupsSearch.toLowerCase())) 
            .map(group => (
              <ListGroup.Item 
                key={group.id}
                action
                onClick={() => handleGroupSelection(group.id)}
                active={selectedGroupIds.includes(group.id)}
              >
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <FontAwesomeIcon icon={faUserGroup} className="me-2" />
                    {group.name}
                  </div>
                  {selectedGroupIds.includes(group.id) && (
                    <Badge bg="success">Выбрано</Badge>
                  )}
                </div>
              </ListGroup.Item>
            ))}
          </ListGroup>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowAddGroupsModal(false)}>
            Отмена
          </Button>
          <Button variant="primary" onClick={handleAddGroups}>
            Добавить выбранные
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showManageGroupsModal} onHide={() => setShowManageGroupsModal(false)} size="lg">
        <Modal.Header closeButton className="text-white">
          <Modal.Title>Управление группами дисциплины: {currentDiscipline?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-white" style={{ maxHeight: '400px', overflowY: 'auto' }}>
          <FormControl
          placeholder="Поиск групп..."
          size="sm"
          className="mb-2 bg-dark text-white"
          value={manageGroupsSearch}
          onChange={(e) => setManageGroupsSearch(e.target.value)}
          />
          {groups.filter(g => g.name.toLowerCase().includes(manageGroupsSearch.toLowerCase())).length === 0 ? (
          <p className="text-center">К дисциплине не привязаны группы</p>
          ) : (
            <>
              <p>Группы в дисциплине ({groups.length}):</p>
              <ListGroup>
                {groups.filter(g => g.name.toLowerCase().includes(manageGroupsSearch.toLowerCase()))
                .map(group => (
                  <ListGroup.Item key={group.id}>
                    <div className="d-flex justify-content-between align-items-center">
                      <div>
                        <FontAwesomeIcon icon={faUserFriends} className="me-2" />
                        {group.name}
                      </div>
                      <Button
                        size="sm"
                        variant="outline-danger"
                        onClick={() => handleRemoveGroup(group.id)}
                        title="Отвязать от дисциплины"
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
          <Button variant="secondary" onClick={() => setShowManageGroupsModal(false)}>
            Закрыть
          </Button>
        </Modal.Footer>
      </Modal>
        
      <Modal show={showAddTeachersModal} onHide={() => setShowAddTeachersModal(false)} size="lg">
  <Modal.Header closeButton>
    <Modal.Title className='text-white'>Добавить преподавателей к дисциплине: {currentDiscipline?.name}</Modal.Title>
  </Modal.Header>
  <Modal.Body style={{ maxHeight: '400px', overflowY: 'auto' }}>
    <FormControl
      placeholder="Поиск преподавателей..."
      size="sm"
      className="mb-2"
      value={addTeachersSearch}
      onChange={(e) => setAddTeachersSearch(e.target.value)}
    />
    <ListGroup>
      {availableTeachers
        .filter(t => !currentDiscipline?.teacherIds?.includes(t.id))
        .filter(s =>
          getStudentFullName(s).toLowerCase().includes(addTeachersSearch.toLowerCase()) ||
          (s.email || "").toLowerCase().includes(addTeachersSearch.toLowerCase())
          )
        .map(teacher => (
          <ListGroup.Item
            key={teacher.id}
            action
            onClick={() => handleTeacherSelection(teacher.id)}
            active={selectedTeacherIds.includes(teacher.id)}
          >
            {teacher.firstName} {teacher.lastName} ({teacher.email})
          </ListGroup.Item>
        ))
      }
    </ListGroup>
  </Modal.Body>
  <Modal.Footer>
    <Button variant="secondary" onClick={() => setShowAddTeachersModal(false)}>Отмена</Button>
    <Button variant="primary" onClick={handleAddTeachers}>Добавить выбранных</Button>
  </Modal.Footer>
</Modal>
<Modal show={showManageTeachersModal} onHide={() => setShowManageTeachersModal(false)} size="lg">
  <Modal.Header closeButton>
    <Modal.Title className='text-white'>Преподаватели дисциплины: {currentDiscipline?.name}</Modal.Title>
  </Modal.Header>
  <Modal.Body style={{ maxHeight: '400px', overflowY: 'auto' }}>
    <FormControl
      placeholder="Поиск преподавателей..."
      size="sm"
      className="mb-2"
      value={manageTeachersSearch}
      onChange={(e) => setManageTeachersSearch(e.target.value)}
    />
    {teachers.filter(s =>
          getStudentFullName(s).toLowerCase().includes(manageTeachersSearch.toLowerCase()) ||
          (s.email || "").toLowerCase().includes(manageTeachersSearch.toLowerCase())
          ).length === 0 ? (
      <p>К дисциплине не привязан ни один преподаватель</p>
    ) : (
      <ListGroup>
       {teachers.filter(s =>
          getStudentFullName(s).toLowerCase().includes(manageTeachersSearch.toLowerCase()) ||
          (s.email || "").toLowerCase().includes(manageTeachersSearch.toLowerCase())
          )
          .map(teacher => (
            <ListGroup.Item key={teacher.id} className="d-flex justify-content-between align-items-center">
              <span>{teacher.firstName} {teacher.lastName} ({teacher.email})</span>
              <Button size="sm" variant="outline-danger" onClick={() => handleRemoveTeacher(teacher.id)}>Отвязать</Button>
            </ListGroup.Item>
          ))
        }
      </ListGroup>
    )}
  </Modal.Body>
  <Modal.Footer>
    <Button variant="secondary" onClick={() => setShowManageTeachersModal(false)}>Закрыть</Button>
  </Modal.Footer>
</Modal>

      <Card className={"border border-dark bg-dark text-white"} style={{display: 'flex', flexDirection: 'column', flex: '1 1 auto', minHeight: 0, overflow: 'hidden' }}>
        <Card.Header className="bg-secondary text-white">
          <div className="container-fluid">
            <div className="row g-3 align-items-center">
              <div className="col-12 col-md-4 col-lg-3">
                <FontAwesomeIcon icon={faList} /> Управление дисциплинами
              </div>
              <div className="col-12 col-md-5 col-lg-4">
                <InputGroup size="sm">
                  <FormControl
                    placeholder="Поиск по названию дисциплины..."
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
              <col style={{ width: "20%" }} />
              <col style={{ width: "20%" }} />
              <col style={{ width: "20%" }} />
              <col style={{ width: "20%" }} />
              <col style={{ width: "20%" }} />
              <col style={{ width: "20%" }} />
            </colgroup>
            <thead>
              <tr>
                <th>Название дисциплины</th>
                <th>Количество групп</th>
                <th>Количество преподавателей</th>
                <th>Действия над группами</th>
                <th>Действия над преподавателями</th>
                <th>Действия над дисциплинами</th>
              </tr>
            </thead>
            <tbody>
              {state?.disciplines?.length === 0 ? (
                <tr align="center">
                  <td colSpan="6">Нет дисциплин</td>
                </tr>
              ) : (
                state?.disciplines?.map((discipline) => (
                  <tr key={discipline.id}>
                    <td>
                      <strong>{discipline.name}</strong>
                      
                    </td>
                    <td>
                      <Badge bg="info" className="me-1">
                        <FontAwesomeIcon icon={faUsers} /> {discipline.groupsCount || 0}
                      </Badge>
                    </td>
                     <td>
                      <Badge bg="info" className="me-1">
                        <FontAwesomeIcon icon={faUsers} /> {discipline.teacherIds?.length || 0}
                      </Badge>
                    </td>
                    <td>
                      <ButtonGroup>
                        <Button
                          size="sm"
                          variant="outline-primary"
                          onClick={() => handleOpenAddGroupsModal(discipline)}
                          title="Добавить группы"
                          className="me-1"
                        >
                          <FontAwesomeIcon icon={faUserGroup} />
                        </Button>
                        <Button
                          size="sm"
                          variant="outline-warning"
                          onClick={() => handleOpenManageGroupsModal(discipline)}
                          title="Управление группами"
                          className="me-1"
                        >
                          <FontAwesomeIcon icon={faUserMinus} />
                        </Button>
                      </ButtonGroup>
                    </td>
                    <td>
                      <Button size="sm" variant="outline-primary" onClick={() => handleOpenAddTeachersModal(discipline)}>
                        <FontAwesomeIcon icon={faUserGroup} />
                        </Button>
                        <Button size="sm" variant="outline-warning" onClick={() => handleOpenManageTeachersModal(discipline)}>
                        <FontAwesomeIcon icon={faUserMinus} />
                        </Button>
                    </td>
                    <td><Button
                          size="sm"
                          variant="outline-info"
                          onClick={() => handleOpenEditModal(discipline)}
                          title="Редактировать"
                          className="me-1"
                        >
                          <FontAwesomeIcon icon={faEdit} />
                        </Button>
                        <Button
                          size="sm"
                          variant="outline-danger"
                          onClick={() => handleOpenDeleteModal(discipline)}
                          title="Удалить"
                        >
                          <FontAwesomeIcon icon={faTrash} />
                        </Button>
                        </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </Card.Body>

        {state?.disciplines?.length > 0 && (
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

export default DisciplinePanel;