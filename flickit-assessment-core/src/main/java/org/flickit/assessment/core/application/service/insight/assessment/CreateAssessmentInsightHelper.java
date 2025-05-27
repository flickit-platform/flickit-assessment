package org.flickit.assessment.core.application.service.insight.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.core.common.MessageKey.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAssessmentInsightHelper {

    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final LoadMaturityLevelPort loadMaturityLevelPort;

    public AssessmentInsight createAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var progress = getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId());
        int questionsCount = progress.questionsCount();
        int answersCount = progress.answersCount();
        int confidenceValue = assessmentResult.getConfidenceValue() != null
            ? (int) Math.ceil(assessmentResult.getConfidenceValue())
            : 0;
        var maturityLevelTitle = loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(),
                assessmentResult.getAssessment().getId())
            .getTitle();
        var assessmentInsightParam = new AssessmentInsightParam(assessmentResult.getAssessment().getMode(),
            maturityLevelTitle,
            questionsCount,
            answersCount,
            confidenceValue,
            locale);
        String insight = buildInsight(assessmentInsightParam);
        return toAssessmentInsight(assessmentResult.getId(), insight);
    }

    private static String buildInsight(AssessmentInsightParam param) {
        return (param.questionsCount == param.answersCount)
            ? buildDefaultCompleteInsight(param)
            : buildDefaultIncompleteInsight(param);
    }

    private static String buildDefaultCompleteInsight(AssessmentInsightParam param) {
        return (AssessmentMode.ADVANCED.equals(param.mode))
            ? MessageBundle.message(ADVANCED_ASSESSMENT_DEFAULT_INSIGHT_COMPLETED,
                param.locale,
                param.maturityLevelTitle,
                param.questionsCount,
                param.confidenceValue)
            : MessageBundle.message(QUICK_ASSESSMENT_DEFAULT_INSIGHT_COMPLETED,
                param.locale,
                param.maturityLevelTitle,
                param.questionsCount);
    }

    private static String buildDefaultIncompleteInsight(AssessmentInsightParam param) {
        return (AssessmentMode.ADVANCED.equals(param.mode))
            ? MessageBundle.message(ADVANCED_ASSESSMENT_DEFAULT_INSIGHT_INCOMPLETE,
                param.locale,
                param.maturityLevelTitle,
                param.answersCount,
                param.questionsCount,
                param.confidenceValue)
            : MessageBundle.message(QUICK_ASSESSMENT_DEFAULT_INSIGHT_INCOMPLETE,
                param.locale,
                param.maturityLevelTitle,
                param.answersCount,
                param.questionsCount);
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

    record AssessmentInsightParam(AssessmentMode mode,
                                  String maturityLevelTitle,
                                  int questionsCount,
                                  int answersCount,
                                  int confidenceValue,
                                  Locale locale) {
    }
}
