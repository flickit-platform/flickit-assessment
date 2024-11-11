package org.flickit.assessment.advice.adapter.out.persistence.attributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;

@Component("adviceAttributeValuePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributeValuePersistenceJpaAdapter implements LoadAttributeCurrentAndTargetLevelIndexPort {

    private final AttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;

    @Override
    public List<Result> loadAttributeCurrentAndTargetLevelIndex(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var maturityLevels = maturityLevelRepository.findAllByKitVersionId(assessmentResult.getKitVersionId());
        var maturityLevelsIdMap = maturityLevels.stream()
            .collect(toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        var attributeIds = attributeLevelTargets.stream()
            .map(AttributeLevelTarget::getAttributeId)
            .toList();

        List<AttributeValueJpaEntity> attrValueEntities =
            repository.findByAssessmentResult_assessment_IdAndAttributeIdIn(assessmentId, attributeIds);

        Map<Long, AttributeValueJpaEntity> attributeIdToAttributeValueMap = attrValueEntities.stream()
            .collect(toMap(AttributeValueJpaEntity::getAttributeId, Function.identity()));

        List<Result> result = new ArrayList<>();
        for (AttributeLevelTarget attributeLevelTarget : attributeLevelTargets) {
            var attributeId = attributeLevelTarget.getAttributeId();
            Long currentMaturityLevelId = attributeIdToAttributeValueMap.get(attributeId).getMaturityLevelId();
            var currentMaturityLevel = maturityLevelsIdMap
                .get(currentMaturityLevelId);
            var targetMaturityLevel = maturityLevelsIdMap
                .get(attributeLevelTarget.getMaturityLevelId());

            result.add(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(
                attributeId,
                currentMaturityLevel.getIndex(),
                targetMaturityLevel.getIndex()
            ));
        }
        return result;
    }
}
