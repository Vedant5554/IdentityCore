package com.example.identitycore.controller;

import com.example.identitycore.domain.AuditEventType;
import com.example.identitycore.domain.AuditLog;
import com.example.identitycore.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class AuditLogIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuditLogRepository auditLogRepository;

    @Test
    void successfulLoginWritesAuditLogEntry() throws Exception {
        String email = "audit-log-test@example.com";
        String password = "Passw0rd123";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"%s","password":"%s"}
                            """.formatted(email, password)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"%s","password":"%s"}
                            """.formatted(email, password)))
                .andExpect(status().isOk());

        List<AuditLog> logs = auditLogRepository.findAll();

        boolean hasLoginSuccessEntry = logs.stream()
                .anyMatch(log -> log.getEventType() == AuditEventType.LOGIN_SUCCESS);

        assertThat(hasLoginSuccessEntry).isTrue();
    }

    @Test
    void failedLoginWritesAuditLogEntry() throws Exception {
        String email = "audit-log-fail-test@example.com";
        String password = "Passw0rd123";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"%s","password":"%s"}
                            """.formatted(email, password)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"%s","password":"WrongPassword1"}
                            """.formatted(email)))
                .andExpect(status().isUnauthorized());

        List<AuditLog> logs = auditLogRepository.findAll();

        boolean hasLoginFailureEntry = logs.stream()
                .anyMatch(log -> log.getEventType() == AuditEventType.LOGIN_FAILURE);

        assertThat(hasLoginFailureEntry).isTrue();
    }
}