package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Locale;
import java.util.stream.Stream;

import static org.flickit.assessment.core.application.service.insight.assessment.AssessmentInsightBuilderHelper.buildAssessmentInsight;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class AssessmentInsightBuilderHelperTest {

    @ParameterizedTest
    @MethodSource("buildAdvancedAssessmentParams")
    void testBuildInsight_whenAssessmentModeIsAdvanced(AssessmentMode mode, int subjectCount, int questionCount, int answerCount, Locale locale) {
        var param = new AssessmentInsightBuilderHelper.Param("Maturity", questionCount, answerCount, 85, mode, subjectCount, locale);

        String result = buildAssessmentInsight(param);
        String expected = buildExpectedMessage(param);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @MethodSource("buildQuickAssessmentParams")
    void testBuildInsight_whenAssessmentModeIsQuick(AssessmentMode mode, int subjectCount, int questionCount, int answerCount, Locale locale) {
        var param = new AssessmentInsightBuilderHelper.Param("Maturity", questionCount, answerCount, 85, mode, subjectCount, locale);

        String result = buildAssessmentInsight(param);
        String expected = buildExpectedMessage(param);

        assertEquals(expected, result);
    }

    private String buildExpectedMessage(AssessmentInsightBuilderHelper.Param param) {
        boolean isCompleted = param.answersCount() == param.questionsCount();

        String messageKey = generateInsightMessageKey(param.mode(), param.subjectCount(), isCompleted);

        return isCompleted
            ? param.mode() == AssessmentMode.ADVANCED
            ? MessageBundle.message(messageKey, param.locale(), param.maturityLevelTitle(), param.questionsCount(), param.confidenceValue())
            : MessageBundle.message(messageKey, param.locale(), param.maturityLevelTitle(), param.questionsCount())
            : param.mode() == AssessmentMode.ADVANCED
            ? MessageBundle.message(messageKey, param.locale(), param.maturityLevelTitle(), param.answersCount(), param.questionsCount(), param.confidenceValue())
            : MessageBundle.message(messageKey, param.locale(), param.maturityLevelTitle(), param.answersCount(), param.questionsCount());
    }

    private String generateInsightMessageKey(AssessmentMode mode, int subjectCount, boolean isCompleted) {
        String assessmentMode = mode == AssessmentMode.ADVANCED
            ? "advanced-assessment-default-insight"
            : "quick-assessment-default-insight";
        String subjectDescriptor = subjectCount > 1 ? "multiple-subjects" : "one-subject";
        String completionStatus = isCompleted ? "completed" : "incomplete";

        return String.join(".", assessmentMode, subjectDescriptor, completionStatus);
    }

    private static Stream<Arguments> buildAdvancedAssessmentParams() {
        Locale persianLocale = Locale.of(KitLanguage.FA.getCode());
        return Stream.of(
            Arguments.of(AssessmentMode.ADVANCED, 1, 10, 10, Locale.ENGLISH),
            Arguments.of(AssessmentMode.ADVANCED, 1, 10, 9, Locale.ENGLISH),
            Arguments.of(AssessmentMode.ADVANCED, 2, 10, 10, Locale.ENGLISH),
            Arguments.of(AssessmentMode.ADVANCED, 2, 10, 9, Locale.ENGLISH),
            Arguments.of(AssessmentMode.ADVANCED, 1, 10, 10, persianLocale),
            Arguments.of(AssessmentMode.ADVANCED, 1, 10, 9, persianLocale),
            Arguments.of(AssessmentMode.ADVANCED, 2, 10, 10, persianLocale),
            Arguments.of(AssessmentMode.ADVANCED, 2, 10, 9, persianLocale)
        );
    }

    private static Stream<Arguments> buildQuickAssessmentParams() {
        Locale persianLocale = Locale.of(KitLanguage.FA.getCode());
        return Stream.of(
            Arguments.of(AssessmentMode.QUICK, 1, 10, 10, Locale.ENGLISH),
            Arguments.of(AssessmentMode.QUICK, 1, 10, 9, Locale.ENGLISH),
            Arguments.of(AssessmentMode.QUICK, 2, 10, 10, Locale.ENGLISH),
            Arguments.of(AssessmentMode.QUICK, 2, 10, 9, Locale.ENGLISH),
            Arguments.of(AssessmentMode.QUICK, 1, 10, 10, persianLocale),
            Arguments.of(AssessmentMode.QUICK, 1, 10, 9, persianLocale),
            Arguments.of(AssessmentMode.QUICK, 2, 10, 10, persianLocale),
            Arguments.of(AssessmentMode.QUICK, 2, 10, 9, persianLocale)
        );
    }
}
