package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccess;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.LeaveExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveExpertGroupServiceTest {

    @InjectMocks
    private LeaveExpertGroupService service;

    @Mock
    private LoadExpertGroupAccessPort loadExpertGroupAccessPort;

    @Mock
    private DeleteExpertGroupMemberPort deleteExpertGroupMemberPort;


    @Test
    void tesLeaveExpertGroup_WhenUserHasNotAccess_ShouldThrowAccessDeniedException() {
        var param = createParam(LeaveExpertGroupUseCase.Param.ParamBuilder::build);

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.leaveExpertGroup(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void tesLeaveExpertGroup_ValidParameters_ShouldLeaveExpertGroup() {
        var param = createParam(LeaveExpertGroupUseCase.Param.ParamBuilder::build);

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(Optional.ofNullable(mock(ExpertGroupAccess.class)));

        service.leaveExpertGroup(param);

        verify(loadExpertGroupAccessPort, times(1)).loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId());
    }



    private LeaveExpertGroupUseCase.Param createParam(Consumer<LeaveExpertGroupUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private LeaveExpertGroupUseCase.Param.ParamBuilder paramBuilder() {
        return LeaveExpertGroupUseCase.Param.builder()
            .expertGroupId(123L)
            .currentUserId(UUID.randomUUID());
    }
}
