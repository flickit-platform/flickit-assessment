package org.flickit.assessment.core.adapter.out.persistence.attributematurityscore;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.MaturityScore;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttributeMaturityScorePersistenceJpaAdapter {

    private final AttributeMaturityScoreJpaRepository repository;

    public void saveOrUpdate(UUID attributeValueId, MaturityScore score) {
        var existingEntity = repository.findByAttributeValueIdAndMaturityLevelId(attributeValueId, score.getMaturityLevelId());
        existingEntity.ifPresentOrElse(x -> {
            x.setScore(score.getScore());
            repository.save(x);
        }, () -> {
            AttributeMaturityScoreJpaEntity entity =
                new AttributeMaturityScoreJpaEntity(attributeValueId, score.getMaturityLevelId(), score.getScore());
            repository.save(entity);
        });
    }
}
