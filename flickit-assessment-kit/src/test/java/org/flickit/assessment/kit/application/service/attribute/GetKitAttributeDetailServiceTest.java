package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeDetailUseCase.Result;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.CountAttributeImpactfulQuestionsPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadAttributeMaturityLevelsPort;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitAttributeDetailServiceTest {

    @InjectMocks
    private GetKitAttributeDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAttributePort loadAttributePort;

    @Mock
    private CountAttributeImpactfulQuestionsPort countAttributeImpactfulQuestionsPort;

    @Mock
    private LoadAttributeMaturityLevelsPort loadAttributeMaturityLevelsPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Test
    void testGetKitAttributeDetail_WhenAttributeExist_shouldReturnAttributeDetails() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var expectedQuestionCount = 14;
        var kitVersionId = 10L;
        Attribute expectedAttribute = AttributeMother.attributeWithTitle("EgAttribute");
        List<LoadAttributeMaturityLevelsPort.Result> expectedMaturityLevels =
            List.of(new LoadAttributeMaturityLevelsPort.Result(1L, "MaturityLevelEg", 1, 15));
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);

        when(loadAttributePort.load(param.getAttributeId(), kitVersionId))
            .thenReturn(expectedAttribute);
        when(countAttributeImpactfulQuestionsPort.countQuestions(param.getAttributeId(), kitVersionId))
            .thenReturn(expectedQuestionCount);
        when(loadAttributeMaturityLevelsPort.loadAttributeLevels(param.getAttributeId(), kitVersionId))
            .thenReturn(expectedMaturityLevels);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);

        Result result = service.getKitAttributeDetail(param);

        assertEquals(expectedAttribute.getId(), result.id());
        assertEquals(expectedAttribute.getIndex(), result.index());
        assertEquals(expectedAttribute.getTitle(), result.title());
        assertEquals(expectedQuestionCount, result.questionCount());
        assertEquals(expectedAttribute.getWeight(), result.weight());
        assertEquals(expectedAttribute.getDescription(), result.description());
        assertEquals(expectedMaturityLevels.size(), result.maturityLevels().size());
    }

    @Test
    void testGetKitAttributeDetail_WhenKitDoesNotExist_ThrowsException() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getKitAttributeDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetKitAttributeDetail_WhenUserIsNotMember_ThrowsException() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getKitAttributeDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }
}
