package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Result;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentServiceTest {

    @InjectMocks
    private GetAssessmentService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Test
    void testGetAssessment_validResult() {
        var maturityLevel = MaturityLevelMother.levelThree();
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(null, maturityLevel);
        Assessment assessment = assessmentResult.getAssessment();
        UUID assessmentId = assessment.getId();
        User assessmentCreator = new User(assessment.getCreatedBy(), "Display name");
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadById(assessment.getCreatedBy())).thenReturn(Optional.of(assessmentCreator));
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));

        Result result = service.getAssessment(new Param(assessmentId, currentUserId));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        assertEquals(assessment.getTitle(), result.title());
        assertEquals(assessment.getSpace().getId(), result.space().getId());
        assertEquals(assessment.getSpace().getTitle(), result.space().getTitle());
        assertEquals(assessment.getAssessmentKit().getId(), result.kit().getId());
        assertEquals(assessment.getAssessmentKit().getTitle(), result.kit().getTitle());
        assertEquals(assessment.getCreationTime(), result.creationTime());
        assertEquals(assessment.getLastModificationTime(), result.lastModificationTime());
        assertEquals(assessmentCreator.getId(), result.createdBy().getId());
        assertEquals(assessmentCreator.getDisplayName(), result.createdBy().getDisplayName());
        assertEquals(maturityLevel, result.maturityLevel());
        assertEquals(assessmentResult.getIsCalculateValid(), result.isCalculateValid());

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
        verify(loadUserPort, times(1)).loadById(any());
    }

    @Test
    void getAssessment_invalidAssessmentId() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId))
            .thenReturn(Optional.empty());

        Param param = new Param(assessmentId, currentUserId);
        assertThrows(ResourceNotFoundException.class, () -> service.getAssessment(param));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
        verify(loadUserPort, never()).loadById(any());
    }

    @Test
    void getAssessment_UserHasNotAccess() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(false);

        Param param = new Param(assessmentId, currentUserId);
        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, never()).getAssessmentById(any());
    }
}
