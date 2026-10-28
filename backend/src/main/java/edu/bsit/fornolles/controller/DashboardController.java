package edu.bsit.fornolles.controller;

import edu.bsit.fornolles.dto.DashboardResponse;
import edu.bsit.fornolles.dto.UserResponse;
import edu.bsit.fornolles.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private static final String AUTH = "Authorization";
    private final UserService service;

    public DashboardController(UserService service) {
        this.service = service;
    }

    /** Any logged-in user (401 if the token is missing or unknown). */
    @GetMapping
    public DashboardResponse home(
            @RequestHeader(value = AUTH, required = false) String t) {
        return service.dashboard(service.authenticate(t));
    }

    /** ADMIN only (401 if not logged in, 403 if the role is not ADMIN). */
    @GetMapping("/users")
    public List<UserResponse> users(
            @RequestHeader(value = AUTH, required = false) String t) {
        service.requireRole(service.authenticate(t), "ADMIN");
        return service.findAll();
    }
}
