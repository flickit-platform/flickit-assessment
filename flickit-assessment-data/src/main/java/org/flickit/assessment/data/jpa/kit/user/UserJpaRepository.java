package org.flickit.assessment.data.jpa.kit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);

    @Query("""
        SELECT u.email AS email
        FROM UserJpaEntity u
        WHERE u.id = :userId
        """)
    Optional<String> findEmailByUserId(@Param(value = "userId") UUID userId);
}
