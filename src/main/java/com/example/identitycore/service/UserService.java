package com.example.identitycore.service;

import com.example.identitycore.domain.User;
import com.example.identitycore.dto.RegisterRequest;
import com.example.identitycore.repository.UserRepository;
import org.springframework.stereotype.Service;


public interface UserService {
    User register(RegisterRequest request);
}
