import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import api from "../api/client";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  const onChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const { data } = await api.post("/auth/login", form);
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data.user));
      navigate("/dashboard");
    } catch (err) {
      setError(
        err.response ? "Invalid email or password"
                     : "Cannot reach the server. Is the Spring Boot backend running on port 8080?"
      );
    }
  };

  return (
    <form onSubmit={onSubmit} className="card auth-card">
      <h1>Welcome back</h1>
      {location.state?.registered && (
        <div className="alert alert-success">Registration successful! You can log in now.</div>
      )}
      <div className="field">
        <label htmlFor="email">Email</label>
        <input id="email" name="email" type="email" placeholder="Email"
          value={form.email} onChange={onChange} required />
      </div>
      <div className="field">
        <label htmlFor="password">Password</label>
        <input id="password" name="password" type="password" placeholder="Password"
          value={form.password} onChange={onChange} required />
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      <button className="btn btn-primary btn-block" type="submit">Login</button>
      <p className="switch-link">No account yet? <Link to="/register">Register</Link></p>
    </form>
  );
}
