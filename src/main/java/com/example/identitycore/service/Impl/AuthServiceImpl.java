package com.example.identitycore.service.Impl;

import com.example.identitycore.domain.*;
import com.example.identitycore.dto.TokenPair;
import com.example.identitycore.exception.AccountLockedException;
import com.example.identitycore.exception.InvalidCredentialsException;
import com.example.identitycore.exception.TokenReuseDetectedException;
import com.example.identitycore.repository.RefreshTokenRepository;
import com.example.identitycore.repository.UserRepository;
import com.example.identitycore.security.JwtService;
import com.example.identitycore.service.AuditLogService;
import com.example.identitycore.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final long REFRESH_TOKEN_TTL_DAYS = 7;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuditLogService auditLogService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public TokenPair login(
            String email,
            String rawPassword,
            String ipAddress
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getStatus() == UserStatus.LOCKED) {
            throw new AccountLockedException();
        }

        // ---------------------------------------------------------
        // BCrypt performance measurement
        // ---------------------------------------------------------

        long bcryptStart = System.nanoTime();

        boolean passwordMatches = passwordEncoder.matches(
                rawPassword,
                user.getPasswordHash()
        );

        long bcryptTime =
                (System.nanoTime() - bcryptStart) / 1_000_000;

        System.out.println(
                "BCrypt verification: " + bcryptTime + " ms"
        );

        // ---------------------------------------------------------
        // Failed login
        // ---------------------------------------------------------

        if (!passwordMatches) {

            userRepository.incrementFailedLoginCount(user.getId());

            auditLogService.log(
                    user.getId(),
                    AuditEventType.LOGIN_FAILURE,
                    ipAddress,
                    null
            );

            int updatedCount = user.getFailedLoginCount() + 1;

            if (updatedCount >= MAX_FAILED_ATTEMPTS) {

                userRepository.lockUser(user.getId());

                auditLogService.log(
                        user.getId(),
                        AuditEventType.ACCOUNT_LOCKED,
                        ipAddress,
                        null
                );
            }

            throw new InvalidCredentialsException();
        }

        // ---------------------------------------------------------
        // Successful login
        // ---------------------------------------------------------

        userRepository.resetFailedLoginCount(user.getId());

        auditLogService.log(
                user.getId(),
                AuditEventType.LOGIN_SUCCESS,
                ipAddress,
                null
        );

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        UUID familyId = UUID.randomUUID();

        return issueTokenPair(user, familyId);
    }

    @Override
    @Transactional(noRollbackFor = TokenReuseDetectedException.class)
    public TokenPair refresh(String rawRefreshToken) {

        String hash = hash(rawRefreshToken);

        RefreshToken existing = refreshTokenRepository
                .findByTokenHash(hash)
                .orElseThrow(InvalidCredentialsException::new);

        if (existing.isRevoked()) {

            refreshTokenRepository.revokeFamily(
                    existing.getFamilyId()
            );

            throw new TokenReuseDetectedException();
        }

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidCredentialsException();
        }

        existing.setRevoked(true);

        User user = userRepository.findById(existing.getUserId())
                .orElseThrow(InvalidCredentialsException::new);

        return issueTokenPair(user, existing.getFamilyId());
    }

    private TokenPair issueTokenPair(
            User user,
            UUID familyId
    ) {

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                permissions
        );

        String rawRefreshToken =
                UUID.randomUUID().toString()
                        + UUID.randomUUID();

        RefreshToken tokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hash(rawRefreshToken))
                .familyId(familyId)
                .expiresAt(
                        Instant.now().plus(
                                REFRESH_TOKEN_TTL_DAYS,
                                ChronoUnit.DAYS
                        )
                )
                .revoked(false)
                .build();

        refreshTokenRepository.save(tokenEntity);

        return new TokenPair(
                accessToken,
                rawRefreshToken
        );
    }

    private String hash(String value) {

        try {

            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(value.getBytes());

            return Base64.getEncoder()
                    .encodeToString(digest);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to hash token",
                    e
            );
        }
    }
}