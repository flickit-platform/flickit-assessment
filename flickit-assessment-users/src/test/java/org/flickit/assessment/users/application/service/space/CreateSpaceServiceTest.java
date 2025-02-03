package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountUserSpacesPort;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_SPACE_PERSONAL_SPACE_MAX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSpaceServiceTest {

    @InjectMocks
    private CreateSpaceService service;

    @Mock
    private CreateSpacePort createSpacePort;

    @Mock
    private CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Mock
    private CountUserSpacesPort countUserSpacesPort;

    @Spy
    AppSpecProperties appSpecProperties = appSpecProperties();

    @Test
    void testCreateSpace_whenSpaceTypeIsPremium_thenCreateSpaceWithoutCheckingSpaceLimits() {
        var param = createParam(b -> b.type("PREMIUM"));
        long createdSpaceId = 0L;

        when(createSpacePort.persist(any())).thenReturn(createdSpaceId);

        service.createSpace(param);

        ArgumentCaptor<SpaceUserAccess> captor = ArgumentCaptor.forClass(SpaceUserAccess.class);
        verify(createSpaceUserAccessPort).persist(captor.capture());
        var capturedAccess = captor.getValue();
        assertEquals(createdSpaceId, capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());

        verifyNoInteractions(countUserSpacesPort, appSpecProperties);
    }

    @Test
    void testCreateSpace_whenSpaceTypeIsPersonalWithLessThanSpaceLimits_thenCreateSpaceCheckingSpaceLimits() {
        var param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);
        long createdSpaceId = 0L;

        when(createSpacePort.persist(any())).thenReturn(createdSpaceId);
        when(countUserSpacesPort.countUserSpaces(param.getCurrentUserId(), SpaceType.PERSONAL)).thenReturn(0);

        service.createSpace(param);

        ArgumentCaptor<SpaceUserAccess> captor = ArgumentCaptor.forClass(SpaceUserAccess.class);
        verify(createSpaceUserAccessPort).persist(captor.capture());
        var capturedAccess = captor.getValue();
        assertEquals(createdSpaceId, capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());
    }

    @Test
    void testCreateSpace_whenSpaceTypeIsPersonalWithMoreThanSpaceLimits_thenCreateSpaceCheckingSpaceLimits() {
        var param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);

        when(countUserSpacesPort.countUserSpaces(param.getCurrentUserId(), SpaceType.PERSONAL)).thenReturn(2);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createSpace(param));
        assertEquals(CREATE_SPACE_PERSONAL_SPACE_MAX, throwable.getMessage());
    }

    private AppSpecProperties appSpecProperties() {
        var properties = new AppSpecProperties();
        properties.setSpace(new AppSpecProperties.Space());
        properties.getSpace().setMaxPersonalSpaces(1);
        return properties;
    }

    private CreateSpaceUseCase.Param createParam(Consumer<CreateSpaceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateSpaceUseCase.Param.ParamBuilder paramBuilder() {
        return CreateSpaceUseCase.Param.builder()
            .title("title")
            .type("PERSONAL")
            .currentUserId(UUID.randomUUID());
    }
}
