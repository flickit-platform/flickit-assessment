package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.core.application.domain.AssessmentMode;

import java.util.Locale;

import static org.flickit.assessment.core.application.domain.AssessmentMode.ADVANCED;

public class AssessmentInsightMessageBuilder {

    private static final String ADVANCED_ASSESSMENT_DEFAULT_INSIGHT = "advanced-assessment-default-insight";
    private static final String QUICK_ASSESSMENT_DEFAULT_INSIGHT = "quick-assessment-default-insight";
    private static final String ONE_SUBJECT = "one-subject";
    private static final String MULTIPLE_SUBJECTS = "multiple-subjects";
    private static final String COMPLETED = "completed";
    private static final String INCOMPLETE = "incomplete";

    public static String buildInsightMessage(Param param) {
        boolean isCompleted = param.answersCount == param.questionsCount;
        var insightMessageKey = buildMessageKey(param.mode, param.subjectCount, isCompleted);
        return isCompleted
            ? buildMessageForCompletedAssessment(param, insightMessageKey)
            : buildMessageForIncompletedAssessment(param, insightMessageKey);
    }

    private static String buildMessageKey(AssessmentMode mode, int subjectCount, boolean isCompleted) {
        String assessmentMode = ADVANCED == mode
            ? ADVANCED_ASSESSMENT_DEFAULT_INSIGHT
            : QUICK_ASSESSMENT_DEFAULT_INSIGHT;
        String subjectDescriptor = subjectCount > 1 ? MULTIPLE_SUBJECTS : ONE_SUBJECT;
        String completionStatus = isCompleted ? COMPLETED : INCOMPLETE;

        return String.join(".", assessmentMode, subjectDescriptor, completionStatus);
    }

    private static String buildMessageForCompletedAssessment(Param param, String messageKey) {
        return ADVANCED == param.mode
            ? MessageBundle.message(messageKey, param.locale, param.maturityLevelTitle, param.questionsCount, param.confidenceValue)
            : MessageBundle.message(messageKey, param.locale, param.maturityLevelTitle, param.questionsCount);
    }

    private static String buildMessageForIncompletedAssessment(Param param, String messageKey) {
        return ADVANCED == param.mode
            ? MessageBundle.message(messageKey, param.locale, param.maturityLevelTitle, param.answersCount, param.questionsCount, param.confidenceValue)
            : MessageBundle.message(messageKey, param.locale, param.maturityLevelTitle, param.answersCount, param.questionsCount);
    }

    public record Param(
        String maturityLevelTitle,
        int questionsCount,
        int answersCount,
        int confidenceValue,
        AssessmentMode mode,
        int subjectCount,
        Locale locale) {
    }
}
