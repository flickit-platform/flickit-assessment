package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AssessmentInsightBuilderHelperTest {

    @InjectMocks
    private AssessmentInsightBuilderHelper helper;

    @ParameterizedTest
    @MethodSource("buildAdvancedAssessmentParams")
    void testBuildInsight_whenAssessmentModeIsAdvancedAndLanguageIsEnglishAndAssessmentIsIncomplete_oneSubject(AssessmentMode mode, int subjectCount, int questionCount, int answerCount) {
        Locale locale = Locale.ENGLISH;
        var param = new AssessmentInsightBuilderHelper.Param("Maturity",  questionCount, answerCount, 85, mode, subjectCount, locale);

        String result = helper.build(param);

        assertResult(locale, param, result);
    }

    @ParameterizedTest
    @MethodSource("buildQuickAssessmentParams")
    void testBuildInsight_whenAssessmentModeIsQuick(AssessmentMode mode, int subjectCount, int questionCount, int answerCount) {
        Locale locale = Locale.ENGLISH;
        var param = new AssessmentInsightBuilderHelper.Param("Maturity", questionCount, answerCount, 85, mode, subjectCount, locale);

        String result = helper.build(param);

        assertResult(locale, param, result);
    }

    private void assertResult(Locale locale, AssessmentInsightBuilderHelper.Param param, String result) {
        if (Locale.ENGLISH.equals(locale)) {
            assertEnglishResult(param, result);
            assertSubjectsEnglish(param, result);
        } else {
            assertPersianResult(result);
            assertSubjectsPersian(param, result);
        }
    }

    private void assertEnglishResult(AssessmentInsightBuilderHelper.Param param, String result) {
        assertTrue(result.contains("Maturity"));
        assertTrue(result.contains("all"));
        if (AssessmentMode.ADVANCED.equals(param.mode())) {
            assertTrue(result.contains("10"));
            assertTrue(result.contains("85"));
        } else {
            assertFalse(result.contains("85"));
        }
    }

    private void assertPersianResult(String result) {
        assertTrue(result.contains("سطح اطمینان"));
        assertTrue(result.contains("۱۰"));
        assertTrue(result.contains("۸۵"));
    }

    private void assertSubjectsEnglish(AssessmentInsightBuilderHelper.Param param, String result) {
        if (param.subjectCount() > 1)
            assertTrue(result.contains("weighted average"));
        else
            assertFalse(result.contains("weighted average"));

    }

    private void assertSubjectsPersian(AssessmentInsightBuilderHelper.Param param, String result) {
        if (param.subjectCount() > 1)
            assertTrue(result.contains("میانگین وزنی"));
        else
            assertFalse(result.contains("میانگین وزنی"));
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> buildAdvancedAssessmentParams() {
        Locale persianLocale = Locale.of(KitLanguage.FA.getCode());
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.ADVANCED, 1, 10, 10, Locale.ENGLISH),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.ADVANCED, 1, 10, 9, Locale.ENGLISH),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.ADVANCED, 2, 10, 10, Locale.ENGLISH),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.ADVANCED, 2, 10, 9, Locale.ENGLISH),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.ADVANCED, 1, 10, 10, persianLocale),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.ADVANCED, 1, 10, 9, persianLocale),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.ADVANCED, 2, 10, 10, persianLocale),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.ADVANCED, 2, 10, 9, persianLocale)
        );
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> buildQuickAssessmentParams() {
        Locale persianLocale = Locale.of(KitLanguage.FA.getCode());
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.QUICK, 1, 10, 10, Locale.ENGLISH),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.QUICK, 1, 10, 9, Locale.ENGLISH),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.QUICK, 2, 10, 10, Locale.ENGLISH),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.QUICK, 2, 10, 9, Locale.ENGLISH),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.QUICK, 1, 10, 10, persianLocale),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.QUICK, 1, 10, 9, persianLocale),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.QUICK, 2, 10, 10, persianLocale),
            org.junit.jupiter.params.provider.Arguments.of(AssessmentMode.QUICK, 2, 10, 9, persianLocale)
        );
    }
}
