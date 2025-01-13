package org.flickit.assessment.core.application.service.assessment;


import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CountAssessmentsPort;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CountSpaceMembersPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.flickit.assessment.core.test.fixture.application.SpaceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentServiceTest {

    @InjectMocks
    private CreateAssessmentService service;

    @Mock
    private CreateAssessmentPort createAssessmentPort;

    @Mock
    private CreateAssessmentResultPort createAssessmentResultPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateAttributeValuePort createAttributeValuePort;

    @Mock
    private CountSpaceMembersPort countSpaceMembersPort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private CheckKitAccessPort checkKitAccessPort;

    @Mock
    private LoadSpacePort loadSpacePort;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    private CountAssessmentsPort countAssessmentsPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Spy
    AppSpecProperties appSpecProperties = appSpecProperties();

    private final AssessmentKit privateKit = AssessmentKitMother.kit();
    private final AssessmentKit publicKit = AssessmentKitMother.publicKit();

    private final Attribute qa1 = AttributeMother.simpleAttribute();
    private final Attribute qa2 = AttributeMother.simpleAttribute();
    private final Attribute qa3 = AttributeMother.simpleAttribute();
    private final Attribute qa4 = AttributeMother.simpleAttribute();
    private final Attribute qa5 = AttributeMother.simpleAttribute();

    private final List<Subject> expectedSubjects = List.of(
        new Subject(1L, "subject2", 1, List.of(qa1, qa2)),
        new Subject(2L, "subject1", 1, List.of(qa3, qa4)),
        new Subject(3L, "subject3", 1, List.of(qa5))
    );

    @Test
    void testCreateAssessment_ValidParam_PersistsAndReturnsId() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPersonalSpaceWithOwnerId(UUID.randomUUID());
        var expectedId = UUID.randomUUID();

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);
        when(countSpaceMembersPort.countSpaceMembers(param.getSpaceId())).thenReturn(1);

        CreateAssessmentUseCase.Result result = service.createAssessment(param);
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateAssessmentPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createPortParam.capture());

        assertEquals(param.getTitle(), createPortParam.getValue().code());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(param.getKitId(), createPortParam.getValue().assessmentKitId());
        assertNotNull(createPortParam.getValue().creationTime());

        ArgumentCaptor<UUID> grantPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> grantPortUserId = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> grantPortRoleId = ArgumentCaptor.forClass(Integer.class);
        verify(grantUserAssessmentRolePort, times(2)).persist(grantPortAssessmentId.capture(),
            grantPortUserId.capture(),
            grantPortRoleId.capture());

        assertEquals(expectedId, grantPortAssessmentId.getAllValues().getFirst());
        assertEquals(space.getOwnerId(), grantPortUserId.getAllValues().getFirst());
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().getFirst());

        assertEquals(expectedId, grantPortAssessmentId.getAllValues().get(1));
        assertEquals(param.getCurrentUserId(), grantPortUserId.getAllValues().get(1));
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().get(1));
    }

    @Test
    void testCreateAssessment_CurrentUserIsSpaceOwner_PersistOneAssessmentUserRole() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPersonalSpaceWithOwnerId(param.getCurrentUserId());
        var expectedId = UUID.randomUUID();

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);
        when(countSpaceMembersPort.countSpaceMembers(param.getSpaceId())).thenReturn(1);


        CreateAssessmentUseCase.Result result = service.createAssessment(param);
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateAssessmentPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createPortParam.capture());

        assertEquals(param.getTitle(), createPortParam.getValue().code());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(param.getKitId(), createPortParam.getValue().assessmentKitId());
        assertNotNull(createPortParam.getValue().creationTime());

        ArgumentCaptor<UUID> grantPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> grantPortUserId = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> grantPortRoleId = ArgumentCaptor.forClass(Integer.class);
        verify(grantUserAssessmentRolePort, times(1)).persist(grantPortAssessmentId.capture(),
            grantPortUserId.capture(),
            grantPortRoleId.capture());

        assertEquals(expectedId, grantPortAssessmentId.getAllValues().getFirst());
        assertEquals(param.getCurrentUserId(), grantPortUserId.getAllValues().getFirst());
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().getFirst());
    }

    @Test
    void testCreateAssessment_ValidParamPersonalSpaceAndPublicKit_PersistsAssessmentResult() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPersonalSpaceWithOwnerId(UUID.randomUUID());
        var assessmentId = UUID.randomUUID();

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(assessmentId);
        UUID expectedResultId = UUID.randomUUID();
        when(createAssessmentResultPort.persist(any(CreateAssessmentResultPort.Param.class))).thenReturn(expectedResultId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);
        when(countSpaceMembersPort.countSpaceMembers(param.getSpaceId())).thenReturn(1);


        service.createAssessment(param);

        ArgumentCaptor<CreateAssessmentResultPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentResultPort.Param.class);
        verify(createAssessmentResultPort).persist(createPortParam.capture());

        assertEquals(assessmentId, createPortParam.getValue().assessmentId());
        assertNotNull(createPortParam.getValue().lastModificationTime());
        assertFalse(createPortParam.getValue().isCalculateValid());
    }

    @Test
    void testCreateAssessment_ValidParam_PersistsSubjectValues() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPersonalSpaceWithOwnerId(UUID.randomUUID());

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(publicKit.getKitVersion())).thenReturn(expectedSubjects);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);

        service.createAssessment(param);

        verify(createSubjectValuePort, times(1)).persistAll(anyList(), any());
        verify(grantUserAssessmentRolePort, times(2)).persist(any(), any(UUID.class), anyInt());
    }

    @Test
    void testCreateAssessment_ValidCommand_PersistsAttributeValue() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPersonalSpaceWithOwnerId(UUID.randomUUID());

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(publicKit.getKitVersion())).thenReturn(expectedSubjects);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);
        when(countSpaceMembersPort.countSpaceMembers(param.getSpaceId())).thenReturn(1);


        service.createAssessment(param);

        verify(grantUserAssessmentRolePort, times(2)).persist(any(), any(UUID.class), anyInt());
        verify(createAttributeValuePort, times(1)).persistAll(anySet(), any());
    }

    @Test
    void testCreateAssessment_WhenUserDoesNotHaveAccessToSpace_ThenThrowsReturnUpgradeRequiredException() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateAssessment_WhenUserDoesNotHaveAccessToKit_ThenThrowsException() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ValidationException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_KIT_NOT_ALLOWED, throwable.getMessageKey());
    }

    @Test
    void testCreateAssessment_WhenSpaceIsPersonalAndExceedsMaxAssessmentLimits_ThenThrowsException() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPersonalSpaceWithOwnerId(UUID.randomUUID());
        var kit = AssessmentKitMother.kit();

        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(kit);
        when(countAssessmentsPort.countSpaceAssessments(param.getSpaceId())).thenReturn(2);
        when(countSpaceMembersPort.countSpaceMembers(param.getSpaceId())).thenReturn(1);


        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_PERSONAL_SPACE_ASSESSMENTS_MAX, throwable.getMessage());
    }

    @Test
    void testCreateAssessment_WhenSpaceIsPersonalAndAssessmentKitIsPrivate_ThenThrowsException() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPersonalSpaceWithOwnerId(UUID.randomUUID());

        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(privateKit);
        when(countAssessmentsPort.countSpaceAssessments(param.getSpaceId())).thenReturn(0);
        when(countSpaceMembersPort.countSpaceMembers(param.getSpaceId())).thenReturn(1);


        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_PERSONAL_SPACE_PRIVATE_KIT_MAX, throwable.getMessage());
    }

    @Test
    void testCreateAssessment_WhenSpaceIsPremiumAndSubscriptionExpired_ThenThrowsException() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPremiumExpiredSpace(UUID.randomUUID());
        var kit = AssessmentKitMother.kit();

        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(kit);


        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_PREMIUM_SPACE_EXPIRED, throwable.getMessage());
    }

    @Test
    void testCreateAssessment_ValidParamPersonalSpaceMaxMembersAndPublicKit_PersistsAssessmentResult() {
        var param = createParam(CreateAssessmentUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPersonalSpaceWithOwnerId(UUID.randomUUID());

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);
        when(countSpaceMembersPort.countSpaceMembers(param.getSpaceId())).thenReturn(10);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_PERSONAL_SPACE_MEMBERS_MAX, throwable.getMessage());
    }

    private AppSpecProperties appSpecProperties() {
        var properties = new AppSpecProperties();
        var space = new AppSpecProperties.Space();
        space.setMaxPersonalSpaces(1);
        space.setMaxPersonalSpaceAssessments(2);
        space.setMaxPersonalSpaceMembers(3);
        properties.setSpace(space);
        return properties;
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentUseCase.Param.builder()
            .title("title")
            .shortTitle("shortTitle")
            .spaceId(0L)
            .kitId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
