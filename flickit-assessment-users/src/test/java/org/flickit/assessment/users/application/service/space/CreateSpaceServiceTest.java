package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpacesPort;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_SPACE_BASIC_SPACE_MAX;
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
    CountSpacesPort countSpacesPort;

    @Captor
    ArgumentCaptor<Space> spaceCaptor;

    @Captor
    ArgumentCaptor<SpaceUserAccess> userAccessCaptor;

    @Spy
    AppSpecProperties appSpecProperties = appSpecProperties();

    private final int maxBasicSpaces = 1;

    @Test
    void testCreateSpace_whenReachedBasicSpaceLimit_thenShouldThrowUpgradeRequiredException() {
        var param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);

        when(countSpacesPort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createSpace(param));
        assertEquals(CREATE_SPACE_BASIC_SPACE_MAX, throwable.getMessage());

        verify(appSpecProperties, times(1)).getSpace();
        verifyNoInteractions(createSpacePort);
    }

    @Test
    void testCreateSpace_whenValidParamsAndSpaceIsBasic_thenSuccessfulSpaceCreationWithoutNotification() {
        var param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);

        when(countSpacesPort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces - 1);

        var result = service.createSpace(param);

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
        assertEquals(result.id(), capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());

        verify(appSpecProperties, times(1)).getSpace();

        assertInstanceOf(CreateSpaceUseCase.CreateBasic.class, result);
    }

    @Test
    void testCreateSpace_whenValidParamsAndSpaceIsPremium_thenSuccessfulSpaceCreationWithNotification() {
        var param = createParam(b -> b.type(SpaceType.PREMIUM.getCode()));

        var result = service.createSpace(param);

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
        assertEquals(result.id(), capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());

        assertInstanceOf(CreateSpaceUseCase.CreatePremium.class, result);

        var premiumResult = (CreateSpaceUseCase.CreatePremium) result;

        assertEquals(appSpecProperties.getEmail().getAdminEmail(), premiumResult.notificationCmd().adminEmail());
        assertEquals(SpaceType.PREMIUM.getCode(), premiumResult.notificationCmd().space().getType().getCode());
        assertNotNull(premiumResult.notificationCmd().space().getCreationTime());
        assertEquals(param.getTitle(), premiumResult.notificationCmd().space().getTitle());

        assertInstanceOf(CreateSpaceUseCase.CreatePremium.class, result);
        verifyNoInteractions(countSpacesPort);
    }

    AppSpecProperties appSpecProperties() {
        AppSpecProperties properties = new AppSpecProperties();
        properties.setEmail(new AppSpecProperties.Email());
        properties.getEmail().setAdminEmail("admin@email.com");
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
