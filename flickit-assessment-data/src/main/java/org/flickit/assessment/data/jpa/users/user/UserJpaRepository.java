package org.flickit.assessment.data.jpa.users.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    @Query("""
            SELECT u.email AS email
            FROM UserJpaEntity u
            WHERE u.id = :userId
        """)
    Optional<String> findEmailByUserId(@Param(value = "userId") UUID userId);

    @Query("""
            SELECT u.id AS userId
            FROM UserJpaEntity u
            WHERE u.email = :email
        """)
    Optional<UUID> findUserIdByEmail(@Param(value = "email") String email);

    @Modifying
    @Query("""
            UPDATE UserJpaEntity a
            SET a.displayName = :displayName,
                a.bio = :bio,
                a.linkedin = :linkedin,
                a.lastModificationTime = :lastModificationTime
            WHERE a.id = :id
        """)
    void update(@Param("id") UUID id,
                @Param("displayName") String displayName,
                @Param("bio") String bio,
                @Param("linkedin") String linkedin,
                @Param("lastModificationTime") LocalDateTime lastModificationTime);

    @Modifying
    @Query("""
            UPDATE UserJpaEntity a
            SET a.picture = :picture,
                a.lastModificationTime = :lastModificationTime
            WHERE a.id = :id
        """)
    void updatePicture(@Param("id") UUID id,
                       @Param("picture") String picture,
                       @Param("lastModificationTime") LocalDateTime lastModificationTime);
}
