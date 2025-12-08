import './App.css'
import React, { useState, useEffect, useCallback, useContext  } from 'react';
import MyToast from './MyToast';
import * as userActions from './service/userActions';
import {
  Card,
  Table,
  ButtonGroup,
  Button,
  InputGroup,
  FormControl,
  Form,
  Modal,
} from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faList,
  faTrash,
  faEdit,
  faPlus,
  faStepBackward,
  faFastBackward,
  faStepForward,
  faFastForward,
  faSearch,
  faTimes,
} from "@fortawesome/free-solid-svg-icons";
import { getCurrentUserFromToken } from './service/jwtHelper';
import { AuthContent } from './AuthContent';

const UserPanel = () => {
  const { setView } = useContext(AuthContent);
  const [roleFilter, setRoleFilter] = useState("");
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState("success");
  
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState("create");
  const [currentUser, setCurrentUser] = useState(null);
  
  const [formData, setFormData] = useState({
    login: "",
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    role: "STUDENT"
  });

  const [state, setState] = useState({
    users: [],
    search: "",
    currentPage: 1,
    usersPerPage: 5,
    totalPages: 0,
    totalElements: 0,
  });
  
  const getUsers = useCallback(async (page) => {
    try {
      const pageNumber = page - 1;
      const data = await userActions.filterUsers(
        state.search,
        roleFilter,
        pageNumber,
        state.usersPerPage
      );
      
      setState(prev => ({
        ...prev,
        users: data.content,
        totalPages: data.totalPages,
        totalElements: data.totalElements,
        currentPage: data.number + 1,
      }));
    } catch (error) {
      console.error("Ошибка при загрузке пользователей:", error);
      showToastMessage("Ошибка загрузки пользователей", "danger");
    }
  }, [state.search, state.usersPerPage, roleFilter]);
  const handleRoleFilterChange = (e) => {
  setRoleFilter(e.target.value);
};
  useEffect(() => {
    getUsers(state.currentPage);
  }, [getUsers, state.currentPage]);
  useEffect(() => {
    getUsers(1);
  }, [roleFilter]);
  const handleSearchChange = (e) => {
    setState(prev => ({ ...prev, search: e.target.value }));
  };

  const handleCancelSearch = () => {
    setState(prev => ({ ...prev, search: "" }));
    getUsers(1);
  };

  const handleSearch = () => {
    getUsers(1);
  };

  const paginationActions = {
    firstPage: () => getUsers(1),
    prevPage: () => getUsers(state.currentPage - 1),
    nextPage: () => getUsers(state.currentPage + 1),
    lastPage: () => getUsers(state.totalPages),
  };

  const showToastMessage = (message, type = "success") => {
    setToastMessage(message);
    setToastType(type);
    setShowToast(true);
    setTimeout(() => setShowToast(false), 3000);
  };
  
  const handleDeleteUser = async (userId, userEmail) => {
    try {
      await userActions.deleteUser(userId);

      const currentUser = getCurrentUserFromToken();
      const currentUserEmail = currentUser?.sub;
      if (currentUserEmail === userEmail) {
        localStorage.removeItem("auth_token");
        localStorage.removeItem("token");
        setView("login")
        return;
      }

      showToastMessage("Пользователь успешно удален", "danger");
      await getUsers(state.currentPage);
    } catch (error) {
      console.error("Ошибка при удалении:", error);
      showToastMessage("Ошибка при удалении пользователя", "danger");
    }
  };


  const handleOpenCreateModal = () => {
    setFormData({
      login: "",
      email: "",
      password: "",
      firstName: "",
      lastName: "",
      role: "STUDENT"
    });
    setModalMode("create");
    setShowModal(true);
  };

  const handleOpenEditModal = (user) => {
    setCurrentUser(user);
    setFormData({
      login: user.login,
      email: user.email,
      password: "", 
      firstName: user.firstName,
      lastName: user.lastName,
      role: user.role
    });
    setModalMode("edit");
    setShowModal(true);
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmitUser = async () => {
    try {
      if (modalMode === "create") {
        const { password, ...dataWithoutPassword } = formData;
        await userActions.saveUser(dataWithoutPassword);
        showToastMessage("Пользователь создан. Пароль отправлен на почту", "success");
      } else {
        const userData = {
          ...formData,
          id: currentUser.id
        };
        await userActions.updateUser(currentUser.id, userData);
        
        showToastMessage("Пользователь обновлен", "success");
        
      }
      
      setShowModal(false);
      getUsers(state.currentPage);
    } catch (error) {
      console.error("Ошибка при сохранении пользователя:", error);
      showToastMessage(error.response?.data?.message || "Ошибка при сохранении", "danger");
    }
  };

  const getFullName = (user) => {
    return `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'Не указано';
  };

  const getRoleName = (role) => {
    const roleNames = {
      'STUDENT': 'Студент',
      'TEACHER': 'Преподаватель',
      'ADMIN': 'Администратор'
    };
    return roleNames[role] || role;
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

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {modalMode === "create" ? "Создать пользователя" : "Редактировать пользователя"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Логин *</Form.Label>
              <Form.Control
                type="text"
                name="login"
                value={formData.login}
                onChange={handleFormChange}
                required
              />
            </Form.Group>
            
            <Form.Group className="mb-3">
              <Form.Label>Email *</Form.Label>
              <Form.Control
                type="email"
                name="email"
                value={formData.email}
                onChange={handleFormChange}
                required
              />
            </Form.Group>
            
            <Form.Group className="mb-3">
              <Form.Label>Имя</Form.Label>
              <Form.Control
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleFormChange}
              />
            </Form.Group>
            
            <Form.Group className="mb-3">
              <Form.Label>Фамилия</Form.Label>
              <Form.Control
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleFormChange}
              />
            </Form.Group>
            
            <Form.Group className="mb-3">
              <Form.Label>Роль *</Form.Label>
              <Form.Select
                name="role"
                value={formData.role}
                onChange={handleFormChange}
              >
                <option value="STUDENT">Студент</option>
                <option value="TEACHER">Преподаватель</option>
                <option value="ADMIN">Администратор</option>
              </Form.Select>
            </Form.Group>
        
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Отмена
          </Button>
          <Button variant="primary" onClick={handleSubmitUser}>
            {modalMode === "create" ? "Создать" : "Сохранить"}
          </Button>
        </Modal.Footer>
      </Modal>

      <Card className={"border border-dark bg-dark text-white"} style={{display: 'flex', flexDirection: 'column', flex: '1 1 auto', minHeight: 0, overflow: 'hidden' }}>
        <Card.Header className="bg-secondary text-white">
          <div className="container-fluid">
            <div className="row g-3 align-items-center">
              <div className="col-12 col-md-4 col-lg-2">
                <FontAwesomeIcon icon={faList} /> Список пользователей
              </div>
              <div className="col-12 col-md-5 col-lg-4">
                <InputGroup size="sm">
                  <FormControl
                    placeholder="Поиск по логину, email, имени..."
                    value={state.search}
                    className="info-border bg-dark text-white"
                    onChange={handleSearchChange}
                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                  />
                  
                  <Button variant="outline-dark" className="no-hover-effect" onClick={handleSearch}>
                    <FontAwesomeIcon icon={faSearch} />
                  </Button>
                  <Button variant="outline-dark" className="no-hover-effect me-2" onClick={handleCancelSearch}>
                    <FontAwesomeIcon icon={faTimes} />
                  </Button>
                  <Form.Select
                  value={roleFilter}
                  onChange={handleRoleFilterChange}
                  className="bg-dark text-white me-2"
                  size="sm"
                  >
                  <option value="">Все роли</option>
                  <option value="STUDENT">Студент</option>
                  <option value="TEACHER">Преподаватель</option>
                  <option value="ADMIN">Администратор</option>
                  </Form.Select>
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
              <col style={{ width: "25%" }} />
              <col style={{ width: "25%" }} />
              <col style={{ width: "20%" }} />
              <col style={{ width: "30%" }} />
            </colgroup>
            <thead>
              <tr>
                <th>ФИО</th>
                <th>Email</th>
                <th>Роль</th>
                <th>Действия</th>
              </tr>
            </thead>
            <tbody>
              {state?.users?.length === 0 ? (
                <tr align="center">
                  <td colSpan="4">Нет пользователей</td>
                </tr>
              ) : (
                state?.users?.map((user) => (
                  <tr key={user.id}>
                    <td>{getFullName(user)}</td>
                    <td>{user.email}</td>
                    <td>
                        {getRoleName(user.role)}
                    </td>
                    <td>
                      <ButtonGroup className="justify-content-between">
                        <Button
                          size="sm"
                          variant="outline-warning"
                          onClick={() => handleOpenEditModal(user)}
                        >
                          <FontAwesomeIcon icon={faEdit} />
                        </Button>
                        <Button
                          size="sm"
                          variant="outline-danger"
                          onClick={() => handleDeleteUser(user.id, user.email)}
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

        {state?.users?.length > 0 && (
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

export default UserPanel;