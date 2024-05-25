package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentPrivilegedUsersUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentPrivilegedUsersPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GET_ASSESSMENT_PRIVILEGED_USERS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentPrivilegedUsersServiceTest {

    @InjectMocks
    private GetAssessmentPrivilegedUsersService service;

    @Mock
    private AssessmentPermissionChecker assessmentPermissionChecker;

    @Mock
    private LoadAssessmentPrivilegedUsersPort port;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetAssessmentPrivilegedUsers_WhenDoesntHaveGetAssessmentPrivilegedUsersPermission_ThenThrowException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        GetAssessmentPrivilegedUsersUseCase.Param param = new GetAssessmentPrivilegedUsersUseCase.Param(assessmentId,
            currentUserId,
            10,
            0);

        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, GET_ASSESSMENT_PRIVILEGED_USERS)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.getAssessmentPrivilegedUsers(param));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testGetAssessmentPrivilegedUsers_WhenValidInputAndPicturePathIsBlank_ThenValidResult(String picturePath) {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        GetAssessmentPrivilegedUsersUseCase.Param useCaseParam = new GetAssessmentPrivilegedUsersUseCase.Param(assessmentId,
            currentUserId,
            10,
            0);

        LoadAssessmentPrivilegedUsersPort.Param portParam = new LoadAssessmentPrivilegedUsersPort.Param(assessmentId,
            useCaseParam.getSize(),
            useCaseParam.getPage());

        LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser.Role manager =
            new LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser.Role(1, "MANAGER");
        LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser expectedPrivilegedUser =
            new LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser(currentUserId,
            "admin@flickit.org",
            "Flickit Admin",
            null,
            picturePath,
            "",
            manager);

        PaginatedResponse<LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser> paginatedResponse =
            new PaginatedResponse<>(List.of(expectedPrivilegedUser),
            0,
            10,
            "roleId",
            "asc",
            10);

        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, GET_ASSESSMENT_PRIVILEGED_USERS)).thenReturn(true);
        when(port.loadAssessmentPrivilegedUsers(portParam)).thenReturn(paginatedResponse);

        var response = service.getAssessmentPrivilegedUsers(useCaseParam);

        assertNotNull(response);
        assertFalse(response.getItems().isEmpty());
        assertEquals(response.getItems().size(), 1);
        GetAssessmentPrivilegedUsersUseCase.AssessmentPrivilegedUser actualPrivilegedUser = response.getItems().get(0);
        assertEquals(expectedPrivilegedUser.id(), actualPrivilegedUser.id());
        assertEquals(expectedPrivilegedUser.email(), actualPrivilegedUser.email());
        assertEquals(expectedPrivilegedUser.displayName(), actualPrivilegedUser.displayName());
        assertEquals(expectedPrivilegedUser.bio(), actualPrivilegedUser.bio());
        assertNull(actualPrivilegedUser.pictureLink());
        assertEquals(expectedPrivilegedUser.linkedin(), actualPrivilegedUser.linkedin());
        assertEquals(expectedPrivilegedUser.role().id(), actualPrivilegedUser.role().id());
        assertEquals(expectedPrivilegedUser.role().title(), actualPrivilegedUser.role().title());
    }

    @Test
    void testGetAssessmentPrivilegedUsers_WhenValidInputAndPicturePathIsNotBlank_ThenValidResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        GetAssessmentPrivilegedUsersUseCase.Param useCaseParam = new GetAssessmentPrivilegedUsersUseCase.Param(assessmentId,
            currentUserId,
            10,
            0);

        LoadAssessmentPrivilegedUsersPort.Param portParam = new LoadAssessmentPrivilegedUsersPort.Param(assessmentId,
            useCaseParam.getSize(),
            useCaseParam.getPage());

        LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser.Role manager =
            new LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser.Role(1, "MANAGER");
        LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser expectedPrivilegedUser =
            new LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser(currentUserId,
                "admin@flickit.org",
                "Flickit Admin",
                null,
                "path/to/picture",
                "",
                manager);

        PaginatedResponse<LoadAssessmentPrivilegedUsersPort.AssessmentPrivilegedUser> paginatedResponse =
            new PaginatedResponse<>(List.of(expectedPrivilegedUser),
                0,
                10,
                "roleId",
                "asc",
                10);

        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, GET_ASSESSMENT_PRIVILEGED_USERS)).thenReturn(true);
        when(port.loadAssessmentPrivilegedUsers(portParam)).thenReturn(paginatedResponse);
        when(createFileDownloadLinkPort.createDownloadLink(expectedPrivilegedUser.picturePath(), Duration.ofDays(1))).thenReturn("cdn.flickit.org/profile.jpg");

        var response = service.getAssessmentPrivilegedUsers(useCaseParam);

        assertNotNull(response);
        assertFalse(response.getItems().isEmpty());
        assertEquals(response.getItems().size(), 1);
        GetAssessmentPrivilegedUsersUseCase.AssessmentPrivilegedUser actualPrivilegedUser = response.getItems().get(0);
        assertEquals(expectedPrivilegedUser.id(), actualPrivilegedUser.id());
        assertEquals(expectedPrivilegedUser.email(), actualPrivilegedUser.email());
        assertEquals(expectedPrivilegedUser.displayName(), actualPrivilegedUser.displayName());
        assertEquals(expectedPrivilegedUser.bio(), actualPrivilegedUser.bio());
        assertNotNull(actualPrivilegedUser.pictureLink());
        assertEquals("cdn.flickit.org/profile.jpg", actualPrivilegedUser.pictureLink());
        assertEquals(expectedPrivilegedUser.linkedin(), actualPrivilegedUser.linkedin());
        assertEquals(expectedPrivilegedUser.role().id(), actualPrivilegedUser.role().id());
        assertEquals(expectedPrivilegedUser.role().title(), actualPrivilegedUser.role().title());
    }

}
