import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/client";

export default function Register() {
  const [form, setForm] = useState({ fullName: "", email: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const onChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await api.post("/auth/register", form);
      navigate("/login", { state: { registered: true } });
    } catch (err) {
      setError(err.response?.data?.error ?? "Check your inputs");
    }
  };

  return (
    <form onSubmit={onSubmit} className="card auth-card">
      <h1>Create an account</h1>
      <div className="field">
        <label htmlFor="fullName">Full name</label>
        <input id="fullName" name="fullName" placeholder="Full name"
          value={form.fullName} onChange={onChange} required />
      </div>
      <div className="field">
        <label htmlFor="email">Email</label>
        <input id="email" name="email" type="email" placeholder="Email"
          value={form.email} onChange={onChange} required />
      </div>
      <div className="field">
        <label htmlFor="password">Password</label>
        <input id="password" name="password" type="password" minLength={8}
          placeholder="At least 8 characters"
          value={form.password} onChange={onChange} required />
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      <button className="btn btn-primary btn-block" type="submit">Register</button>
      <p className="switch-link">Have an account? <Link to="/login">Log in</Link></p>
    </form>
  );
}
