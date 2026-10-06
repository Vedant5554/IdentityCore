package com.example.identitycore.service;

import com.example.identitycore.dto.UserSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Set;
import java.util.UUID;

public interface AdminService {
    Page<UserSummaryResponse> listUsers(Pageable pageable);
    UserSummaryResponse assignRoles(UUID userId, Set<String> roleNames);
}
