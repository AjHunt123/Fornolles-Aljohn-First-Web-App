import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/client";

export default function EditProfile() {
  const stored = JSON.parse(localStorage.getItem("user") || "null");
  const [fullName, setFullName] = useState(stored?.fullName ?? "");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      const { data } = await api.put("/users/me", { fullName });
      localStorage.setItem("user", JSON.stringify(data)); // keep the saved user in sync
      setFullName(data.fullName);
      setSuccess("Profile updated successfully.");
    } catch (err) {
      setError(err.response?.data?.error ?? "Could not update profile");
    }
  };

  return (
    <form onSubmit={onSubmit} className="card auth-card">
      <h1>Edit my profile</h1>
      <p className="muted">You can change your full name. Your email and role cannot be changed.</p>
      {success && <div className="alert alert-success">{success}</div>}
      {error && <div className="alert alert-error">{error}</div>}
      <div className="field">
        <label htmlFor="fullName">Full name</label>
        <input id="fullName" value={fullName}
          onChange={(e) => setFullName(e.target.value)} required />
      </div>
      <div className="field">
        <label htmlFor="email">Email</label>
        <input id="email" value={stored?.email ?? ""} disabled />
      </div>
      <button className="btn btn-primary btn-block" type="submit">Save changes</button>
      <p className="switch-link"><Link to="/dashboard">← Back to dashboard</Link></p>
    </form>
  );
}
