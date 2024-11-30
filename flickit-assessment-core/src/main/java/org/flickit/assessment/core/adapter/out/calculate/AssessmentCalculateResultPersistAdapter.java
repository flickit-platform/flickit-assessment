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
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.springframework.stereotype.Component;

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

        var subjectValues = assessmentResult.getSubjectValues();
        Map<UUID, MaturityLevel> subjectValueIdToLevel = subjectValues.stream()
            .collect(toMap(SubjectValue::getId, SubjectValue::getMaturityLevel));
        var subjectValueEntities = subjectValueRepo.findAllByIdIn(subjectValueIdToLevel.keySet());

        subjectValueEntities.forEach(s -> s.setMaturityLevelId(subjectValueIdToLevel.get(s.getId()).getId()));
        subjectValueRepo.saveAll(subjectValueEntities);

        var attributeValues = subjectValues.stream()
            .flatMap(s -> s.getAttributeValues().stream())
            .toList();
        Map<UUID, MaturityLevel> attributeValueIdToLevel = attributeValues.stream()
            .collect(toMap(AttributeValue::getId, AttributeValue::getMaturityLevel));
        var attributeValueEntities = attributeValueRepo.findAllByIdIn(attributeValueIdToLevel.keySet());

        attributeValueEntities.forEach(a -> a.setMaturityLevelId(attributeValueIdToLevel.get(a.getId()).getId()));
        attributeValueRepo.saveAll(attributeValueEntities);

        var attributeMaturityScoreEntities = attributeValues.stream()
            .flatMap(qav -> qav.getMaturityScores().stream()
                .map(ms -> AttributeMaturityScoreMapper.mapToJpaEntity(qav.getId(), ms)))
            .toList();
        attributeMaturityScoreRepository.saveAll(attributeMaturityScoreEntities);
    }

    @Override
    public void updateCalculatedConfidence(AssessmentResult assessmentResult) {
        assessmentResultRepo.updateAfterCalculateConfidence(
            assessmentResult.getId(),
            assessmentResult.getConfidenceValue(),
            assessmentResult.getIsConfidenceValid(),
            assessmentResult.getLastModificationTime(),
            assessmentResult.getLastConfidenceCalculationTime());

        var subjectValues = assessmentResult.getSubjectValues();
        Map<UUID, Double> subjectValueIdToConfidence = subjectValues.stream()
            .collect(toMap(SubjectValue::getId, SubjectValue::getConfidenceValue));
        var subjectValueEntities = subjectValueRepo.findAllByIdIn(subjectValueIdToConfidence.keySet());

        subjectValueEntities.forEach(s -> s.setConfidenceValue(subjectValueIdToConfidence.get(s.getId())));
        subjectValueRepo.saveAll(subjectValueEntities);

        var attributeValues = subjectValues.stream()
            .flatMap(s -> s.getAttributeValues().stream())
            .toList();
        Map<UUID, Double> attributeValueIdToConfidence = attributeValues.stream()
            .collect(toMap(AttributeValue::getId, AttributeValue::getConfidenceValue));
        var attributeValueEntities = attributeValueRepo.findAllById(attributeValueIdToConfidence.keySet());

        attributeValueEntities.forEach(a -> a.setConfidenceValue(attributeValueIdToConfidence.get(a.getId())));
        attributeValueRepo.saveAll(attributeValueEntities);
    }
}
