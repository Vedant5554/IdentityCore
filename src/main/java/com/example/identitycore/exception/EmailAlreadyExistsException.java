package com.example.identitycore.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("An Account with email " + email + " already exists");
    }
}
