package org.flickit.assessment.data.jpa.core.advicenarration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdviceNarrationJpaRepository extends JpaRepository<AdviceNarrationJpaEntity , UUID> {
}
