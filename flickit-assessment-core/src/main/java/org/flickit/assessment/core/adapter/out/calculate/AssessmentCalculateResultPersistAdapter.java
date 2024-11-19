package org.flickit.assessment.core.adapter.out.calculate;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.attributematurityscore.AttributeMaturityScoreMapper;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class AssessmentCalculateResultPersistAdapter implements
    UpdateCalculatedResultPort,
    UpdateCalculatedConfidencePort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final AttributeValueJpaRepository attributeValueRepo;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;

    @Override
    public void updateCalculatedResult(AssessmentResult assessmentResult) {
        assessmentResultRepo.updateAfterCalculate(assessmentResult.getId(),
            assessmentResult.getMaturityLevel().getId(),
            assessmentResult.getIsCalculateValid(),
            assessmentResult.getLastModificationTime(),
            assessmentResult.getLastCalculationTime());

        List<SubjectValue> subjectValues = assessmentResult.getSubjectValues();
        Map<UUID, MaturityLevel> subjectValueIdToLevel = subjectValues.stream()
            .collect(toMap(SubjectValue::getId, SubjectValue::getMaturityLevel));
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findAllById(subjectValueIdToLevel.keySet());

        subjectValueEntities.forEach(s -> s.setMaturityLevelId(subjectValueIdToLevel.get(s.getId()).getId()));
        subjectValueRepo.saveAll(subjectValueEntities);

        List<AttributeValue> attributeValue = subjectValues.stream()
            .flatMap(s -> s.getAttributeValues().stream()).toList();
        Map<UUID, MaturityLevel> attributeValueIdToLevel = attributeValue.stream()
            .collect(toMap(AttributeValue::getId, AttributeValue::getMaturityLevel));
        List<AttributeValueJpaEntity> attributeValueEntities = attributeValueRepo.findAllById(attributeValueIdToLevel.keySet());

        attributeValueEntities.forEach(a -> a.setMaturityLevelId(attributeValueIdToLevel.get(a.getId()).getId()));
        attributeValueRepo.saveAll(attributeValueEntities);

        List<AttributeMaturityScoreJpaEntity> attributeMaturityScores = new ArrayList<>();
        attributeValue.forEach(qav -> qav.getMaturityScores()
            .forEach(ms -> attributeMaturityScores.add(AttributeMaturityScoreMapper.mapToJpaEntity(qav.getId(), ms))));
        attributeMaturityScoreRepository.saveAll(attributeMaturityScores);
    }

    @Override
    public void updateCalculatedConfidence(AssessmentResult assessmentResult) {
        assessmentResultRepo.updateAfterCalculateConfidence(
            assessmentResult.getId(),
            assessmentResult.getConfidenceValue(),
            assessmentResult.getIsConfidenceValid(),
            assessmentResult.getLastModificationTime(),
            assessmentResult.getLastConfidenceCalculationTime());

        List<SubjectValue> subjectValues = assessmentResult.getSubjectValues();
        Map<UUID, Double> subjectValueIdToConfidence = subjectValues.stream()
            .collect(toMap(SubjectValue::getId, SubjectValue::getConfidenceValue));
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findAllById(subjectValueIdToConfidence.keySet());

        subjectValueEntities.forEach(s -> s.setConfidenceValue(subjectValueIdToConfidence.get(s.getId())));
        subjectValueRepo.saveAll(subjectValueEntities);

        List<AttributeValue> attributeValue = subjectValues.stream()
            .flatMap(s -> s.getAttributeValues().stream()).toList();
        Map<UUID, Double> attributeValueIdToConfidence = attributeValue.stream()
            .collect(toMap(AttributeValue::getId, AttributeValue::getConfidenceValue));
        List<AttributeValueJpaEntity> attributeValueEntities = attributeValueRepo.findAllById(attributeValueIdToConfidence.keySet());

        attributeValueEntities.forEach(a -> a.setConfidenceValue(attributeValueIdToConfidence.get(a.getId())));
        attributeValueRepo.saveAll(attributeValueEntities);
    }
}
