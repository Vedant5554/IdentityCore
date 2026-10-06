package com.example.identitycore.dto;

import com.example.identitycore.domain.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserSummaryResponse(
        UUID id,
        String email,
        String status,
        Set<String> roleNames
) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus().name(),
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())
        );
    }
}