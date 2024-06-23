package org.flickit.assessment.data.jpa.users.spaceinvitee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpaceInviteeJpaRepository extends JpaRepository<SpaceInviteeJpaEntity, UUID> {

    boolean existsBySpaceIdAndEmail(long spaceId, String email);

    void deleteByEmail(String email);

    List<SpaceInviteeJpaEntity> findByEmail(String email);

    @Modifying
    @Query("""
            UPDATE SpaceInviteeJpaEntity s
            SET s.creationTime = :creationTime,
                s.expirationDate = :expirationDate,
                s.createdBy = :createdBy
            WHERE s.spaceId = :spaceId AND s.email = :email
        """)
    void update(@Param("spaceId") long spaceId,
                @Param("email") String email,
                @Param("creationTime") LocalDateTime creationTime,
                @Param("expirationDate") LocalDateTime expirationDate,
                @Param("createdBy") UUID createdBy);

    @Query("""
            SELECT s
            FROM SpaceInviteeJpaEntity s
            WHERE s.spaceId = :spaceId AND s.expirationDate > :currentTime
        """)
    Page<SpaceInviteeJpaEntity> findBySpaceId(@Param("spaceId") long spaceId,
                                              @Param("currentTime") LocalDateTime currentTime,
                                              Pageable pageable);
}
