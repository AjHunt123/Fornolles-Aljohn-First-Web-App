# UserHub — My First Full-Stack Web App

UserHub is a simple user management application built with **Spring Boot** (REST API) and **ReactJS** (Vite).
It demonstrates user registration with validation, login with password hashing, token-based authentication,
role-based authorization (`USER` / `ADMIN`), an **H2** database through Spring Data JPA, and a protected dashboard.

The React frontend talks to the Spring Boot backend through Axios, and the backend stores data in H2.

```
React UI (:5173)  ──HTTP + JSON──▶  Spring Boot API (:8080)  ──SQL──▶  H2 Database (in-memory)
```

## Technologies Used

| Layer    | Technology |
|----------|------------|
| Backend  | Java 21, Spring Boot 4.x, Spring Web (MVC), Spring Data JPA, Bean Validation |
| Database | H2 (in-memory) + H2 console |
| Frontend | ReactJS 18, Vite 5, React Router 6, Axios |
| Build    | Maven (backend), npm (frontend) |

## System Requirements / Prerequisites

- **JDK 21 or newer** (`java -version`)
- **Maven 3.9+** (`mvn -version`) — or run the backend from IntelliJ IDEA / VS Code, which bundle Maven
- **Node.js LTS** with npm (`node -v`, `npm -v`)
- **Git**
- Free ports: **8080** (backend) and **5173** (frontend)
- Optional: **Postman** for API testing

## Project Structure

```
Fornolles-Aljohn-First-Web-App/
├── backend/                         Spring Boot source
│   ├── pom.xml
│   └── src/main/
│       ├── java/edu/bsit/fornolles/
│       │   ├── UserhubApplication.java
│       │   ├── model/               User
│       │   ├── repository/          UserRepository
│       │   ├── dto/                 requests & responses (records)
│       │   ├── service/             UserService (register, login, tokens, roles)
│       │   ├── controller/          AuthController, DashboardController, UserController
│       │   ├── exception/           AuthException, ForbiddenException, ApiExceptionHandler
│       │   └── config/              DataSeeder (CommandLineRunner)
│       └── resources/application.properties
├── frontend/                        ReactJS (Vite) source
│   └── src/
│       ├── api/client.js            Axios instance + token interceptor
│       ├── components/              ProtectedRoute.jsx
│       ├── pages/                   Register, Login, Dashboard, EditProfile, AdminUsers
│       ├── App.jsx, main.jsx
├── docs/
│   ├── REQUIREMENTS.md
│   └── screenshots/
└── README.md
```

## Backend Setup (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

Or open the `backend/` folder in IntelliJ / VS Code and run `UserhubApplication`.
The first run downloads Maven dependencies, so give it a few minutes. When you see
`Tomcat started on port 8080` the API is ready.

- API base URL: **http://localhost:8080/api**
- H2 console: **http://localhost:8080/h2-console** — JDBC URL `jdbc:h2:mem:userhubdb`, user `sa`, empty password

### Default ADMIN account (seeded on every startup)

| Email | Password |
|-------|----------|
| `admin@userhub.com` | `Admin@123` |

Change it in `backend/src/main/resources/application.properties` (`app.admin.*`).

> The database is **in-memory**: all registered users disappear and every login token is cleared whenever the backend restarts. The admin account is created again automatically.

## Frontend Setup (ReactJS + Vite)

Open a **second terminal**:

```bash
cd frontend
npm install
npm run dev
```

- App URL: **http://localhost:5173**
- The frontend calls `http://localhost:8080/api` by default. To use another backend URL, copy `.env.example` to `.env` and edit `VITE_API_URL`.

## Running Both Applications

1. Start the backend (port **8080**).
2. Start the frontend (port **5173**).
3. Open http://localhost:5173 in your browser.

| Application | URL | Port |
|-------------|-----|------|
| React frontend | http://localhost:5173 | 5173 |
| Spring Boot API | http://localhost:8080/api | 8080 |
| H2 console | http://localhost:8080/h2-console | 8080 |

## How to Test the Application

### End-to-end checklist (browser)

1. **Register** at `/register` (try a password shorter than 8 characters, then a valid one).
2. **Register the same email again** → "Email already used" error is shown (409).
3. **Login** at `/login` → you land on the **Dashboard** (name, role, member since, total users).
4. **Refresh** the dashboard → you are still logged in (token kept in `localStorage`).
5. **Edit profile** (`/profile`) → change your full name and save; the dashboard shows the new name.
6. As a normal user, open `/admin/users` → you are redirected back to the dashboard (ADMIN only).
7. **Logout**, then open `/dashboard` directly → you are sent to `/login`.
8. Log in as **admin@userhub.com / Admin@123** → "View all users" appears; open it to see every registered user.
9. **Restart the backend** → you are logged out (tokens live in memory).
10. In the H2 console run `SELECT * FROM USERS;` → passwords are stored as hashes.

### API test (Postman or curl)

```bash
# 1. register  -> 201
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Ana Cruz","email":"ana@bsit.edu","password":"password123"}'

# 2. login     -> 200 { "token": "...", "user": {...} }
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana@bsit.edu","password":"password123"}'

TOKEN=<paste the token here>

curl -i http://localhost:8080/api/dashboard        -H "Authorization: Bearer $TOKEN"   # 200
curl -i http://localhost:8080/api/dashboard/users  -H "Authorization: Bearer $TOKEN"   # 403 (USER, not ADMIN)
curl -i -X PUT http://localhost:8080/api/users/me  -H "Authorization: Bearer $TOKEN" \
     -H "Content-Type: application/json" -d '{"fullName":"Ana C. Cruz"}'              # 200
curl -i -X POST http://localhost:8080/api/auth/logout -H "Authorization: Bearer $TOKEN" # 200
curl -i http://localhost:8080/api/dashboard        -H "Authorization: Bearer $TOKEN"   # 401 (token removed)
curl -i http://localhost:8080/api/dashboard                                             # 401 (no header)
```

## API Summary

| Method | Endpoint | Access | Success | Description |
|--------|----------|--------|---------|-------------|
| POST | `/api/auth/register` | Public | 201 | Register a new USER |
| POST | `/api/auth/login` | Public | 200 | Login → `{ token, user }` |
| POST | `/api/auth/logout` | Token | 200 | Remove the token on the server |
| GET | `/api/dashboard` | Token | 200 | Dashboard data for the logged-in user |
| GET | `/api/dashboard/users` | ADMIN | 200 | List all registered users |
| PUT | `/api/users/me` | Token | 200 | Update own full name |

Error responses are JSON like `{ "error": "message" }`:
**400** validation failed · **401** not logged in / bad credentials / session expired · **403** wrong role · **409** email already used.

## Additional Configuration

| Setting (in `application.properties`) | Default | Purpose |
|---|---|---|
| `server.port` | `8080` | Backend port |
| `spring.datasource.url` | `jdbc:h2:mem:userhubdb` | In-memory H2 database |
| `app.admin.*` | admin@userhub.com / Admin@123 | Seeded ADMIN account |

CORS: the controllers use `@CrossOrigin(origins = "http://localhost:5173")`. If you change the frontend port, update it there too.

**Common problems**

| Symptom | Fix |
|---------|-----|
| "blocked by CORS policy" | Frontend must run on `http://localhost:5173` |
| 404 on `/h2-console` | Make sure the `spring-boot-h2console` dependency is in `pom.xml` |
| Logged out after a backend restart | Expected: sessions are in memory — log in again |
| SQL error near "user" | The table is named `users` (`@Table(name = "users")`) |
| Port 8080 already in use | Stop the old server or change `server.port` |
| "Cannot reach the server" in the UI | Backend is not running |

## Notes (learning project vs. production)

This lab follows the course slides, so it uses simple building blocks: SHA-256 password hashing, tokens kept in a Java `Map`, and an in-memory database. A production app would use BCrypt with a salt, Spring Security + JWT, MySQL/PostgreSQL, HttpOnly cookies and HTTPS.

## Documentation

- Feature list: [`docs/REQUIREMENTS.md`](docs/REQUIREMENTS.md)
- Screenshots: [`docs/screenshots/`](docs/screenshots/)

## Author

Aljohn Niño A. Fornolles — BSIT-3, University of Cebu – Banilad Campus
