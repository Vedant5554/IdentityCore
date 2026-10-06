package com.example.identitycore.repository;

import com.example.identitycore.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginCount = u.failedLoginCount + 1 WHERE u.id = :id")
    void incrementFailedLoginCount(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginCount = 0 WHERE u.id = :id")
    void resetFailedLoginCount(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.status = 'LOCKED' WHERE u.id = :id")
    void lockUser(UUID id);

}
