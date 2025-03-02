package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CreateSpaceServiceTest {

    @InjectMocks
    CreateSpaceService service;

    @Mock
    CreateSpacePort createSpacePort;

    @Mock
    CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Spy
    AppSpecProperties appSpecProperties = appSpecProperties();


    @Test
    void testCreateSpace_whenValidParamsAndSpaceIsBasic_thenSuccessfulSpaceCreationWithoutNotification() {
        var param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);

        var result = service.createSpace(param);

        ArgumentCaptor<Space> createSpaceCaptor = ArgumentCaptor.forClass(Space.class);
        verify(createSpacePort).persist(createSpaceCaptor.capture());
        var capturedSpace = createSpaceCaptor.getValue();
        assertEquals(param.getTitle(), capturedSpace.getTitle());
        assertEquals(generateSlugCode(param.getTitle()), capturedSpace.getCode());
        assertEquals(param.getCurrentUserId(), capturedSpace.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedSpace.getLastModifiedBy());
        assertNotNull(capturedSpace.getCreationTime());
        assertNotNull(capturedSpace.getLastModificationTime());

        ArgumentCaptor<SpaceUserAccess> userAccessCaptor = ArgumentCaptor.forClass(SpaceUserAccess.class);
        verify(createSpaceUserAccessPort).persist(userAccessCaptor.capture());
        var capturedAccess = userAccessCaptor.getValue();
        assertEquals(result.id(), capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());

        assertNull(result.notificationCmd());

        verifyNoInteractions(appSpecProperties);
    }

    @Test
    void testCreateSpace_whenValidParamsAndSpaceIsPremium_thenSuccessfulSpaceCreationWithNotification() {
        var param = createParam(b -> b.type(SpaceType.PREMIUM.getCode()));

        var result = service.createSpace(param);

        ArgumentCaptor<Space> createSpaceCaptor = ArgumentCaptor.forClass(Space.class);
        verify(createSpacePort).persist(createSpaceCaptor.capture());
        var capturedSpace = createSpaceCaptor.getValue();
        assertEquals(param.getTitle(), capturedSpace.getTitle());
        assertEquals(generateSlugCode(param.getTitle()), capturedSpace.getCode());
        assertEquals(param.getCurrentUserId(), capturedSpace.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedSpace.getLastModifiedBy());
        assertNotNull(capturedSpace.getCreationTime());
        assertNotNull(capturedSpace.getLastModificationTime());

        ArgumentCaptor<SpaceUserAccess> userAccessCaptor = ArgumentCaptor.forClass(SpaceUserAccess.class);
        verify(createSpaceUserAccessPort).persist(userAccessCaptor.capture());
        var capturedAccess = userAccessCaptor.getValue();
        assertEquals(result.id(), capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());

        assertEquals(result.id(), result.notificationCmd().spaceId());
        assertEquals(appSpecProperties.getEmail().getAdminEmail(), result.notificationCmd().adminEmail());
    }

    AppSpecProperties appSpecProperties() {
        AppSpecProperties appSpecProperties = new AppSpecProperties();
        appSpecProperties.setEmail(new AppSpecProperties.Email());
        appSpecProperties.getEmail().setAdminEmail("admin@email.com");
        return appSpecProperties;
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
