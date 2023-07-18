package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadQualityAttributeByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.UpdateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QualityAttributeValuePersistenceJpaAdaptor implements
    CreateQualityAttributeValuePort,
        UpdateQualityAttributeValuePort,
    LoadQualityAttributeByResultPort {

    private final QualityAttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public void persistAll(List<Long> qualityAttributeIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId).get();

        List<QualityAttributeValueJpaEntity> entities = qualityAttributeIds.stream().map(qualityAttributeId -> {
            QualityAttributeValueJpaEntity qualityAttributeValue = QualityAttributeValueMapper.mapToJpaEntity(qualityAttributeId);
            qualityAttributeValue.setAssessmentResult(assessmentResult);
            return qualityAttributeValue;
        }).toList();

        repository.saveAll(entities);
    }

    @Override
    public void update(QualityAttributeValue qualityAttributeValue) {
        repository.save(QualityAttributeValueMapper.mapToJpaEntity(qualityAttributeValue));
    }

    @Override
    public Result loadByResultId(Param param) {
        return new Result(repository.findByAssessmentResultId(param.resultId()).stream()
            .map(QualityAttributeValueMapper::mapToDomainModel)
            .toList());
    }
}
