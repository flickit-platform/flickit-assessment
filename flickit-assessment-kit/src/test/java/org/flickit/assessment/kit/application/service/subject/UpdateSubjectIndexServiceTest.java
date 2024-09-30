package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionStatusByIdPort;
import org.flickit.assessment.kit.application.port.out.subject.DecreaseSubjectIndexPort;
import org.flickit.assessment.kit.application.port.out.subject.IncreaseSubjectIndexPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectIndexPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_NOT_UPDATING_STATUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubjectIndexServiceTest {

    @InjectMocks
    private UpdateSubjectIndexService service;

    @Mock
    private LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;

    @Mock
    private LoadKitVersionStatusByIdPort loadKitVersionStatusByIdPort;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private UpdateSubjectIndexPort updateSubjectIndexPort;

    @Mock
    private IncreaseSubjectIndexPort increaseSubjectIndexPort;

    @Mock
    private DecreaseSubjectIndexPort decreaseSubjectIndexPort;

    @Test
    void testUpdateSubjectIndex_CurrentUserIsNotOwnerOfKitExpertGroup_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            13L,
            3,
            UUID.randomUUID());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateSubjectIndex(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadKitVersionStatusByIdPort,
            loadSubjectPort,
            updateSubjectIndexPort,
            increaseSubjectIndexPort,
            decreaseSubjectIndexPort);
    }

    @Test
    void testUpdateSubjectIndex_KitIsOnActiveStatus_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            13L,
            3,
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.ACTIVE);

        var exception = assertThrows(ValidationException.class, () -> service.updateSubjectIndex(param));
        assertEquals(KIT_VERSION_NOT_UPDATING_STATUS, exception.getMessageKey());

        verifyNoInteractions(loadSubjectPort,
            updateSubjectIndexPort,
            increaseSubjectIndexPort,
            decreaseSubjectIndexPort);
    }

    @Test
    void testUpdateSubjectIndex_KitIsOnArchiveStatus_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            13L,
            3,
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.ARCHIVE);

        var exception = assertThrows(ValidationException.class, () -> service.updateSubjectIndex(param));
        assertEquals(KIT_VERSION_NOT_UPDATING_STATUS, exception.getMessageKey());

        verifyNoInteractions(loadSubjectPort,
            updateSubjectIndexPort,
            increaseSubjectIndexPort,
            decreaseSubjectIndexPort);
    }

    @Test
    void testUpdateSubjectIndex_IndexIsSame_DoNothing() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var subject = SubjectMother.subjectWithTitle("subject");
        Param param = new Param(12L,
            13L,
            subject.getIndex(),
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.UPDATING);
        when(loadSubjectPort.load(param.getSubjectId(), param.getKitVersionId())).thenReturn(subject);

        service.updateSubjectIndex(param);

        verifyNoInteractions(updateSubjectIndexPort,
            increaseSubjectIndexPort,
            decreaseSubjectIndexPort);
    }

    @Test
    void testUpdateSubjectIndex_IndexIncreased_UpdateSubjectAndDecreaseRestSubjectsIndexes() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var subject = SubjectMother.subjectWithTitle("subject");
        Param param = new Param(12L,
            13L,
            subject.getIndex() + 5,
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.UPDATING);
        when(loadSubjectPort.load(param.getSubjectId(), param.getKitVersionId())).thenReturn(subject);
        doNothing().when(updateSubjectIndexPort).updateIndex(param.getKitVersionId(), param.getSubjectId(), -1);
        doNothing().when(decreaseSubjectIndexPort)
            .decreaseSubjectsIndexes(param.getKitVersionId(), subject.getIndex() + 1, param.getIndex() + 1);
        doNothing().when(updateSubjectIndexPort).updateIndex(param.getKitVersionId(), param.getSubjectId(), param.getIndex());

        service.updateSubjectIndex(param);

        verifyNoInteractions(increaseSubjectIndexPort);
    }

    @Test
    void testUpdateSubjectIndex_IndexDecreased_UpdateSubjectAndIncreaseRestSubjectsIndexes() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var subject = SubjectMother.subjectWithTitle("subject");
        Param param = new Param(12L,
            13L,
            subject.getIndex() - 1,
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.UPDATING);
        when(loadSubjectPort.load(param.getSubjectId(), param.getKitVersionId())).thenReturn(subject);
        doNothing().when(updateSubjectIndexPort).updateIndex(param.getKitVersionId(), param.getSubjectId(), -1);
        doNothing().when(increaseSubjectIndexPort)
            .increaseSubjectsIndexes(param.getKitVersionId(), param.getIndex(), subject.getIndex());
        doNothing().when(updateSubjectIndexPort).updateIndex(param.getKitVersionId(), param.getSubjectId(), param.getIndex());

        service.updateSubjectIndex(param);

        verifyNoInteractions(decreaseSubjectIndexPort);
    }
}