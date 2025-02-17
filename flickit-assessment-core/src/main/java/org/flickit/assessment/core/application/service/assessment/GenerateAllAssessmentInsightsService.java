package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessment.GenerateAllAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE;

@Service
@Transactional
@RequiredArgsConstructor
public class GenerateAllAssessmentInsightsService implements GenerateAllAssessmentInsightsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final CreateAssessmentInsightPort createAssessmentInsightPort;

    @Override
    public void generateAllAssessmentInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());

        initAssessmentInsight(assessmentResult, locale);
    }

    private void initAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (assessmentInsight.isEmpty()) {
            createAssessmentInsight(assessmentResult, locale);
        }
    }

    private void createAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var progress = getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId());
        var questionsCount = progress.questionsCount();
        var answersCount = progress.answersCount();
        var confidenceValue = assessmentResult.getConfidenceValue() != null
            ? (int) Math.ceil(assessmentResult.getConfidenceValue())
            : 0;
        var maturityLevelTitle = assessmentResult.getMaturityLevel().getTitle();
        var insight = (questionsCount == answersCount)
            ? MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED,
            locale,
            maturityLevelTitle,
            questionsCount,
            confidenceValue)
            : MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE,
            locale,
            maturityLevelTitle,
            answersCount,
            questionsCount,
            confidenceValue);
        createAssessmentInsightPort.persist(toAssessmentInsight(assessmentResult.getId(), insight));
    }

    AssessmentInsight toAssessmentInsight(UUID assessmentResultId, String insight) {
        return new AssessmentInsight(null,
            assessmentResultId,
            insight,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            false);
    }
}
