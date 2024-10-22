package org.flickit.assessment.kit.application.service.kitversion;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitActiveVersionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionStatusPort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
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
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithKitVersionId;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
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

    private final UUID ownerId = UUID.randomUUID();
    private KitVersion kitVersion = createKitVersion(simpleKit());
    List<SubjectQuestionnaire> subjectQuestionnaireList = List.of(
        new SubjectQuestionnaire(null, 11L, 123L),
        new SubjectQuestionnaire(null, 21L, 123L),
        new SubjectQuestionnaire(null, 31L, 456L)
    );

    @Test
    void testActivateKitVersion_userHasNotAccess_ThrowsAccessDeniedException() {
        var param = createParam(ActivateKitVersionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var exception = assertThrows(AccessDeniedException.class, () -> service.activateKitVersion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(updateKitVersionStatusPort, updateKitActiveVersionPort, createSubjectQuestionnairePort);
    }

    @Test
    void testActivateKitVersion_ActiveVersionExists_ArchiveOldVersion() {
        Param param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateKitVersionStatusPort).updateStatus(kitVersion.getKit().getActiveVersionId(), KitVersionStatus.ARCHIVE);
        doNothing().when(updateKitVersionStatusPort).updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        doNothing().when(updateKitActiveVersionPort).updateActiveVersion(kitVersion.getKit().getId(), param.getKitVersionId());
        when(loadSubjectQuestionnairePort.extractPairs(param.getKitVersionId())).thenReturn(subjectQuestionnaireList);

        service.activateKitVersion(param);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<Long, Set<Long>>> captor = ArgumentCaptor.forClass(Map.class);
        verify(createSubjectQuestionnairePort, times(1)).persistAll(captor.capture(), eq(param.getKitVersionId()));

        assertEquals(2, captor.getValue().size());
        assertNotNull(captor.getValue().get(123L));
        assertEquals(2, captor.getValue().get(123L).size());
        assertEquals(Set.of(11L, 21L), captor.getValue().get(123L));
        assertNotNull(captor.getValue().get(456L));
        assertEquals(1, captor.getValue().get(456L).size());
        assertEquals(Set.of(31L), captor.getValue().get(456L));
    }

    @Test
    void testActivateKitVersion_ThereIsNoActiveVersion_ActivateNewKitVersion() {
        var kit = kitWithKitVersionId(null);
        kitVersion = createKitVersion(kit);
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateKitVersionStatusPort).updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        doNothing().when(updateKitActiveVersionPort).updateActiveVersion(kit.getId(), param.getKitVersionId());
        when(loadSubjectQuestionnairePort.extractPairs(param.getKitVersionId())).thenReturn(subjectQuestionnaireList);

        service.activateKitVersion(param);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<Long, Set<Long>>> captor = ArgumentCaptor.forClass(Map.class);
        verify(createSubjectQuestionnairePort, times(1)).persistAll(captor.capture(), eq(param.getKitVersionId()));

        assertEquals(2, captor.getValue().size());
        assertNotNull(captor.getValue().get(123L));
        assertEquals(2, captor.getValue().get(123L).size());
        assertEquals(Set.of(11L, 21L), captor.getValue().get(123L));
        assertNotNull(captor.getValue().get(456L));
        assertEquals(1, captor.getValue().get(456L).size());
        assertEquals(Set.of(31L), captor.getValue().get(456L));
    }

    private ActivateKitVersionUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(kitVersion.getId())
            .currentUserId(UUID.randomUUID());
    }
}
