package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetGraphicalReportUsersUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInviteeListPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInviteMother;
import org.flickit.assessment.core.test.fixture.application.FullUserMother;
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

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_ACCESS_TO_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
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

    @Test
    void testGetGraphicalReportUsers_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(GetGraphicalReportUsersUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getGraphicalReportUsers(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetGraphicalReportUsers_whenThereIsNoUsersWithRequiredPermissionAndNoInviteeUsers_thenReturnResultWithEmptyUsersAndInvitees() {
        var param = createParam(GetGraphicalReportUsersUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(new ArrayList<>());
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(new ArrayList<>());

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertTrue(result.users().isEmpty());
        assertTrue(result.invitees().isEmpty());

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testGetGraphicalReportUsers_whenThereIsAUserWithRequiredPermissionAndNoPictureButNotAnyInviteeUsers_thenReturnResultWithUsersAndEmptyInvitees(String picturePath) {
        var param = createParam(GetGraphicalReportUsersUseCase.Param.ParamBuilder::build);
        var grantedUser = FullUserMother.createFullUser(picturePath);
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(grantedUser));
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(new ArrayList<>());

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.users());
        assertEquals(grantedUser.getId(), result.users().getFirst().id());
        assertEquals(grantedUser.getDisplayName(), result.users().getFirst().displayName());
        assertEquals(grantedUser.getEmail(), result.users().getFirst().email());
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

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetGraphicalReportUser_whenThereIsAUserWithRequiredPermissionAndPictureButNotAnyInviteeUsers_thenReturnResultWithUsersAndEmptyInvitees() {
        var param = createParam(GetGraphicalReportUsersUseCase.Param.ParamBuilder::build);
        var grantedUser = FullUserMother.createFullUser("picture-path");
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(grantedUser));
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(new ArrayList<>());
        when(createFileDownloadLinkPort.createDownloadLink(grantedUser.getPicturePath(), EXPIRY_DURATION)).thenReturn("picture-link");

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.users());
        assertEquals(grantedUser.getId(), result.users().getFirst().id());
        assertEquals(grantedUser.getDisplayName(), result.users().getFirst().displayName());
        assertEquals(grantedUser.getEmail(), result.users().getFirst().email());
        assertNotNull(result.users().getFirst().pictureLink());
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
    }

    @Test
    void testGetGraphicalReportUser_whenThereIsInviteeUsersButNotAnyUser_thenReturnResultWithEmptyUsersAndInvitees() {
        var param = createParam(GetGraphicalReportUsersUseCase.Param.ParamBuilder::build);
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();

        var invitees = List.of(AssessmentInviteMother.notExpiredAssessmentInvite("invitee@test.com"));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(new ArrayList<>());
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(invitees);

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.invitees());
        assertTrue(result.users().isEmpty());
        assertEquals(invitees.getFirst().getEmail(), result.invitees().getFirst().email());

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
    }

    @Test
    void testGetGraphicalReportUser_whenThereIsAUserWithRequiredPermissionAndPictureAndInviteeUsers_thenReturnResultWithUsersAndInvitees() {
        var param = createParam(GetGraphicalReportUsersUseCase.Param.ParamBuilder::build);
        var grantedUser = FullUserMother.createFullUser("picture-path");
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();
        var invitees = List.of(AssessmentInviteMother.notExpiredAssessmentInvite("invitee@test.com"));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(grantedUser));
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(invitees);
        when(createFileDownloadLinkPort.createDownloadLink(grantedUser.getPicturePath(), EXPIRY_DURATION)).thenReturn("picture-link");

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.users());
        assertEquals(grantedUser.getId(), result.users().getFirst().id());
        assertEquals(grantedUser.getDisplayName(), result.users().getFirst().displayName());
        assertEquals(grantedUser.getEmail(), result.users().getFirst().email());
        assertNotNull(result.users().getFirst().pictureLink());
        assertEquals("picture-link", result.users().getFirst().pictureLink());
        assertNotNull(result.invitees());
        assertFalse(result.invitees().isEmpty());
        assertEquals(invitees.getFirst().getEmail(), result.invitees().getFirst().email());

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
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testGetGraphicalReportUser_whenThereIsAUserWithRequiredPermissionAndNoPictureAndInviteeUsers_thenReturnResultWithUsersAndInvitees(String picturePath) {
        var param = createParam(GetGraphicalReportUsersUseCase.Param.ParamBuilder::build);
        var grantedUser = FullUserMother.createFullUser(picturePath);
        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();
        var invitees = List.of(AssessmentInviteMother.notExpiredAssessmentInvite("invitee@test.com"));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentUsersPort.loadAll(any(UUID.class), anyList())).thenReturn(List.of(grantedUser));
        when(loadAssessmentInviteeListPort.loadAll(any(UUID.class), anyList())).thenReturn(invitees);

        var result = service.getGraphicalReportUsers(param);
        assertNotNull(result);
        assertNotNull(result.users());
        assertEquals(grantedUser.getId(), result.users().getFirst().id());
        assertEquals(grantedUser.getDisplayName(), result.users().getFirst().displayName());
        assertEquals(grantedUser.getEmail(), result.users().getFirst().email());
        assertNull(result.users().getFirst().pictureLink());
        assertNotNull(result.invitees());
        assertFalse(result.invitees().isEmpty());
        assertEquals(invitees.getFirst().getEmail(), result.invitees().getFirst().email());

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
    }

    private GetGraphicalReportUsersUseCase.Param createParam(Consumer<GetGraphicalReportUsersUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private GetGraphicalReportUsersUseCase.Param.ParamBuilder paramBuilder() {
        return GetGraphicalReportUsersUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
