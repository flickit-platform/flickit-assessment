package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase.AttributeParam;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase.Param;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAttributeOrdersServiceTest {

    @InjectMocks
    private UpdateAttributeOrdersService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateAttributePort updateAttributePort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateAttributeOrders_CurrentUserIsNotOwnerOfKitExpertGroup_ThrowsException() {
        Param param = createParam(UpdateAttributeOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateAttributeOrders(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(updateAttributePort);
    }

    @Test
    void testUpdateAttributeOrders_ValidParam_UpdateAttributesIndex() {
        Param param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        service.updateAttributeOrders(param);
        ArgumentCaptor<UpdateAttributePort.UpdateOrderParam> portParamCaptor = ArgumentCaptor.forClass(UpdateAttributePort.UpdateOrderParam.class);
        verify(updateAttributePort, times(1)).updateOrders(portParamCaptor.capture());

        assertEquals(param.getKitVersionId(), portParamCaptor.getValue().kitVersionId());
        assertEquals(param.getCurrentUserId(), portParamCaptor.getValue().lastModifiedBy());
        assertNotNull(portParamCaptor.getValue().lastModificationTime());
        assertNotNull(portParamCaptor.getValue().orders());
        assertEquals(param.getAttributes().size(), portParamCaptor.getValue().orders().size());
        assertEquals(param.getAttributes().getFirst().getId(), portParamCaptor.getValue().orders().getFirst().attributeId());
        assertEquals(param.getAttributes().getFirst().getIndex(), portParamCaptor.getValue().orders().getFirst().index());
        assertEquals(param.getAttributes().getLast().getId(), portParamCaptor.getValue().orders().getLast().attributeId());
        assertEquals(param.getAttributes().getLast().getIndex(), portParamCaptor.getValue().orders().getLast().index());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .attributes(List.of(new AttributeParam(2L, 5), new AttributeParam(3L, 6)))
            .subjectId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
