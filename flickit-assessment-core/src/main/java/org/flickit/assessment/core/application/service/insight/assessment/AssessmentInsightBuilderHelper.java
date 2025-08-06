package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.core.application.domain.AssessmentMode;

import java.util.Locale;

public class AssessmentInsightBuilderHelper {

    private static final String ADVANCED_ASSESSMENT_DEFAULT_INSIGHT = "advanced-assessment-default-insight";
    private static final String QUICK_ASSESSMENT_DEFAULT_INSIGHT = "quick-assessment-default-insight";
    private static final String ONE_SUBJECT = "one-subject";
    private static final String MULTIPLE_SUBJECTS = "multiple-subjects";
    private static final String COMPLETED = "completed";
    private static final String INCOMPLETE = "incomplete";

    public static String buildAssessmentInsight(Param param) {
        boolean isCompleted = param.answersCount == param.questionsCount;
        var insightMessageKey = generateInsightMessageMainPart(param.mode, param.subjectCount, isCompleted);
        return isCompleted
            ? generateCompletedInsightMessage(param, insightMessageKey)
            : generateIncompletedInsightMessage(param, insightMessageKey);
    }

    private static String generateCompletedInsightMessage(Param param, String insightMessageKey) {

        return (AssessmentMode.ADVANCED.equals(param.mode()))
            ? MessageBundle.message(insightMessageKey, param.locale, param.maturityLevelTitle, param.questionsCount, param.confidenceValue)
            : MessageBundle.message(insightMessageKey, param.locale, param.maturityLevelTitle, param.questionsCount);
    }

    private static String generateIncompletedInsightMessage(Param param, String insightKey) {
        return (AssessmentMode.ADVANCED.equals(param.mode))
            ? MessageBundle.message(insightKey, param.locale, param.maturityLevelTitle, param.answersCount, param.questionsCount, param.confidenceValue)
            : MessageBundle.message(insightKey, param.locale, param.maturityLevelTitle, param.answersCount, param.questionsCount);
    }

    private static String generateInsightMessageMainPart(AssessmentMode mode, int subjectCount, boolean isCompleted) {
        String assessmentMode = AssessmentMode.ADVANCED.equals(mode)
            ? ADVANCED_ASSESSMENT_DEFAULT_INSIGHT
            : QUICK_ASSESSMENT_DEFAULT_INSIGHT;
        String subjectDescriptor = subjectCount > 1 ? MULTIPLE_SUBJECTS : ONE_SUBJECT;
        String completionStatus = isCompleted ? COMPLETED : INCOMPLETE;

        return String.join(".", assessmentMode, subjectDescriptor, completionStatus);
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
