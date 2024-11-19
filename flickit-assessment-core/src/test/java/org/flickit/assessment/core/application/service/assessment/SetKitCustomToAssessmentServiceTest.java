package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.SetKitCustomToAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetKitCustomToAssessmentServiceTest {

    @InjectMocks
    private SetKitCustomToAssessmentService service;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testSetKitCustomToAssessment_WhenCurrentUserDoesntHaveSetKitCustomPermission_ThenThrowAccessDeniedException() {
        var param = createParam(SetKitCustomToAssessmentUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            AssessmentPermission.SET_KIT_CUSTOM)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.setKitCustomToAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateAssessmentPort);
    }

    @Test
    void testSetKitCustomToAssessment_WhenCurrenUserHaveSetKitCustomPermission_ThenUpdateAssessmentKitCustom() {
        var param = createParam(SetKitCustomToAssessmentUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            AssessmentPermission.SET_KIT_CUSTOM)).thenReturn(true);
        doNothing().when(updateAssessmentPort).updateKitCustomId(param.getAssessmentId(), param.getKitCustomId());

        service.setKitCustomToAssessment(param);

        verify(updateAssessmentPort).updateKitCustomId(param.getAssessmentId(), param.getKitCustomId());
    }

    private SetKitCustomToAssessmentUseCase.Param createParam(Consumer<SetKitCustomToAssessmentUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private SetKitCustomToAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return SetKitCustomToAssessmentUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .kitCustomId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
