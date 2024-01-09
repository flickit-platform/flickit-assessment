package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitdsl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.AssessmentKitDslJpaEntity;
import org.flickit.assessment.kit.application.domain.AssessmentKitDsl;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitDslMapper {


    public static AssessmentKitDslJpaEntity toJpaEntity(String dslFile) {
        return new AssessmentKitDslJpaEntity(null, dslFile, null, LocalDateTime.now());
    }

    public static AssessmentKitDsl toDomainModel(AssessmentKitDslJpaEntity entity) {
        return new AssessmentKitDsl(
            entity.getId(),
            entity.getDslFile(),
            entity.getAssessmentKitId(),
            entity.getCreationTime()
        );
    }
}
