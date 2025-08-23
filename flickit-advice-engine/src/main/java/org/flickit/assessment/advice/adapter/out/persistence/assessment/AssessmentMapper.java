package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.Assessment;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentMapper {

    public static Assessment mapToDomain(AssessmentJpaEntity entity) {
        return new Assessment(entity.getId(), entity.getTitle(), entity.getShortTitle());
    }
}
