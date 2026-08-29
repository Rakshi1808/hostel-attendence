import { useState, useEffect } from "react";
import LoginPage        from "./components/LoginPage";
import StudentDashboard from "./components/StudentDashboard";
import WardenDashboard  from "./components/WardenDashboard";
import AdminDashboard   from "./components/AdminDashboard";

export default function App() {
  const [role, setRole] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const saved = localStorage.getItem("role");
    if (token && saved) setRole(saved);
  }, []);

  function handleLogin(r) { setRole(r); }

  function handleLogout() {
    localStorage.clear();
    setRole(null);
  }

  if (!role)              return <LoginPage onLogin={handleLogin} />;
  if (role === "student") return <StudentDashboard onLogout={handleLogout} />;
  if (role === "warden")  return <WardenDashboard  onLogout={handleLogout} />;
  if (role === "admin")   return <AdminDashboard   onLogout={handleLogout} />;
}