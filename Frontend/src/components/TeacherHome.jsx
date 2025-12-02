import React, { useContext } from "react";
import { AuthContent } from "./AuthContent";

const TeacherWelcome = () => {
  const { email } = useContext(AuthContent);

  return (
    <div className="container mt-4">
      <h1 className="text-white">Личный кабинет учителя</h1>

      <div className="row">
        <div className="col-12">
          <h2 className="text-white">Добро пожаловать, {email}!</h2>
          <p className="text-light">Здесь можно.</p>
        </div>
      </div>
    </div>
  );
};

export default TeacherWelcome;
