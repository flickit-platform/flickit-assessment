package org.flickit.assessment.core.adapter.out.persistence.attributematurityscore;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.MaturityScore;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMaturityScoreMapper {

    public static AttributeMaturityScoreJpaEntity mapToJpaEntity(UUID attributeValueId, MaturityScore maturityScore) {
        return new AttributeMaturityScoreJpaEntity(
            attributeValueId,
            maturityScore.getMaturityLevelId(),
            maturityScore.getScore()
        );
    }
}
