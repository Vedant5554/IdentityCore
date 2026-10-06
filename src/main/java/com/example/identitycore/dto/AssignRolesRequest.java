package com.example.identitycore.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record AssignRolesRequest(
        @NotEmpty
        Set<String> roleNames
) {}