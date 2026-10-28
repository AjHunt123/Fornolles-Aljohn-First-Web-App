package edu.bsit.fornolles.exception;

/** Logged in but the role is not allowed -> HTTP 403. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
