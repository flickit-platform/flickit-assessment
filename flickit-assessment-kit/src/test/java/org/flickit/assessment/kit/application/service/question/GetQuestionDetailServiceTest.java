package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionAndKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAllAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactByQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactByQuestionPort.AttributeImpact;
import org.flickit.assessment.kit.test.fixture.application.AnswerOptionImpactMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_DETAIL_QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetQuestionDetailServiceTest {

    @InjectMocks
    private GetQuestionDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAnswerOptionsByQuestionAndKitPort loadAnswerOptionsByQuestionPort;

    @Mock
    private LoadQuestionImpactByQuestionPort loadQuestionImpactByQuestionPort;

    @Mock
    private LoadAllAttributesPort loadAllAttributesPort;

    @Test
    void testGetQuestionDetail_WhenQuestionExist_shouldReturnQuestionDetails() {
        var questionId = 2L;
        var param = new Param(2000L, questionId, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        var answerOption1 = createAnswerOption(questionId, "1st option", 0);
        var answerOption2 = createAnswerOption(questionId, "2nd option", 1);
        var answerOption3 = createAnswerOption(questionId, "3rd option", 2);

        var answerOptions = List.of(
            answerOption1,
            answerOption2,
            answerOption3
        );

        var attribute = AttributeMother.attributeWithTitle("Impacted Attribute");
        var expectedAffectedLevel = new LoadQuestionImpactByQuestionPort.AffectedLevel(
            MaturityLevelMother.levelOne(),
            1,
            List.of(
                AnswerOptionImpactMother.createAnswerOptionImpact(answerOption1.getId(), 0),
                AnswerOptionImpactMother.createAnswerOptionImpact(answerOption2.getId(), 0.5),
                AnswerOptionImpactMother.createAnswerOptionImpact(answerOption3.getId(), 1)
            )
        );
        var attributeImpact = new AttributeImpact(attribute.getId(),
            List.of(
                expectedAffectedLevel
            ));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionIdAndKitId(param.getQuestionId(), param.getKitId())).thenReturn(answerOptions);
        when(loadQuestionImpactByQuestionPort.loadQuestionImpactByQuestionId(param.getQuestionId())).thenReturn(List.of(attributeImpact));
        when(loadAllAttributesPort.loadAllByIds(List.of(attribute.getId()))).thenReturn(List.of(attribute));

        var result = service.getQuestionDetail(param);
        var impact = result.attributeImpacts().get(0);

        assertEquals(answerOptions.size(), result.options().size());
        assertEquals(1, result.attributeImpacts().size());
        assertEquals(attribute.getId(), impact.id());
        assertEquals(attribute.getTitle(), impact.title());

        var affectedLevel = impact.affectedLevels().get(0);
        assertEquals(1, impact.affectedLevels().size());
        assertEquals(expectedAffectedLevel.weight(), affectedLevel.weight());
        assertEquals(expectedAffectedLevel.maturityLevel().getId(), affectedLevel.maturityLevel().id());
        assertEquals(expectedAffectedLevel.maturityLevel().getIndex(), affectedLevel.maturityLevel().index());
        assertEquals(expectedAffectedLevel.maturityLevel().getTitle(), affectedLevel.maturityLevel().title());

    }

    @Test
    void testGetQuestionDetail_WhenKitDoesNotExist_ThrowsException() {
        var param = new Param(2000L, 2L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(
            checkExpertGroupAccessPort,
            loadAnswerOptionsByQuestionPort,
            loadQuestionImpactByQuestionPort,
            loadAllAttributesPort
        );
    }

    @Test
    void testGetQuestionDetail_WhenQuestionDoesNotExist_ThrowsException() {
        var questionId = 2L;
        var param = new Param(2000L, questionId, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionIdAndKitId(param.getQuestionId(), param.getKitId())).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionDetail(param));
        assertEquals(GET_QUESTION_DETAIL_QUESTION_ID_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(
            loadQuestionImpactByQuestionPort,
            loadAllAttributesPort
        );
    }

    @Test
    void testGetQuestionDetail_WhenUserIsNotMember_ThrowsException() {
        var param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.getQuestionDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(
            loadAnswerOptionsByQuestionPort,
            loadQuestionImpactByQuestionPort,
            loadAllAttributesPort
        );
    }
}

