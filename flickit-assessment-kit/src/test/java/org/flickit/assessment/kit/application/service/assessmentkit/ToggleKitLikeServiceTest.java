package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.ToggleKitLikeUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitlike.CheckKitLikeExistencePort;
import org.flickit.assessment.kit.application.port.out.kitlike.CountKitLikePort;
import org.flickit.assessment.kit.application.port.out.kitlike.CreateKitLikePort;
import org.flickit.assessment.kit.application.port.out.kitlike.DeleteKitLikePort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleKitLikeServiceTest {

    @InjectMocks
    private ToggleKitLikeService service;

    @Mock
    private LoadAssessmentKitPort loadKitPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private CheckKitLikeExistencePort checkKitLikeExistencePort;

    @Mock
    private CreateKitLikePort createKitLikePort;

    @Mock
    private DeleteKitLikePort deleteKitLikePort;

    @Mock
    private CountKitLikePort countKitLikePort;

    @Test
    void testToggleKitLike_WhenKitDoesNotExist_ThrowsException() {
        ToggleKitLikeUseCase.Param param = new ToggleKitLikeUseCase.Param(12L, UUID.randomUUID());

        when(loadKitPort.load(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.toggleKitLike(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(loadKitExpertGroupPort,
            checkExpertGroupAccessPort,
            checkKitLikeExistencePort,
            createKitLikePort,
            deleteKitLikePort);
    }

    @Test
    void testToggleKitLike_WhenKitIsPrivateAndUserIsNotMember_ThrowsException() {
        var privateKit = AssessmentKitMother.privateKit();
        var expectedExpertGroup = ExpertGroupMother.createExpertGroup();
        ToggleKitLikeUseCase.Param param = new ToggleKitLikeUseCase.Param(privateKit.getId(), UUID.randomUUID());

        when(loadKitPort.load(param.getKitId())).thenReturn(privateKit);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expectedExpertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expectedExpertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.toggleKitLike(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(checkKitLikeExistencePort,
            createKitLikePort,
            deleteKitLikePort);
    }

    @Test
    void testToggleKitLike_WhenKitLikeDoesNotExist_AddLike() {
        var privateKit = AssessmentKitMother.privateKit();
        var expectedExpertGroup = ExpertGroupMother.createExpertGroup();
        ToggleKitLikeUseCase.Param param = new ToggleKitLikeUseCase.Param(privateKit.getId(), UUID.randomUUID());

        when(loadKitPort.load(param.getKitId())).thenReturn(privateKit);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expectedExpertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expectedExpertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitLikeExistencePort.exist(param.getKitId(), param.getCurrentUserId())).thenReturn(false);
        doNothing().when(createKitLikePort).create(param.getKitId(), param.getCurrentUserId());
        when(countKitLikePort.countByKitId(param.getKitId())).thenReturn(1);

        ToggleKitLikeUseCase.Result result = service.toggleKitLike(param);
        assertEquals(1, result.likes());

        verifyNoInteractions(deleteKitLikePort);
    }

    @Test
    void testToggleKitLike_WhenKitLikeExist_DeleteLike() {
        var privateKit = AssessmentKitMother.privateKit();
        var expectedExpertGroup = ExpertGroupMother.createExpertGroup();
        ToggleKitLikeUseCase.Param param = new ToggleKitLikeUseCase.Param(privateKit.getId(), UUID.randomUUID());

        when(loadKitPort.load(param.getKitId())).thenReturn(privateKit);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expectedExpertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expectedExpertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitLikeExistencePort.exist(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        doNothing().when(deleteKitLikePort).delete(param.getKitId(), param.getCurrentUserId());
        when(countKitLikePort.countByKitId(param.getKitId())).thenReturn(0);

        ToggleKitLikeUseCase.Result result = service.toggleKitLike(param);
        assertEquals(0, result.likes());

        verifyNoInteractions(createKitLikePort);
    }
}
