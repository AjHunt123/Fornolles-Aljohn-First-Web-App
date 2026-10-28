package edu.bsit.fornolles.controller;

import edu.bsit.fornolles.dto.LoginRequest;
import edu.bsit.fornolles.dto.LoginResponse;
import edu.bsit.fornolles.dto.RegisterRequest;
import edu.bsit.fornolles.dto.UserResponse;
import edu.bsit.fornolles.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService service;

    public AuthController(UserService s) {
        this.service = s;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        return service.register(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    /** Server-side logout: invalidates the token that was sent in the Authorization header. */
    @PostMapping("/logout")
    public Map<String, String> logout(
            @RequestHeader(value = "Authorization", required = false) String t) {
        service.logout(t);
        return Map.of("message", "Logged out successfully");
    }
}
