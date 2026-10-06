package com.example.identitycore.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super("Account is locked due to too many failed login attempts");
    }
}