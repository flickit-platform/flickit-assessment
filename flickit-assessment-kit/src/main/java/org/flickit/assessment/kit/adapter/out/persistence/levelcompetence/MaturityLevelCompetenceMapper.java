package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelCompetenceMapper {

    public static MaturityLevelCompetence mapToDomainModel(LevelCompetenceJpaEntity entity) {
        return new MaturityLevelCompetence(
            entity.getId(),
            entity.getEffectiveLevelId(),
            entity.getValue()
        );
    }
}
