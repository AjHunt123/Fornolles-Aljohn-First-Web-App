package edu.bsit.fornolles.config;

import edu.bsit.fornolles.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds a default ADMIN account when the application starts.
 * (The H2 database is in-memory, so this runs again after every restart.)
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserService service;
    private final String fullName;
    private final String email;
    private final String password;

    public DataSeeder(UserService service,
                      @Value("${app.admin.full-name}") String fullName,
                      @Value("${app.admin.email}") String email,
                      @Value("${app.admin.password}") String password) {
        this.service = service;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        service.createAdminIfMissing(fullName, email, password);
        System.out.println("Default ADMIN ready: " + email);
    }
}
