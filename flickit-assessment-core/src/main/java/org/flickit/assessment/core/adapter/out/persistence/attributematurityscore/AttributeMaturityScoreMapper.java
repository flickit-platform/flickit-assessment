package org.flickit.assessment.core.adapter.out.persistence.attributematurityscore;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.MaturityScore;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMaturityScoreMapper {

    public static MaturityScore mapToDomain(AttributeMaturityScoreJpaEntity entity) {
        return new MaturityScore(
            entity.getMaturityLevelId(),
            entity.getScore()
        );
    }
}
