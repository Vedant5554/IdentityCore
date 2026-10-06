package com.example.identitycore.service.Impl;

import com.example.identitycore.domain.Role;
import com.example.identitycore.domain.User;
import com.example.identitycore.dto.UserSummaryResponse;
import com.example.identitycore.exception.InvalidCredentialsException;
import com.example.identitycore.exception.RoleNotFoundException;
import com.example.identitycore.exception.UserNotFoundException;
import com.example.identitycore.repository.RoleRepository;
import com.example.identitycore.repository.UserRepository;
import com.example.identitycore.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }



    @Override
    @Transactional
    public Page<UserSummaryResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserSummaryResponse::from);
    }

    @Override
    @Transactional
    public UserSummaryResponse assignRoles(UUID userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Set<Role> resolvedRoles = roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new RoleNotFoundException(name)))
                .collect(Collectors.toSet());

        user.setRoles(resolvedRoles);
        User saved = userRepository.save(user);

        return UserSummaryResponse.from(saved);
    }
}