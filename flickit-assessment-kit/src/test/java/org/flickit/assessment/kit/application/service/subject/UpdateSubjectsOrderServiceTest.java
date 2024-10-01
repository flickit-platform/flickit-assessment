package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.SubjectOrderParam;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectsIndexPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.application.domain.KitVersionStatus.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_NOT_UPDATING_STATUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubjectsOrderServiceTest {

    @InjectMocks
    private UpdateSubjectsOrderService service;

    @Mock
    private LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private UpdateSubjectsIndexPort updateSubjectsIndexPort;

    @Test
    void testUpdateSubjectsOrder_CurrentUserIsNotOwnerOfKitExpertGroup_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            List.of(new SubjectOrderParam(5L, 2), new SubjectOrderParam(6L, 1)),
            UUID.randomUUID());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateSubjectsOrder(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadKitVersionPort, updateSubjectsIndexPort);
    }

    @Test
    void testUpdateSubjectsOrder_KitIsOnActiveStatus_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            List.of(new SubjectOrderParam(5L, 2), new SubjectOrderParam(6L, 1)),
            expertGroup.getOwnerId());
        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit(), ACTIVE);

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);

        var exception = assertThrows(ValidationException.class, () -> service.updateSubjectsOrder(param));
        assertEquals(KIT_VERSION_NOT_UPDATING_STATUS, exception.getMessageKey());

        verifyNoInteractions(updateSubjectsIndexPort);
    }

    @Test
    void testUpdateSubjectsOrder_KitIsOnArchiveStatus_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            List.of(new SubjectOrderParam(5L, 2), new SubjectOrderParam(6L, 1)),
            expertGroup.getOwnerId());
        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit(), ARCHIVE);

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);

        var exception = assertThrows(ValidationException.class, () -> service.updateSubjectsOrder(param));
        assertEquals(KIT_VERSION_NOT_UPDATING_STATUS, exception.getMessageKey());

        verifyNoInteractions(updateSubjectsIndexPort);
    }

    @Test
    void testUpdateSubjectsOrder_ValidParam_UpdateSubjectsIndex() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            List.of(new SubjectOrderParam(5L, 2), new SubjectOrderParam(6L, 1)),
            expertGroup.getOwnerId());
        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit(), UPDATING);

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        doNothing().when(updateSubjectsIndexPort).updateIndexes(param.getKitVersionId(), param.getSubjectOrders());

        service.updateSubjectsOrder(param);
    }
}