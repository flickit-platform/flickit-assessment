package org.flickit.assessment.core.application.service.assessment;


import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_KIT_NOT_ALLOWED;
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
    private LoadAssessmentKitVersionIdPort loadAssessmentKitVersionIdPort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private CheckKitAccessPort checkKitAccessPort;

    @Mock
    private LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Test
    void testCreateAssessment_ValidParam_PersistsAndReturnsId() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            1L,
            "title example",
            "short title",
            1L,
            currentUserId
        );
        UUID expectedId = UUID.randomUUID();
        UUID spaceOwnerId = UUID.randomUUID();


        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), currentUserId)).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpaceOwnerPort.loadOwnerId(any())).thenReturn(spaceOwnerId);

        CreateAssessmentUseCase.Result result = service.createAssessment(param);
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateAssessmentPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createPortParam.capture());

        assertEquals("title-example", createPortParam.getValue().code());
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
        assertEquals(spaceOwnerId, grantPortUserId.getAllValues().getFirst());
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().getFirst());

        assertEquals(expectedId, grantPortAssessmentId.getAllValues().get(1));
        assertEquals(param.getCurrentUserId(), grantPortUserId.getAllValues().get(1));
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().get(1));
    }

    @Test
    void testCreateAssessment_CurrentUserIsSpaceOwner_PersistOneAssessmentUserRole() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            1L,
            "title example",
            "short title",
            1L,
            currentUserId
        );
        UUID expectedId = UUID.randomUUID();


        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), currentUserId)).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpaceOwnerPort.loadOwnerId(any())).thenReturn(currentUserId);

        CreateAssessmentUseCase.Result result = service.createAssessment(param);
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateAssessmentPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createPortParam.capture());

        assertEquals("title-example", createPortParam.getValue().code());
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
        assertEquals(currentUserId, grantPortUserId.getAllValues().getFirst());
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().getFirst());
    }

    @Test
    void testCreateAssessment_ValidParam_PersistsAssessmentResult() {
        UUID createdBy = UUID.randomUUID();
        Param param = new Param(
            1L,
            "title example",
            "short title",
            1L,
            createdBy
        );
        UUID assessmentId = UUID.randomUUID();

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), createdBy)).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(assessmentId);
        UUID expectedResultId = UUID.randomUUID();
        when(createAssessmentResultPort.persist(any(CreateAssessmentResultPort.Param.class))).thenReturn(expectedResultId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpaceOwnerPort.loadOwnerId(any())).thenReturn(UUID.randomUUID());

        service.createAssessment(param);

        ArgumentCaptor<CreateAssessmentResultPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentResultPort.Param.class);
        verify(createAssessmentResultPort).persist(createPortParam.capture());

        assertEquals(assessmentId, createPortParam.getValue().assessmentId());
        assertNotNull(createPortParam.getValue().lastModificationTime());
        assertFalse(createPortParam.getValue().isCalculateValid());
    }

    @Test
    void testCreateAssessment_ValidParam_PersistsSubjectValues() {
        long assessmentKitId = 1L;
        long kitVersionId = 123L;
        UUID createdBy = UUID.randomUUID();
        Param param = new Param(
            1L,
            "title example",
            "short title",
            assessmentKitId,
            createdBy
        );

        Attribute qa1 = AttributeMother.simpleAttribute();
        Attribute qa2 = AttributeMother.simpleAttribute();
        Attribute qa3 = AttributeMother.simpleAttribute();
        Attribute qa4 = AttributeMother.simpleAttribute();
        Attribute qa5 = AttributeMother.simpleAttribute();

        List<Subject> expectedSubjects = List.of(
            new Subject(2L, "subject2", 1, List.of(qa3, qa4)),
            new Subject(1L, "subject1", 1, List.of(qa1, qa2)),
            new Subject(3L, "subject3", 1, List.of(qa5))
        );

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), createdBy)).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitVersionIdPort.loadVersionId(assessmentKitId)).thenReturn(kitVersionId);
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(kitVersionId)).thenReturn(expectedSubjects);
        when(loadSpaceOwnerPort.loadOwnerId(any())).thenReturn(UUID.randomUUID());

        service.createAssessment(param);

        verify(createSubjectValuePort, times(1)).persistAll(anyList(), any());
        verify(grantUserAssessmentRolePort, times(2)).persist(any(), any(UUID.class), anyInt());
    }

    @Test
    void testCreateAssessment_ValidCommand_PersistsAttributeValue() {
        long assessmentKitId = 1L;
        Long kitVersionId = 123L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            1L,
            "title example",
            "short title",
            assessmentKitId,
            currentUserId
        );
        Attribute qa1 = AttributeMother.simpleAttribute();
        Attribute qa2 = AttributeMother.simpleAttribute();
        Attribute qa3 = AttributeMother.simpleAttribute();
        Attribute qa4 = AttributeMother.simpleAttribute();
        Attribute qa5 = AttributeMother.simpleAttribute();

        List<Subject> expectedSubjects = List.of(
            new Subject(1L, "subject2", 1, List.of(qa1, qa2)),
            new Subject(2L, "subject1", 1, List.of(qa3, qa4)),
            new Subject(3L, "subject3", 1, List.of(qa5))
        );

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), currentUserId)).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitVersionIdPort.loadVersionId(assessmentKitId)).thenReturn(kitVersionId);
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(kitVersionId)).thenReturn(expectedSubjects);
        when(loadSpaceOwnerPort.loadOwnerId(any())).thenReturn(UUID.randomUUID());

        service.createAssessment(param);

        verify(loadSpaceOwnerPort, times(1)).loadOwnerId(any());
        verify(grantUserAssessmentRolePort, times(2)).persist(any(), any(UUID.class), anyInt());
        verify(createAttributeValuePort, times(1)).persistAll(anyList(), any());
    }

    @Test
    void testCreateAssessment_WhenUserDoesNotHaveAccessToSpace_ThenThrowsException() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            1L,
            "title example",
            "short title",
            1L,
            currentUserId
        );

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateAssessment_WhenUserDoesNotHaveAccessToKit_ThenThrowsException() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            1L,
            "title example",
            "short title",
            1L,
            currentUserId
        );

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), currentUserId)).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ValidationException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_KIT_NOT_ALLOWED, throwable.getMessageKey());
    }
}
