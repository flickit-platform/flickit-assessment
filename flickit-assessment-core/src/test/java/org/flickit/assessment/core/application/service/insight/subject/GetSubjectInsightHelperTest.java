package org.flickit.assessment.core.application.service.insight.subject;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.test.fixture.application.SubjectInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.SubjectInsightMother.subjectInsightWithTimesAndApproved;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSubjectInsightHelperTest {

    @InjectMocks
    private GetSubjectInsightHelper helper;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Mock
    private LoadSubjectInsightsPort loadSubjectInsightsPort;

    private final AssessmentResult assessmentResult = validResult();
    private final Long subjectId = 153L;
    private final UUID currentUserId = UUID.randomUUID();

    @Test
    void testGetSubjectInsightHelper_whenSubjectInsightDoesNotExist_thenReturnEmpty() {
        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightPort.load(assessmentResult.getId(), subjectId)).thenReturn(Optional.empty());

        var result = helper.getSubjectInsight(assessmentResult, subjectId, currentUserId);

        assertNotNull(result);
        assertNull(result.getDefaultInsight());
        assertNull(result.getAssessorInsight());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
        verifyNoInteractions(loadSubjectInsightsPort);
    }

    @Test
    void testGetSubjectInsightHelper_whenInsightCreatedByAssessorBeforeCalculationAndNotApprovedAndEditable_thenReturnInvalidAssessorInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightPort.load(assessmentResult.getId(), subjectId)).thenReturn(Optional.of(subjectInsight));

        var result = helper.getSubjectInsight(assessmentResult, subjectId, currentUserId);

        assertNull(result.getDefaultInsight());
        assertNotNull(result.getAssessorInsight());
        assertEquals(subjectInsight.getInsight(), result.getAssessorInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertFalse(result.getAssessorInsight().isValid());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
        verifyNoInteractions(loadSubjectInsightsPort);
    }

    @Test
    void testGetSubjectInsightHelper_whenInsightCreatedByAssessorAndApprovedBeforeCalculationAndNotEditable_thenReturnInvalidAssessorInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(2);
        var insightLastModificationTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightLastModificationTime, true);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);
        when(loadSubjectInsightPort.load(assessmentResult.getId(), subjectId)).thenReturn(Optional.of(subjectInsight));

        var result = helper.getSubjectInsight(assessmentResult, subjectId, currentUserId);

        assertNull(result.getDefaultInsight());
        assertNotNull(result.getAssessorInsight());
        assertEquals(subjectInsight.getInsight(), result.getAssessorInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertFalse(result.getAssessorInsight().isValid());
        assertFalse(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadSubjectInsightsPort);
    }

    @Test
    void testGetSubjectInsightHelper_whenInsightCreatedByAssessorBeforeCalculationAndApprovedAfterCalculationAndEditable_thenReturnValidAssessorInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var insightLastModificationTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightLastModificationTime, true);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightPort.load(assessmentResult.getId(), subjectId)).thenReturn(Optional.of(subjectInsight));

        var result = helper.getSubjectInsight(assessmentResult, subjectId, currentUserId);

        assertNull(result.getDefaultInsight());
        assertNotNull(result.getAssessorInsight());
        assertEquals(subjectInsight.getInsight(), result.getAssessorInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertTrue(result.getAssessorInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadSubjectInsightsPort);
    }

    @Test
    void testGetSubjectInsightHelper_whenInsightInitializedBeforeCalculationAndNotApprovedAndEditable_thenReturnInvalidDefaultInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightPort.load(assessmentResult.getId(), subjectId)).thenReturn(Optional.of(subjectInsight));

        var result = helper.getSubjectInsight(assessmentResult, subjectId, currentUserId);

        assertNull(result.getAssessorInsight());
        assertNotNull(result.getDefaultInsight());
        assertEquals(subjectInsight.getInsight(), result.getDefaultInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertFalse(result.getDefaultInsight().isValid());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
        verifyNoInteractions(loadSubjectInsightsPort);
    }

    @Test
    void testGetSubjectInsightHelper_whenInsightInitializedAfterCalculationAndNotApprovedAndEditable_thenReturnValidDefaultInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightPort.load(assessmentResult.getId(), subjectId)).thenReturn(Optional.of(subjectInsight));

        var result = helper.getSubjectInsight(assessmentResult, subjectId, currentUserId);

        assertNull(result.getAssessorInsight());
        assertNotNull(result.getDefaultInsight());
        assertEquals(subjectInsight.getInsight(), result.getDefaultInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertTrue(result.getDefaultInsight().isValid());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
        verifyNoInteractions(loadSubjectInsightsPort);
    }

    @Test
    void testGetSubjectInsightHelper_whenInsightInitializedBeforeCalculationAndApprovedAfterCalculationAndEditable_thenReturnValidDefaultInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var insightLastCalculationTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightLastCalculationTime, true);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightPort.load(assessmentResult.getId(), subjectId)).thenReturn(Optional.of(subjectInsight));

        var result = helper.getSubjectInsight(assessmentResult, subjectId, currentUserId);

        assertNull(result.getAssessorInsight());
        assertNotNull(result.getDefaultInsight());
        assertEquals(subjectInsight.getInsight(), result.getDefaultInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertTrue(result.getDefaultInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadSubjectInsightsPort);
    }


    @Test
    void testGetSubjectInsightsHelper_whenInsightCreatedByAssessorBeforeCalculationAndNotApprovedAndEditable_thenReturnInvalidAssessorInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight));

        var result = helper.getSubjectInsights(assessmentResult, currentUserId);

        var resultInsight = result.get(subjectInsight.getSubjectId());
        assertNull(resultInsight.getDefaultInsight());
        assertNotNull(resultInsight.getAssessorInsight());
        assertEquals(subjectInsight.getInsight(), resultInsight.getAssessorInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), resultInsight.getAssessorInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), resultInsight.getAssessorInsight().getLastModificationTime());
        assertFalse(resultInsight.getAssessorInsight().isValid());
        assertTrue(resultInsight.isEditable());
        assertFalse(resultInsight.getApproved());
        verifyNoInteractions(loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsightsHelper_whenInsightCreatedByAssessorAndApprovedBeforeCalculationAndNotEditable_thenReturnInvalidAssessorInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(2);
        var insightLastModificationTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightLastModificationTime, true);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight));

        var result = helper.getSubjectInsights(assessmentResult, currentUserId);

        var resultInsight = result.get(subjectInsight.getSubjectId());
        assertNull(resultInsight.getDefaultInsight());
        assertNotNull(resultInsight.getAssessorInsight());
        assertEquals(subjectInsight.getInsight(), resultInsight.getAssessorInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), resultInsight.getAssessorInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), resultInsight.getAssessorInsight().getLastModificationTime());
        assertFalse(resultInsight.getAssessorInsight().isValid());
        assertFalse(resultInsight.isEditable());
        assertTrue(resultInsight.getApproved());
        verifyNoInteractions(loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsightsHelper_whenInsightCreatedByAssessorBeforeCalculationAndApprovedAfterCalculationAndEditable_thenReturnValidAssessorInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var insightLastModificationTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightLastModificationTime, true);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight));

        var result = helper.getSubjectInsights(assessmentResult, currentUserId);

        var resultInsight = result.get(subjectInsight.getSubjectId());
        assertNull(resultInsight.getDefaultInsight());
        assertNotNull(resultInsight.getAssessorInsight());
        assertEquals(subjectInsight.getInsight(), resultInsight.getAssessorInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), resultInsight.getAssessorInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), resultInsight.getAssessorInsight().getLastModificationTime());
        assertTrue(resultInsight.getAssessorInsight().isValid());
        assertTrue(resultInsight.isEditable());
        assertTrue(resultInsight.getApproved());
        verifyNoInteractions(loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsightsHelper_whenInsightInitializedBeforeCalculationAndNotApprovedAndEditable_thenReturnInvalidDefaultInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight));

        var result = helper.getSubjectInsights(assessmentResult, currentUserId);

        var resultInsight = result.get(subjectInsight.getSubjectId());
        assertNull(resultInsight.getAssessorInsight());
        assertNotNull(resultInsight.getDefaultInsight());
        assertEquals(subjectInsight.getInsight(), resultInsight.getDefaultInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), resultInsight.getDefaultInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), resultInsight.getDefaultInsight().getLastModificationTime());
        assertFalse(resultInsight.getDefaultInsight().isValid());
        assertTrue(resultInsight.isEditable());
        assertFalse(resultInsight.getApproved());
        verifyNoInteractions(loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsightsHelper_whenInsightInitializedAfterCalculationAndNotApprovedAndEditable_thenReturnValidDefaultInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight));

        var result = helper.getSubjectInsights(assessmentResult, currentUserId);

        var resultInsight = result.get(subjectInsight.getSubjectId());
        assertNull(resultInsight.getAssessorInsight());
        assertNotNull(resultInsight.getDefaultInsight());
        assertEquals(subjectInsight.getInsight(), resultInsight.getDefaultInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), resultInsight.getDefaultInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), resultInsight.getDefaultInsight().getLastModificationTime());
        assertTrue(resultInsight.getDefaultInsight().isValid());
        assertTrue(resultInsight.isEditable());
        assertFalse(resultInsight.getApproved());
        verifyNoInteractions(loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsightsHelper_whenInsightInitializedBeforeCalculationAndApprovedAfterCalculationAndEditable_thenReturnValidDefaultInsight() {
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var insightLastCalculationTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightLastCalculationTime, true);

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight));

        var result = helper.getSubjectInsights(assessmentResult, currentUserId);

        var resultInsight = result.get(subjectInsight.getSubjectId());
        assertNull(resultInsight.getAssessorInsight());
        assertNotNull(resultInsight.getDefaultInsight());
        assertEquals(subjectInsight.getInsight(), resultInsight.getDefaultInsight().getInsight());
        assertEquals(subjectInsight.getInsightTime(), resultInsight.getDefaultInsight().getCreationTime());
        assertEquals(subjectInsight.getLastModificationTime(), resultInsight.getDefaultInsight().getLastModificationTime());
        assertTrue(resultInsight.getDefaultInsight().isValid());
        assertTrue(resultInsight.isEditable());
        assertTrue(resultInsight.getApproved());
        verifyNoInteractions(loadSubjectInsightPort);
    }
}
