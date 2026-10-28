package edu.bsit.fornolles.dto;

import java.time.LocalDate;

public record DashboardResponse(String fullName, String email, String role,
                                LocalDate memberSince, long totalUsers) {
}
