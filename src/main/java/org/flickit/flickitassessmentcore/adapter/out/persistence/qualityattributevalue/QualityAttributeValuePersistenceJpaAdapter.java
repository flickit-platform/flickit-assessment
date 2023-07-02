package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QualityAttributeValuePersistenceJpaAdapter implements
    SaveQualityAttributeValuePort {

    private final QualityAttributeValueJpaRepository repository;

    @Override
    public void saveQualityAttributeValue(QualityAttributeValue qualityAttributeValue) {
        repository.save(QualityAttributeValueMapper.mapToJpaEntity(qualityAttributeValue));
    }
}
