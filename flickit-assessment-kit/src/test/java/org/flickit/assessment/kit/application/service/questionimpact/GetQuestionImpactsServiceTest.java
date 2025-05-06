package org.flickit.assessment.kit.application.service.questionimpact;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionimpact.GetQuestionImpactsUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.service.question.GetQuestionImpactsService;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.attributeWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.allLevels;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
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
    private LoadAttributesPort loadAttributesPort;

    private final KitVersion kitVersion = KitVersionMother.createKitVersion(simpleKit());

    @Test
    void testGetQuestionImpacts_currentUserIsNotExpertGroupMember_throwsAccessDeniedException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionImpacts(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionPort, loadMaturityLevelsPort, loadAttributesPort);
    }

    @Test
    void testGetQuestionImpacts_questionIdNotExist_throwsAccessDeniedException() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenThrow(new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionImpacts(param));
        assertEquals(QUESTION_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort, loadAttributesPort);
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


        var impact1 = createQuestionImpact(attr1.getId(), maturityLevels.get(3).getId(), 1, question.getId());
        var impact2 = createQuestionImpact(attr1.getId(), maturityLevels.get(4).getId(), 1, question.getId());
        var impact3 = createQuestionImpact(attr2.getId(), maturityLevels.get(3).getId(), 3, question.getId());

        var impacts = List.of(impact1, impact2, impact3);

        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        question.setOptions(answerOptions);
        question.setImpacts(impacts);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);
        when(loadAttributesPort.loadAllByIdsAndKitVersionId(anyList(), anyLong())).thenReturn(List.of(attr1, attr2));
        when(loadMaturityLevelsPort.loadAllByKitVersionId(param.getKitVersionId())).thenReturn(maturityLevels);

        var result = service.getQuestionImpacts(param);
        var attributeIdsArgument = ArgumentCaptor.forClass(List.class);
        verify(loadAttributesPort, times(1)).loadAllByIdsAndKitVersionId(attributeIdsArgument.capture(), eq(param.getKitVersionId()));

        assertTrue(attributeIdsArgument.getValue().containsAll(List.of(attr1.getId(), attr2.getId())));

        assertEquals(2, result.attributeImpacts().size());

        var attributeImpact1 = result.attributeImpacts().getFirst();
        assertEquals(attr1.getId(), attributeImpact1.attributeId());
        assertEquals(attr1.getTitle(), attributeImpact1.title());
        assertEquals(2, attributeImpact1.impacts().size());

        var attr1AffectedLevel1 = attributeImpact1.impacts().getFirst();
        assertEquals(impact1.getAttributeId(), attributeImpact1.attributeId());
        assertEquals(impact1.getMaturityLevelId(), attr1AffectedLevel1.maturityLevel().maturityLevelId());

        var attr1AffectedLevel2 = attributeImpact1.impacts().get(1);
        assertEquals(impact2.getAttributeId(), attributeImpact1.attributeId());
        assertEquals(impact2.getMaturityLevelId(), attr1AffectedLevel2.maturityLevel().maturityLevelId());

        var attributeImpact2 = result.attributeImpacts().get(1);
        var attr2AffectedLevel1 = attributeImpact1.impacts().getFirst();
        assertEquals(impact3.getAttributeId(), attributeImpact2.attributeId());
        assertEquals(impact3.getMaturityLevelId(), attr2AffectedLevel1.maturityLevel().maturityLevelId());
    }

    @Test
    void testGetQuestionImpacts_WhenQuestionNoImpacts_ThenReturnEmptyResult() {
        var param = createParam(GetQuestionImpactsUseCase.Param.ParamBuilder::build);

        var question = createQuestion();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);

        var result = service.getQuestionImpacts(param);
        assertEquals(0, result.attributeImpacts().size());

        verifyNoInteractions(loadMaturityLevelsPort, loadAttributesPort);
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
