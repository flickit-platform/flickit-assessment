package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.DeleteAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.CreateAdviceHelper;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private final LoadAttributesPort loadAttributesPort;
    private final CreateAdviceHelper createAdviceHelper;
    private final CreateAiAdviceNarrationHelper createAiAdviceNarrationHelper;
    private final DeleteAdviceItemPort deleteAdviceItemPort;
    private final DeleteAdviceNarrationPort deleteAdviceNarrationPort;

    @Override
    public void refreshAssessmentAdvice(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        if (param.getForceRegenerate()) {
            var targets = prepareAttributeLevelTargets(assessmentResult);
            regenerateAdviceIfNecessary(assessmentResult, targets);
        }
    }

    private void regenerateAdviceIfNecessary(AssessmentResult assessmentResult, List<AttributeLevelTarget> targets) {
        if (!targets.isEmpty()) {
            log.info("Regenerating advice for [assessmentId={} and assessmentResultId={}]", assessmentResult.getAssessmentId(), assessmentResult.getId());
            deleteAdvice(assessmentResult);
            generateAdvice(assessmentResult, targets);
        }
    }

    private List<AttributeLevelTarget> prepareAttributeLevelTargets(AssessmentResult result) {
        var attributes = loadAttributesPort.loadAll(result.getAssessmentId(), result.getKitVersionId(), result.getLanguage());
        var maturityLevels = loadMaturityLevelsPort.loadAll(result.getAssessmentId());
        return buildAttributeLevelTargets(attributes, maturityLevels);
    }

    List<AttributeLevelTarget> buildAttributeLevelTargets(List<LoadAttributesPort.Result> attributes, List<MaturityLevel> maturityLevels) {
        List<MaturityLevel> sortedMaturityLevels = maturityLevels.stream()
            .sorted(Comparator.comparingInt(MaturityLevel::getIndex))
            .toList();

        return attributes.stream()
            .map(attribute -> buildAttributeLevelTarget(attribute, sortedMaturityLevels))
            .flatMap(Optional::stream)
            .toList();
    }

    Optional<AttributeLevelTarget> buildAttributeLevelTarget(LoadAttributesPort.Result attribute, List<MaturityLevel> sortedMaturityLevels) {
        int currentLevelIndex = attribute.maturityLevel().index();

        return sortedMaturityLevels.stream()
            .filter(level -> level.getIndex() > currentLevelIndex)
            .findFirst()
            .map(level -> new AttributeLevelTarget(attribute.id(), level.getId()));
    }

    private void deleteAdvice(AssessmentResult assessmentResult) {
        deleteAdviceItemPort.deleteAll(assessmentResult.getId());
        deleteAdviceNarrationPort.deleteAll(assessmentResult.getId());
    }

    private void generateAdvice(AssessmentResult result, List<AttributeLevelTarget> targets) {
        var adviceListItems = createAdviceHelper.createAdvice(result.getAssessmentId(), targets);
        createAiAdviceNarrationHelper.createAiAdviceNarration(result, adviceListItems, targets);
    }
}
