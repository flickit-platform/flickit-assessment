package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.AttributeParam;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.Param;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributesIndexPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
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
class UpdateAttributesOrderServiceTest {

    @InjectMocks
    private UpdateAttributesOrderService service;

    @Mock
    private LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;

    @Mock
    private UpdateAttributesIndexPort updateAttributesIndexPort;

    @Test
    void testUpdateAttributesOrder_CurrentUserIsNotOwnerOfKitExpertGroup_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            List.of(new AttributeParam(5L, 2), new AttributeParam(6L, 1)),
            UUID.randomUUID());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateAttributesOrder(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(updateAttributesIndexPort);
    }

    @Test
    void testUpdateAttributesOrder_ValidParam_UpdateAttributesIndex() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            List.of(new AttributeParam(5L, 2), new AttributeParam(6L, 1)),
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        doNothing().when(updateAttributesIndexPort).updateIndexes(param.getKitVersionId(), param.getAttributes());

        service.updateAttributesOrder(param);
        verify(updateAttributesIndexPort, times(1)).updateIndexes(param.getKitVersionId(), param.getAttributes());
    }
}