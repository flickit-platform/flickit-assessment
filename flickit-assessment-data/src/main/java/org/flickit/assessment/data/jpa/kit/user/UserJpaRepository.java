package org.flickit.assessment.data.jpa.kit.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    void deleteByKitIdAndUserId(Long kitId, Long userId);
}
