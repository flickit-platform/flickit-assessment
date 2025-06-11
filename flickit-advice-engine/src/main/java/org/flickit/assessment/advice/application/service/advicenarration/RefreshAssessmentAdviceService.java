package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeValuesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.CreateAdviceHelper;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

        return buildTargets(attributeValues, maturityLevels);
    }

    private List<AttributeLevelTarget> buildTargets(List<LoadAttributeValuesPort.Result> attributeValues,
                                                    List<MaturityLevel> maturityLevels) {
        var maturityIndexMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, MaturityLevel::getIndex));

        var sortedLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getIndex))
            .toList();

        return attributeValues.stream()
            .flatMap(value -> toTarget(value, maturityIndexMap.get(value.maturityLevelId()), sortedLevels).stream())
            .toList();
    }

    private Optional<AttributeLevelTarget> toTarget(LoadAttributeValuesPort.Result value,
                                                    int currentLevelIndex,
                                                    List<MaturityLevel> sortedLevels) {
        return sortedLevels.stream()
            .dropWhile(level -> level.getIndex() <= currentLevelIndex)
            .findFirst()
            .map(nextLevel -> new AttributeLevelTarget(value.attributeId(), nextLevel.getId()));
    }

    private void generateAdvice(AssessmentResult result, List<AttributeLevelTarget> targets) {
        var adviceListItems = createAdviceHelper.createAdvice(result.getAssessmentId(), targets);
        createAiAdviceNarrationHelper.createAiAdviceNarration(result, adviceListItems, targets);
    }
}
