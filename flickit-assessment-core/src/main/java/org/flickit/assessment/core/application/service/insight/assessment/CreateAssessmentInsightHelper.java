package org.flickit.assessment.core.application.service.insight.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAssessmentInsightHelper {

    private final GetAssessmentProgressPort getAssessmentProgressPort;

    public AssessmentInsight createAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var progress = getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId());
        int questionsCount = progress.questionsCount();
        int answersCount = progress.answersCount();
        int confidenceValue = assessmentResult.getConfidenceValue() != null
            ? (int) Math.ceil(assessmentResult.getConfidenceValue())
            : 0;
        var maturityLevelTitle = assessmentResult.getMaturityLevel().getTitle();
        String insight = (questionsCount == answersCount)
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
        return toAssessmentInsight(assessmentResult.getId(), insight);
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
