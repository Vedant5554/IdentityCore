package com.example.identitycore.controller;

import com.example.identitycore.dto.AssignRolesRequest;
import com.example.identitycore.dto.UserSummaryResponse;
import com.example.identitycore.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<Page<UserSummaryResponse>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.listUsers(pageable));
    }

    @PatchMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<UserSummaryResponse> assignRoles(
            @PathVariable UUID id,
            @Valid @RequestBody AssignRolesRequest request
    ) {
        return ResponseEntity.ok(adminService.assignRoles(id, request.roleNames()));
    }
}