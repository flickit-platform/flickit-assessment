package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.CreateAdviceHelper;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.REFRESH_ASSESSMENT_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

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
    private final LoadAdviceItemPort loadAdviceItemPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Override
    public void refreshAssessmentAdvice(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        List<MaturityLevel> maturityLevels = loadMaturityLevelsPort.loadAll(assessmentResult.getAssessmentId());
        List<LoadAttributesPort.Result> attributes = loadAttributesPort.loadAll(param.getAssessmentId());
        var attributeLevelTargets = buildAttributeLevelTargets(attributes, maturityLevels);

        if (loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId()).isEmpty() ||
            loadAdviceItemPort.loadAll(assessmentResult.getId()).isEmpty()) {
            var adviceListItems = createAdviceHelper.createAdvice(assessmentResult.getAssessmentId(), attributeLevelTargets);
            createAiAdviceNarrationHelper.createAiAdviceNarration(assessmentResult, adviceListItems, attributeLevelTargets);
        }
    }

    List<AttributeLevelTarget> buildAttributeLevelTargets(List<LoadAttributesPort.Result> attributes, List<MaturityLevel> maturityLevels) {
        List<MaturityLevel> sortedMaturityLevels = maturityLevels.stream()
            .sorted(Comparator.comparingInt(MaturityLevel::getIndex))
            .toList();

        return attributes.stream()
            .map(attribute -> buildAttributeLevelTarget(attribute, sortedMaturityLevels))
            .filter(Objects::nonNull)
            .toList();
    }

    AttributeLevelTarget buildAttributeLevelTarget(LoadAttributesPort.Result attribute, List<MaturityLevel> sortedMaturityLevels) {
        int currentLevelIndex = attribute.maturityLevel().index();

        return sortedMaturityLevels.stream()
            .filter(level -> level.getIndex() > currentLevelIndex)
            .findFirst()
            .map(level -> new AttributeLevelTarget(attribute.id(), level.getId()))
            .orElse(null);
    }
}
