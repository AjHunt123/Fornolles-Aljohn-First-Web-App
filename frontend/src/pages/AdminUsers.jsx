import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/client";

export default function AdminUsers() {
  const [users, setUsers] = useState(null);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    api.get("/dashboard/users")
      .then((res) => setUsers(res.data))
      .catch((err) => {
        if (err.response?.status === 403) navigate("/dashboard");
        else setError(err.response?.data?.error ?? "Could not load users");
      });
  }, [navigate]);

  if (error) return <div className="alert alert-error">{error}</div>;
  if (!users) return <p className="center-screen">Loading users…</p>;

  return (
    <div>
      <h1>All users</h1>
      <p className="lead">{users.length} registered {users.length === 1 ? "user" : "users"}</p>
      <div className="card table-wrap">
        <table>
          <thead>
            <tr><th>ID</th><th>Full name</th><th>Email</th><th>Role</th></tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.fullName}</td>
                <td>{u.email}</td>
                <td><span className={`badge badge-${u.role.toLowerCase()}`}>{u.role}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <p><Link to="/dashboard">← Back to dashboard</Link></p>
    </div>
  );
}
