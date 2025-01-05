package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessment.GrantAccessToReportUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.CreateAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.CreateSpaceInvitePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.flickit.assessment.core.test.fixture.application.UserMother;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_ACCESS_TO_REPORT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_GRAPHICAL_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.REPORT_VIEWER;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantAccessToReportServiceTest {

    @InjectMocks
    private GrantAccessToReportService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentPort loadAssessmentPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Mock
    private CreateSpaceInvitePort createSpaceInvitePort;

    @Mock
    private CreateAssessmentInvitePort createAssessmentInvitePort;

    @Mock
    private AppSpecProperties appSpecProperties;

    @Mock
    private SendEmailPort sendEmailPort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private CreateAssessmentSpaceUserAccessPort createAssessmentSpaceUserAccessPort;

    @Captor
    private ArgumentCaptor<CreateAssessmentSpaceUserAccessPort.Param> createAssessmentSpaceUserAccessParamCaptor;

    @Captor
    private ArgumentCaptor<CreateSpaceInvitePort.Param> createSpaceInviteParamCaptor;

    @Captor
    private ArgumentCaptor<CreateAssessmentInvitePort.Param> createAssessmentInviteParamCaptor;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    @Test
    void testGrantAccessToReport_whenUserWhoGrantAccessDoesNotHaveRequiredPermissions_thenThrowAccessDeniedException() {
        var param = createParam(GrantAccessToReportUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.grantAccessToReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadUserPort,
            grantUserAssessmentRolePort,
            loadUserRoleForAssessmentPort,
            loadAssessmentPort,
            createSpaceInvitePort,
            createAssessmentInvitePort,
            appSpecProperties,
            sendEmailPort,
            checkSpaceAccessPort,
            createAssessmentSpaceUserAccessPort);
    }

    @Test
    void testGrantAccessToReport_whenTheAssessmentThatUserIsGrantedReportAccessOnItDoesntExist_thenThrowResourceNotFoundException() {
        var param = createParam(GrantAccessToReportUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.grantAccessToReport(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadUserPort,
            grantUserAssessmentRolePort,
            loadUserRoleForAssessmentPort,
            createSpaceInvitePort,
            createAssessmentInvitePort,
            appSpecProperties,
            sendEmailPort,
            checkSpaceAccessPort,
            createAssessmentSpaceUserAccessPort);
    }

    @Test
    void testGrantAccessToReport_whenTheUserMemberOfAssessmentSpaceAndDoesntHaveAnyRoleOnAssessment_thenGrantRequiredRole() {
        var assessment = AssessmentMother.assessment();
        var accessGrantedUser = UserMother.createUser();
        var param = createParam(b -> b.assessmentId(assessment.getId()).email(accessGrantedUser.getEmail()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.of(accessGrantedUser));
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), accessGrantedUser.getId())).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), accessGrantedUser.getId())).thenReturn(Optional.empty());
        doNothing().when(grantUserAssessmentRolePort).persist(param.getAssessmentId(), accessGrantedUser.getId(), REPORT_VIEWER.getId());

        service.grantAccessToReport(param);

        verify(grantUserAssessmentRolePort).persist(param.getAssessmentId(), accessGrantedUser.getId(), REPORT_VIEWER.getId());

        verifyNoInteractions(createAssessmentSpaceUserAccessPort,
            createSpaceInvitePort,
            createAssessmentInvitePort,
            appSpecProperties,
            sendEmailPort);
    }

    @Test
    void testGrantAccessToReport_whenTheUserIsNotMemberOfAssessmentSpaceAndDoesntHaveAnyRoleOnAssessment_thenMakeMemberAndGrantRequiredRole() {
        var assessment = AssessmentMother.assessment();
        var accessGrantedUser = UserMother.createUser();
        var param = createParam(b -> b.assessmentId(assessment.getId()).email(accessGrantedUser.getEmail()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.of(accessGrantedUser));
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), accessGrantedUser.getId())).thenReturn(false);
        doNothing().when(createAssessmentSpaceUserAccessPort).persist(any(CreateAssessmentSpaceUserAccessPort.Param.class));
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), accessGrantedUser.getId())).thenReturn(Optional.empty());
        doNothing().when(grantUserAssessmentRolePort).persist(param.getAssessmentId(), accessGrantedUser.getId(), REPORT_VIEWER.getId());

        service.grantAccessToReport(param);

        verify(createAssessmentSpaceUserAccessPort).persist(createAssessmentSpaceUserAccessParamCaptor.capture());
        assertNotNull(createAssessmentSpaceUserAccessParamCaptor.getValue());
        assertEquals(param.getAssessmentId(), createAssessmentSpaceUserAccessParamCaptor.getValue().assessmentId());
        assertEquals(accessGrantedUser.getId(), createAssessmentSpaceUserAccessParamCaptor.getValue().userId());
        assertEquals(param.getCurrentUserId(), createAssessmentSpaceUserAccessParamCaptor.getValue().createdBy());
        assertNotNull(createAssessmentSpaceUserAccessParamCaptor.getValue().creationTime());

        verify(grantUserAssessmentRolePort).persist(param.getAssessmentId(), accessGrantedUser.getId(), REPORT_VIEWER.getId());

        verifyNoInteractions(createSpaceInvitePort,
            createAssessmentInvitePort,
            appSpecProperties,
            sendEmailPort);
    }

    @Test
    void testGrantAccessToReport_whenTheUserIsNotMemberOfAssessmentSpaceAndHaveARoleWithRequiredPermission_thenThrowResourceAlreadyExistsException() {
        var assessment = AssessmentMother.assessment();
        var accessGrantedUser = UserMother.createUser();
        var param = createParam(b -> b.assessmentId(assessment.getId()).email(accessGrantedUser.getEmail()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.of(accessGrantedUser));
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), accessGrantedUser.getId())).thenReturn(false);
        doNothing().when(createAssessmentSpaceUserAccessPort).persist(any(CreateAssessmentSpaceUserAccessPort.Param.class));
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), accessGrantedUser.getId())).thenReturn(Optional.of(AssessmentUserRole.MANAGER));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), accessGrantedUser.getId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.grantAccessToReport(param));
        assertEquals(GRANT_ACCESS_TO_REPORT_USER_ALREADY_GRANTED, throwable.getMessage());

        verify(createAssessmentSpaceUserAccessPort).persist(createAssessmentSpaceUserAccessParamCaptor.capture());
        assertNotNull(createAssessmentSpaceUserAccessParamCaptor.getValue());
        assertEquals(param.getAssessmentId(), createAssessmentSpaceUserAccessParamCaptor.getValue().assessmentId());
        assertEquals(accessGrantedUser.getId(), createAssessmentSpaceUserAccessParamCaptor.getValue().userId());
        assertEquals(param.getCurrentUserId(), createAssessmentSpaceUserAccessParamCaptor.getValue().createdBy());
        assertNotNull(createAssessmentSpaceUserAccessParamCaptor.getValue().creationTime());

        verifyNoInteractions(grantUserAssessmentRolePort,
            createSpaceInvitePort,
            createAssessmentInvitePort,
            appSpecProperties,
            sendEmailPort);
    }

    @Test
    void testGrantAccessToReport_whenTheUserIsMemberOfAssessmentSpaceAndHaveARoleWithRequiredPermission_thenThrowResourceAlreadyExistsException() {
        var assessment = AssessmentMother.assessment();
        var accessGrantedUser = UserMother.createUser();
        var param = createParam(b -> b.assessmentId(assessment.getId()).email(accessGrantedUser.getEmail()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.of(accessGrantedUser));
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), accessGrantedUser.getId())).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), accessGrantedUser.getId())).thenReturn(Optional.of(AssessmentUserRole.ASSESSOR));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), accessGrantedUser.getId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.grantAccessToReport(param));
        assertEquals(GRANT_ACCESS_TO_REPORT_USER_ALREADY_GRANTED, throwable.getMessage());

        verifyNoInteractions(
            grantUserAssessmentRolePort,
            createSpaceInvitePort,
            createAssessmentInvitePort,
            appSpecProperties,
            sendEmailPort,
            createAssessmentSpaceUserAccessPort);
    }

    @Test
    void testGrantAccessToReport_whenTheUserIsNotMemberOfAssessmentSpaceAndDoesntHaveRoleWithRequiredPermission_thenThrowAccessDeniedException() {
        var assessment = AssessmentMother.assessment();
        var accessGrantedUser = UserMother.createUser();
        var param = createParam(b -> b.assessmentId(assessment.getId()).email(accessGrantedUser.getEmail()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.of(accessGrantedUser));
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), accessGrantedUser.getId())).thenReturn(false);
        doNothing().when(createAssessmentSpaceUserAccessPort).persist(any(CreateAssessmentSpaceUserAccessPort.Param.class));
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), accessGrantedUser.getId())).thenReturn(Optional.of(AssessmentUserRole.ASSOCIATE));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), accessGrantedUser.getId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.grantAccessToReport(param));
        assertEquals(GRANT_ACCESS_TO_REPORT_NOT_ALLOWED_CONTACT_ASSESSMENT_MANAGER, throwable.getMessage());

        verify(createAssessmentSpaceUserAccessPort).persist(createAssessmentSpaceUserAccessParamCaptor.capture());
        assertNotNull(createAssessmentSpaceUserAccessParamCaptor.getValue());
        assertEquals(param.getAssessmentId(), createAssessmentSpaceUserAccessParamCaptor.getValue().assessmentId());
        assertEquals(accessGrantedUser.getId(), createAssessmentSpaceUserAccessParamCaptor.getValue().userId());
        assertEquals(param.getCurrentUserId(), createAssessmentSpaceUserAccessParamCaptor.getValue().createdBy());
        assertNotNull(createAssessmentSpaceUserAccessParamCaptor.getValue().creationTime());

        verifyNoInteractions(grantUserAssessmentRolePort,
            createSpaceInvitePort,
            createAssessmentInvitePort,
            appSpecProperties,
            sendEmailPort);
    }

    @Test
    void testGrantAccessToReport_whenTheUserIsMemberOfAssessmentSpaceAndDoesntHaveRoleWithRequiredPermission_thenThrowAccessDeniedException() {
        var assessment = AssessmentMother.assessment();
        var accessGrantedUser = UserMother.createUser();
        var param = createParam(b -> b.assessmentId(assessment.getId()).email(accessGrantedUser.getEmail()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.of(accessGrantedUser));
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), accessGrantedUser.getId())).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), accessGrantedUser.getId())).thenReturn(Optional.of(AssessmentUserRole.ASSOCIATE));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), accessGrantedUser.getId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.grantAccessToReport(param));
        assertEquals(GRANT_ACCESS_TO_REPORT_NOT_ALLOWED_CONTACT_ASSESSMENT_MANAGER, throwable.getMessage());

        verifyNoInteractions(
            grantUserAssessmentRolePort,
            createSpaceInvitePort,
            createAssessmentInvitePort,
            appSpecProperties,
            sendEmailPort,
            createAssessmentSpaceUserAccessPort);
    }

    @Test
    void testGrantAccessToReport_whenTheUserIsNotFoundedByEmailAndAppSpecSupportEmailIsNull_thenSendEmailToInviteThemToSpaceAndAssessmentWithoutSupportEmail() {
        var assessment = AssessmentMother.assessment();
        var param = createParam(b -> b.assessmentId(assessment.getId()));
        String subject =  MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, "flickit");
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY_WITHOUT_SUPPORT_EMAIL, "localhost", "flickit");

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.empty());
        doNothing().when(createSpaceInvitePort).persist(any(CreateSpaceInvitePort.Param.class));
        doNothing().when(createAssessmentInvitePort).persist(any(CreateAssessmentInvitePort.Param.class));
        when(appSpecProperties.getName()).thenReturn("flickit");
        when(appSpecProperties.getHost()).thenReturn("localhost");
        when(appSpecProperties.getSupportEmail()).thenReturn(null);
        doNothing().when(sendEmailPort).send(param.getEmail(), subject, body);

        service.grantAccessToReport(param);

        verify(createSpaceInvitePort).persist(createSpaceInviteParamCaptor.capture());
        assertNotNull(createSpaceInviteParamCaptor.getValue());
        assertEquals(assessment.getSpace().getId(), createSpaceInviteParamCaptor.getValue().spaceId());
        assertEquals(param.getEmail(), createSpaceInviteParamCaptor.getValue().email());
        LocalDateTime creationTime = createSpaceInviteParamCaptor.getValue().creationTime();
        assertNotNull(creationTime);
        LocalDateTime expirationDate = createSpaceInviteParamCaptor.getValue().expirationDate();
        assertNotNull(expirationDate);
        assertEquals(EXPIRY_DURATION.toDays(), ChronoUnit.DAYS.between(creationTime, expirationDate));
        assertEquals(param.getCurrentUserId(), createSpaceInviteParamCaptor.getValue().createdBy());

        verify(createAssessmentInvitePort).persist(createAssessmentInviteParamCaptor.capture());
        assertNotNull(createAssessmentInviteParamCaptor.getValue());
        assertEquals(param.getAssessmentId(), createAssessmentInviteParamCaptor.getValue().assessmentId());
        assertEquals(param.getEmail(), createAssessmentInviteParamCaptor.getValue().email());
        assertEquals(REPORT_VIEWER.getId(), createAssessmentInviteParamCaptor.getValue().roleId());
        creationTime = createAssessmentInviteParamCaptor.getValue().creationTime();
        assertNotNull(creationTime);
        expirationDate = createAssessmentInviteParamCaptor.getValue().expirationTime();
        assertNotNull(expirationDate);
        assertEquals(EXPIRY_DURATION.toDays(), ChronoUnit.DAYS.between(creationTime, expirationDate));
        assertEquals(param.getCurrentUserId(), createAssessmentInviteParamCaptor.getValue().createdBy());

        verify(sendEmailPort).send(param.getEmail(), subject, body);

        verifyNoInteractions(grantUserAssessmentRolePort, loadUserRoleForAssessmentPort);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testGrantAccessToReport_whenTheUserIsNotFoundedByEmailAndAppSpecWithoutSupportEmail_thenSendEmailToInviteThemToSpaceAndAssessmentWithoutSupportEmail(String supportEmail) {
        var assessment = AssessmentMother.assessment();
        var param = createParam(b -> b.assessmentId(assessment.getId()));
        String subject =  MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, "flickit");
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY_WITHOUT_SUPPORT_EMAIL, "localhost", "flickit");

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.empty());
        doNothing().when(createSpaceInvitePort).persist(any(CreateSpaceInvitePort.Param.class));
        doNothing().when(createAssessmentInvitePort).persist(any(CreateAssessmentInvitePort.Param.class));
        when(appSpecProperties.getName()).thenReturn("flickit");
        when(appSpecProperties.getHost()).thenReturn("localhost");
        when(appSpecProperties.getSupportEmail()).thenReturn(supportEmail);
        doNothing().when(sendEmailPort).send(param.getEmail(), subject, body);

        service.grantAccessToReport(param);

        verify(createSpaceInvitePort).persist(createSpaceInviteParamCaptor.capture());
        assertNotNull(createSpaceInviteParamCaptor.getValue());
        assertEquals(assessment.getSpace().getId(), createSpaceInviteParamCaptor.getValue().spaceId());
        assertEquals(param.getEmail(), createSpaceInviteParamCaptor.getValue().email());
        LocalDateTime creationTime = createSpaceInviteParamCaptor.getValue().creationTime();
        assertNotNull(creationTime);
        LocalDateTime expirationDate = createSpaceInviteParamCaptor.getValue().expirationDate();
        assertNotNull(expirationDate);
        assertEquals(EXPIRY_DURATION.toDays(), ChronoUnit.DAYS.between(creationTime, expirationDate));
        assertEquals(param.getCurrentUserId(), createSpaceInviteParamCaptor.getValue().createdBy());

        verify(createAssessmentInvitePort).persist(createAssessmentInviteParamCaptor.capture());
        assertNotNull(createAssessmentInviteParamCaptor.getValue());
        assertEquals(param.getAssessmentId(), createAssessmentInviteParamCaptor.getValue().assessmentId());
        assertEquals(param.getEmail(), createAssessmentInviteParamCaptor.getValue().email());
        assertEquals(REPORT_VIEWER.getId(), createAssessmentInviteParamCaptor.getValue().roleId());
        creationTime = createAssessmentInviteParamCaptor.getValue().creationTime();
        assertNotNull(creationTime);
        expirationDate = createAssessmentInviteParamCaptor.getValue().expirationTime();
        assertNotNull(expirationDate);
        assertEquals(EXPIRY_DURATION.toDays(), ChronoUnit.DAYS.between(creationTime, expirationDate));
        assertEquals(param.getCurrentUserId(), createAssessmentInviteParamCaptor.getValue().createdBy());

        verify(sendEmailPort).send(param.getEmail(), subject, body);

        verifyNoInteractions(grantUserAssessmentRolePort, loadUserRoleForAssessmentPort);
    }

    @Test
    void testGrantAccessToReport_whenTheUserIsNotFoundedByEmailAndAppSpecWithSupportEmail_thenSendEmailToInviteThemToSpaceAndAssessmentWithSupportEmail() {
        var assessment = AssessmentMother.assessment();
        var param = createParam(b -> b.assessmentId(assessment.getId()));
        String subject =  MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, "flickit");
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY, "localhost", "flickit", "support@flickit.com");

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.empty());
        doNothing().when(createSpaceInvitePort).persist(any(CreateSpaceInvitePort.Param.class));
        doNothing().when(createAssessmentInvitePort).persist(any(CreateAssessmentInvitePort.Param.class));
        when(appSpecProperties.getName()).thenReturn("flickit");
        when(appSpecProperties.getHost()).thenReturn("localhost");
        when(appSpecProperties.getSupportEmail()).thenReturn("support@flickit.com");
        doNothing().when(sendEmailPort).send(param.getEmail(), subject, body);

        service.grantAccessToReport(param);

        verify(createSpaceInvitePort).persist(createSpaceInviteParamCaptor.capture());
        assertNotNull(createSpaceInviteParamCaptor.getValue());
        assertEquals(assessment.getSpace().getId(), createSpaceInviteParamCaptor.getValue().spaceId());
        assertEquals(param.getEmail(), createSpaceInviteParamCaptor.getValue().email());
        LocalDateTime creationTime = createSpaceInviteParamCaptor.getValue().creationTime();
        assertNotNull(creationTime);
        LocalDateTime expirationDate = createSpaceInviteParamCaptor.getValue().expirationDate();
        assertNotNull(expirationDate);
        assertEquals(EXPIRY_DURATION.toDays(), ChronoUnit.DAYS.between(creationTime, expirationDate));
        assertEquals(param.getCurrentUserId(), createSpaceInviteParamCaptor.getValue().createdBy());

        verify(createAssessmentInvitePort).persist(createAssessmentInviteParamCaptor.capture());
        assertNotNull(createAssessmentInviteParamCaptor.getValue());
        assertEquals(param.getAssessmentId(), createAssessmentInviteParamCaptor.getValue().assessmentId());
        assertEquals(param.getEmail(), createAssessmentInviteParamCaptor.getValue().email());
        assertEquals(REPORT_VIEWER.getId(), createAssessmentInviteParamCaptor.getValue().roleId());
        creationTime = createAssessmentInviteParamCaptor.getValue().creationTime();
        assertNotNull(creationTime);
        expirationDate = createAssessmentInviteParamCaptor.getValue().expirationTime();
        assertNotNull(expirationDate);
        assertEquals(EXPIRY_DURATION.toDays(), ChronoUnit.DAYS.between(creationTime, expirationDate));
        assertEquals(param.getCurrentUserId(), createAssessmentInviteParamCaptor.getValue().createdBy());

        verify(sendEmailPort).send(param.getEmail(), subject, body);

        verifyNoInteractions(grantUserAssessmentRolePort, loadUserRoleForAssessmentPort);
    }

    private GrantAccessToReportUseCase.Param createParam(Consumer<GrantAccessToReportUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private GrantAccessToReportUseCase.Param.ParamBuilder paramBuilder() {
        return GrantAccessToReportUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .email("test@test.com")
            .currentUserId(UUID.randomUUID());
    }
}
