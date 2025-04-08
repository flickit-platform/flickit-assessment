package org.flickit.assessment.data.jpa.kit.measure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MeasureJpaRepository extends JpaRepository<MeasureJpaEntity, MeasureJpaEntity.EntityId> {

    List<MeasureJpaEntity> findAllByKitVersionId(long kitVersionId);

    List<MeasureJpaEntity> findAllByKitVersionIdOrderByIndex(Long activeVersionId);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    Optional<MeasureJpaEntity> findByCodeAndKitVersionId(String code, Long kitVersionId);

    void deleteByIdAndKitVersionId(long measureId, long kitVersionId);

    List<MeasureJpaEntity> findAllByIdInAndKitVersionId(Collection<Long> ids, long kitVersionId);
}
