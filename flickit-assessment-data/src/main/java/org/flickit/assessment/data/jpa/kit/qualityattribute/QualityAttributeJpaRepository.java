package org.flickit.assessment.data.jpa.kit.qualityattribute;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityAttributeJpaRepository extends JpaRepository<QualityAttributeJpaEntity, Long> {
    QualityAttributeJpaEntity findByCodeAndKitId(String code, Long kitId);
}
