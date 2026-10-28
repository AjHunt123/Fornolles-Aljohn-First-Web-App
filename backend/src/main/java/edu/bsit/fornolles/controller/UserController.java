package edu.bsit.fornolles.controller;

import edu.bsit.fornolles.dto.UpdateProfileRequest;
import edu.bsit.fornolles.dto.UserResponse;
import edu.bsit.fornolles.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /** Edit My Profile: a logged-in user updates their own full name. */
    @PutMapping("/me")
    public UserResponse updateMe(
            @RequestHeader(value = "Authorization", required = false) String t,
            @Valid @RequestBody UpdateProfileRequest req) {
        return service.updateProfile(service.authenticate(t), req.fullName());
    }
}
