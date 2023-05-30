package org.flickit.flickitassessmentcore.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.mapper.QualityAttributeValueMapper;
import org.flickit.flickitassessmentcore.adapter.out.persistence.repository.QualityAttributeValueRepository;
import org.flickit.flickitassessmentcore.application.port.out.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QualityAttributeValuePersistenceAdapter implements SaveQualityAttributeValuePort {

    private final QualityAttributeValueRepository qualityAttributeValueRepository;
    private final QualityAttributeValueMapper qualityAttributeValueMapper;

    @Override
    public void saveQualityAttributeValue(QualityAttributeValue qualityAttributeValue) {
        qualityAttributeValueRepository.save(qualityAttributeValueMapper.mapToJpaEntity(qualityAttributeValue));
    }
}
