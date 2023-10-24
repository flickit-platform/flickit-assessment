package org.flickit.flickitassessmentcore.adapter.out.persistence.attributematurityscore;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.MaturityScore;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMaturityScoreMapper {

    public static MaturityScore mapToDomain(AttributeMaturityScoreJpaEntity entity) {
        return new MaturityScore(
            entity.getMaturityLevelId(),
            entity.getScore()
        );
    }
}
