package org.flickit.assessment.kit.application.service.questionimpact;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionimpact.GetQuestionImpactsUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAllAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.service.question.GetQuestionImpactsService;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionImpactMother.createAnswerOptionImpact;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.attributeWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.allLevels;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestionWithAnswerRangeId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

    private final KitVersion kitVersion = KitVersionMother.createKitVersion(simpleKit());

    @Test
    void testGetQuestionImpacts_kitVersionIdDoesNotExist_throwsResourceNotFoundException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionImpacts(param));
        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(checkExpertGroupAccessPort, loadQuestionPort, loadMaturityLevelsPort, loadAllAttributesPort);
    }

    @Test
    void testGetQuestionImpacts_currentUserIsNotExpertGroupMember_throwsAccessDeniedException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionImpacts(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionPort, loadMaturityLevelsPort, loadAllAttributesPort);
    }

    @Test
    void testGetQuestionImpacts_questionIdNotExist_throwsAccessDeniedException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenThrow(new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionImpacts(param));
        assertEquals(QUESTION_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort, loadAllAttributesPort);
    }

    @Test
    void testGetQuestionImpacts_WhenQuestionIdHasNoAnswerRangeId_ThenThrowValidationException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(createQuestionWithAnswerRangeId(null));

        var throwable = assertThrows(ValidationException.class, () -> service.getQuestionImpacts(param));
        assertEquals(GET_QUESTION_IMPACTS_QUESTION_ANSWER_RANGE_ID_NOT_NULL, throwable.getMessageKey());

        verifyNoInteractions(loadMaturityLevelsPort, loadAllAttributesPort);
    }

    @Test
    void testGetQuestionImpacts_validParameters_loadQuestionImpactsSuccessfully() {
        var attr1 = attributeWithTitle("attr1");
        var attr2 = attributeWithTitle("attr2");
        var maturityLevels = allLevels();
        var question = createQuestion();

        var answerOption1 = createAnswerOption(question.getAnswerRangeId(), "1st option", 0);
        var answerOption2 = createAnswerOption(question.getAnswerRangeId(), "2nd option", 1);
        var answerOption3 = createAnswerOption(question.getAnswerRangeId(), "3rd option", 2);

        var answerOptions = List.of(answerOption1, answerOption2, answerOption3);

        var optionImpacts = List.of(
            createAnswerOptionImpact(answerOption1.getId(), 0),
            createAnswerOptionImpact(answerOption2.getId(), 0.5),
            createAnswerOptionImpact(answerOption3.getId(), 1));

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

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);
        when(loadAllAttributesPort.loadAllByIdsAndKitVersionId(List.of(attr1.getId(), attr2.getId()), param.getKitVersionId())).thenReturn(List.of(attr1, attr2));
        when(loadMaturityLevelsPort.loadAllByKitVersionId(param.getKitVersionId())).thenReturn(maturityLevels);

        var result = service.getQuestionImpacts(param);

        assertEquals(2, result.attributeImpacts().size());

        var attributeImpact1 = result.attributeImpacts().getFirst();
        assertEquals(attr1.getId(), attributeImpact1.attributeId());
        assertEquals(attr1.getTitle(), attributeImpact1.title());
        assertEquals(2, attributeImpact1.impacts().size());

        var attr1AffectedLevel1 = attributeImpact1.impacts().getFirst();
        assertEquals(impact1.getAttributeId(), attributeImpact1.attributeId());
        assertEquals(impact1.getMaturityLevelId(), attr1AffectedLevel1.maturityLevel().maturityLevelId());
        assertEquals(optionImpacts.size(), attr1AffectedLevel1.optionValues().size());

        var attr1AffectedLevel2 = attributeImpact1.impacts().get(1);
        assertEquals(impact2.getAttributeId(), attributeImpact1.attributeId());
        assertEquals(impact2.getMaturityLevelId(), attr1AffectedLevel2.maturityLevel().maturityLevelId());
        assertEquals(optionImpacts.size(), attr1AffectedLevel2.optionValues().size());

        var attributeImpact2 = result.attributeImpacts().get(1);
        var attr2AffectedLevel1 = attributeImpact1.impacts().getFirst();
        assertEquals(impact3.getAttributeId(), attributeImpact2.attributeId());
        assertEquals(impact3.getMaturityLevelId(), attr2AffectedLevel1.maturityLevel().maturityLevelId());
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
