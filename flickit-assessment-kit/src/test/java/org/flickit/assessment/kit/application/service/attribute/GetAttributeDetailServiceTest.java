package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase.Result;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadAttributeMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.question.LoadAttributeQuestionCountPort;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeDetailServiceTest {

    @InjectMocks
    private GetAttributeDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAttributePort loadAttributePort;

    @Mock
    private LoadAttributeQuestionCountPort loadAttributeQuestionCountPort;

    @Mock
    private LoadAttributeMaturityLevelPort loadAttributeMaturityLevelPort;

    @Test
    void testGetAttributeDetail_WhenAttributeExist_shouldReturnAttributeDetails() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var expectedQuestionCount = 14;
        Attribute expectedAttribute = AttributeMother.attributeWithTitle("EgAttribute");
        List<MaturityLevel> expectedMaturityLevels = List.of(new MaturityLevel(1L, "MaturityLevelEg", 1, 15));
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);

        when(loadAttributePort.loadByIdAndKitId(param.getAttributeId(), param.getKitId()))
            .thenReturn(Optional.of(expectedAttribute));
        when(loadAttributeQuestionCountPort.loadByAttributeId(param.getAttributeId()))
            .thenReturn(expectedQuestionCount);
        when(loadAttributeMaturityLevelPort.loadByAttributeId(param.getAttributeId()))
            .thenReturn(expectedMaturityLevels);

        Result result = service.getAttributeDetail(param);

        assertEquals(expectedAttribute.getId(), result.id());
        assertEquals(expectedAttribute.getIndex(), result.index());
        assertEquals(expectedAttribute.getTitle(), result.title());
        assertEquals(expectedQuestionCount, result.questionCount());
        assertEquals(expectedAttribute.getWeight(), result.weight());
        assertEquals(expectedAttribute.getDescription(), result.description());
        assertIterableEquals(expectedMaturityLevels, result.maturityLevels());
    }

    @Test
    void testGetAttributeDetail_WhenKitDoesNotExist_ThrowsException() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getAttributeDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetAttributeDetail_WhenUserIsNotMember_ThrowsException() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getAttributeDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }
}
