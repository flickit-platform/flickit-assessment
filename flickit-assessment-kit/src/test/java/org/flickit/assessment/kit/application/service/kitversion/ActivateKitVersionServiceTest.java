package org.flickit.assessment.kit.application.service.kitversion;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitActiveVersionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionStatusPort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateKitVersionServiceTest {

    @InjectMocks
    private ActivateKitVersionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateKitVersionStatusPort updateKitVersionStatusPort;

    @Mock
    private LoadSubjectQuestionnairePort loadSubjectQuestionnairePort;

    @Mock
    private UpdateKitActiveVersionPort updateKitActiveVersionPort;

    @Mock
    private CreateSubjectQuestionnairePort createSubjectQuestionnairePort;

    List<SubjectQuestionnaire> subjectQuestionnaireList = List.of(
        new SubjectQuestionnaire(null, 11L, 123L),
        new SubjectQuestionnaire(null, 21L, 123L),
        new SubjectQuestionnaire(null, 31L, 456L)
    );

    @Test
    void testActivateKitVersion_userHasNotAccess_ThrowsException() {
        Param param = new Param(12L, UUID.randomUUID());
        UUID expertGroupOwner = UUID.randomUUID();
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        KitVersion kitVersion = KitVersionMother.createKitVersion(kit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId()))
            .thenReturn(expertGroupOwner);

        var exception = assertThrows(AccessDeniedException.class, () -> service.activateKitVersion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(updateKitVersionStatusPort, updateKitActiveVersionPort, createSubjectQuestionnairePort);
    }

    @Test
    void testActivateKitVersion_ActiveVersionExist_ArchiveOldVersion() {
        Param param = new Param(12L, UUID.randomUUID());
        UUID expertGroupOwner = param.getCurrentUserId();
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        KitVersion kitVersion = KitVersionMother.createKitVersion(kit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId()))
            .thenReturn(expertGroupOwner);
        doNothing().when(updateKitVersionStatusPort)
            .updateStatus(kit.getActiveVersionId(), KitVersionStatus.ARCHIVE);
        doNothing().when(updateKitVersionStatusPort)
            .updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        doNothing().when(updateKitActiveVersionPort)
            .updateActiveVersion(kit.getId(), param.getKitVersionId());
        when(loadSubjectQuestionnairePort.extractPairs(param.getKitVersionId()))
            .thenReturn(subjectQuestionnaireList);

        service.activateKitVersion(param);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<Long, Set<Long>>> captor = ArgumentCaptor.forClass(Map.class);

        verify(updateKitVersionStatusPort, times(1))
            .updateStatus(kit.getActiveVersionId(), KitVersionStatus.ARCHIVE);
        verify(updateKitVersionStatusPort, times(1))
            .updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        verify(updateKitActiveVersionPort, times(1))
            .updateActiveVersion(kit.getId(), param.getKitVersionId());
        verify(createSubjectQuestionnairePort, times(1))
            .persistAll(captor.capture(), anyLong());

        assertEquals(2, captor.getValue().size());
        assertEquals(2, captor.getValue().get(123L).size());
        assertEquals(1, captor.getValue().get(456L).size());

    }

    @Test
    void testActivateKitVersion_ThereIsNoActiveVersion_ActivateNewKitVersion() {
        Param param = new Param(12L, UUID.randomUUID());
        UUID expertGroupOwner = param.getCurrentUserId();
        AssessmentKit kit = AssessmentKitMother.kitWithKitVersionId(null);
        KitVersion kitVersion = KitVersionMother.createKitVersion(kit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId()))
            .thenReturn(expertGroupOwner);
        doNothing().when(updateKitVersionStatusPort)
            .updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        doNothing().when(updateKitActiveVersionPort)
            .updateActiveVersion(kit.getId(), param.getKitVersionId());
        when(loadSubjectQuestionnairePort.extractPairs(param.getKitVersionId()))
            .thenReturn(subjectQuestionnaireList);

        service.activateKitVersion(param);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<Long, Set<Long>>> captor = ArgumentCaptor.forClass(Map.class);

        verify(updateKitVersionStatusPort, times(1))
            .updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        verify(updateKitActiveVersionPort, times(1))
            .updateActiveVersion(kit.getId(), param.getKitVersionId());
        verify(createSubjectQuestionnairePort, times(1))
            .persistAll(captor.capture(), anyLong());

        assertEquals(2, captor.getValue().size());
        assertEquals(2, captor.getValue().get(123L).size());
        assertEquals(1, captor.getValue().get(456L).size());
    }
}
