package com.example.identitycore.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String name) {
        super("Unknown role: " + name);
    }
}