package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.SubjectOrderParam;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectsIndexPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
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

        verifyNoInteractions(updateSubjectsIndexPort);
    }

    @Test
    void testUpdateSubjectsOrder_ValidParam_UpdateSubjectsIndex() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            List.of(new SubjectOrderParam(5L, 2), new SubjectOrderParam(6L, 1)),
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        doNothing().when(updateSubjectsIndexPort).updateIndexes(param.getKitVersionId(), param.getSubjectOrders());

        service.updateSubjectsOrder(param);
    }
}