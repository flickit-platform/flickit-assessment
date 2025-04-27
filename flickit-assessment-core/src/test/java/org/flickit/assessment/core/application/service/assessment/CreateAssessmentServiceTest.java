package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.util.SlugCodeUtil;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CountAssessmentsPort;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
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
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.flickit.assessment.core.test.fixture.application.AssessmentKitMother.kit;
import static org.flickit.assessment.core.test.fixture.application.AssessmentKitMother.publicKit;
import static org.flickit.assessment.core.test.fixture.application.SpaceMother.createExpiredPremiumSpace;
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

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Spy
    AppSpecProperties appSpecProperties = appSpecProperties();

    private final AssessmentKit privateKit = kit();
    private final AssessmentKit publicKit = publicKit();

    private final Space space = SpaceMother.createBasicSpace();
    private Param param = createParam(Param.ParamBuilder::build);


    @Test
    void testCreateAssessment_whenSpaceNotFound_thenThrowResourceNotFoundException() {
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAssessment(param));
        assertEquals(COMMON_SPACE_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(createAssessmentPort,
            createAssessmentResultPort,
            createAttributeValuePort,
            createSubjectValuePort,
            loadAssessmentKitPort,
            checkSpaceAccessPort,
            checkKitAccessPort,
            grantUserAssessmentRolePort,
            loadSubjectsPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAssessment_whenCurrentUserIsNotSpaceMember_thenThrowUpgradeRequiredException() {
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(space));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(createAssessmentPort,
            createAssessmentResultPort,
            createAttributeValuePort,
            createSubjectValuePort,
            loadAssessmentKitPort,
            checkKitAccessPort,
            grantUserAssessmentRolePort,
            loadSubjectsPort,
            countAssessmentsPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAssessment_whenAssessmentKitNotFound_thenThrowResourceNotFoundException() {
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(space));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAssessment(param));
        assertEquals(ASSESSMENT_KIT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(createAssessmentPort,
            createAssessmentResultPort,
            createAttributeValuePort,
            createSubjectValuePort,
            checkKitAccessPort,
            grantUserAssessmentRolePort,
            loadSubjectsPort,
            countAssessmentsPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAssessment_whenCurrentUserDoesNotHaveAccessToKit_thenThrowValidationException() {
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(space));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)).thenReturn(Optional.of(publicKit));
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ValidationException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_KIT_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(createAssessmentPort,
            createAssessmentResultPort,
            createAttributeValuePort,
            createSubjectValuePort,
            grantUserAssessmentRolePort,
            loadSubjectsPort,
            countAssessmentsPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAssessment_whenSpaceIsBasicAndExceedsMaxAssessmentLimits_thenThrowUpgradeRequiredException() {
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(space));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)).thenReturn(Optional.of(publicKit));
        when(countAssessmentsPort.countSpaceAssessments(param.getSpaceId())).thenReturn(2);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_BASIC_SPACE_ASSESSMENTS_MAX, throwable.getMessage());

        verifyNoInteractions(createAssessmentPort,
            createAssessmentResultPort,
            createAttributeValuePort,
            createSubjectValuePort,
            grantUserAssessmentRolePort,
            loadSubjectsPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAssessment_whenSpaceIsBasicAndAssessmentKitIsPrivate_thenThrowUpgradeRequiredException() {
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(space));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)).thenReturn(Optional.of(privateKit));
        when(countAssessmentsPort.countSpaceAssessments(param.getSpaceId())).thenReturn(0);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_BASIC_SPACE_PRIVATE_KIT_NOT_ALLOWED, throwable.getMessage());

        verify(appSpecProperties).getSpace();

        verifyNoInteractions(createAssessmentPort,
            createAssessmentResultPort,
            createAttributeValuePort,
            createSubjectValuePort,
            grantUserAssessmentRolePort,
            loadSubjectsPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAssessment_whenSpaceIsPremiumAndSubscriptionIsExpired_thenThrowUpgradeRequiredException() {
        var premiumExpiredSpace = createExpiredPremiumSpace(UUID.randomUUID());

        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(premiumExpiredSpace));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)).thenReturn(Optional.of(publicKit));

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_PREMIUM_SPACE_EXPIRED, throwable.getMessage());

        verifyNoInteractions(createAssessmentPort,
            createAssessmentResultPort,
            createAttributeValuePort,
            createSubjectValuePort,
            grantUserAssessmentRolePort,
            loadSubjectsPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAssessment_whenLangIsNotSupportedInKit_thenThrowValidationException() {
        param = createParam(b -> b.lang("FA"));
        publicKit.setSupportedLanguages(List.of(KitLanguage.EN));

        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(space));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)).thenReturn(Optional.of(publicKit));

        var throwable = assertThrows(ValidationException.class, () -> service.createAssessment(param));
        assertNotNull(CREATE_ASSESSMENT_LANGUAGE_NOT_SUPPORTED, throwable.getMessage());

        verifyNoInteractions(createAssessmentPort,
            createAssessmentResultPort,
            createAttributeValuePort,
            createSubjectValuePort,
            grantUserAssessmentRolePort,
            loadSubjectsPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAssessment_whenParamsAreValid_thenCreateAssessmentAndAssessmentResult() {
        param = createParam(b -> b.lang("FA"));
        UUID expectedAssessmentId = UUID.randomUUID();

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedAssessmentId);
        when(createAssessmentResultPort.persist(any(CreateAssessmentResultPort.Param.class))).thenReturn(UUID.randomUUID());
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(publicKit.getKitVersion())).thenReturn(createSubjects());
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(space));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)).thenReturn(Optional.of(publicKit));
        when(loadMaturityLevelsPort.loadAll(publicKit.getKitVersion())).thenReturn(MaturityLevelMother.allLevels());

        var result = service.createAssessment(param);
        assertNotNull(result);
        assertEquals(expectedAssessmentId, result.id());

        ArgumentCaptor<CreateAssessmentPort.Param> createAssessmentPortCaptor = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createAssessmentPortCaptor.capture());
        assertCreateAssessmentPort(createAssessmentPortCaptor);

        verify(grantUserAssessmentRolePort).persist(result.id(),
            param.getCurrentUserId(),
            AssessmentUserRole.MANAGER.getId());

        ArgumentCaptor<CreateAssessmentResultPort.Param> createAssessmentResultPortCaptor = ArgumentCaptor.forClass(CreateAssessmentResultPort.Param.class);
        verify(createAssessmentResultPort).persist(createAssessmentResultPortCaptor.capture());
        assertEquals(expectedAssessmentId, createAssessmentResultPortCaptor.getValue().assessmentId());
        assertEquals(publicKit.getKitVersion(), createAssessmentResultPortCaptor.getValue().kitVersionId());
        assertEquals(MaturityLevelMother.levelOne().getId(), createAssessmentResultPortCaptor.getValue().maturityLevelId());
        assertEquals(0.0, createAssessmentResultPortCaptor.getValue().confidenceValue());
        assertNotNull(createAssessmentResultPortCaptor.getValue().lastModificationTime());
        assertFalse(createAssessmentResultPortCaptor.getValue().isCalculateValid());
        assertFalse(createAssessmentResultPortCaptor.getValue().isConfidenceValid());
        assertEquals(KitLanguage.valueOf(param.getLang()).getId(), createAssessmentResultPortCaptor.getValue().langId());

        verify(createSubjectValuePort, times(1)).persistAll(anyList(), any());
        verify(createAttributeValuePort, times(1)).persistAll(anySet(), any());

        verify(appSpecProperties).getSpace();
    }

    @Test
    void testCreateAssessment_whenCurrentUserIsNotSpaceOwner_thenGrantAccessToSpaceOwnerToo() {
        param = createParam(b -> b.currentUserId(UUID.randomUUID()).lang(null));
        UUID expectedAssessmentId = UUID.randomUUID();

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedAssessmentId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(publicKit.getKitVersion())).thenReturn(expectedResponse);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(Optional.of(space));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)).thenReturn(Optional.of(publicKit));
        when(loadMaturityLevelsPort.loadAll(publicKit.getKitVersion())).thenReturn(MaturityLevelMother.allLevels());

        CreateAssessmentUseCase.Result result = service.createAssessment(param);
        assertNotNull(result);
        assertEquals(expectedAssessmentId, result.id());

        verify(grantUserAssessmentRolePort).persist(result.id(),
            param.getCurrentUserId(),
            AssessmentUserRole.MANAGER.getId());

        verify(grantUserAssessmentRolePort).persist(result.id(),
            space.getOwnerId(),
            AssessmentUserRole.MANAGER.getId());

        ArgumentCaptor<CreateAssessmentResultPort.Param> createAssessmentResultPortCaptor = ArgumentCaptor.forClass(CreateAssessmentResultPort.Param.class);
        verify(createAssessmentResultPort).persist(createAssessmentResultPortCaptor.capture());
        assertEquals(expectedAssessmentId, createAssessmentResultPortCaptor.getValue().assessmentId());
        assertEquals(publicKit.getKitVersion(), createAssessmentResultPortCaptor.getValue().kitVersionId());
        assertEquals(MaturityLevelMother.levelOne().getId(), createAssessmentResultPortCaptor.getValue().maturityLevelId());
        assertEquals(0.0, createAssessmentResultPortCaptor.getValue().confidenceValue());
        assertNotNull(createAssessmentResultPortCaptor.getValue().lastModificationTime());
        assertFalse(createAssessmentResultPortCaptor.getValue().isCalculateValid());
        assertFalse(createAssessmentResultPortCaptor.getValue().isConfidenceValid());
        assertEquals(publicKit.getLanguage().getId(), createAssessmentResultPortCaptor.getValue().langId());

        verify(createSubjectValuePort, times(1)).persistAll(anyList(), any());
        verify(createAttributeValuePort, times(1)).persistAll(anySet(), any());

        verify(appSpecProperties).getSpace();
    }

    private AppSpecProperties appSpecProperties() {
        var properties = new AppSpecProperties();
        properties.setSpace(new AppSpecProperties.Space());
        properties.getSpace().setMaxBasicSpaces(1);
        properties.getSpace().setMaxBasicSpaceAssessments(2);
        return properties;
    }

    private List<Subject> createSubjects() {
        Attribute qa1 = AttributeMother.simpleAttribute();
        Attribute qa2 = AttributeMother.simpleAttribute();
        Attribute qa3 = AttributeMother.simpleAttribute();
        Attribute qa4 = AttributeMother.simpleAttribute();
        Attribute qa5 = AttributeMother.simpleAttribute();

        return List.of(
            new Subject(1L, 1, "subject2", "description2", 1, List.of(qa1, qa2)),
            new Subject(2L, 2, "subject1", "description1", 1, List.of(qa3, qa4)),
            new Subject(3L, 3, "subject3", "description3", 1, List.of(qa5))
        );
    }

    private void assertCreateAssessmentPort(ArgumentCaptor<CreateAssessmentPort.Param> createPortParam) {
        assertEquals(SlugCodeUtil.generateSlugCode(param.getTitle()), createPortParam.getValue().code());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(param.getShortTitle(), createPortParam.getValue().shortTitle());
        assertEquals(param.getKitId(), createPortParam.getValue().assessmentKitId());
        assertEquals(param.getSpaceId(), createPortParam.getValue().spaceId());
        assertNotNull(createPortParam.getValue().creationTime());
        assertEquals(0, createPortParam.getValue().deletionTime());
        assertFalse(createPortParam.getValue().deleted());
        assertEquals(param.getCurrentUserId(), createPortParam.getValue().createdBy());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentUseCase.Param.builder()
            .title("New Assessment")
            .shortTitle("Short Title")
            .spaceId(space.getId())
            .kitId(publicKit.getId())
            .lang("EN")
            .currentUserId(UUID.randomUUID());
    }
}
