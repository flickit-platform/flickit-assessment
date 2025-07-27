package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.GetUserProfileUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
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
class GetUserProfileServiceTest {

    @InjectMocks
    private GetUserProfileService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Mock
    private LoadUserSurveyPort loadUserSurveyPort;

    private final UUID currentUserId = UUID.randomUUID();
    private GetUserProfileUseCase.Param param = createParam(b -> b.currentUserId(currentUserId));
    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    @Test
    void testGetUserProfile_whenUserDoesNotExist_thenThrowResourceNotFoundException() {
        param = createParam(b -> b.currentUserId(UUID.randomUUID()));

        when(loadUserPort.loadUser(param.getCurrentUserId())).thenThrow(new ResourceNotFoundException(USER_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getUserProfile(param));
        assertThat(throwable).hasMessage(USER_ID_NOT_FOUND);

        verifyNoInteractions(createFileDownloadLinkPort,
            loadUserSurveyPort);
    }

    @Test
    void testGetUserProfile_whenParametersAreValid_thenReturnValidResult() {
        User expectedUser = createUser(currentUserId, "path/to/picture");
        String pictureLink = "cdn.flickit.org" + expectedUser.getPicturePath();

        when(loadUserPort.loadUser(currentUserId)).thenReturn(expectedUser);
        when(createFileDownloadLinkPort.createDownloadLink(expectedUser.getPicturePath(), EXPIRY_DURATION)).thenReturn(pictureLink);
        when(loadUserSurveyPort.loadByUserId(currentUserId)).thenReturn(Optional.empty());

        GetUserProfileUseCase.UserProfile actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.id());
        assertEquals(expectedUser.getEmail(), actualUser.email());
        assertEquals(expectedUser.getDisplayName(), actualUser.displayName());
        assertEquals(expectedUser.getBio(), actualUser.bio());
        assertEquals(expectedUser.getLinkedin(), actualUser.linkedin());
        assertEquals(pictureLink, actualUser.pictureLink());
        assertTrue(actualUser.showSurvey());
    }

    @Test
    void testGetUserProfile_whenParametersAreValidAndPictureIsNull_thenReturnValidResultWithoutPictureLink() {
        User expectedUser = createUser(currentUserId, null);
        var userSurvey = createWithCompletedAndDontShowAgain(true, false);

        when(loadUserPort.loadUser(currentUserId)).thenReturn(expectedUser);
        when(loadUserSurveyPort.loadByUserId(currentUserId)).thenReturn(Optional.of(userSurvey));

        GetUserProfileUseCase.UserProfile actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.id());
        assertEquals(expectedUser.getEmail(), actualUser.email());
        assertEquals(expectedUser.getDisplayName(), actualUser.displayName());
        assertEquals(expectedUser.getBio(), actualUser.bio());
        assertEquals(expectedUser.getLinkedin(), actualUser.linkedin());
        assertNull(actualUser.pictureLink());
        assertFalse(actualUser.showSurvey());

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetUserProfile_whenParametersAreValidAndPictureIsBlank_thenReturnValidResultWithoutPictureLink() {
        User expectedUser = createUser(currentUserId, "");
        var userSurvey = createWithCompletedAndDontShowAgain(false, true);

        when(loadUserPort.loadUser(currentUserId)).thenReturn(expectedUser);
        when(loadUserSurveyPort.loadByUserId(currentUserId)).thenReturn(Optional.of(userSurvey));

        GetUserProfileUseCase.UserProfile actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.id());
        assertEquals(expectedUser.getEmail(), actualUser.email());
        assertEquals(expectedUser.getDisplayName(), actualUser.displayName());
        assertEquals(expectedUser.getBio(), actualUser.bio());
        assertEquals(expectedUser.getLinkedin(), actualUser.linkedin());
        assertNull(actualUser.pictureLink());
        assertFalse(actualUser.showSurvey());

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetUserProfile_whenParametersAreValidAndSurveyIsNotCompletedAndDontShowAgainIsFalse_thenReturnValidResult() {
        User expectedUser = createUser(currentUserId, "");
        var userSurvey = createWithCompletedAndDontShowAgain(false, false);

        when(loadUserPort.loadUser(currentUserId)).thenReturn(expectedUser);
        when(loadUserSurveyPort.loadByUserId(currentUserId)).thenReturn(Optional.of(userSurvey));

        GetUserProfileUseCase.UserProfile actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.id());
        assertEquals(expectedUser.getEmail(), actualUser.email());
        assertEquals(expectedUser.getDisplayName(), actualUser.displayName());
        assertEquals(expectedUser.getBio(), actualUser.bio());
        assertEquals(expectedUser.getLinkedin(), actualUser.linkedin());
        assertNull(actualUser.pictureLink());
        assertTrue(actualUser.showSurvey());

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    private GetUserProfileUseCase.Param createParam(Consumer<GetUserProfileUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetUserProfileUseCase.Param.ParamBuilder paramBuilder() {
        return GetUserProfileUseCase.Param.builder()
            .currentUserId(UUID.randomUUID());
    }
}
