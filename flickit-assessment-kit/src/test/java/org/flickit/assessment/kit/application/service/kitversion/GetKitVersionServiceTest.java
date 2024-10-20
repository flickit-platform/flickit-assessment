package org.flickit.assessment.kit.application.service.kitversion;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.kitversion.GetKitVersionUseCase;
import org.flickit.assessment.kit.application.port.in.kitversion.GetKitVersionUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitVersionServiceTest {

    @InjectMocks
    private GetKitVersionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Test
    void testGetKitVersionService_kitVersionIdNotExist_ShouldReturnResourceNotFoundException() {
        Param param = createParam(GetKitVersionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, ()-> service.getKitVersion(param));

        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadKitExpertGroupPort, checkExpertGroupAccessPort);
    }

    @Test
    void testGetKitVersionService_kitIdNotExist_ShouldReturnResourceNotFoundException() {
        Param param = createParam(GetKitVersionUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadKitExpertGroupPort.loadKitExpertGroup(kitVersion.getKit().getId())).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, ()-> service.getKitVersion(param));

        assertEquals(KIT_ID_NOT_FOUND, throwable.getMessage());
        verifyNoInteractions(checkExpertGroupAccessPort);
    }

    @Test
    void testGetKitVersionService_currentUserDoesNotHaveExpertGroupAccess_ShouldReturnAccessDeniedException() {
        Param param = createParam(GetKitVersionUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadKitExpertGroupPort.loadKitExpertGroup(kitVersion.getKit().getId())).thenReturn(expertGroup);

        var throwable = assertThrows(AccessDeniedException.class, ()-> service.getKitVersion(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetKitVersionService_validParameters_ShouldReturnKitVersionId() {
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = createParam(paramBuilder -> paramBuilder.currentUserId(expertGroup.getOwnerId()));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadKitExpertGroupPort.loadKitExpertGroup(kitVersion.getKit().getId())).thenReturn(expertGroup);

        var throwable = assertThrows(AccessDeniedException.class, ()-> service.getKitVersion(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return GetKitVersionUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
