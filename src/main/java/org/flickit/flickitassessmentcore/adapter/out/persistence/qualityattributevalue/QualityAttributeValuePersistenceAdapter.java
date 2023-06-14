package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.LoadQAValuesByQAIdsPort;
import org.flickit.flickitassessmentcore.application.port.out.LoadQualityAttributeValuesByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class QualityAttributeValuePersistenceAdapter implements
    SaveQualityAttributeValuePort,
    LoadQualityAttributeValuesByResultPort,
    LoadQAValuesByQAIdsPort {

    private final QualityAttributeValueRepository qualityAttributeValueRepository;

    @Override
    public void saveQualityAttributeValue(QualityAttributeValue qualityAttributeValue) {
        qualityAttributeValueRepository.save(QualityAttributeValueMapper.mapToJpaEntity(qualityAttributeValue));
    }

    @Override
    public Set<QualityAttributeValue> loadQualityAttributeValuesByResultId(UUID resultId) {
        return qualityAttributeValueRepository.findQualityAttributeValueByResultId(resultId)
            .stream()
            .map(QualityAttributeValueMapper::mapToDomainModel)
            .collect(Collectors.toSet());
    }

    @Override
    public List<QualityAttributeValue> LoadQAValuesByQAIds(Set<Long> qaIds) {
        return qualityAttributeValueRepository.findQualityAttributeValuesByQualityAttributeIds(qaIds)
            .stream()
            .map(QualityAttributeValueMapper::mapToDomainModel)
            .collect(Collectors.toList());
    }
}
