import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/client";

export default function Dashboard() {
  const [info, setInfo] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    api.get("/dashboard").then((res) => setInfo(res.data))
      .catch(() => navigate("/login"));
  }, [navigate]);

  // Server-side logout: ask the API to invalidate the token, then clear it here too.
  const logout = async () => {
    try {
      await api.post("/auth/logout");
    } catch {
      // even if the call fails we still clear the local session
    }
    localStorage.clear();
    navigate("/login");
  };

  if (!info) return <p className="center-screen">Loading…</p>;

  return (
    <div className="card auth-card">
      <h1>Welcome, {info.fullName}!</h1>
      <p>
        <span className={`badge badge-${info.role.toLowerCase()}`}>{info.role}</span>
      </p>

      <dl className="details">
        <dt>Email</dt>
        <dd>{info.email}</dd>
        <dt>Member since</dt>
        <dd>{info.memberSince}</dd>
        <dt>Total users</dt>
        <dd>{info.totalUsers}</dd>
      </dl>

      <div className="actions">
        <Link to="/profile" className="btn btn-outline">Edit profile</Link>
        {info.role === "ADMIN" && (
          <Link to="/admin/users" className="btn btn-primary">View all users</Link>
        )}
        <button className="btn btn-danger" onClick={logout}>Logout</button>
      </div>
    </div>
  );
}
