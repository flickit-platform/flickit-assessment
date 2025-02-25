package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpacePort;
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

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_SPACE_BASIC_SPACE_MAX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSpaceServiceTest {

    @InjectMocks
    CreateSpaceService service;

    @Mock
    CreateSpacePort createSpacePort;

    @Mock
    CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Mock
    CountSpacePort countSpacePort;

    @Spy
    AppSpecProperties appSpecProperties = appSpecProperties();

    @Test
    void testCreateSpace_basicSpacesIn_successful() { //TODO: Consider
        var param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);

        when(countSpacePort.countBasicSpaces(param.getCurrentUserId())).thenReturn(1);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createSpace(param));
        assertEquals(CREATE_SPACE_BASIC_SPACE_MAX, throwable.getMessage());

        verifyNoInteractions(createSpacePort);
    }

    @Test
    void testCreateSpace_validParams_successful() {
        var param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);
        long createdSpaceId = 0L;

        when(countSpacePort.countBasicSpaces(param.getCurrentUserId())).thenReturn(0);
        when(createSpacePort.persist(any())).thenReturn(createdSpaceId);

        service.createSpace(param);

        ArgumentCaptor<Space> createSpaceCaptor = ArgumentCaptor.forClass(Space.class);
        verify(createSpacePort).persist(createSpaceCaptor.capture());
        var capturedSpace = createSpaceCaptor.getValue();
        assertEquals(param.getTitle(), capturedSpace.getTitle());
        assertEquals(generateSlugCode(param.getTitle()), capturedSpace.getCode());
        SpaceType expectedSpaceType = SpaceType.valueOf(param.getType());
        assertEquals(expectedSpaceType.getId(), capturedSpace.getType().getId());
        assertEquals(expectedSpaceType.getCode(), capturedSpace.getType().getCode());
        assertEquals(expectedSpaceType.getTitle(), capturedSpace.getType().getTitle());
        assertEquals(param.getCurrentUserId(), capturedSpace.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedSpace.getLastModifiedBy());
        assertNotNull(capturedSpace.getCreationTime());
        assertNotNull(capturedSpace.getLastModificationTime());

        ArgumentCaptor<SpaceUserAccess> userAccessCaptor = ArgumentCaptor.forClass(SpaceUserAccess.class);
        verify(createSpaceUserAccessPort).persist(userAccessCaptor.capture());
        var capturedAccess = userAccessCaptor.getValue();
        assertEquals(createdSpaceId, capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());
    }

    AppSpecProperties appSpecProperties() {
        AppSpecProperties properties = new AppSpecProperties();
        AppSpecProperties.Space space = new AppSpecProperties.Space();
        space.setMaxBasicSpaces(1);
        properties.setSpace(space);
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
            .type(SpaceType.BASIC.getCode())
            .currentUserId(UUID.randomUUID());
    }
}
