package org.flickit.assessment.kit.application.service.questionimpact;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.questionimpact.GetQuestionImpactsUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAllAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.service.question.GetQuestionImpactsService;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionImpactMother.createAnswerOptionImpact;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetQuestionImpactsServiceTest {

    @InjectMocks
    GetQuestionImpactsService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private LoadAllAttributesPort loadAllAttributesPort;

    @Test
    void testGetQuestionImpactsService_kitVersionIdDoesNotExist_throwsResourceNotFoundException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionImpacts(param));

        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());

        verify(loadKitVersionPort, times(1)).load(param.getKitVersionId());
        verifyNoInteractions(checkExpertGroupAccessPort, loadQuestionPort, loadMaturityLevelsPort, loadAllAttributesPort);
    }

    @Test
    void testGetQuestionImpactsService_currentUserIsNotExpertGroupMember_throwsAccessDeniedException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(KitVersionMother.createKitVersion(assessmentKit));
        when(checkExpertGroupAccessPort.checkIsMember(assessmentKit.getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionImpacts(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadKitVersionPort, times(1)).load(param.getKitVersionId());
        verify(checkExpertGroupAccessPort, times(1)).checkIsMember(assessmentKit.getExpertGroupId(), param.getCurrentUserId());
        verifyNoInteractions(loadQuestionPort, loadMaturityLevelsPort, loadAllAttributesPort);
    }

    @Test
    void testGetQuestionImpactsService_questionIdNotExist_throwsAccessDeniedException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(KitVersionMother.createKitVersion(assessmentKit));
        when(checkExpertGroupAccessPort.checkIsMember(assessmentKit.getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenThrow(new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionImpacts(param));

        assertEquals(QUESTION_ID_NOT_FOUND, throwable.getMessage());

        verify(loadKitVersionPort, times(1)).load(param.getKitVersionId());
        verify(checkExpertGroupAccessPort, times(1)).checkIsMember(assessmentKit.getExpertGroupId(), param.getCurrentUserId());
        verifyNoInteractions(loadMaturityLevelsPort, loadAllAttributesPort);
    }

    @Test
    void testGetQuestionImpactsService_validParameters_loadQuestionImpactsSuccessfully() {
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

        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        question.setOptions(answerOptions);
        question.setImpacts(impacts);
        var assessmentKit = AssessmentKitMother.simpleKit();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(KitVersionMother.createKitVersion(assessmentKit));
        when(checkExpertGroupAccessPort.checkIsMember(assessmentKit.getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);
        when(loadAllAttributesPort.loadAllByIdsAndKitVersionId(List.of(attr1.getId(), attr2.getId()), param.getKitVersionId())).thenReturn(List.of(attr1, attr2));
        when(loadMaturityLevelsPort.loadAllByKitVersionId(param.getKitVersionId())).thenReturn(maturityLevels);

        var result = service.getQuestionImpacts(param);

        assertEquals(2, result.attributes().size());

        var attributeImpact1 = result.attributes().getFirst();
        assertEquals(attr1.getId(), attributeImpact1.id());
        assertEquals(attr1.getTitle(), attributeImpact1.title());
        assertEquals(2, attributeImpact1.impacts().size());

        var attr1AffectedLevel1 = attributeImpact1.impacts().getFirst();
        assertEquals(impact1.getAttributeId(), attributeImpact1.id());
        assertEquals(impact1.getMaturityLevelId(), attr1AffectedLevel1.maturityLevel().id());
        assertEquals(optionImpacts.size(), attr1AffectedLevel1.optionValues().size());

        var attr1AffectedLevel2 = attributeImpact1.impacts().get(1);
        assertEquals(impact2.getAttributeId(), attributeImpact1.id());
        assertEquals(impact2.getMaturityLevelId(), attr1AffectedLevel2.maturityLevel().id());
        assertEquals(optionImpacts.size(), attr1AffectedLevel2.optionValues().size());

        var attributeImpact2 = result.attributes().get(1);
        var attr2AffectedLevel1 = attributeImpact1.impacts().getFirst();
        assertEquals(impact3.getAttributeId(), attributeImpact2.id());
        assertEquals(impact3.getMaturityLevelId(), attr2AffectedLevel1.maturityLevel().id());
        assertEquals(optionImpacts.size(), attr2AffectedLevel1.optionValues().size());
    }

    private GetQuestionImpactsUseCase.Param createParam(Consumer<GetQuestionImpactsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetQuestionImpactsUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionImpactsUseCase.Param.builder()
            .questionId(1L)
            .kitVersionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
