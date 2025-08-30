package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.port.in.adviceitem.UpdateAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.UpdateAdviceItemPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.advice.common.ErrorMessageKey.UPDATE_ADVICE_ITEM_ASSESSMENT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_ADVICE_ITEM;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAdviceItemServiceTest {

    @InjectMocks
    UpdateAdviceItemService service;

    @Mock
    LoadAdviceItemPort loadAdviceItemPort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    UpdateAdviceItemPort updateAdviceItemPort;

    @Test
    void testUpdateAdviceItem_whenAssessmentNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(UpdateAdviceItemUseCase.Param.ParamBuilder::build);

        when(loadAdviceItemPort.loadAssessmentIdById(param.getAdviceItemId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateAdviceItem(param));
        assertEquals(UPDATE_ADVICE_ITEM_ASSESSMENT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(assessmentAccessChecker, updateAdviceItemPort);
    }

    @Test
    void testUpdateAdviceItem_whenUserIsNotAuthorized_thenThrowAccessDeniedException() {
        var param = createParam(UpdateAdviceItemUseCase.Param.ParamBuilder::build);
        var assessmentId = UUID.randomUUID();

        when(loadAdviceItemPort.loadAssessmentIdById(param.getAdviceItemId())).thenReturn(Optional.of(assessmentId));
        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), MANAGE_ADVICE_ITEM)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateAdviceItem(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateAdviceItemPort);
    }

    @Test
    void testUpdateAdviceItem_whenValidParameter_thenUpdatesAdviceItemSuccessfully() {
        var param = createParam(UpdateAdviceItemUseCase.Param.ParamBuilder::build);
        var assessmentId = UUID.randomUUID();

        when(loadAdviceItemPort.loadAssessmentIdById(param.getAdviceItemId())).thenReturn(Optional.of(assessmentId));
        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), MANAGE_ADVICE_ITEM)).thenReturn(true);

        service.updateAdviceItem(param);

        ArgumentCaptor<UpdateAdviceItemPort.Param> argumentCaptor = ArgumentCaptor.forClass(UpdateAdviceItemPort.Param.class);
        verify(updateAdviceItemPort).update(argumentCaptor.capture());
        assertEquals(param.getTitle(), argumentCaptor.getValue().title());
        assertEquals(param.getDescription(), argumentCaptor.getValue().description());
        assertEquals(param.getCost(), argumentCaptor.getValue().cost().name());
        assertEquals(param.getImpact(), argumentCaptor.getValue().impact().name());
        assertEquals(param.getPriority(), argumentCaptor.getValue().priority().name());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().lastModifiedBy());
    }

    private UpdateAdviceItemUseCase.Param createParam(Consumer<UpdateAdviceItemUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAdviceItemUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAdviceItemUseCase.Param.builder()
            .adviceItemId(UUID.randomUUID())
            .title("title")
            .description("description")
            .cost("LOW")
            .impact("MEDIUM")
            .priority("HIGH")
            .currentUserId(UUID.randomUUID());
    }
}
