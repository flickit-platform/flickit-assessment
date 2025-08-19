package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.common.application.domain.space.SpaceStatus;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpacesPort;
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
    CountSpacesPort countSpacesPort;

    @Captor
    ArgumentCaptor<SpaceUserAccess> userAccessCaptor;

    @Captor
    private ArgumentCaptor<CreateSpacePort.Param> createSpaceCaptor;

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

        verify(createSpacePort).persist(createSpaceCaptor.capture());
        var capturedSpace = createSpaceCaptor.getValue();
        assertEquals(param.getTitle(), capturedSpace.title());
        assertEquals(generateSlugCode(param.getTitle()), capturedSpace.code());
        assertEquals(SpaceStatus.ACTIVE, capturedSpace.status());
        SpaceType expectedSpaceType = SpaceType.valueOf(param.getType());
        assertEquals(expectedSpaceType.getId(), capturedSpace.type().getId());
        assertEquals(expectedSpaceType.getCode(), capturedSpace.type().getCode());
        assertEquals(expectedSpaceType.getTitle(), capturedSpace.type().getTitle());
        assertEquals(param.getCurrentUserId(), capturedSpace.createdBy());
        assertNotNull(capturedSpace.creationTime());
        assertFalse(capturedSpace.isDefault());

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

        verify(createSpacePort).persist(createSpaceCaptor.capture());
        var capturedSpace = createSpaceCaptor.getValue();
        assertEquals(param.getTitle(), capturedSpace.title());
        assertEquals(generateSlugCode(param.getTitle()), capturedSpace.code());
        assertEquals(SpaceStatus.ACTIVE, capturedSpace.status());
        SpaceType expectedSpaceType = SpaceType.valueOf(param.getType());
        assertEquals(expectedSpaceType.getId(), capturedSpace.type().getId());
        assertEquals(expectedSpaceType.getCode(), capturedSpace.type().getCode());
        assertEquals(expectedSpaceType.getTitle(), capturedSpace.type().getTitle());
        assertEquals(param.getCurrentUserId(), capturedSpace.createdBy());
        assertNotNull(capturedSpace.creationTime());
        assertFalse(capturedSpace.isDefault());

        verify(createSpaceUserAccessPort).persist(userAccessCaptor.capture());
        var capturedAccess = userAccessCaptor.getValue();
        assertEquals(result.id(), capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());

        assertInstanceOf(CreateSpaceUseCase.CreatePremium.class, result);

        var premiumResult = (CreateSpaceUseCase.CreatePremium) result;

        assertEquals(appSpecProperties.getEmail().getAdminEmail(), premiumResult.notificationCmd().adminEmail());
        assertNotNull(premiumResult.notificationCmd().creationTime());
        assertEquals(param.getTitle(), premiumResult.notificationCmd().title());

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
