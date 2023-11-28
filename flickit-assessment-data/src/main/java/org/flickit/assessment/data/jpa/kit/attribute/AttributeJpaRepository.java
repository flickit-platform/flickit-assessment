package org.flickit.assessment.data.jpa.kit.attribute;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeJpaRepository extends JpaRepository<AttributeJpeEntity, Long> {

    List<AttributeJpeEntity> findAllBySubjectId(long subjectId);
}
