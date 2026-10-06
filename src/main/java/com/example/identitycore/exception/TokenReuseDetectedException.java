package com.example.identitycore.exception;

public class TokenReuseDetectedException extends RuntimeException {
    public TokenReuseDetectedException() {
        super("Refresh token reuse detected; all sessions for this token family have been revoked");
    }
}