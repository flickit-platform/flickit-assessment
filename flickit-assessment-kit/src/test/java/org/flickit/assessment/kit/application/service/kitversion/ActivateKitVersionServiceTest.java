package org.flickit.assessment.kit.application.service.kitversion;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitActiveVersionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionStatusPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateKitVersionServiceTest {

    @InjectMocks
    private ActivateKitVersionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateKitVersionStatusPort updateKitVersionStatusPort;

    @Mock
    private UpdateKitActiveVersionPort updateKitActiveVersionPort;

    @Test
    void testActivateKitVersion_userHasNotAccess_ThrowsException() {
        Param param = new Param(12L, UUID.randomUUID());
        UUID expertGroupOwner = UUID.randomUUID();
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        KitVersion kitVersion = KitVersionMother.createKitVersion(kit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId()))
            .thenReturn(expertGroupOwner);

        var exception = assertThrows(AccessDeniedException.class, () -> service.activateKitVersion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(updateKitVersionStatusPort, updateKitActiveVersionPort);
    }

    @Test
    void testActivateKitVersion_ActiveVersionExist_ArchiveOldVersion() {
        Param param = new Param(12L, UUID.randomUUID());
        UUID expertGroupOwner = param.getCurrentUserId();
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        KitVersion kitVersion = KitVersionMother.createKitVersion(kit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId()))
            .thenReturn(expertGroupOwner);
        doNothing().when(updateKitVersionStatusPort)
            .updateStatus(kit.getKitVersionId(), KitVersionStatus.ARCHIVE);
        doNothing().when(updateKitVersionStatusPort)
            .updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        doNothing().when(updateKitActiveVersionPort)
            .updateActiveVersion(kit.getId(), param.getKitVersionId());

        service.activateKitVersion(param);

        verify(updateKitVersionStatusPort, times(1))
            .updateStatus(kit.getKitVersionId(), KitVersionStatus.ARCHIVE);
        verify(updateKitVersionStatusPort, times(1))
            .updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        verify(updateKitActiveVersionPort, times(1))
            .updateActiveVersion(kit.getId(), param.getKitVersionId());
    }

    @Test
    void testActivateKitVersion_ThereIsNoActiveVersion_ActivateNewKitVersion() {
        Param param = new Param(12L, UUID.randomUUID());
        UUID expertGroupOwner = param.getCurrentUserId();
        AssessmentKit kit = AssessmentKitMother.kitWithKitVersionId(null);
        KitVersion kitVersion = KitVersionMother.createKitVersion(kit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId()))
            .thenReturn(expertGroupOwner);
        doNothing().when(updateKitVersionStatusPort)
            .updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        doNothing().when(updateKitActiveVersionPort)
            .updateActiveVersion(kit.getId(), param.getKitVersionId());

        service.activateKitVersion(param);

        verify(updateKitVersionStatusPort, times(1))
            .updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        verify(updateKitActiveVersionPort, times(1))
            .updateActiveVersion(kit.getId(), param.getKitVersionId());
    }
}