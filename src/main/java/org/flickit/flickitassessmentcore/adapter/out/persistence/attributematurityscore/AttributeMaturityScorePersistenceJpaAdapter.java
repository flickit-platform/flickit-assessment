package org.flickit.flickitassessmentcore.adapter.out.persistence.attributematurityscore;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.MaturityScore;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttributeMaturityScorePersistenceJpaAdapter {

    private final AttributeMaturityScoreJpaRepository repository;
    private final QualityAttributeValueJpaRepository qualityAttributeValueRepo;

    public void saveOrUpdate(UUID qualityAttributeValueId, MaturityScore s) {
        var savedEntity = repository.findByAttributeValue_IdAndMaturityLevelId(qualityAttributeValueId, s.getMaturityLevelId());
        if (savedEntity.isPresent()) {
            var entity = savedEntity.get();
            entity.setScore(s.getScore());
            repository.save(entity);
        }
        else {
            var qualityAttributeValue = qualityAttributeValueRepo.findById(qualityAttributeValueId).get();
            var entity = new AttributeMaturityScoreJpaEntity(null, qualityAttributeValue, s.getMaturityLevelId(), s.getScore());
            repository.save(entity);
        }
    }
}
