package edu.bsit.fornolles.service;

import edu.bsit.fornolles.dto.*;
import edu.bsit.fornolles.exception.AuthException;
import edu.bsit.fornolles.exception.ForbiddenException;
import edu.bsit.fornolles.model.User;
import edu.bsit.fornolles.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserRepository repo;
    // active sessions: token -> userId (lives in memory, so a backend restart logs everyone out)
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // ------------------------------------------------------------------ Feature 1: registration
    public UserResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (repo.existsByEmail(email))
            throw new IllegalArgumentException("Email already used");
        User u = new User();
        u.setFullName(req.fullName().trim());
        u.setEmail(email);
        u.setPassword(hash(req.password()));
        return toResponse(repo.save(u));
    }

    // ------------------------------------------------------------------ Feature 2: authn & authz
    public LoginResponse login(LoginRequest req) {
        User u = repo.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> new AuthException("Invalid credentials"));
        if (!u.getPassword().equals(hash(req.password())))
            throw new AuthException("Invalid credentials");
        String token = UUID.randomUUID().toString();
        sessions.put(token, u.getId());
        return new LoginResponse(token, toResponse(u));
    }

    /** Reads "Authorization: Bearer <token>" and returns the logged-in user, or throws 401. */
    public User authenticate(String header) {
        String token = extractToken(header);
        Long id = sessions.get(token);
        if (id == null) throw new AuthException("Session expired");
        return repo.findById(id).orElseThrow(() -> new AuthException("Session expired"));
    }

    /** Authorization gate: throws 403 unless the user has the required role. */
    public void requireRole(User u, String role) {
        if (!role.equals(u.getRole()))
            throw new ForbiddenException("Access denied");
    }

    // ------------------------------------------------------------------ Extra: server-side logout
    /** Removes the token from the session map, so it can never be used again. */
    public void logout(String header) {
        sessions.remove(extractToken(header));
    }

    // ------------------------------------------------------------------ Feature 3: dashboard
    public DashboardResponse dashboard(User u) {
        return new DashboardResponse(u.getFullName(), u.getEmail(),
                u.getRole(), u.getCreatedAt(), repo.count());
    }

    public List<UserResponse> findAll() {
        return repo.findAll(Sort.by("id")).stream().map(this::toResponse).toList();
    }

    // ------------------------------------------------------------------ Extra: edit my profile
    public UserResponse updateProfile(User u, String fullName) {
        u.setFullName(fullName.trim());
        return toResponse(repo.save(u));
    }

    // ------------------------------------------------------------------ Extra: seed an ADMIN
    public void createAdminIfMissing(String fullName, String email, String rawPassword) {
        String e = email.trim().toLowerCase();
        if (repo.existsByEmail(e)) return;
        User admin = new User();
        admin.setFullName(fullName);
        admin.setEmail(e);
        admin.setPassword(hash(rawPassword));
        admin.setRole("ADMIN");
        repo.save(admin);
    }

    // ------------------------------------------------------------------ helpers
    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer "))
            throw new AuthException("Please log in");
        return header.substring(7).trim();
    }

    private String hash(String raw) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getFullName(),
                u.getEmail(), u.getRole());
    }
}
