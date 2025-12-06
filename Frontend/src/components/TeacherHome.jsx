import React, { useContext } from "react";
import { AuthContent } from "./AuthContent";

const TeacherWelcome = () => {
  const { email, role } = useContext(AuthContent);
  

  return (
    <div className="container mt-4">
      <h1 className="text-white">Личный кабинет учителя</h1>

      <div className="row">
        <div className="col-12">
          <h2 className="text-white">Добро пожаловать, {email || "пользователь"}!</h2>
          <p className="text-light">Роль: {role || "не определена"}</p>
          <p className="text-light">Здесь можно...</p>
        </div>
      </div>
    </div>
  );
};

export default TeacherWelcome;