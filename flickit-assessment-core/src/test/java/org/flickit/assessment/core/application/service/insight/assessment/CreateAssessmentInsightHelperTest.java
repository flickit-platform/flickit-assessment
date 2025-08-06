package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.*;
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

    @Mock
    private CountSubjectsPort countSubjectsPort;

    @Test
    void testCreateAssessmentInsight_whenQuickAssessmentWithOneSubjectIsComplete_thenCreateCompleteQuickAssessmentInsight() {
        var assessmentResult = validResultWithAssessmentMode(AssessmentMode.QUICK);
        var locale = Locale.ENGLISH;
        var progress = new GetAssessmentProgressPort.Result(assessmentResult.getId(), 10, 10);
        var subjectsCount = 1;

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progress);
        when(loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId()))
            .thenReturn(assessmentResult.getMaturityLevel());
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);

        var result = helper.createAssessmentInsight(assessmentResult, locale);

        assertNull(result.getId());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertNotNull(result.getInsightTime());
        assertNotNull(result.getLastModificationTime());
        assertNotNull(result.getInsight());
        assertNull(result.getInsightBy());
        assertFalse(result.isApproved());
    }

    @Test
    void testCreateAssessmentInsight_whenAdvancedAssessmentWithMultipleSubjectsIsIncomplete_thenCreateCompleteQuickAssessmentInsight() {
        var assessmentResult = validResultWithAssessmentMode(AssessmentMode.QUICK);
        assessmentResult.setConfidenceValue(null);
        var locale = Locale.of(KitLanguage.FA.getCode());
        var progress = new GetAssessmentProgressPort.Result(assessmentResult.getId(), 9, 10);
        var subjectsCount = 2;

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progress);
        when(loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId()))
            .thenReturn(assessmentResult.getMaturityLevel());
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);

        var result = helper.createAssessmentInsight(assessmentResult, locale);

        assertNull(result.getId());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertNotNull(result.getInsightTime());
        assertNotNull(result.getLastModificationTime());
        assertNotNull(result.getInsight());
        assertNull(result.getInsightBy());
        assertFalse(result.isApproved());
    }
}
