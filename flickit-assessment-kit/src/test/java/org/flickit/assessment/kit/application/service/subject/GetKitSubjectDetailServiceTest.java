package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.subject.GetKitSubjectDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.CountSubjectQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitSubjectDetailServiceTest {

    @InjectMocks
    private GetKitSubjectDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private CountSubjectQuestionsPort countSubjectQuestionsPort;

    @Test
    void testGetKitSubjectDetail_WhenSubjectExist_ShouldReturnSubjectDetails() {
        var param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = createExpertGroup();
        var questionsCount = 14;
        var kitVersionId = 1L;
        var attribute = AttributeMother.attributeWithTitle("attribute");
        var subject = SubjectMother.subjectWithAttributes("subject", List.of(attribute));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        when(loadSubjectPort.load(param.getSubjectId(), kitVersionId)).thenReturn(subject);
        when(countSubjectQuestionsPort.countBySubjectId(param.getSubjectId(), kitVersionId)).thenReturn(questionsCount);

        var result = service.getKitSubjectDetail(param);

        assertEquals(questionsCount, result.questionsCount());
        assertEquals(subject.getDescription(), result.description());
        assertEquals(1, result.attributes().size());

        var resultAttribute = result.attributes().getFirst();
        assertEquals(attribute.getId(), resultAttribute.id());
        assertEquals(attribute.getTitle(), resultAttribute.title());
        assertEquals(attribute.getIndex(), resultAttribute.index());
    }

    @Test
    void testGetKitSubjectDetail_WhenKitDoesNotExist_ThrowsException() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getKitSubjectDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetKitSubjectDetail_WhenUserIsNotMember_ThrowsException() {
        var param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getKitSubjectDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }
}
