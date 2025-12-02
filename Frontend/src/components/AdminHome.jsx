import { useContext } from "react";
import { AuthContent } from "./AuthContent";

const AdminHome = () => {
  const {setRole} = useContext(AuthContent);
  setRole("ADMIN");
  return (
   <div className="container mt-4">
    <h1 className="text-white" >Страница сотрудника</h1>
  <div className="row">
    <div className="col-12">
      <h2 className="text-white">Добро пожаловать, сотрудник!</h2>
      <p className="text-light">Выберите одно из доступных действий ниже.</p>
    </div>

</div>
</div>
  );
};

export default AdminHome;