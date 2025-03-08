package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_INSIGHT;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.*;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentInsightHelperTest {

    @InjectMocks
    private GetAssessmentInsightHelper helper;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    private final AssessmentResult assessmentResult = validResult();
    private final UUID currentUserId = UUID.randomUUID();

    @Test
    void testGetAssessmentInsightHelperHelper_whenAssessmentInsightDoesNotExist_thenReturnEmpty() {
        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());

        var result = helper.getAssessmentInsight(assessmentResult, currentUserId);

        assertNotNull(result);
        assertNull(result.getDefaultInsight());
        assertNull(result.getAssessorInsight());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
    }

    @Test
    void testGetAssessmentInsightHelper_whenAssessmentInsightExistsAndIsValidAndEditable_thenReturnAssessorInsight() {
        var assessmentInsight = createWithAssessmentResultId(assessmentResult.getId());

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));

        var result = helper.getAssessmentInsight(assessmentResult, currentUserId);

        assertNull(result.getDefaultInsight());
        assertNotNull(result.getAssessorInsight());
        assertEquals(assessmentInsight.getInsight(), result.getAssessorInsight().getInsight());
        assertEquals(assessmentInsight.getInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(assessmentInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertTrue(result.getAssessorInsight().isValid());
        assertTrue(result.isEditable());
    }

    @Test
    void testGetAssessmentInsightHelper_whenAssessmentInsightExistsAndIsNotValidAndNotEditable_thenReturnAssessorInsight() {
        var assessmentInsight = createWithMinInsightTime();

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ASSESSMENT_INSIGHT)).thenReturn(false);
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));

        var result = helper.getAssessmentInsight(assessmentResult, currentUserId);

        assertNotNull(result.getAssessorInsight());
        assertEquals(assessmentInsight.getInsight(), result.getAssessorInsight().getInsight());
        assertEquals(assessmentInsight.getInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(assessmentInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertFalse(result.getAssessorInsight().isValid());
        assertNull(result.getDefaultInsight());
        assertFalse(result.isEditable());
        assertFalse(result.getApproved());
    }

    @Test
    void testGetAssessmentInsightHelper_whenDefaultInsightExistsAndIsValidAndEditable_ReturnDefaultInsight() {
        var assessmentInsight = createDefaultInsightWithAssessmentResultId(assessmentResult.getId());

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));

        var result = helper.getAssessmentInsight(assessmentResult, currentUserId);

        assertNotNull(result.getDefaultInsight().getInsight());
        assertEquals(assessmentInsight.getInsight(), result.getDefaultInsight().getInsight());
        assertEquals(assessmentInsight.getInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(assessmentInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertTrue(result.getDefaultInsight().isValid());
        assertNull(result.getAssessorInsight());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
    }

    @Test
    void testGetAssessmentInsightHelper_whenDefaultInsightExistsAndIsNotValidAndNotEditable_ReturnDefaultInsight() {
        var lastCalculateTime = assessmentResult.getLastCalculationTime();
        var assessmentInsight = createDefaultInsightWithTimesAndApprove(lastCalculateTime.minusDays(1),
            lastCalculateTime.minusDays(1),
            true);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ASSESSMENT_INSIGHT)).thenReturn(false);
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));

        var result = helper.getAssessmentInsight(assessmentResult, currentUserId);

        assertNotNull(result.getDefaultInsight().getInsight());
        assertEquals(assessmentInsight.getInsight(), result.getDefaultInsight().getInsight());
        assertEquals(assessmentInsight.getInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(assessmentInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertFalse(result.getDefaultInsight().isValid());
        assertNull(result.getAssessorInsight());
        assertFalse(result.isEditable());
        assertTrue(result.getApproved());
    }
}
