package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetGraphicalReportUsersUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInviteeListPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_ACCESS_TO_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInviteMother.assessmentInviteWithRole;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetGraphicalReportUsersServiceTest {

    @InjectMocks
    private GetGraphicalReportUsersService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentUsersPort loadAssessmentUsersPort;

    @Mock
    private LoadAssessmentInviteeListPort loadAssessmentInviteeListPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Captor
    private ArgumentCaptor<List<Integer>> roleIdsCaptor;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);
    private final GetGraphicalReportUsersUseCase.Param param = createParam(GetGraphicalReportUsersUseCase.Param.ParamBuilder::build);

    @Test
    void testGetGraphicalReportUsers_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getGraphicalReportUsers(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verifyNoInteractions(loadAssessmentUsersPort,
            loadAssessmentInviteeListPort,
            createFileDownloadLinkPort);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testGetGraphicalReportUsers_whenThereIsAUserWithRequiredPermissionAndNoPictureButNotAnyInviteeUsers_thenReturnResultWithUsersAndEmptyInvitees(String picturePath) {
        var reportUser = createReportUser(picturePath, param.getCurrentUserId(), AssessmentUserRole.REPORT_VIEWER);
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(reportUser));
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(new ArrayList<>());

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.users());
        assertEquals(reportUser.id(), result.users().getFirst().id());
        assertEquals(reportUser.displayName(), result.users().getFirst().displayName());
        assertEquals(reportUser.email(), result.users().getFirst().email());
        assertTrue(result.users().getFirst().deletable());
        assertNull(result.users().getFirst().pictureLink());
        assertTrue(result.invitees().isEmpty());

        verify(loadAssessmentUsersPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(loadAssessmentInviteeListPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(assessmentAccessChecker, times(2)).isAuthorized(any(), any(), any());
        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetGraphicalReportUser_whenThereIsAUserWithRequiredPermissionAndPictureButNotAnyInviteeUsers_thenReturnResultWithUsersAndEmptyInvitees() {
        var reportUser = createReportUser("picture-path", UUID.randomUUID(), AssessmentUserRole.REPORT_VIEWER);
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(reportUser));
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(new ArrayList<>());
        when(createFileDownloadLinkPort.createDownloadLink(reportUser.picturePath(), EXPIRY_DURATION)).thenReturn("picture-link");

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.users());
        assertEquals(reportUser.id(), result.users().getFirst().id());
        assertEquals(reportUser.displayName(), result.users().getFirst().displayName());
        assertEquals(reportUser.email(), result.users().getFirst().email());
        assertNotNull(result.users().getFirst().pictureLink());
        assertTrue(result.users().getFirst().deletable());
        assertEquals("picture-link", result.users().getFirst().pictureLink());
        assertTrue(result.invitees().isEmpty());

        verify(loadAssessmentUsersPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(loadAssessmentInviteeListPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(assessmentAccessChecker, times(2)).isAuthorized(any(), any(), any());
    }

    @Test
    void testGetGraphicalReportUser_whenThereIsInviteeUsersButNotAnyUser_thenReturnResultWithEmptyUsersAndInvitees() {
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();
        var invite = assessmentInviteWithRole(AssessmentUserRole.REPORT_VIEWER, param.getCurrentUserId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(new ArrayList<>());
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(invite));

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.invitees());
        assertTrue(result.users().isEmpty());
        assertEquals(invite.getId(), result.invitees().getFirst().id());
        assertEquals(invite.getEmail(), result.invitees().getFirst().email());
        assertTrue(result.invitees().getFirst().deletable());

        verify(loadAssessmentUsersPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(loadAssessmentInviteeListPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(assessmentAccessChecker, times(2)).isAuthorized(any(), any(), any());
    }

    @Test
    void testGetGraphicalReportUser_whenThereIsAUserWithRequiredPermissionAndPictureAndInviteeUsers_thenReturnResultWithUsersAndInvitees() {
        var reportUser = createReportUser("picture-path", UUID.randomUUID(), AssessmentUserRole.REPORT_VIEWER);
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();
        var invite = assessmentInviteWithRole(AssessmentUserRole.ASSESSOR, param.getCurrentUserId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(reportUser));
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(invite));
        when(createFileDownloadLinkPort.createDownloadLink(reportUser.picturePath(), EXPIRY_DURATION)).thenReturn("picture-link");

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.users());
        assertEquals(reportUser.id(), result.users().getFirst().id());
        assertEquals(reportUser.displayName(), result.users().getFirst().displayName());
        assertEquals(reportUser.email(), result.users().getFirst().email());
        assertTrue(result.users().getFirst().deletable());
        assertNotNull(result.users().getFirst().pictureLink());
        assertEquals("picture-link", result.users().getFirst().pictureLink());
        assertNotNull(result.invitees());
        assertFalse(result.invitees().isEmpty());
        assertEquals(invite.getId(), result.invitees().getFirst().id());
        assertEquals(invite.getEmail(), result.invitees().getFirst().email());
        assertFalse(result.invitees().getFirst().deletable());

        verify(loadAssessmentUsersPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(loadAssessmentInviteeListPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(assessmentAccessChecker, times(2)).isAuthorized(any(), any(), any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testGetGraphicalReportUser_whenThereIsAUserWithRequiredPermissionAndNoPictureAndInviteeUsers_thenReturnResultWithUsersAndInvitees(String picturePath) {
        var reportUser = createReportUser(picturePath, param.getCurrentUserId(), AssessmentUserRole.MANAGER);
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();
        var invite = assessmentInviteWithRole(AssessmentUserRole.MANAGER, UUID.randomUUID());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(reportUser));
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(invite));

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.users());
        assertEquals(reportUser.id(), result.users().getFirst().id());
        assertEquals(reportUser.displayName(), result.users().getFirst().displayName());
        assertEquals(reportUser.email(), result.users().getFirst().email());
        assertNull(result.users().getFirst().pictureLink());
        assertFalse(result.users().getFirst().deletable());
        assertNotNull(result.invitees());
        assertFalse(result.invitees().isEmpty());
        assertEquals(invite.getId(), result.invitees().getFirst().id());
        assertEquals(invite.getEmail(), result.invitees().getFirst().email());
        assertFalse(result.invitees().getFirst().deletable());

        verify(loadAssessmentUsersPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verify(loadAssessmentInviteeListPort).loadAll(eq(param.getAssessmentId()), roleIdsCaptor.capture());
        assertNotNull(roleIdsCaptor.getValue());
        assertFalse(roleIdsCaptor.getValue().isEmpty());
        for (int i = 0; i < roleIds.size(); i++)
            assertEquals(roleIds.get(i), roleIdsCaptor.getValue().get(i));

        verifyNoInteractions(createFileDownloadLinkPort);
        verify(assessmentAccessChecker, times(2)).isAuthorized(any(), any(), any());
    }

    private LoadAssessmentUsersPort.ReportUser createReportUser(String picturePath, UUID inviterId, AssessmentUserRole role) {
        return new LoadAssessmentUsersPort.ReportUser(
            UUID.randomUUID(),
            "displayname",
            "email@flickit.org",
            picturePath,
            inviterId,
            role
        );
    }

    private GetGraphicalReportUsersUseCase.Param createParam(Consumer<GetGraphicalReportUsersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetGraphicalReportUsersUseCase.Param.ParamBuilder paramBuilder() {
        return GetGraphicalReportUsersUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
