package com.example.identitycore.service;

import com.example.identitycore.domain.AuditEventType;
import com.example.identitycore.domain.AuditLog;
import com.example.identitycore.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async("auditLogExecutor")
    @Transactional
    public void log(
            UUID userId,
            AuditEventType eventType,
            String ipAddress,
            String metadata
    ) {
        AuditLog entry = AuditLog.builder()
                .userId(userId)
                .eventType(eventType)
                .ipAddress(ipAddress)
                .metadata(metadata)
                .build();

        auditLogRepository.save(entry);
    }
}