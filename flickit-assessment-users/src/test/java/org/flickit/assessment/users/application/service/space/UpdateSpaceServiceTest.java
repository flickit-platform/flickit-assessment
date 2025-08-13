package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.space.UpdateSpacePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_SPACE_SPACE_DEFAULT_SPACE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSpaceServiceTest {

    @InjectMocks
    private UpdateSpaceService service;

    @Mock
    private LoadSpacePort loadSpacePort;

    @Mock
    private UpdateSpacePort updateSpacePort;

    private final UpdateSpaceUseCase.Param param = createParam(UpdateSpaceUseCase.Param.ParamBuilder::build);

    @Test
    void testUpdateSpace_whenSpaceDoesNotExist_thenResourceNotFound() {
        when(loadSpacePort.loadOwnerId(param.getId())).thenThrow(new ResourceNotFoundException(SPACE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateSpace(param));
        assertEquals(SPACE_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(updateSpacePort);
    }

    @Test
    void testUpdateSpace_whenUserIsNotOwner_thenThrowsAccessDeniedException() {
        when(loadSpacePort.loadOwnerId(param.getId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateSpace(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateSpacePort);
    }

    @Test
    void testUpdateSpace_whenSpaceIsDefault_thenThrowsValidationException() {
        when(loadSpacePort.loadOwnerId(param.getId())).thenReturn(param.getCurrentUserId());
        when(loadSpacePort.checkIsDefault(param.getId())).thenReturn(true);

        var throwable = assertThrows(ValidationException.class, () -> service.updateSpace(param));
        assertEquals(UPDATE_SPACE_SPACE_DEFAULT_SPACE, throwable.getMessageKey());

        verifyNoInteractions(updateSpacePort);
    }

    @Test
    void testUpdateSpace_whenParametersAreValid_thenSuccessfulUpdate() {
        when(loadSpacePort.loadOwnerId(param.getId())).thenReturn(param.getCurrentUserId());
        when(loadSpacePort.checkIsDefault(param.getId())).thenReturn(false);

        service.updateSpace(param);

        var captor = ArgumentCaptor.forClass(UpdateSpacePort.Param.class);
        verify(updateSpacePort).updateSpace(captor.capture());

        assertEquals(param.getId(), captor.getValue().id());
        assertEquals(generateSlugCode(param.getTitle()), captor.getValue().code());
        assertEquals(param.getTitle(), captor.getValue().title());
        assertEquals(param.getCurrentUserId(), captor.getValue().lastModifiedBy());
        assertNotNull(captor.getValue().lastModificationTime());
    }

    private UpdateSpaceUseCase.Param createParam(Consumer<UpdateSpaceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateSpaceUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateSpaceUseCase.Param.builder()
            .id(0L)
            .title("title")
            .currentUserId(UUID.randomUUID());
    }
}
