package com.example.identitycore.repository;

import com.example.identitycore.domain.User;
import com.example.identitycore.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest
public class UserRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",postgres::getJdbcUrl);
        registry.add("spring.datasource.username",postgres::getUsername);
        registry.add("spring.datasource.password",postgres::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Test
    void findUserByEmail() {
        User user = User.builder()
                .email("vedant@test.com")
                .passwordHash("hashed-value-not-real-here")
                .status(UserStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .failedLoginCount(0)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("vedant@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("vedant@test.com");
    }

    @Test
    void existsByEmailReturnsFalseForUnknownEmail(){
        assertThat(userRepository.existsByEmail("nobady@test.com")).isFalse();
    }
}
