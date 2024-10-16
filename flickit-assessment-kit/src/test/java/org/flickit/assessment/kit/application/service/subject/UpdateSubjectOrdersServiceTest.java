package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectOrdersUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectOrdersUseCase.SubjectParam;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
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
class UpdateSubjectOrdersServiceTest {

    @InjectMocks
    private UpdateSubjectOrdersService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateSubjectPort updateSubjectPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateSubjectOrders_CurrentUserIsNotOwnerOfKitExpertGroup_ThrowsException() {
        Param param = createParam(UpdateSubjectOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateSubjectOrders(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(updateSubjectPort);
    }

    @Test
    void testUpdateSubjectOrders_ValidParam_UpdateSubjectOrders() {
        Param param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        service.updateSubjectOrders(param);
        ArgumentCaptor<UpdateSubjectPort.UpdateOrderParam> portParamCaptor = ArgumentCaptor.forClass(UpdateSubjectPort.UpdateOrderParam.class);
        verify(updateSubjectPort, times(1)).updateOrders(portParamCaptor.capture());

        assertEquals(param.getKitVersionId(), portParamCaptor.getValue().kitVersionId());
        assertEquals(param.getCurrentUserId(), portParamCaptor.getValue().lastModifiedBy());
        assertNotNull(portParamCaptor.getValue().lastModificationTime());
        assertNotNull(portParamCaptor.getValue().orders());
        assertEquals(param.getSubjects().size(), portParamCaptor.getValue().orders().size());
        assertEquals(param.getSubjects().getFirst().getId(), portParamCaptor.getValue().orders().getFirst().subjectId());
        assertEquals(param.getSubjects().getFirst().getIndex(), portParamCaptor.getValue().orders().getFirst().index());
        assertEquals(param.getSubjects().getLast().getId(), portParamCaptor.getValue().orders().getLast().subjectId());
        assertEquals(param.getSubjects().getLast().getIndex(), portParamCaptor.getValue().orders().getLast().index());
    }

    private UpdateSubjectOrdersUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .subjects(List.of(new SubjectParam(2L, 5), new SubjectParam(3L, 6)))
            .currentUserId(UUID.randomUUID());
    }
}
