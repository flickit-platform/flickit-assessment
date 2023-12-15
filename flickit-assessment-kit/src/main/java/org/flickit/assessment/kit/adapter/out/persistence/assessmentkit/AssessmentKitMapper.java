package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.kit.application.domain.AssessmentKit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitMapper {
    public static AssessmentKit mapToDomainModel(AssessmentKitJpaEntity entity) {
        return new AssessmentKit(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getSummary(),
            entity.getAbout(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getIsActive(),
            entity.getIsPrivate(),
            entity.getExpertGroup().getId(),
            null,
            null,
            null
        );
    }
}
