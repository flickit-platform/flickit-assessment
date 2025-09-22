package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.application.domain.space.SpaceStatus;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.CreateUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @Mock
    private CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Captor
    private ArgumentCaptor<CreateSpacePort.Param> createSpaceCaptor;

    @Captor
    private ArgumentCaptor<SpaceUserAccess> createSpaceUserAccessCaptor;

    @Captor
    private ArgumentCaptor<CreateUserPort.Param> createUserCaptor;

    @Test
    void testCreateUserService_whenParametersAreValid_thenReturnValidResult() {
        var param = createParam(CreateUserUseCase.Param.ParamBuilder::build);
        var userId = UUID.randomUUID();

        when(createUserPort.persist(createUserCaptor.capture())).thenReturn(userId);

        var result = service.createUser(param);

        assertEquals(userId, result.userId());

        verify(createSpacePort).persist(createSpaceCaptor.capture());

        var capturedUser = createUserCaptor.getValue();
        assertEquals(param.getUserId(), capturedUser.id());
        assertEquals(param.getDisplayName(), capturedUser.displayName());
        assertEquals(param.getEmail(), capturedUser.email());
        assertNotNull(capturedUser.creationTime());
        assertNotNull(capturedUser.lastModificationTime());

        var capturedSpace = createSpaceCaptor.getValue();
        assertEquals(generateSlugCode(DEFAULT_SPACE_TITLE), capturedSpace.code());
        assertEquals(DEFAULT_SPACE_TITLE, capturedSpace.title());
        assertEquals(SpaceType.BASIC, capturedSpace.type());
        assertEquals(userId, capturedSpace.createdBy());
        assertEquals(SpaceStatus.ACTIVE, capturedSpace.status());
        assertNull(capturedSpace.subscriptionExpiry());
        assertTrue(capturedSpace.isDefault());
        assertNotNull(capturedSpace.creationTime());

        verify(createSpaceUserAccessPort).persist(createSpaceUserAccessCaptor.capture());
        assertEquals(userId, createSpaceUserAccessCaptor.getValue().getCreatedBy());
        assertEquals(userId, createSpaceUserAccessCaptor.getValue().getUserId());
        assertNotNull(createSpaceUserAccessCaptor.getValue().getCreationTime());
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
