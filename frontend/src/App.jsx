import { Navigate, Route, Routes } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute";
import Register from "./pages/Register";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import EditProfile from "./pages/EditProfile";
import AdminUsers from "./pages/AdminUsers";

export default function App() {
  return (
    <main className="container">
      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={
          <ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/profile" element={
          <ProtectedRoute><EditProfile /></ProtectedRoute>} />
        <Route path="/admin/users" element={
          <ProtectedRoute role="ADMIN"><AdminUsers /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </main>
  );
}
