package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMaturityLevelOrdersServiceTest {

    @InjectMocks
    private UpdateMaturityLevelOrdersService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateMaturityLevelPort updateMaturityLevelPort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateMaturityLevelOrdersService_kitVersionIdNotFound_NotFoundException() {
        UpdateMaturityLevelOrdersUseCase.Param param = createParam(UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.changeOrders(param));
        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());
        verifyNoInteractions(loadExpertGroupOwnerPort, updateMaturityLevelPort);
    }

    @Test
    void testUpdateMaturityLevelOrdersService_currentUserIsNotOwner_AccessDeniedException() {
        UpdateMaturityLevelOrdersUseCase.Param param = createParam(UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.changeOrders(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verifyNoInteractions(updateMaturityLevelPort);
    }

    @Test
    void testUpdateMaturityLevelOrdersService_validParameters_SuccessfulUpdate() {
        UpdateMaturityLevelOrdersUseCase.Param param = createParam(UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());

        assertDoesNotThrow(() -> service.changeOrders(param));
    }

    private UpdateMaturityLevelOrdersUseCase.Param createParam(Consumer<UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
        return paramBuilder.build();
    }

    private UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateMaturityLevelOrdersUseCase.Param.builder()
            .kitVersionId(1L)
            .orders(List.of())
            .currentUserId(UUID.randomUUID());
    }
}
