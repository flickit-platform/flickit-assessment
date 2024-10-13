package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.SubjectParam;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectsIndexPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
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
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateSubjectsIndexPort updateSubjectsIndexPort;

    @Test
    void testUpdateSubjectsOrder_CurrentUserIsNotOwnerOfKitExpertGroup_ThrowsException() {
        Param param = new Param(12L,
            List.of(new SubjectParam(5L, 2), new SubjectParam(6L, 1)),
            UUID.randomUUID());

        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateSubjectsOrder(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(updateSubjectsIndexPort);
    }

    @Test
    void testUpdateSubjectsOrder_ValidParam_UpdateSubjectsIndex() {
        Param param = new Param(12L,
            List.of(new SubjectParam(5L, 2), new SubjectParam(6L, 1)),
            UUID.randomUUID());

        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());

        service.updateSubjectsOrder(param);
        verify(updateSubjectsIndexPort, times(1)).updateIndexes(param.getKitVersionId(), param.getSubjects());
    }
}