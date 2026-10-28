package edu.bsit.fornolles.exception;

/** Not logged in / bad credentials / expired token -> HTTP 401. */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
