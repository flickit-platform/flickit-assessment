package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.flickit.assessment.core.common.MessageKey.*;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.*;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelFive;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentInsightHelperTest {

    @InjectMocks
    private CreateAssessmentInsightHelper helper;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Mock
    private LoadMaturityLevelPort loadMaturityLevelPort;

    @Test
    void testCreateAssessmentInsight_whenQuickAssessmentIsComplete_thenCreateCompleteQuickAssessmentInsight() {
        var assessmentResult = validResultWithAssessmentMode(AssessmentMode.QUICK);
        var locale = Locale.ENGLISH;
        var progress = new GetAssessmentProgressPort.Result(assessmentResult.getId(), 10, 10);
        var expectedDefaultInsight = MessageBundle.message(QUICK_ASSESSMENT_DEFAULT_INSIGHT_COMPLETED,
            locale,
            assessmentResult.getMaturityLevel().getTitle(),
            progress.questionsCount(),
            Math.ceil(assessmentResult.getConfidenceValue()));

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progress);
        when(loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId()))
            .thenReturn(assessmentResult.getMaturityLevel());

        var result = helper.createAssessmentInsight(assessmentResult, locale);

        assertNull(result.getId());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertEquals(expectedDefaultInsight, result.getInsight());
        assertNotNull(result.getInsightTime());
        assertNotNull(result.getLastModificationTime());
        assertNull(result.getInsightBy());
        assertFalse(result.isApproved());
    }

    @Test
    void testCreateAssessmentInsight_whenLocaleIsPersianAndQuickAssessmentIsIncomplete_thenCreatePersianIncompleteQuickAssessmentInsight() {
        var assessmentResult = validResultWithAssessmentMode(AssessmentMode.QUICK);
        var locale = Locale.of(KitLanguage.FA.getCode());
        var progress = new GetAssessmentProgressPort.Result(assessmentResult.getId(), 10, 11);
        var expectedDefaultInsight = MessageBundle.message(QUICK_ASSESSMENT_DEFAULT_INSIGHT_INCOMPLETE,
            locale,
            assessmentResult.getMaturityLevel().getTitle(),
            progress.answersCount(),
            progress.questionsCount(),
            Math.ceil(assessmentResult.getConfidenceValue()));

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progress);
        when(loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId()))
            .thenReturn(assessmentResult.getMaturityLevel());

        var result = helper.createAssessmentInsight(assessmentResult, locale);

        assertNull(result.getId());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertEquals(expectedDefaultInsight, result.getInsight());
        assertNotNull(result.getInsightTime());
        assertNotNull(result.getLastModificationTime());
        assertNull(result.getInsightBy());
        assertFalse(result.isApproved());
    }

    @Test
    void testCreateAssessmentInsight_whenAdvancedAssessmentIsIncompleteWithNullConfidenceValue_thenCreateIncompleteAdvancedAssessmentInsight() {
        var assessmentResult = validResultWithSubjectValuesAndMaturityLevel(null, levelFive());
        var locale = Locale.of(KitLanguage.FA.getCode());
        var progress = new GetAssessmentProgressPort.Result(assessmentResult.getId(), 10, 11);
        var expectedDefaultInsight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE,
            locale,
            assessmentResult.getMaturityLevel().getTitle(),
            progress.answersCount(),
            progress.questionsCount(),
            0);

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progress);
        when(loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId()))
            .thenReturn(assessmentResult.getMaturityLevel());

        var result = helper.createAssessmentInsight(assessmentResult, locale);

        assertNull(result.getId());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertEquals(expectedDefaultInsight, result.getInsight());
        assertNotNull(result.getInsightTime());
        assertNotNull(result.getLastModificationTime());
        assertNull(result.getInsightBy());
        assertFalse(result.isApproved());
    }

    @Test
    void testCreateAssessmentInsight_whenAdvancedAssessmentIsComplete_thenCreateCompleteAdvancedAssessmentInsight() {
        var assessmentResult = validResultWithAssessmentMode(AssessmentMode.ADVANCED);
        var locale = Locale.of(KitLanguage.EN.getCode());
        var progress = new GetAssessmentProgressPort.Result(assessmentResult.getId(), 11, 11);
        var expectedDefaultInsight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED,
            locale,
            assessmentResult.getMaturityLevel().getTitle(),
            progress.questionsCount(),
            Math.ceil(assessmentResult.getConfidenceValue()));

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progress);
        when(loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId()))
            .thenReturn(assessmentResult.getMaturityLevel());

        var result = helper.createAssessmentInsight(assessmentResult, locale);

        assertNull(result.getId());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertEquals(expectedDefaultInsight, result.getInsight());
        assertNotNull(result.getInsightTime());
        assertNotNull(result.getLastModificationTime());
        assertNull(result.getInsightBy());
        assertFalse(result.isApproved());
    }
}
