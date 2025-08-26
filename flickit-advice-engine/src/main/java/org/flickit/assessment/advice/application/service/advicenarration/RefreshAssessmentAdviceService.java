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

import static java.lang.Math.ceilDiv;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.advice.common.ErrorMessageKey.REFRESH_ASSESSMENT_ADVICE_MEDIAN_MATURITY_LEVEL_NOT_FOUND;
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

    private static final int MIN_REQUIRED_TARGET_ATTRIBUTES_SIZE = 2;
    private static final int MAX_FURTHEST_TARGET_ATTRIBUTES_SIZE = 2;
    private static final int MIN_REQUIRED_IMPROVABLE_QUESTIONS_SIZE = 10;

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

    private AttributeTargetsDto prepareAttributeLevelTargets(AssessmentResult result) {
        var attributeValues = loadAttributeValuesPort.loadAll(result.getId());
        var maturityLevels = loadMaturityLevelsPort.loadAll(result.getAssessmentId());

        List<MaturityLevel> sortedLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getIndex))
            .toList();

        MaturityLevel midLevel = extractMidLevel(sortedLevels);
        MaturityLevel maxLevel = sortedLevels.getLast();

        var levelIdToIndexMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, MaturityLevel::getIndex));

        Set<Long> weakAttributeIds = new HashSet<>();
        List<LoadAttributeValuesPort.Result> nonWeakAttributes = new ArrayList<>();

        for (LoadAttributeValuesPort.Result attributeValue : attributeValues) {
            if (attributeValue.maturityLevelId() == maxLevel.getId())
                continue;
            if (levelIdToIndexMap.get(attributeValue.maturityLevelId()) < midLevel.getIndex())
                weakAttributeIds.add(attributeValue.attributeId());
            else
                nonWeakAttributes.add(attributeValue);
        }

        var weakAttributeTargets = buildWeakAttributeTargets(weakAttributeIds, midLevel);
        if (weakAttributeTargets.size() >= MIN_REQUIRED_TARGET_ATTRIBUTES_SIZE)
            return AttributeTargetsDto.of(weakAttributeTargets, Collections.emptyList());

        List<Long> attributeIds = attributeValues.stream().map(LoadAttributeValuesPort.Result::attributeId).toList();
        var attributes = loadAttributesPort.loadByIdsAndAssessmentId(attributeIds, result.getAssessmentId());
        var nonWeakAttributeTargets = buildNonWeakAttributeTargets(attributes, sortedLevels, nonWeakAttributes, maxLevel);

        return AttributeTargetsDto.of(weakAttributeTargets, nonWeakAttributeTargets);
    }

    private MaturityLevel extractMidLevel(List<MaturityLevel> maturityLevels) {
        int midLevelIndex = ceilDiv(maturityLevels.size(), 2);
        return maturityLevels.stream()
            .filter(m -> m.getIndex() == midLevelIndex)
            .findFirst()
            .orElseThrow(() ->
                new ResourceNotFoundException(REFRESH_ASSESSMENT_ADVICE_MEDIAN_MATURITY_LEVEL_NOT_FOUND)); // Can't happen
    }

    List<AttributeLevelTarget> buildWeakAttributeTargets(Set<Long> weakAttributeIds,
                                                         MaturityLevel midLevel) {
        return weakAttributeIds.stream()
            .map(value -> new AttributeLevelTarget(value, midLevel.getId()))
            .toList();
    }

    private List<AttributeLevelTarget> buildNonWeakAttributeTargets(List<Attribute> attributes,
                                                                    List<MaturityLevel> maturityLevels,
                                                                    List<LoadAttributeValuesPort.Result> attributeValues,
                                                                    MaturityLevel maxLevel) {
        var attributeIdToWeightMap = attributes.stream()
            .collect(toMap(Attribute::getId, Attribute::getWeight));
        var maturityLevelIdToIndexMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, MaturityLevel::getIndex));

        var mostImportantAttributes = attributeValues.stream()
            .map(v -> {
                var score = attributeIdToWeightMap.getOrDefault(v.attributeId(), 1) *
                    (maxLevel.getIndex() - maturityLevelIdToIndexMap.get(v.maturityLevelId()));
                return Map.entry(v.attributeId(), score);
            })
            .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .collect(toSet());

        Comparator<LoadAttributeValuesPort.Result> scoreComparator =
            Comparator.comparingInt(v ->
                (maxLevel.getIndex() - maturityLevelIdToIndexMap.get(v.maturityLevelId()))
                    * attributeIdToWeightMap.get(v.attributeId())
            );
        return attributeValues.stream()
            .filter(v -> mostImportantAttributes.contains(v.attributeId()))
            .sorted(scoreComparator)
            .flatMap(value -> toTarget(
                    value.attributeId(),
                    maturityLevelIdToIndexMap.get(value.maturityLevelId()),
                    maturityLevels
                ).stream()
            )
            .toList().reversed();
    }

    private Optional<AttributeLevelTarget> toTarget(long attributeId,
                                                    int currentLevelIndex,
                                                    List<MaturityLevel> sortedLevels) {
        return sortedLevels.stream()
            .dropWhile(level -> level.getIndex() <= currentLevelIndex)
            .findFirst()
            .map(nextLevel -> new AttributeLevelTarget(attributeId, nextLevel.getId()));
    }

    private void generateAdvice(AssessmentResult result, AttributeTargetsDto targets) {
        var weakAttributeTargets = targets.weakAttributeTargets;
        var nonWeakAttributeTargets = targets.nonWeakAttributeTargets;

        if (weakAttributeTargets.size() < MIN_REQUIRED_TARGET_ATTRIBUTES_SIZE) {
            for (int i = 0; i < MAX_FURTHEST_TARGET_ATTRIBUTES_SIZE; i++) {
                AttributeLevelTarget next = nonWeakAttributeTargets.pollFirst();
                if (next == null) break;
                weakAttributeTargets.add(next);
            }
        }

        var improvableQuestions = new ArrayList<>(
            createAdviceHelper.createAdvice(result.getAssessmentId(), List.copyOf(weakAttributeTargets))
        );
        while (improvableQuestions.size() < MIN_REQUIRED_IMPROVABLE_QUESTIONS_SIZE && !nonWeakAttributeTargets.isEmpty()) {
            AttributeLevelTarget next = nonWeakAttributeTargets.pollFirst();
            improvableQuestions.addAll(
                createAdviceHelper.createAdvice(result.getAssessmentId(), List.of(next))
            );
            weakAttributeTargets.add(next);
        }

        createAiAdviceNarrationHelper.createAiAdviceNarration(result, improvableQuestions, List.copyOf(weakAttributeTargets));
    }

    record AttributeTargetsDto(List<AttributeLevelTarget> weakAttributeTargets,
                               Deque<AttributeLevelTarget> nonWeakAttributeTargets) {

        public boolean isEmpty() {
            return weakAttributeTargets.isEmpty() && nonWeakAttributeTargets.isEmpty();
        }

        public static AttributeTargetsDto of(List<AttributeLevelTarget> weakAttributeTargets,
                                             List<AttributeLevelTarget> nonWeakAttributeTargets) {
            return new AttributeTargetsDto(new ArrayList<>(weakAttributeTargets), new ArrayDeque<>(nonWeakAttributeTargets));
        }
    }
}
