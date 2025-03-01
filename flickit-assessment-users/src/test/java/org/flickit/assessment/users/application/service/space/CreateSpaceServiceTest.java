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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_SPACE_BASIC_SPACE_MAX;
import static org.junit.jupiter.api.Assertions.*;
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

    @Captor
    ArgumentCaptor<Space> spaceCaptor;

    @Captor
    ArgumentCaptor<SpaceUserAccess> userAccessCaptor;

    private final CreateSpaceUseCase.Param param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);
    private final long createdSpaceId = 0L;
    private final int maxBasicSpaces = 2;

    @Test
    void testCreateSpace_whenReachedBasicSpaceLimit_thenShouldThrowUpgradeRequiredException() {
        when(countSpacePort.countBasicSpaces(param.getCurrentUserId())).thenReturn(maxBasicSpaces);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createSpace(param));
        assertEquals(CREATE_SPACE_BASIC_SPACE_MAX, throwable.getMessage());

        verify(appSpecProperties, times(1)).getSpace();
        verifyNoInteractions(createSpacePort);
    }

    @Test
    void testCreateSpace_whenValidParamsWithBasicSpace_thenSuccessfullyCreateSpace() {
        when(countSpacePort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces - 1);

        service.createSpace(param);

        verify(createSpacePort).persist(spaceCaptor.capture());
        var capturedSpace = spaceCaptor.getValue();
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

        verify(createSpaceUserAccessPort).persist(userAccessCaptor.capture());
        var capturedAccess = userAccessCaptor.getValue();
        assertEquals(createdSpaceId, capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testCreateSpace_whenValidParamsWithPremiumSpace_thenSuccessfullyCreateSpace() {
        var premiumParam = createParam(b -> b.type(SpaceType.PREMIUM.getCode()));

        service.createSpace(premiumParam);

        verify(createSpacePort).persist(spaceCaptor.capture());
        var capturedSpace = spaceCaptor.getValue();
        assertEquals(premiumParam.getTitle(), capturedSpace.getTitle());
        assertEquals(generateSlugCode(premiumParam.getTitle()), capturedSpace.getCode());
        SpaceType expectedSpaceType = SpaceType.valueOf(premiumParam.getType());
        assertEquals(expectedSpaceType.getId(), capturedSpace.getType().getId());
        assertEquals(expectedSpaceType.getCode(), capturedSpace.getType().getCode());
        assertEquals(expectedSpaceType.getTitle(), capturedSpace.getType().getTitle());
        assertEquals(premiumParam.getCurrentUserId(), capturedSpace.getCreatedBy());
        assertEquals(premiumParam.getCurrentUserId(), capturedSpace.getLastModifiedBy());
        assertNotNull(capturedSpace.getCreationTime());
        assertNotNull(capturedSpace.getLastModificationTime());

        verify(createSpaceUserAccessPort).persist(userAccessCaptor.capture());
        var capturedAccess = userAccessCaptor.getValue();
        assertEquals(createdSpaceId, capturedAccess.getSpaceId());
        assertEquals(premiumParam.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(premiumParam.getCurrentUserId(), capturedAccess.getUserId());

        verify(appSpecProperties, times(1)).getSpace();
        verifyNoInteractions(countSpacePort);
    }

    AppSpecProperties appSpecProperties() {
        AppSpecProperties properties = new AppSpecProperties();
        properties.setSpace(new AppSpecProperties.Space());
        properties.getSpace().setMaxBasicSpaces(maxBasicSpaces);
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
