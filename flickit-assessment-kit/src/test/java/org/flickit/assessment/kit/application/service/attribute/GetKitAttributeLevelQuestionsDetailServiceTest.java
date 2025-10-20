package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeLevelQuestionsDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.question.LoadAttributeLevelQuestionsPort;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.ATTRIBUTE_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.MATURITY_LEVEL_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitAttributeLevelQuestionsDetailServiceTest {

    @InjectMocks
    private GetKitAttributeLevelQuestionsDetailService service;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private LoadAttributeLevelQuestionsPort loadAttributeLevelQuestionsPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Test
    void testGetKitAttributeLevelQuestionsDetail_CurrentUserIsNotMemberOfKitExpertGroup_ThrowsException() {
        GetKitAttributeLevelQuestionsDetailUseCase.Param param = new GetKitAttributeLevelQuestionsDetailUseCase.Param(
            1L,
            1L,
            1L,
            UUID.randomUUID());

        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getKitAttributeLevelQuestionsDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetKitAttributeLevelQuestionsDetail_AttributeWithGivenAttributeIdAndKitIdDoesNotExist_ThrowsException() {
        long kitId = 123L;
        long attributeId = 223L;
        long kitVersionId = 345L;
        long maturityLevel = 333L;
        UUID currentUserId = UUID.randomUUID();

        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadAttributeLevelQuestionsPort.loadAttributeLevelQuestions(kitVersionId, attributeId, maturityLevel))
            .thenThrow(new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND));
        when(loadActiveKitVersionIdPort.loadKitVersionId(kitId)).thenReturn(kitVersionId);

        var param = new GetKitAttributeLevelQuestionsDetailUseCase.Param(
            kitId,
            attributeId,
            maturityLevel,
            currentUserId);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getKitAttributeLevelQuestionsDetail(param));
        assertEquals(ATTRIBUTE_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetKitAttributeLevelQuestionsDetail_MaturityLevelWithGivenMaturityLevelIdAndKitIdDoesNotExist_ThrowsException() {
        long kitId = 123L;
        long kitVersionId = 345L;
        long attributeId = 223L;
        long maturityLevel = 333L;
        UUID currentUserId = UUID.randomUUID();

        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadAttributeLevelQuestionsPort.loadAttributeLevelQuestions(kitVersionId, attributeId, maturityLevel))
            .thenThrow(new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND));
        when(loadActiveKitVersionIdPort.loadKitVersionId(kitId)).thenReturn(kitVersionId);

        var param = new GetKitAttributeLevelQuestionsDetailUseCase.Param(
            kitId,
            attributeId,
            maturityLevel,
            currentUserId);
        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getKitAttributeLevelQuestionsDetail(param));
        assertEquals(MATURITY_LEVEL_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetKitAttributeLevelQuestionsDetail_ValidInput_ValidResult() {
        long kitId = 123L;
        long kitVersionId = 223L;
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var attr1 = AttributeMother.attributeWithTitle("attr1");
        var attr2 = AttributeMother.attributeWithTitle("attr2");
        var maturityLevel2 = MaturityLevelMother.levelTwo();
        var maturityLevel3 = MaturityLevelMother.levelThree();
        var question1 = QuestionMother.createQuestionWithOptions();
        var question2 = QuestionMother.createQuestionWithOptions();
        var question3 = QuestionMother.createQuestionWithOptions();
        var measure1 = MeasureMother.measureWithTitle("Measure1");
        var measure2 = MeasureMother.measureWithTitle("Measure2");
        var answerRange1 = AnswerRangeMother.createReusableAnswerRangeWithTwoOptions();
        var answerRange2 = AnswerRangeMother.createNonReusableAnswerRangeWithTwoOptions();

        var impact1 = createQuestionImpact(attr1.getId(), maturityLevel2.getId(), 1, question1.getId());
        var impact2 = createQuestionImpact(attr1.getId(), maturityLevel2.getId(), 1, question2.getId());
        var impact3 = createQuestionImpact(attr2.getId(), maturityLevel3.getId(), 3, question3.getId());

        question1.setImpacts(List.of(impact1));
        question2.setImpacts(List.of(impact2));
        question3.setImpacts(List.of(impact3));

        var param = new GetKitAttributeLevelQuestionsDetailUseCase.Param(
            kitId,
            attr1.getId(),
            maturityLevel2.getId(),
            UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);

        var portResult = List.of(new LoadAttributeLevelQuestionsPort.Result(question1, QuestionnaireMother.questionnaireWithTitle("title1"), measure1, answerRange1),
            new LoadAttributeLevelQuestionsPort.Result(question2, QuestionnaireMother.questionnaireWithTitle("title2"), measure2, answerRange2));

        when(loadAttributeLevelQuestionsPort.loadAttributeLevelQuestions(kitVersionId, attr1.getId(), maturityLevel2.getId()))
            .thenReturn(portResult);
        when(loadActiveKitVersionIdPort.loadKitVersionId(kitId)).thenReturn(kitVersionId);

        var result = service.getKitAttributeLevelQuestionsDetail(param);

        assertEquals(2, result.questionsCount());
        //Assert Question 1
        var resultQuestion1 = result.questions().getFirst();
        assertNull(resultQuestion1.answerOptions());
        assertEquals(resultQuestion1.answerRange().title(), answerRange1.getTitle());
        assertQuestion(result.questions().getFirst(), question1, impact1, "title1");
        //Assert Question 2
        assertQuestion(result.questions().getLast(), question2, impact2, "title2");
        var resultQuestion2 = result.questions().getLast();
        assertNull(resultQuestion2.answerRange());
        var question2AnswerOption = resultQuestion2.answerOptions().getFirst();
        assertNotNull(question2AnswerOption);
        assertEquals(question2.getOptions().size(), resultQuestion2.answerOptions().size());
        assertEquals(question2.getOptions().getFirst().getTitle(), question2AnswerOption.title());
        assertEquals(question2.getOptions().getFirst().getIndex(), question2AnswerOption.index());
        assertEquals(question2.getOptions().getFirst().getValue(), question2AnswerOption.value());
    }

    private static void assertQuestion(GetKitAttributeLevelQuestionsDetailUseCase.Result.Question resultQuestion, Question question, QuestionImpact impact, String title) {
        assertNotNull(resultQuestion);
        assertEquals(question.getId(), resultQuestion.id());
        assertEquals(question.getTitle(), resultQuestion.title());
        assertEquals(question.getIndex(), resultQuestion.index());
        assertTrue(resultQuestion.mayNotBeApplicable());
        assertTrue(resultQuestion.advisable());
        assertEquals(impact.getWeight(), resultQuestion.weight());
        assertEquals(title, resultQuestion.questionnaire());
    }
}
