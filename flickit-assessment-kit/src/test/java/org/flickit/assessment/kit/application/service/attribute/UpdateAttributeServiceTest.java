package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.util.SlugCodeUtil;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitByVersionIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionStatusByIdPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionModificationInfoPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_NOT_UPDATING_STATUS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAttributeServiceTest {

    @InjectMocks
    private UpdateAttributeService service;

    @Mock
    private LoadAssessmentKitByVersionIdPort loadAssessmentKitByVersionIdPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadKitVersionStatusByIdPort loadKitVersionStatusByIdPort;

    @Mock
    private UpdateAttributePort updateAttributePort;

    @Mock
    private UpdateKitVersionModificationInfoPort updateKitVersionModificationInfoPort;

    @Test
    void testUpdateAttribute_CurrentUserIsNotOwnerOfKitExpertGroup_ThrowsException() {
        Param param = new Param(13L, 12L,
            10, "Attribute",
            "simple description",
            20, 14L,
            UUID.randomUUID());
        var expertGroupOwnerId = UUID.randomUUID();

        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        when(loadAssessmentKitByVersionIdPort.loadByVersionId(param.getKitVersionId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(expertGroupOwnerId);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateAttribute(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadKitVersionStatusByIdPort, updateAttributePort, updateKitVersionModificationInfoPort);
    }

    @Test
    void testUpdateAttribute_KitIsOnActiveStatus_ThrowsException() {
        Param param = new Param(13L, 12L,
            10, "Attribute",
            "simple description",
            20, 14L,
            UUID.randomUUID());

        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        when(loadAssessmentKitByVersionIdPort.loadByVersionId(param.getKitVersionId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.ACTIVE);

        var exception = assertThrows(ValidationException.class, () -> service.updateAttribute(param));
        assertEquals(KIT_VERSION_NOT_UPDATING_STATUS, exception.getMessageKey());

        verifyNoInteractions(updateAttributePort, updateKitVersionModificationInfoPort);
    }

    @Test
    void testUpdateAttribute_KitIsOnArchiveStatus_ThrowsException() {
        Param param = new Param(13L, 12L,
            10, "Attribute",
            "simple description",
            20, 14L,
            UUID.randomUUID());

        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        when(loadAssessmentKitByVersionIdPort.loadByVersionId(param.getKitVersionId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.ARCHIVE);

        var exception = assertThrows(ValidationException.class, () -> service.updateAttribute(param));
        assertEquals(KIT_VERSION_NOT_UPDATING_STATUS, exception.getMessageKey());

        verifyNoInteractions(updateAttributePort, updateKitVersionModificationInfoPort);
    }

    @Test
    void testUpdateAttribute_ValidParam_UpdateAttributeAndKitVersion() {
        Param param = new Param(13L, 12L,
            10, "Attribute",
            "simple description",
            20, 14L,
            UUID.randomUUID());

        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        when(loadAssessmentKitByVersionIdPort.loadByVersionId(param.getKitVersionId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId())).thenReturn(KitVersionStatus.UPDATING);
        doNothing().when(updateAttributePort).update(any());
        doNothing().when(updateKitVersionModificationInfoPort).updateModificationInfo(eq(param.getKitVersionId()), any(), eq(param.getCurrentUserId()));
        service.updateAttribute(param);

        var attributeUpdateParam = ArgumentCaptor.forClass(UpdateAttributePort.Param.class);
        verify(updateAttributePort, times(1)).update(attributeUpdateParam.capture());

        assertEquals(param.getKitVersionId(), attributeUpdateParam.getValue().kitVersionId());
        assertEquals(param.getAttributeId(), attributeUpdateParam.getValue().id());
        assertEquals(SlugCodeUtil.generateSlugCode(param.getTitle()), attributeUpdateParam.getValue().code());
        assertEquals(param.getTitle(), attributeUpdateParam.getValue().title());
        assertEquals(param.getDescription(), attributeUpdateParam.getValue().description());
        assertEquals(param.getSubjectId(), attributeUpdateParam.getValue().subjectId());
        assertEquals(param.getIndex(), attributeUpdateParam.getValue().index());
        assertEquals(param.getWeight(), attributeUpdateParam.getValue().weight());
        assertEquals(param.getCurrentUserId(), attributeUpdateParam.getValue().lastModifiedBy());
        assertNotNull(attributeUpdateParam.getValue().lastModificationTime());

        verify(updateKitVersionModificationInfoPort, times(1)).updateModificationInfo(eq(param.getKitVersionId()), any(), eq(param.getCurrentUserId()));
    }
}