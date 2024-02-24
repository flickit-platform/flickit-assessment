package org.flickit.assessment.advice.adapter.out.persistence.attributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("adviceAttributeValuePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributeValuePersistenceJpaAdapter implements LoadAttributeCurrentAndTargetLevelIndexPort {

    private final QualityAttributeValueJpaRepository repository;
    private final MaturityLevelJpaRepository maturityLevelRepository;

    @Override
    public List<Result> loadAttributeCurrentAndTargetLevelIndex(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        var attributeIds = attributeLevelTargets.stream().map(AttributeLevelTarget::attributeId).toList();
        var qualityAttributeValues = repository.findByAssessmentResult_assessment_IdAndQualityAttributeIdIn(assessmentId, attributeIds);
        var qualityAttributeValuesIdMap = qualityAttributeValues.stream()
            .collect(Collectors.toMap(QualityAttributeValueJpaEntity::getQualityAttributeId, Function.identity()));

        var maturityLevels = maturityLevelRepository.findAllInKitWithOneId(attributeLevelTargets.get(0).maturityLevelId());
        var maturityLevelsIdMap = maturityLevels
            .stream().collect(Collectors.toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        List<Result> result = new ArrayList<>();
        for (AttributeLevelTarget attributeLevelTarget : attributeLevelTargets) {
            var attributeId = attributeLevelTarget.attributeId();
            var currentMaturityLevel = maturityLevelsIdMap
                .get(qualityAttributeValuesIdMap.get(attributeId).getMaturityLevelId());
            var targetMaturityLevel = maturityLevelsIdMap
                .get(attributeLevelTarget.maturityLevelId());

            result.add(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(
                attributeId,
                currentMaturityLevel.getIndex(),
                targetMaturityLevel.getIndex()
            ));
        }
        return result;
    }
}
