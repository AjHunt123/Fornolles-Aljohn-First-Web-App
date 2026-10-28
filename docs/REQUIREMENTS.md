# UserHub — Implemented Features

Legend: **Implemented** = the code is in the repository and wired React ↔ Spring Boot ↔ H2.

## A. Core Features (from the lab slides)

| # | Feature | Description | Expected behavior | API endpoint | Status |
|---|---------|-------------|-------------------|--------------|--------|
| 1 | Layered backend | Spring Boot API split into model (entity), repository, service, controller, DTO and exception packages under `edu.bsit.fornolles` | Controllers receive HTTP, `UserService` holds the business rules, `UserRepository` talks to H2 through Spring Data JPA | — | Implemented |
| 2 | H2 + JPA | In-memory H2 database, `ddl-auto=update` builds the `users` table from the `User` entity | Data is inspectable at `/h2-console`; resets on every restart | — | Implemented |
| 3 | User registration + validation | New users sign up with full name, email and password | Name and email required, valid email format, password ≥ 8 chars → otherwise **400**. Duplicate email → **409** "Email already used". Success → **201** with the user (never the password). New users get role `USER` | `POST /api/auth/register` | Implemented |
| 4 | Authentication + password hashing | Login with email and password | Password stored as a SHA-256 hash. Wrong email or password → **401** "Invalid credentials" (same message for both). Success → **200** `{ token, user }` | `POST /api/auth/login` | Implemented |
| 5 | Token-based authentication | Random UUID token mapped to the user in a `ConcurrentHashMap` | Client sends `Authorization: Bearer <token>`; missing header → **401** "Please log in"; unknown token → **401** "Session expired" | all protected endpoints | Implemented |
| 6 | Role-based authorization (USER / ADMIN) | `requireRole()` is the authorization gate | A `USER` calling an ADMIN endpoint gets **403** "Access denied" | `GET /api/dashboard/users` | Implemented |
| 7 | Protected dashboard endpoint | Personal summary for the logged-in user | Returns full name, email, role, member-since date and total user count; **401** without a valid token | `GET /api/dashboard` | Implemented |
| 8 | Exception handling + HTTP responses | `AuthException`, `ForbiddenException` and `ApiExceptionHandler` (`@RestControllerAdvice`) | Clean JSON `{ "error": "..." }` with 400 / 401 / 403 / 409 | — | Implemented |
| 9 | React + Vite + React Router + Axios | Single-page app with client-side routing | Routes `/register`, `/login`, `/dashboard`, `/profile`, `/admin/users`; unknown paths redirect to `/login` | — | Implemented |
| 10 | Registration page | Form with required fields and `minLength=8` password | Shows the API error message; on success redirects to login with a success banner | `POST /api/auth/register` | Implemented |
| 11 | Login page | Email + password form | Stores `token` and `user` in `localStorage`, then opens the dashboard; shows an error on failure | `POST /api/auth/login` | Implemented |
| 12 | Protected user dashboard | Page for logged-in users only | Loads data from the API on open; shows name, role badge, member since and total users | `GET /api/dashboard` | Implemented |
| 13 | Token handling (Axios interceptor) | `api/client.js` adds the `Bearer` token to every request | Token survives page refresh; a `401` response clears the session and returns the user to `/login` | — | Implemented |
| 14 | Protected routes + logout | `ProtectedRoute` (optional `role` prop) | No token → `/login`; wrong role → `/dashboard`; Logout button ends the session | — | Implemented |

## B. Additional Features (50 points)

| # | Feature | Points | Description | Expected behavior | API endpoint | Status |
|---|---------|--------|-------------|-------------------|--------------|--------|
| 15 | Seed an ADMIN user | 10 | `DataSeeder` implements `CommandLineRunner` | On every startup creates `admin@userhub.com` / `Admin@123` with role `ADMIN` (hashed password) if it does not exist yet | — | Implemented |
| 16 | Admin users page | 15 | ADMIN-only page at `/admin/users` | Table of all registered users (ID, name, email, role). Backend enforces ADMIN (**403** otherwise); the React route and the "View all users" button are also restricted to ADMIN | `GET /api/dashboard/users` | Implemented |
| 17 | Server-side logout | 10 | Endpoint that removes the token from the session map | After logout the same token returns **401** "Session expired"; the Logout button calls this endpoint, then clears `localStorage` | `POST /api/auth/logout` | Implemented |
| 18 | Edit My Profile | 15 | Logged-in users update their full name on `/profile` | Name required (2–100 chars); email and role cannot be changed; the saved user in `localStorage` is updated; **401** without a token | `PUT /api/users/me` | Implemented |

## Screenshots

Saved in `docs/screenshots/`:

- `01-register.png` — registration page (ideally with a validation or duplicate-email error)
- `02-login.png` — login page
- `03-dashboard-user.png` — user dashboard
- `04-edit-profile.png` — edit profile (after saving)
- `05-admin-users.png` — admin users page
- `06-admin-dashboard.png` — dashboard when logged in as ADMIN
- `07-h2-console.png` — `USERS` table with hashed passwords
- `08-forbidden-403.png` — Postman: USER calling `GET /api/dashboard/users` → 403
- `09-logout-401.png` — Postman: same token after logout → 401
