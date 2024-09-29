package org.flickit.assessment.kit.application.service.levelcompetence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.levelcompetence.CreateLevelCompetenceUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionStatusByIdPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_NOT_UPDATING_STATUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLevelCompetenceServiceTest {

    @InjectMocks
    private CreateLevelCompetenceService service;

    @Mock
    private LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;

    @Mock
    private LoadKitVersionStatusByIdPort loadKitVersionStatusByIdPort;

    @Mock
    private CreateLevelCompetencePort createLevelCompetencePort;

    @Test
    void testCreateLevelCompetence_CurrentUserIsNotOwnerOfKitExpertGroup_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            13L,
            15L,
            80,
            UUID.randomUUID());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);

        var exception = assertThrows(AccessDeniedException.class, () -> service.createLevelCompetence(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadKitVersionStatusByIdPort, createLevelCompetencePort);
    }

    @Test
    void testCreateLevelCompetence_KitIsOnActiveStatus_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            13L,
            15L,
            80,
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.ACTIVE);

        var exception = assertThrows(ValidationException.class, () -> service.createLevelCompetence(param));
        assertEquals(KIT_VERSION_NOT_UPDATING_STATUS, exception.getMessageKey());

        verifyNoInteractions(createLevelCompetencePort);
    }

    @Test
    void testCreateLevelCompetence_KitIsOnArchiveStatus_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            13L,
            15L,
            80,
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.ARCHIVE);

        var exception = assertThrows(ValidationException.class, () -> service.createLevelCompetence(param));
        assertEquals(KIT_VERSION_NOT_UPDATING_STATUS, exception.getMessageKey());

        verifyNoInteractions(createLevelCompetencePort);
    }

    @Test
    void testCreateLevelCompetence_ValidParam_CreateLevelCompetence() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        Param param = new Param(12L,
            13L,
            15L,
            80,
            expertGroup.getOwnerId());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.UPDATING);
        when(createLevelCompetencePort.persist(anyLong(), anyLong(), anyInt(), anyLong(), any(UUID.class))).thenReturn(153L);

        service.createLevelCompetence(param);

        var affectedLevelIdParam = ArgumentCaptor.forClass(Long.class);
        var effectiveLevelIdParam = ArgumentCaptor.forClass(Long.class);
        var kitVersionIdParam = ArgumentCaptor.forClass(Long.class);
        var valueParam = ArgumentCaptor.forClass(Integer.class);
        var createdByIdParam = ArgumentCaptor.forClass(UUID.class);

        verify(createLevelCompetencePort, times(1)).persist(affectedLevelIdParam.capture(),
            effectiveLevelIdParam.capture(),
            valueParam.capture(),
            kitVersionIdParam.capture(),
            createdByIdParam.capture());

        assertEquals(param.getAffectedLevelId(), affectedLevelIdParam.getValue());
        assertEquals(param.getEffectiveLevelId(), effectiveLevelIdParam.getValue());
        assertEquals(param.getKitVersionId(), kitVersionIdParam.getValue());
        assertEquals(param.getValue(), valueParam.getValue());
        assertEquals(param.getCurrentUserId(), createdByIdParam.getValue());
    }
}