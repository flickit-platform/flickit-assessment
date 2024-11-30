package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.LeaveExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.LEAVE_EXPERT_GROUP_NOT_ALLOWED;
import static org.flickit.assessment.users.test.fixture.application.ExpertGroupAccessMother.activeAccess;
import static org.flickit.assessment.users.test.fixture.application.ExpertGroupAccessMother.pendingAccess;
import static org.flickit.assessment.users.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveExpertGroupServiceTest {

    @InjectMocks
    private LeaveExpertGroupService service;

    @Mock
    private LoadExpertGroupAccessPort loadExpertGroupAccessPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private DeleteExpertGroupMemberPort deleteExpertGroupMemberPort;

    private final ExpertGroup expertGroup = createExpertGroup("", UUID.randomUUID());
    private final UUID ownerId = UUID.randomUUID();

    @Test
    void testLeaveExpertGroup_WhenCurrentUserIsNotExpertGroupMember_ShouldThrowAccessDeniedException() {
        var param = createParam(b -> b.currentUserId(UUID.randomUUID()));

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.leaveExpertGroup(param));
        Assertions.assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadExpertGroupOwnerPort);
    }

    @Test
    void testLeaveExpertGroup_WhenCurrentUserIsExpertGroupOwner_ShouldThrowValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        var expertGroupAccess = activeAccess();

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(Optional.of(expertGroupAccess));
        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(ValidationException.class, () -> service.leaveExpertGroup(param));
        Assertions.assertEquals(LEAVE_EXPERT_GROUP_NOT_ALLOWED, throwable.getMessageKey());
    }

    @Test
    void testLeaveExpertGroup_WhenCurrentUserIsNotOwnerAndHasAccess_ShouldLeaveExpertGroup() {
        var param = createParam(LeaveExpertGroupUseCase.Param.ParamBuilder::build);
        var expertGroupAccess = pendingAccess(UUID.randomUUID());

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(Optional.of(expertGroupAccess));
        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(UUID.randomUUID());

        service.leaveExpertGroup(param);

        verify(deleteExpertGroupMemberPort, times(1)).deleteMember(param.getExpertGroupId(), param.getCurrentUserId());
    }

    private LeaveExpertGroupUseCase.Param createParam(Consumer<LeaveExpertGroupUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private LeaveExpertGroupUseCase.Param.ParamBuilder paramBuilder() {
        return LeaveExpertGroupUseCase.Param.builder()
            .expertGroupId(expertGroup.getId())
            .currentUserId(UUID.randomUUID());
    }
}
