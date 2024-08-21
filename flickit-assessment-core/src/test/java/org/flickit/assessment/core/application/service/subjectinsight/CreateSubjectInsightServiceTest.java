package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.subjectinsight.CreateSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CheckSubjectInsightExistPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSubjectInsightServiceTest {

    @InjectMocks
    private CreateSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CheckSubjectInsightExistPort checkSubjectInsightExistPort;

    @Mock
    private CreateSubjectInsightPort createSubjectInsightPort;

    @Mock
    private UpdateSubjectInsightPort updateSubjectInsightPort;


    @Test
    void testCreateSubjectInsight_ValidParam_Persists() {
        CreateSubjectInsightUseCase.Param param = new CreateSubjectInsightUseCase.Param(UUID.randomUUID(),
            115L,
            "insight",
            UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(checkSubjectInsightExistPort.exists(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(false);

        doNothing().when(createSubjectInsightPort).persist(any());

        service.createSubjectInsight(param);

        var createPortParam = ArgumentCaptor.forClass(CreateSubjectInsightPort.Param.class);
        verify(createSubjectInsightPort).persist(createPortParam.capture());
        assertEquals(assessmentResult.getId(), createPortParam.getValue().assessmentResultId());
        assertEquals(param.getSubjectId(), createPortParam.getValue().subjectId());
        assertEquals(param.getInsight(), createPortParam.getValue().insight());
        assertEquals(param.getCurrentUserId(), createPortParam.getValue().insightBy());
        assertNotNull(createPortParam.getValue().insightTime());

        verifyNoInteractions(updateSubjectInsightPort);
    }

    @Test
    void testCreateSubjectInsight_ValidParamAlreadyExists_Update() {
        CreateSubjectInsightUseCase.Param param = new CreateSubjectInsightUseCase.Param(UUID.randomUUID(),
            115L,
            "insight",
            UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(checkSubjectInsightExistPort.exists(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(true);

        doNothing().when(updateSubjectInsightPort).update(any());

        service.createSubjectInsight(param);

        var updatePortParam = ArgumentCaptor.forClass(UpdateSubjectInsightPort.Param.class);
        verify(updateSubjectInsightPort, times(1)).update(updatePortParam.capture());
        assertEquals(assessmentResult.getId(), updatePortParam.getValue().assessmentResultId());
        assertEquals(param.getSubjectId(), updatePortParam.getValue().subjectId());
        assertEquals(param.getInsight(), updatePortParam.getValue().insight());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().insightBy());
        assertNotNull(updatePortParam.getValue().insightTime());

        verifyNoInteractions(createSubjectInsightPort);
    }

    @Test
    void testCreateSubjectInsight_UserHasNoAccess_ThrowsException() {
        CreateSubjectInsightUseCase.Param param = new CreateSubjectInsightUseCase.Param(UUID.randomUUID(),
            115L,
            "insight",
            UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.createSubjectInsight(param), COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(loadAssessmentResultPort,
            createSubjectInsightPort,
            createSubjectInsightPort,
            updateSubjectInsightPort);
    }
}
