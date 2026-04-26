import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Inbox from "./pages/Inbox";
import Semana from "./pages/Semana";
import Urgentes from "./pages/Urgentes";
import Concluidas from "./pages/Concluida";
import Layout from "@/components/Layout";
import PrivateRoute from "./components/PrivateRoute";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route element={<PrivateRoute />}>
          <Route element={<Layout />}>
            <Route path="/" element={<Navigate to="/inbox" />} />
            <Route path="/inbox" element={<Inbox />} />
            <Route path="/semana" element={<Semana />} />
            <Route path="/urgentes" element={<Urgentes />} />
            <Route path="/concluidas" element={<Concluidas />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;