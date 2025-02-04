package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.subjectinsight.GetSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.in.subjectinsight.GetSubjectInsightUseCase.Param;
import org.flickit.assessment.core.application.port.in.subjectinsight.GetSubjectInsightUseCase.Result;
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
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.SubjectInsightMother.subjectInsightWithTimesAndApproved;
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
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.getSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsight_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getSubjectInsight(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort, loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsight_whenInsightDoesNotExist_thenReturnEmptyInsight() {
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.empty());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);

        Result result = service.getSubjectInsight(param);

        assertNotNull(result);
        assertNull(result.defaultInsight());
        assertNull(result.assessorInsight());
        assertFalse(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetSubjectInsight_whenInsightCreatedByAssessorBeforeCalculationAndNotApprovedAndEditable_thenReturnInvalidAssessorInsight() {
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);

        Result result = service.getSubjectInsight(param);

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(subjectInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertFalse(result.assessorInsight().isValid());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetSubjectInsight_whenInsightCreatedByAssessorAndApprovedBeforeCalculationAndNotEditable_thenReturnInvalidAssessorInsight() {
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(2);
        var insightLastModificationTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightLastModificationTime, true);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);

        Result result = service.getSubjectInsight(param);

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(subjectInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertFalse(result.assessorInsight().isValid());
        assertFalse(result.editable());
        assertTrue(result.approved());
    }

    @Test
    void testGetSubjectInsight_whenInsightCreatedByAssessorBeforeCalculationAndApprovedAfterCalculationAndEditable_thenReturnValidAssessorInsight() {
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var insightLastModificationTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = subjectInsightWithTimesAndApproved(insightTime, insightLastModificationTime, true);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);

        Result result = service.getSubjectInsight(param);

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(subjectInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertTrue(result.assessorInsight().isValid());
        assertTrue(result.editable());
        assertTrue(result.approved());
    }

    @Test
    void testGetSubjectInsight_whenInsightInitializedBeforeCalculationAndNotApprovedAndEditable_thenReturnInvalidDefaultInsight() {
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);

        Result result = service.getSubjectInsight(param);

        assertNull(result.assessorInsight());
        assertNotNull(result.defaultInsight());
        assertEquals(subjectInsight.getInsight(), result.defaultInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.defaultInsight().creationTime());
        assertFalse(result.defaultInsight().isValid());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetSubjectInsight_whenInsightInitializedAfterCalculationAndNotApprovedAndEditable_thenReturnValidDefaultInsight() {
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var insightTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightTime, false);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);

        Result result = service.getSubjectInsight(param);

        assertNull(result.assessorInsight());
        assertNotNull(result.defaultInsight());
        assertEquals(subjectInsight.getInsight(), result.defaultInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.defaultInsight().creationTime());
        assertTrue(result.defaultInsight().isValid());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetSubjectInsight_whenInsightInitializedBeforeCalculationAndApprovedAfterCalculationAndEditable_thenReturnValidDefaultInsight() {
        var param = createParam(GetSubjectInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var insightTime = assessmentResult.getLastCalculationTime().minusDays(1);
        var insightLastCalculationTime = assessmentResult.getLastCalculationTime().plusDays(1);
        var subjectInsight = SubjectInsightMother.defaultSubjectInsight(insightTime, insightLastCalculationTime, true);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);

        Result result = service.getSubjectInsight(param);

        assertNull(result.assessorInsight());
        assertNotNull(result.defaultInsight());
        assertEquals(subjectInsight.getInsight(), result.defaultInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.defaultInsight().creationTime());
        assertTrue(result.defaultInsight().isValid());
        assertTrue(result.editable());
        assertTrue(result.approved());
    }

    private GetSubjectInsightUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private GetSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return GetSubjectInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
