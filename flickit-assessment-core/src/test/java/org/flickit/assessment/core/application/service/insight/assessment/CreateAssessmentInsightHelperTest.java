package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel;
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
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Test
    void testCreateAssessmentInsight_whenAssessmentIsComplete_thenCreateCompleteAssessmentInsight() {
        var assessmentResult = validResult();
        var locale = Locale.ENGLISH;
        var progress = new GetAssessmentProgressPort.Result(assessmentResult.getId(), 10, 10);
        var expectedDefaultInsight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED,
            locale,
            assessmentResult.getMaturityLevel().getTitle(),
            progress.questionsCount(),
            Math.ceil(assessmentResult.getConfidenceValue()));

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progress);
        when(loadMaturityLevelsPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId()))
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
    void testCreateAssessmentInsight_whenAssessmentIsIncompleteWithNullConfidenceValue_thenCreateIncompleteAssessmentInsight() {
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
        when(loadMaturityLevelsPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId()))
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