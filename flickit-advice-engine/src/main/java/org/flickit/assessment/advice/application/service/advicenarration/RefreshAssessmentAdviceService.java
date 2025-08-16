package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeValuesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.CreateAdviceHelper;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.REFRESH_ASSESSMENT_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshAssessmentAdviceService implements RefreshAssessmentAdviceUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAttributeValuesPort loadAttributeValuesPort;
    private final CreateAdviceHelper createAdviceHelper;
    private final CreateAiAdviceNarrationHelper createAiAdviceNarrationHelper;
    private final DeleteAdviceItemPort deleteAdviceItemPort;
    private final LoadAdviceItemPort loadAdviceItemPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final LoadAttributesPort loadAttributesPort;

    private static final int MAX_TARGETS_LIMIT = 2;

    @Override
    public void refreshAssessmentAdvice(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        if (param.getForceRegenerate() || !loadAdviceItemPort.existsByAssessmentResultId(assessmentResult.getId())
            || !loadAdviceNarrationPort.existsByAssessmentResultId(assessmentResult.getId()))
            regenerateAdviceIfNecessary(assessmentResult);
    }

    private void regenerateAdviceIfNecessary(AssessmentResult assessmentResult) {
        var targets = prepareAttributeLevelTargets(assessmentResult);
        if (!targets.isEmpty()) {
            log.info("Regenerating advice for [assessmentId={} and assessmentResultId={}]", assessmentResult.getAssessmentId(), assessmentResult.getId());
            deleteAdviceItemPort.deleteAllAiGenerated(assessmentResult.getId());
            generateAdvice(assessmentResult, targets);
        }
    }

    private List<AttributeLevelTarget> prepareAttributeLevelTargets(AssessmentResult result) {
        var attributeValues = loadAttributeValuesPort.loadAll(result.getId());
        var maturityLevels = loadMaturityLevelsPort.loadAll(result.getAssessmentId());
        List<Long> attributeIds = attributeValues.stream().map(LoadAttributeValuesPort.Result::attributeId).toList();
        var attributes = loadAttributesPort.loadByIdsAndAssessmentId(attributeIds, result.getAssessmentId());

        return buildTargets(attributeValues, maturityLevels, attributes);
    }

    private List<AttributeLevelTarget> buildTargets(List<LoadAttributeValuesPort.Result> attributeValues,
                                                    List<MaturityLevel> maturityLevels,
                                                    List<Attribute> attributes) {
        Map<Long, Integer> maturityLevelIdToIndexMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, MaturityLevel::getIndex));

        List<MaturityLevel> sortedLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getIndex))
            .toList();

        MaturityLevel maxLevel = Collections.max(maturityLevels, comparingInt(MaturityLevel::getIndex));

        var weakLevelIds = sortedLevels.stream()
            .filter(e -> e.getIndex() <= maxLevel.getIndex() / 2)
            .map(MaturityLevel::getId)
            .collect(Collectors.toSet());

        Set<Long> weakAttributeIds = attributeValues.stream()
            .filter(e -> weakLevelIds.contains(e.maturityLevelId()))
            .map(LoadAttributeValuesPort.Result::attributeId)
            .collect(Collectors.toSet());

        Set<Long> finalWeakAttributeIds =
            (weakAttributeIds.size() < 2)
                ? Stream.concat(
                weakAttributeIds.stream(),
                selectFurthestAttributes(attributeValues, maturityLevels, attributes).stream()
            ).collect(Collectors.toSet())
                : Set.copyOf(weakAttributeIds);

        return attributeValues.stream()
            .filter(v -> finalWeakAttributeIds.contains(v.attributeId()))
            .flatMap(value -> toTarget(
                    value.attributeId(),
                    maturityLevelIdToIndexMap.get(value.maturityLevelId()),
                    sortedLevels
                ).stream()
            )
            .toList();
    }

    private Set<Long> selectFurthestAttributes(List<LoadAttributeValuesPort.Result> attributeValues,
                                               List<MaturityLevel> maturityLevels,
                                               List<Attribute> attributes) {
        Map<Long, Integer> maturityLevelIdToIndexMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, MaturityLevel::getIndex));

        Map<Long, Integer> attributeIdToWeightMap = attributes.stream()
            .collect(toMap(Attribute::getId, Attribute::getWeight));

        MaturityLevel maxLevel = Collections.max(maturityLevels, comparingInt(MaturityLevel::getIndex));

        return attributeValues.stream()
            .filter(v -> v.maturityLevelId() != maxLevel.getId())
            .map(v -> Map.entry(
                v.attributeId(),
                attributeIdToWeightMap.getOrDefault(v.attributeId(), 1) *
                    (maxLevel.getIndex() - maturityLevelIdToIndexMap.get(v.maturityLevelId()))
            ))
            .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
            .limit(MAX_TARGETS_LIMIT)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    private Optional<AttributeLevelTarget> toTarget(long attributeId,
                                                    int currentLevelIndex,
                                                    List<MaturityLevel> sortedLevels) {
        return sortedLevels.stream()
            .dropWhile(level -> level.getIndex() <= currentLevelIndex)
            .findFirst()
            .map(nextLevel -> new AttributeLevelTarget(attributeId, nextLevel.getId()));
    }

    private void generateAdvice(AssessmentResult result, List<AttributeLevelTarget> targets) {
        var adviceListItems = createAdviceHelper.createAdvice(result.getAssessmentId(), targets);
        createAiAdviceNarrationHelper.createAiAdviceNarration(result, adviceListItems, targets);
    }
}
