package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.GetUsersMeUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.users.common.ErrorMessageKey.USER_ID_NOT_FOUND;
import static org.flickit.assessment.users.test.fixture.application.UserMother.createUser;
import static org.flickit.assessment.users.test.fixture.application.UserSurveyMother.createWithCompletedAndDontShowAgain;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUsersMeServiceTest {

    @InjectMocks
    private GetUsersMeService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Mock
    private LoadUserSurveyPort loadUserSurveyPort;

    @Mock
    private LoadSpacePort loadSpacePort;

    private final GetUsersMeUseCase.Param param = createParam(GetUsersMeUseCase.Param.ParamBuilder::build);
    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);
    private final long defaultSpaceId = 101L;

    @Test
    void testGetUsersMe_whenUserDoesNotExist_thenThrowResourceNotFoundException() {
        when(loadUserPort.loadUser(param.getCurrentUserId())).thenThrow(new ResourceNotFoundException(USER_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getUserProfile(param));
        assertThat(throwable).hasMessage(USER_ID_NOT_FOUND);

        verifyNoInteractions(createFileDownloadLinkPort,
            loadUserSurveyPort,
            loadSpacePort);
    }

    @Test
    void testGetUsersMe_whenParametersAreValid_thenReturnValidResult() {
        User expectedUser = createUser(param.getCurrentUserId(), "path/to/picture");
        String pictureLink = "cdn.flickit.org" + expectedUser.getPicturePath();

        when(loadUserPort.loadUser(param.getCurrentUserId())).thenReturn(expectedUser);
        when(createFileDownloadLinkPort.createDownloadLink(expectedUser.getPicturePath(), EXPIRY_DURATION)).thenReturn(pictureLink);
        when(loadSpacePort.loadDefaultSpaceId(param.getCurrentUserId())).thenReturn(defaultSpaceId);
        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.empty());

        GetUsersMeUseCase.Result actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.id());
        assertEquals(expectedUser.getEmail(), actualUser.email());
        assertEquals(expectedUser.getDisplayName(), actualUser.displayName());
        assertEquals(pictureLink, actualUser.pictureLink());
        assertEquals(defaultSpaceId, actualUser.defaultSpaceId());
        assertTrue(actualUser.showSurvey());
    }

    @Test
    void testGetUsersMe_whenParametersAreValidAndPictureIsNull_thenReturnValidResultWithoutPictureLink() {
        User expectedUser = createUser(param.getCurrentUserId(), null);
        var userSurvey = createWithCompletedAndDontShowAgain(true, false);

        when(loadUserPort.loadUser(param.getCurrentUserId())).thenReturn(expectedUser);
        when(loadSpacePort.loadDefaultSpaceId(param.getCurrentUserId())).thenReturn(defaultSpaceId);
        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.of(userSurvey));

        GetUsersMeUseCase.Result actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.id());
        assertEquals(expectedUser.getEmail(), actualUser.email());
        assertEquals(expectedUser.getDisplayName(), actualUser.displayName());
        assertNull(actualUser.pictureLink());
        assertEquals(defaultSpaceId, actualUser.defaultSpaceId());
        assertFalse(actualUser.showSurvey());

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetUsersMe_whenParametersAreValidAndPictureIsBlank_thenReturnValidResultWithoutPictureLink() {
        User expectedUser = createUser(param.getCurrentUserId(), "");
        var userSurvey = createWithCompletedAndDontShowAgain(false, true);

        when(loadUserPort.loadUser(param.getCurrentUserId())).thenReturn(expectedUser);
        when(loadSpacePort.loadDefaultSpaceId(param.getCurrentUserId())).thenReturn(defaultSpaceId);
        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.of(userSurvey));

        GetUsersMeUseCase.Result actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.id());
        assertEquals(expectedUser.getEmail(), actualUser.email());
        assertEquals(expectedUser.getDisplayName(), actualUser.displayName());
        assertNull(actualUser.pictureLink());
        assertEquals(defaultSpaceId, actualUser.defaultSpaceId());
        assertFalse(actualUser.showSurvey());

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetUsersMe_whenParametersAreValidAndSurveyIsNotCompletedAndDontShowAgainIsFalse_thenReturnValidResult() {
        User expectedUser = createUser(param.getCurrentUserId(), "");
        var userSurvey = createWithCompletedAndDontShowAgain(false, false);

        when(loadUserPort.loadUser(param.getCurrentUserId())).thenReturn(expectedUser);
        when(loadSpacePort.loadDefaultSpaceId(param.getCurrentUserId())).thenReturn(defaultSpaceId);
        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.of(userSurvey));

        GetUsersMeUseCase.Result actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.id());
        assertEquals(expectedUser.getEmail(), actualUser.email());
        assertEquals(expectedUser.getDisplayName(), actualUser.displayName());
        assertNull(actualUser.pictureLink());
        assertEquals(defaultSpaceId, actualUser.defaultSpaceId());
        assertTrue(actualUser.showSurvey());

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    private GetUsersMeUseCase.Param createParam(Consumer<GetUsersMeUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetUsersMeUseCase.Param.ParamBuilder paramBuilder() {
        return GetUsersMeUseCase.Param.builder()
            .currentUserId(UUID.randomUUID());
    }
}
