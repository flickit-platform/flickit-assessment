package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.GetUserProfileUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.users.common.ErrorMessageKey.USER_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfileServiceTest {

    @InjectMocks
    private GetUserProfileService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetUserProfile_ValidInputs_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        GetUserProfileUseCase.Param param = new GetUserProfileUseCase.Param(currentUserId);

        User expectedUser = new User(currentUserId,
            "admin@flickit.com",
            "admin",
            "admin bio",
            "linkedin.com/flickit-admin",
            "path/to/picture");
        when(loadUserPort.loadUser(currentUserId)).thenReturn(expectedUser);
        String pictureLink = "cdn.flickit.org" + expectedUser.getPicture();
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any(Duration.class))).thenReturn(pictureLink);

        User actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getDisplayName(), actualUser.getDisplayName());
        assertEquals(expectedUser.getBio(), actualUser.getBio());
        assertEquals(expectedUser.getLinkedin(), actualUser.getLinkedin());
        assertEquals(expectedUser.getPicture(), actualUser.getPicture());
    }

    @Test
    void testGetUserProfile_NullPicture_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        GetUserProfileUseCase.Param param = new GetUserProfileUseCase.Param(currentUserId);

        User expectedUser = new User(currentUserId,
            "admin@flickit.com",
            "admin",
            "admin bio",
            "linkedin.com/flickit-admin",
            null);
        when(loadUserPort.loadUser(currentUserId)).thenReturn(expectedUser);

        User actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getDisplayName(), actualUser.getDisplayName());
        assertEquals(expectedUser.getBio(), actualUser.getBio());
        assertEquals(expectedUser.getLinkedin(), actualUser.getLinkedin());
        assertEquals(expectedUser.getPicture(), actualUser.getPicture());
    }

    @Test
    void testGetUserProfile_BlankPicture_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        GetUserProfileUseCase.Param param = new GetUserProfileUseCase.Param(currentUserId);

        User expectedUser = new User(currentUserId,
            "admin@flickit.com",
            "admin",
            "admin bio",
            "linkedin.com/flickit-admin",
            "");
        when(loadUserPort.loadUser(currentUserId)).thenReturn(expectedUser);

        User actualUser = service.getUserProfile(param);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getDisplayName(), actualUser.getDisplayName());
        assertEquals(expectedUser.getBio(), actualUser.getBio());
        assertEquals(expectedUser.getLinkedin(), actualUser.getLinkedin());
        assertEquals(expectedUser.getPicture(), actualUser.getPicture());
    }

    @Test
    void testGetUserProfile_ValidInput_UserNotFound() {
        UUID currentUserId = UUID.randomUUID();
        GetUserProfileUseCase.Param param = new GetUserProfileUseCase.Param(currentUserId);

        when(loadUserPort.loadUser(currentUserId))
            .thenThrow(new ResourceNotFoundException(USER_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getUserProfile(param));
        assertThat(throwable).hasMessage(USER_ID_NOT_FOUND);
    }
}
