package com.example.identitycore.dto;

import com.example.identitycore.domain.User;


import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String status,
        boolean emailVerified
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus().name(),
                user.isEmailVerified()
        );
    }
}
