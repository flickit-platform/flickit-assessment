package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentServiceTest {

    @InjectMocks
    private UpdateAssessmentService service;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private UpdateAssessmentResultPort updateAssessmentResultPort;

    private final UUID id = UUID.randomUUID();
    private final UUID currentUserId = UUID.randomUUID();
    private UpdateAssessmentUseCase.Param param = createParam(UpdateAssessmentUseCase.Param.ParamBuilder::build);

    @Test
    void testUpdateAssessment_whenParametersAreValid_thenUpdatedAndReturnsId() {
        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(true);
        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));

        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());
        verify(updateAssessmentResultPort).updateLanguage(KitLanguage.FA);

        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(param.getTitle(), updatePortParam.getValue().title());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().lastModifiedBy());
        assertNotNull(updatePortParam.getValue().title());
        assertNotNull(updatePortParam.getValue().lastModificationTime());
    }

    @Test
    void testUpdateAssessment_whenParametersAreValidAndLangNotProvided_thenUpdatedAndReturnsId() {
        param = createParam(b -> b.lang(null));

        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(true);
        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));

        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());

        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(param.getTitle(), updatePortParam.getValue().title());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().lastModifiedBy());
        assertNotNull(updatePortParam.getValue().title());
        assertNotNull(updatePortParam.getValue().lastModificationTime());

        verifyNoInteractions(updateAssessmentResultPort);
    }

    @Test
    void testUpdateAssessment_whenUserHasNoAccessToAssessment_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private UpdateAssessmentUseCase.Param createParam(Consumer<UpdateAssessmentUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAssessmentUseCase.Param.builder()
            .id(UUID.randomUUID())
            .title("title")
            .shortTitle("shortTitle")
            .lang("FA")
            .currentUserId(currentUserId);
    }
}
