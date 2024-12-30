package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.UpdateSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBJECT_INSIGHT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubjectInsightServiceTest {

    @InjectMocks
    private UpdateSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Mock
    private UpdateSubjectInsightPort updateSubjectInsightPort;

    @Test
    void testUpdateSubjectInsight_whenValidParamAlreadyExists_thenUpdate() {
        UpdateSubjectInsightUseCase.Param param = new UpdateSubjectInsightUseCase.Param(UUID.randomUUID(),
            115L,
            "insight",
            UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResult();
        SubjectInsight subjectInsight = new SubjectInsight(assessmentResult.getId(),
            param.getSubjectId(),
            "old insight",
            LocalDateTime.of(2022, 2, 20, 0, 0),
            param.getCurrentUserId(), true);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.of(subjectInsight));

        doNothing().when(updateSubjectInsightPort).update(any());

        service.updateSubjectInsight(param);

        var updatePortParam = ArgumentCaptor.forClass(SubjectInsight.class);
        verify(updateSubjectInsightPort, times(1)).update(updatePortParam.capture());
        assertEquals(assessmentResult.getId(), updatePortParam.getValue().getAssessmentResultId());
        assertEquals(param.getSubjectId(), updatePortParam.getValue().getSubjectId());
        assertEquals(param.getInsight(), updatePortParam.getValue().getInsight());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().getInsightBy());
        assertNotNull(updatePortParam.getValue().getInsightTime());
        assertTrue(updatePortParam.getValue().isApproved());
    }

    @Test
    void testUpdateSubjectInsight_whenUserHasNoAccess_thenThrowsException() {
        UpdateSubjectInsightUseCase.Param param = new UpdateSubjectInsightUseCase.Param(UUID.randomUUID(),
            115L,
            "insight",
            UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.updateSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadAssessmentResultPort, updateSubjectInsightPort, loadSubjectInsightPort);
    }

    @Test
    void testUpdateSubjectInsight_whenSubjectInsightDoesntExist_thenThrowResourceNotFoundException() {
        UpdateSubjectInsightUseCase.Param param = new UpdateSubjectInsightUseCase.Param(UUID.randomUUID(),
            115L,
            "insight",
            UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateSubjectInsight(param));
        assertEquals(SUBJECT_INSIGHT_ID_NOT_FOUND, throwable.getMessage());
    }
}
