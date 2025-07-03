package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceStatus;
import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.user.CreateUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.application.service.constant.SpaceConstants.DEFAULT_SPACE_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @InjectMocks
    private CreateUserService service;

    @Mock
    private CreateUserPort createUserPort;

    @Mock
    private CreateSpacePort createSpacePort;

    @Test
    void testCreateUserService_whenParametersAreValid_thenReturnValidResult() {
        var param = createParam(CreateUserUseCase.Param.ParamBuilder::build);
        var userId = UUID.randomUUID();

        when(createUserPort.persist(param.getUserId(), param.getDisplayName(), param.getEmail())).thenReturn(userId);
        var spaceArgumentCaptor = ArgumentCaptor.forClass(Space.class);

        var result = service.createUser(param);
        verify(createSpacePort).persist(spaceArgumentCaptor.capture());
        var capturedSpace = spaceArgumentCaptor.getValue();

        assertEquals(userId, result.userId());
        assertNull(capturedSpace.getId());
        assertEquals(generateSlugCode(DEFAULT_SPACE_TITLE), capturedSpace.getCode());
        assertEquals(DEFAULT_SPACE_TITLE, capturedSpace.getTitle());
        assertEquals(SpaceType.BASIC, capturedSpace.getType());
        assertEquals(userId, capturedSpace.getOwnerId());
        assertEquals(SpaceStatus.ACTIVE, capturedSpace.getStatus());
        assertNull(capturedSpace.getSubscriptionExpiry());
        assertTrue(capturedSpace.isDefault());
        assertNotNull(capturedSpace.getCreationTime());
        assertNotNull(capturedSpace.getLastModificationTime());
        assertEquals(userId, capturedSpace.getCreatedBy());
        assertEquals(userId, capturedSpace.getLastModifiedBy());
    }

    private CreateUserUseCase.Param createParam(Consumer<CreateUserUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateUserUseCase.Param.ParamBuilder paramBuilder() {
        return CreateUserUseCase.Param.builder()
            .userId(UUID.randomUUID())
            .email("admin@flickit.com")
            .displayName("Display Name");
    }
}
