package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.space.GetSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceDetailsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.test.fixture.application.SpaceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSpaceServiceTest {

    @InjectMocks
    GetSpaceService service;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    LoadSpaceDetailsPort loadSpaceDetailsPort;

    private final GetSpaceUseCase.Param param = createParam(GetSpaceUseCase.Param.ParamBuilder::build);

    @Test
    void testGetSpace_WhenUserIsOwner_ThenReturnSpaceWithEditableTrue() {
        var space = SpaceMother.basicSpace(param.getCurrentUserId());
        var portResult = new LoadSpaceDetailsPort.Result(space, 1, 2);

        when(checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadSpaceDetailsPort.loadSpace(param.getId())).thenReturn(portResult);

        var result = service.getSpace(param);
        assertEquals(portResult.space().getId(), result.space().getId());
        assertEquals(portResult.space().getCode(), result.space().getCode());
        assertEquals(portResult.space().getTitle(), result.space().getTitle());
        assertEquals(space.getType().getCode(), result.space().getType().getCode());
        assertEquals(space.getType().getTitle(), result.space().getType().getTitle());
        assertTrue(result.editable());
        assertEquals(portResult.space().getLastModificationTime(), result.space().getLastModificationTime());
        assertEquals(portResult.membersCount(), result.membersCount());
        assertEquals(portResult.assessmentsCount(), result.assessmentsCount());
    }

    @Test
    void testGetSpace_WhenUserIsNotOwner_ThenReturnSpaceWithEditableFalse() {
        var space = SpaceMother.basicSpace(UUID.randomUUID());
        var portResult = new LoadSpaceDetailsPort.Result(space, 1, 2);

        when(checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadSpaceDetailsPort.loadSpace(param.getId())).thenReturn(portResult);

        var result = service.getSpace(param);
        assertEquals(portResult.space().getId(), result.space().getId());
        assertEquals(portResult.space().getCode(), result.space().getCode());
        assertEquals(portResult.space().getTitle(), result.space().getTitle());
        assertEquals(space.getType().getCode(), result.space().getType().getCode());
        assertEquals(space.getType().getTitle(), result.space().getType().getTitle());
        assertFalse(result.editable());
        assertEquals(portResult.space().getLastModificationTime(), result.space().getLastModificationTime());
        assertEquals(portResult.membersCount(), result.membersCount());
        assertEquals(portResult.assessmentsCount(), result.assessmentsCount());
    }

    @Test
    void testGetSpace_WhenSpaceDoesNotExist_ThenThrowResourceNotFoundException() {
        when(checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadSpaceDetailsPort.loadSpace(param.getId()))
            .thenThrow(new ResourceNotFoundException(SPACE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getSpace(param));
        assertEquals(SPACE_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetSpace_WhenSpaceAccessNotFound_ThenThrowAccessDeniedException() {
        when(checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getSpace(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadSpaceDetailsPort);
    }

    private GetSpaceUseCase.Param createParam(Consumer<GetSpaceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetSpaceUseCase.Param.ParamBuilder paramBuilder() {
        return GetSpaceUseCase.Param.builder()
            .id(123L)
            .currentUserId(UUID.randomUUID());
    }
}
