package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CloneKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CloneKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.CheckKitVersionExistencePort;
import org.flickit.assessment.kit.application.port.out.kitversion.CreateKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CLONE_KIT_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithKitVersionId;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloneKitServiceTest {

    @InjectMocks
    private CloneKitService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private CreateKitVersionPort createKitVersionPort;

    @Mock
    private CheckKitVersionExistencePort checkKitVersionExistencePort;

    @Mock
    private CloneKitPort cloneKitPort;

    private final AssessmentKit kit = simpleKit();
    private final UUID ownerId = UUID.randomUUID();

    @Test
    void testCloneKit_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(CloneKitUseCase.Param.ParamBuilder::build);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.cloneKitUseCase(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(createKitVersionPort, checkKitVersionExistencePort);
    }

    @Test
    void testCloneKit_WhenCurrentUserIsExpertGroupOwnerAndKitDoesNotHaveAnyActiveVersion_ThenThrowValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        var assessmentKit = kitWithKitVersionId(null);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(ValidationException.class, () -> service.cloneKitUseCase(param));
        assertEquals(CLONE_KIT_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(createKitVersionPort, checkKitVersionExistencePort);
    }

    @Test
    void testCloneKit_WhenCurrentUserIsExpertGroupOwnerAndKitAlreadyHasAnUpdatingVersion_ThenThrowValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(checkKitVersionExistencePort.exists(kit.getId(), KitVersionStatus.UPDATING)).thenReturn(true);

        var throwable = assertThrows(ValidationException.class, () -> service.cloneKitUseCase(param));
        assertEquals(CLONE_KIT_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(createKitVersionPort);
    }

    @Test
    void testCloneKit_WhenCurrentUserIsExpertGroupOwnerAndKitDoesNotHaveUpdatingKitVersion_ThenCloneKit() {
        var updatingKitVersion = KitVersionMother.createKitVersion(kit);
        var param = createParam(b -> b.currentUserId(ownerId));

        var createKitVersionPortParam = new CreateKitVersionPort.Param(param.getKitId(), KitVersionStatus.UPDATING,
            param.getCurrentUserId());

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(checkKitVersionExistencePort.exists(kit.getId(), KitVersionStatus.UPDATING)).thenReturn(false);
        when(createKitVersionPort.persist(createKitVersionPortParam)).thenReturn(updatingKitVersion.getId());

        doNothing().when(cloneKitPort).cloneKit(any(CloneKitPort.Param.class));

        long result = service.cloneKitUseCase(param);
        assertEquals(updatingKitVersion.getId(), result);

        ArgumentCaptor<CloneKitPort.Param> clonePortCaptor = ArgumentCaptor.forClass(CloneKitPort.Param.class);
        verify(cloneKitPort).cloneKit(clonePortCaptor.capture());
        assertEquals(kit.getActiveVersionId(), clonePortCaptor.getValue().activeKitVersionId());
        assertEquals(updatingKitVersion.getId(), clonePortCaptor.getValue().updatingKitVersionId());
        assertEquals(param.getCurrentUserId(), clonePortCaptor.getValue().clonedBy());
        assertNotNull(clonePortCaptor.getValue().cloneTime());
    }

    private CloneKitUseCase.Param createParam(Consumer<CloneKitUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private CloneKitUseCase.Param.ParamBuilder paramBuilder() {
        return CloneKitUseCase.Param.builder()
            .kitId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
