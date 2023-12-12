package org.flickit.assessment.data.jpa.kit.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    @Query("SELECT u FROM UserJpaEntity u " +
        "WHERE u.id IN (SELECT uk FROM AssessmentKitUserJpaEntity uk WHERE uk.kitId = :kitId)")
    Page<UserJpaEntity> findAllKitUsers(Long kitId, Pageable pageable);
}
