package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUsersUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
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

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_USER_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentUsersServiceTest {

    @InjectMocks
    private GetAssessmentUsersService service;

    @Mock
    private AssessmentPermissionChecker assessmentPermissionChecker;

    @Mock
    private LoadAssessmentUsersPort port;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetAssessmentUsers_WhenDoesNotHaveRequiredPermission_ThenThrowException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAssessmentUsersUseCase.Param(assessmentId, currentUserId, 10, 0);

        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_USER_LIST)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentUsers(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testGetAssessmentUsers_WhenValidInputAndPicturePathIsBlank_ThenValidResult(String picturePath) {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var useCaseParam = new GetAssessmentUsersUseCase.Param(assessmentId, currentUserId, 10, 0);

        LoadAssessmentUsersPort.Param portParam = new LoadAssessmentUsersPort.Param(assessmentId,
            useCaseParam.getSize(),
            useCaseParam.getPage());

        var manager = new LoadAssessmentUsersPort.AssessmentUser.Role(1, "MANAGER");
        var expectedAssessmentUser = new LoadAssessmentUsersPort.AssessmentUser(currentUserId,
            "admin@flickit.org",
            "Flickit Admin",
            picturePath,
            manager,
            true);

        PaginatedResponse<LoadAssessmentUsersPort.AssessmentUser> paginatedResponse =
            new PaginatedResponse<>(List.of(expectedAssessmentUser),
                0,
                10,
                "roleId",
                "asc",
                10);

        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_USER_LIST)).thenReturn(true);
        when(port.loadAssessmentUsers(portParam)).thenReturn(paginatedResponse);

        var response = service.getAssessmentUsers(useCaseParam);

        assertNotNull(response);
        assertFalse(response.getItems().isEmpty());
        assertEquals(1, response.getItems().size());
        GetAssessmentUsersUseCase.AssessmentUser actualAssessmentUser = response.getItems().get(0);
        assertEquals(expectedAssessmentUser.id(), actualAssessmentUser.id());
        assertEquals(expectedAssessmentUser.email(), actualAssessmentUser.email());
        assertEquals(expectedAssessmentUser.displayName(), actualAssessmentUser.displayName());
        assertNull(actualAssessmentUser.pictureLink());
        assertEquals(expectedAssessmentUser.role().id(), actualAssessmentUser.role().id());
        assertEquals(expectedAssessmentUser.role().title(), actualAssessmentUser.role().title());
        assertEquals(expectedAssessmentUser.editable(), actualAssessmentUser.editable());
    }

    @Test
    void testGetAssessmentUsers_WhenValidInputAndPicturePathIsNotBlank_ThenValidResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var useCaseParam = new GetAssessmentUsersUseCase.Param(assessmentId, currentUserId, 10, 0);

        LoadAssessmentUsersPort.Param portParam = new LoadAssessmentUsersPort.Param(assessmentId,
            useCaseParam.getSize(),
            useCaseParam.getPage());

        LoadAssessmentUsersPort.AssessmentUser.Role manager =
            new LoadAssessmentUsersPort.AssessmentUser.Role(1, "MANAGER");
        LoadAssessmentUsersPort.AssessmentUser expectedAssessmentUser =
            new LoadAssessmentUsersPort.AssessmentUser(currentUserId,
                "admin@flickit.org",
                "Flickit Admin",
                "path/to/picture",
                manager,
                true);

        PaginatedResponse<LoadAssessmentUsersPort.AssessmentUser> paginatedResponse =
            new PaginatedResponse<>(List.of(expectedAssessmentUser),
                0,
                10,
                "roleId",
                "asc",
                10);

        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_USER_LIST)).thenReturn(true);
        when(port.loadAssessmentUsers(portParam)).thenReturn(paginatedResponse);
        when(createFileDownloadLinkPort.createDownloadLink(expectedAssessmentUser.picturePath(), Duration.ofDays(1))).thenReturn("cdn.flickit.org/profile.jpg");

        var response = service.getAssessmentUsers(useCaseParam);

        assertNotNull(response);
        assertFalse(response.getItems().isEmpty());
        assertEquals(1, response.getItems().size());
        GetAssessmentUsersUseCase.AssessmentUser actualAssessmentUser = response.getItems().get(0);
        assertEquals(expectedAssessmentUser.id(), actualAssessmentUser.id());
        assertEquals(expectedAssessmentUser.email(), actualAssessmentUser.email());
        assertEquals(expectedAssessmentUser.displayName(), actualAssessmentUser.displayName());
        assertNotNull(actualAssessmentUser.pictureLink());
        assertEquals("cdn.flickit.org/profile.jpg", actualAssessmentUser.pictureLink());
        assertEquals(expectedAssessmentUser.role().id(), actualAssessmentUser.role().id());
        assertEquals(expectedAssessmentUser.role().title(), actualAssessmentUser.role().title());
        assertEquals(expectedAssessmentUser.editable(), actualAssessmentUser.editable());
    }
}
