package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAllAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionImpactMother.createAnswerOptionImpact;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitQuestionDetailServiceTest {

    @InjectMocks
    private GetKitQuestionDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAllAttributesPort loadAllAttributesPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Test
    void testGetKitQuestionDetail_WhenQuestionExist_shouldReturnQuestionDetails() {
        long kitId = 123L;
        long kitVersionId = 456L;
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var attr1 = AttributeMother.attributeWithTitle("attr1");
        var attr2 = AttributeMother.attributeWithTitle("attr2");
        var maturityLevels = MaturityLevelMother.allLevels();
        var question = QuestionMother.createQuestion();

        var answerOption1 = createAnswerOption(question.getId(), "1st option", 0);
        var answerOption2 = createAnswerOption(question.getId(), "2nd option", 1);
        var answerOption3 = createAnswerOption(question.getId(), "3rd option", 2);

        var answerOptions = List.of(
            answerOption1,
            answerOption2,
            answerOption3
        );

        var optionImpacts = List.of(
            createAnswerOptionImpact(answerOption1.getId(), 0),
            createAnswerOptionImpact(answerOption2.getId(), 0.5),
            createAnswerOptionImpact(answerOption3.getId(), 1)
        );
        var impact1 = createQuestionImpact(attr1.getId(), maturityLevels.get(3).getId(), 1, question.getId());
        var impact2 = createQuestionImpact(attr1.getId(), maturityLevels.get(4).getId(), 1, question.getId());
        var impact3 = createQuestionImpact(attr2.getId(), maturityLevels.get(3).getId(), 3, question.getId());

        impact1.setOptionImpacts(optionImpacts);
        impact2.setOptionImpacts(optionImpacts);
        impact3.setOptionImpacts(optionImpacts);

        var impacts = List.of(impact1, impact2, impact3);

        var param = new Param(kitId, question.getId(), UUID.randomUUID());

        question.setOptions(answerOptions);
        question.setImpacts(impacts);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(question.getId(), kitVersionId)).thenReturn(question);
        when(loadAllAttributesPort.loadAllByIdsAndKitVersionId(List.of(attr1.getId(), attr2.getId()), kitVersionId)).thenReturn(List.of(attr1, attr2));
        when(loadMaturityLevelsPort.loadByKitVersionId(kitVersionId)).thenReturn(maturityLevels);
        when(loadActiveKitVersionIdPort.loadKitVersionId(kitId)).thenReturn(kitVersionId);

        var result = service.getKitQuestionDetail(param);

        assertEquals(answerOptions.size(), result.options().size());
        assertEquals(2, result.attributeImpacts().size());

        var attributeImpact1 = result.attributeImpacts().getFirst();
        assertEquals(attr1.getId(), attributeImpact1.id());
        assertEquals(attr1.getTitle(), attributeImpact1.title());
        assertEquals(2, attributeImpact1.affectedLevels().size());

        var attr1AffectedLevel1 = attributeImpact1.affectedLevels().getFirst();
        assertEquals(impact1.getAttributeId(), attributeImpact1.id());
        assertEquals(impact1.getMaturityLevelId(), attr1AffectedLevel1.maturityLevel().id());
        assertEquals(optionImpacts.size(), attr1AffectedLevel1.optionValues().size());

        var attr1AffectedLevel2 = attributeImpact1.affectedLevels().get(1);
        assertEquals(impact2.getAttributeId(), attributeImpact1.id());
        assertEquals(impact2.getMaturityLevelId(), attr1AffectedLevel2.maturityLevel().id());
        assertEquals(optionImpacts.size(), attr1AffectedLevel2.optionValues().size());

        var attributeImpact2 = result.attributeImpacts().get(1);
        var attr2AffectedLevel1 = attributeImpact1.affectedLevels().getFirst();
        assertEquals(impact3.getAttributeId(), attributeImpact2.id());
        assertEquals(impact3.getMaturityLevelId(), attr2AffectedLevel1.maturityLevel().id());
        assertEquals(optionImpacts.size(), attr2AffectedLevel1.optionValues().size());
    }

    @Test
    void testGetKitQuestionDetail_WhenKitDoesNotExist_ThrowsException() {
        var param = new Param(2000L, 2L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getKitQuestionDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(
            loadActiveKitVersionIdPort,
            checkExpertGroupAccessPort,
            loadAllAttributesPort
        );
    }

    @Test
    void testGetKitQuestionDetail_WhenQuestionDoesNotExist_ThrowsException() {
        long kitId = 123L;
        long kitVersionId = 153L;
        long questionId = 2L;
        var param = new Param(kitId, questionId, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        when(loadQuestionPort.load(questionId, kitVersionId)).thenThrow(new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getKitQuestionDetail(param));
        assertEquals(QUESTION_ID_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(
            loadAllAttributesPort
        );
    }

    @Test
    void testGetKitQuestionDetail_WhenUserIsNotMember_ThrowsException() {
        var param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.getKitQuestionDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(
            loadAllAttributesPort
        );
    }
}

