package org.flickit.assessment.advice.adapter.out.persistence.attributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component("adviceAttributeValuePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributeValuePersistenceJpaAdapter implements LoadAttributeCurrentAndTargetLevelIndexPort {

    private final QualityAttributeValueJpaRepository repository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public List<Result> loadAttributeCurrentAndTargetLevelIndex(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        var maturityLevels = maturityLevelRepository.findAllInKitVersionWithOneId(attributeLevelTargets.get(0).getMaturityLevelId());
        var maturityLevelsIdMap = maturityLevels.stream()
            .collect(toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        var attributeIds = attributeLevelTargets.stream()
            .map(AttributeLevelTarget::getAttributeId)
            .toList();
        var attributes = attributeRepository.findByIdIn(attributeIds);
        Map<UUID, Long> attributeRefNumToIdMap = attributes.stream()
            .collect(toMap(AttributeJpaEntity::getRefNum, AttributeJpaEntity::getId));
        var attributeValues = repository.findByAssessmentResult_assessment_IdAndAttributeRefNumIn(assessmentId, attributeRefNumToIdMap.keySet().stream().toList());

        Map<Long, QualityAttributeValueJpaEntity> attributeIdToAttributeValueMap = attributeValues.stream()
            .collect(toMap(a -> attributeRefNumToIdMap.get(a.getAttributeRefNum()), a -> a));

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
