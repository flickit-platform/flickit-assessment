package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.GetSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.SubjectInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSubjectInsightServiceTest {

    @InjectMocks
    private GetSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Test
    void testGetSubjectInsight_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsight_whenSubjectInsightExistsAndIsValidAndEditableAndCreatedByAssessor_thenReturnAssessorInsight() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResult();
        SubjectInsight subjectInsight = SubjectInsightMother.approvedSubjectInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);

        GetSubjectInsightUseCase.Result result = service.getSubjectInsight(param);

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(subjectInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertTrue(result.assessorInsight().isValid());
        assertTrue(result.editable());
        assertTrue(result.approved());

    }

    @Test
    void testGetSubjectInsight_whenSubjectInsightExistsAndIsNotValidAndNotEditableAndCreatedByAssessor_thenReturnAssessorInsight() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResult();
        SubjectInsight subjectInsight = new SubjectInsight(assessmentResult.getId(), param.getSubjectId(),
            "assessor insight",
            assessmentResult.getLastCalculationTime().minusDays(1),
            param.getCurrentUserId(),
            true
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);

        GetSubjectInsightUseCase.Result result = service.getSubjectInsight(param);

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(subjectInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertFalse(result.assessorInsight().isValid());
        assertFalse(result.editable());
        assertTrue(result.approved());
    }

    @Test
    void testGetSubjectInsight_whenSubjectInsightExistsAndIsValidAndEditableAndIsNotCreatedByAssessor_thenReturnDefaultInsight() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResult();
        SubjectInsight subjectInsight = SubjectInsightMother.defaultSubjectInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);

        GetSubjectInsightUseCase.Result result = service.getSubjectInsight(param);

        assertNull(result.assessorInsight());
        assertNotNull(result.defaultInsight());
        assertEquals(subjectInsight.getInsight(), result.defaultInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.defaultInsight().creationTime());
        assertTrue(result.defaultInsight().isValid());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetSubjectInsight_whenSubjectInsightDoesNotExist_thenThrowResourceNotFoundException() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.empty());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);

        var result = service.getSubjectInsight(param);

        assertNotNull(result);
        assertNull(result.defaultInsight());
        assertNull(result.assessorInsight());
        assertFalse(result.editable());
        assertFalse(result.approved());
    }
}
