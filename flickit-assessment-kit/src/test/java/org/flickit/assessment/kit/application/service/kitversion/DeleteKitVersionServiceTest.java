package org.flickit.assessment.kit.application.service.kitversion;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.kitversion.DeleteKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.DeleteKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_VERSION_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createActiveKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteKitVersionServiceTest {

    @InjectMocks
    private DeleteKitVersionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private DeleteKitVersionPort deleteKitVersionPort;

    private final UUID ownerId = UUID.randomUUID();

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testDeleteKitVersion_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(DeleteKitVersionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteKitVersion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteKitVersionPort);
    }

    @Test
    void testDeleteKitVersion_WhenCurrentUserIsExpertGroupOwnerAndKitVersionStatusIsNotUpdating_ThenThrowValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        KitVersion activeKitVersion = createActiveKitVersion(simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(activeKitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(activeKitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(ValidationException.class, () -> service.deleteKitVersion(param));
        assertEquals(DELETE_KIT_VERSION_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(deleteKitVersionPort);
    }

    @Test
    void testDeleteKitVersion_WhenCurrentUserIsExpertGroupOwnerAndKitVersionStatusIsUpdating_ThenDeleteKitVersion() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(deleteKitVersionPort).delete(param.getKitVersionId());

        service.deleteKitVersion(param);

        verify(deleteKitVersionPort).delete(param.getKitVersionId());
    }

    private DeleteKitVersionUseCase.Param createParam(Consumer<DeleteKitVersionUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private DeleteKitVersionUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteKitVersionUseCase.Param.builder()
                .kitVersionId(kitVersion.getId())
                .currentUserId(UUID.randomUUID());
    }
}
