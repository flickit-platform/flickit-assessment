package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeValuesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.CreateAdviceHelper;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public void refreshAssessmentAdvice(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var attributeLevelTargets = prepareAttributeLevelTargets(assessmentResult);
        if (param.getForceRegenerate())
            regenerateAdvice(assessmentResult, attributeLevelTargets);
    }

    private List<AttributeLevelTarget> prepareAttributeLevelTargets(AssessmentResult result) {
        var attributeValues = loadAttributeValuesPort.loadAll(result.getId());
        var maturityLevels = loadMaturityLevelsPort.loadAll(result.getAssessmentId());
        return buildAttributeLevelTargets(attributeValues, maturityLevels);
    }

    List<AttributeLevelTarget> buildAttributeLevelTargets(List<LoadAttributeValuesPort.Result> attributeValues, List<MaturityLevel> maturityLevels) {
        List<MaturityLevel> sortedMaturityLevels = maturityLevels.stream()
            .sorted(Comparator.comparingInt(MaturityLevel::getIndex))
            .toList();

        return attributeValues.stream()
            .map(av -> buildAttributeLevelTarget(av, sortedMaturityLevels))
            .flatMap(Optional::stream)
            .toList();
    }

    private void regenerateAdvice(AssessmentResult result, List<AttributeLevelTarget> targets) {
        log.info("Regenerating advice for assessmentId=[{}]", result.getAssessmentId());
        var adviceListItems = createAdviceHelper.createAdvice(result.getAssessmentId(), targets);
        createAiAdviceNarrationHelper.createAiAdviceNarration(result, adviceListItems, targets);
    }

    Optional<AttributeLevelTarget> buildAttributeLevelTarget(LoadAttributeValuesPort.Result attributeValue, List<MaturityLevel> sortedMaturityLevels) {
        var map = sortedMaturityLevels.stream()
            .collect(Collectors.toMap(MaturityLevel::getId, MaturityLevel::getIndex));
        int currentLevelIndex = map.get(attributeValue.maturityLevelId());

        return sortedMaturityLevels.stream()
            .filter(level -> level.getIndex() > currentLevelIndex)
            .findFirst()
            .map(level -> new AttributeLevelTarget(attributeValue.attributeId(), level.getId()));
    }
}
