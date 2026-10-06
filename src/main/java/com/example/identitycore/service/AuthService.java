package com.example.identitycore.service;

import com.example.identitycore.dto.TokenPair;

public interface AuthService {

    TokenPair login(String email , String rawPassword ,String ipAddress);
    TokenPair refresh(String rawRefreshToken);
}
