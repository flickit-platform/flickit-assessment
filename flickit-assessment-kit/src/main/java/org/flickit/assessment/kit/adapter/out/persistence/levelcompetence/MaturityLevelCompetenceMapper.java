package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelCompetenceMapper {

    public static MaturityLevelCompetence mapToDomainModel(LevelCompetenceJpaEntity entity) {
        return new MaturityLevelCompetence(
            entity.getId(),
            entity.getEffectiveLevelId(),
            entity.getValue()
        );
    }

    public static LevelCompetenceJpaEntity mapToCreateJpaEntity(Long affectedLevelId,
                                                                Long effectiveLevelId,
                                                                int value,
                                                                Long kitVersionId,
                                                                UUID createdBy) {
        return new LevelCompetenceJpaEntity(null,
            kitVersionId,
            affectedLevelId,
            effectiveLevelId,
            value,
            LocalDateTime.now(),
            LocalDateTime.now(),
            createdBy,
            createdBy
        );
    }
}
