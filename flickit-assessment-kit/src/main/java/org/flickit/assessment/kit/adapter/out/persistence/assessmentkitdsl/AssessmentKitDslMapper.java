package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitdsl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.AssessmentKitDslJpaEntity;
import org.flickit.assessment.kit.application.domain.AssessmentKitDsl;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitDslMapper {

    public static AssessmentKitDsl toDomainModel(AssessmentKitDslJpaEntity entity) {
        return new AssessmentKitDsl(
            entity.getId(),
            entity.getDslPath(),
            entity.getJsonPath(),
            entity.getAssessmentKitId(),
            entity.getCreationTime()
        );
    }
}
