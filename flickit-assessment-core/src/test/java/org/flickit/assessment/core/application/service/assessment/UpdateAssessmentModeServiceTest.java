package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentModeUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_ASSESSMENT_MODE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentModeServiceTest {

    @InjectMocks
    private UpdateAssessmentModeService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    private final UpdateAssessmentModeUseCase.Param param = createParam(UpdateAssessmentModeUseCase.Param.ParamBuilder::build);

    @Test
    void updateAssessmentMode_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_ASSESSMENT_MODE))
            .thenReturn(false);

        var exception = assertThrows(RuntimeException.class, () -> service.updateAssessmentMode(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void updateAssessmentMode_whenParamsAreValid_thenSuccess() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_ASSESSMENT_MODE))
            .thenReturn(true);
        ArgumentCaptor<UpdateAssessmentPort.UpdateModeParam> argumentCaptor = ArgumentCaptor.forClass(UpdateAssessmentPort.UpdateModeParam.class);

        service.updateAssessmentMode(param);
        verify(updateAssessmentPort).updateMode(argumentCaptor.capture());

        assertEquals(param.getAssessmentId(), argumentCaptor.getValue().assessmentId());
        assertEquals(AssessmentMode.ADVANCED, argumentCaptor.getValue().mode());
        assertNotNull(argumentCaptor.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().lastModifiedBy());
    }

    private UpdateAssessmentModeUseCase.Param createParam(Consumer<UpdateAssessmentModeUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAssessmentModeUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAssessmentModeUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .mode(AssessmentMode.ADVANCED.name())
            .currentUserId(UUID.randomUUID());
    }
}
