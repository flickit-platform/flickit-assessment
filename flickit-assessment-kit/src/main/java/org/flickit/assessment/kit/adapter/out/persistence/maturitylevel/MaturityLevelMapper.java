package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.kit.application.domain.MaturityLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static MaturityLevel mapToKitDomainModel(MaturityLevelJpaEntity entity) {
        return new MaturityLevel(
            entity.getId(),
            entity.getTitle(),
            entity.getTitle(),
            null,
            entity.getValue()
        );
    }

    public static MaturityLevelJpaEntity mapToJpaEntity(MaturityLevel level, Long kitId) {
        return new MaturityLevelJpaEntity(
            null,
            level.getTitle(),
            level.getValue(),
            kitId
        );
    }
}
