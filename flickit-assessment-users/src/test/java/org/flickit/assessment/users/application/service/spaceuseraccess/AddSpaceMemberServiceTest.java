package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.space.CheckDefaultSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddSpaceMemberServiceTest {

    @InjectMocks
    AddSpaceMemberService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private CheckDefaultSpacePort checkDefaultSpacePort;

    @Mock
    private CreateSpaceUserAccessPort createSpaceUserAccessPort;

    private final AddSpaceMemberUseCase.Param param = createParam(AddSpaceMemberUseCase.Param.ParamBuilder::build);
    private final UUID userId = UUID.randomUUID();

    @Test
    void testAddSpaceMember_whenParametersAreValid_thenSuccessfulAddMember() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.of(userId));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), userId)).thenReturn(false);
        when(checkDefaultSpacePort.checkIsDefault(param.getSpaceId())).thenReturn(false);
        var spaceUserAccessCaptor = ArgumentCaptor.forClass(SpaceUserAccess.class);

        service.addMember(param);
        verify(createSpaceUserAccessPort).persist(spaceUserAccessCaptor.capture());
        var capturedUserAccess = spaceUserAccessCaptor.getValue();

        assertEquals(param.getSpaceId(), capturedUserAccess.getSpaceId());
        assertEquals(userId, capturedUserAccess.getUserId());
        assertEquals(param.getCurrentUserId(), capturedUserAccess.getCreatedBy());
        assertNotNull(capturedUserAccess.getCreationTime());

        verify(checkSpaceAccessPort, times(2)).checkIsMember(anyLong(), any(UUID.class));
    }

    @Test
    void testAddSpaceMember_whenCurrentUserIsNotSpaceMember_thenThrowAccessDeniedException() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.addMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadUserPort, createSpaceUserAccessPort);
    }

    @Test
    void testAddSpaceMember_whenSpaceIsDefault_thenThrowAccessDeniedException() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkDefaultSpacePort.checkIsDefault(param.getSpaceId())).thenReturn(true);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.addMember(param));
        assertEquals(ADD_SPACE_MEMBER_SPACE_DEFAULT_SPACE, throwable.getMessage());

        verifyNoInteractions(loadUserPort, createSpaceUserAccessPort);
    }

    @Test
    void testAddSpaceMember_whenInviteeIsNotPlatformMember_thenThrowResourceNotFoundException() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkDefaultSpacePort.checkIsDefault(param.getSpaceId())).thenReturn(false);
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.addMember(param));
        assertEquals(USER_BY_EMAIL_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(createSpaceUserAccessPort);
    }

    @Test
    void testAddSpaceMember_whenInviteeIsAlreadyMember_thenThrowResourceAlreadyExistsException() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.of(userId));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), userId)).thenReturn(true);

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.addMember(param));
        assertEquals(ADD_SPACE_MEMBER_SPACE_USER_DUPLICATE, throwable.getMessage());

        verify(checkSpaceAccessPort, times(2)).checkIsMember(anyLong(), any(UUID.class));
        verifyNoInteractions(createSpaceUserAccessPort);
    }

    private AddSpaceMemberUseCase.Param createParam(Consumer<AddSpaceMemberUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private AddSpaceMemberUseCase.Param.ParamBuilder paramBuilder() {
        return AddSpaceMemberUseCase.Param.builder()
            .spaceId(0L)
            .email("admin@flickit.org")
            .currentUserId(UUID.randomUUID());
    }
}
