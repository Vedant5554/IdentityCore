package com.example.identitycore.service.Impl;

import com.example.identitycore.domain.User;
import com.example.identitycore.domain.UserStatus;
import com.example.identitycore.dto.RegisterRequest;
import com.example.identitycore.exception.EmailAlreadyExistsException;
import com.example.identitycore.repository.UserRepository;
import com.example.identitycore.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .status(UserStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .failedLoginCount(0)
                .build();

        return userRepository.save(user);

    }


}
