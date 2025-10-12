package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMiniMother.createAttributeMini;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitQuestionDetailServiceTest {

    @InjectMocks
    private GetKitQuestionDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Mock
    private LoadAnswerRangePort loadAnswerRangePort;

    @Mock
    private LoadMeasurePort loadMeasurePort;

    @Test
    void testGetKitQuestionDetail_whenQuestionExistsAndAnswerRangeIsReusable_thenReturnQuestionDetails() {
        long kitId = 123L;
        long kitVersionId = 456L;
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var attr1 = createAttributeMini();
        var attr2 = createAttributeMini();
        var maturityLevels = MaturityLevelMother.allLevels();
        var question = QuestionMother.createQuestion();

        var impact1 = createQuestionImpact(attr1.getId(), maturityLevels.get(3).getId(), 1, question.getId());
        var impact2 = createQuestionImpact(attr1.getId(), maturityLevels.get(4).getId(), 1, question.getId());
        var impact3 = createQuestionImpact(attr2.getId(), maturityLevels.get(3).getId(), 3, question.getId());
        var impacts = List.of(impact1, impact2, impact3);

        var answerRange = AnswerRangeMother.createReusableAnswerRangeWithTwoOptions();
        var measure = MeasureMother.measureWithTitle("title");

        var param = new Param(kitId, question.getId(), UUID.randomUUID());

        question.setImpacts(impacts);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(question.getId(), kitVersionId)).thenReturn(question);
        when(loadAttributesPort.loadAllByIdsAndKitVersionId(anyList(), anyLong())).thenReturn(List.of(attr1, attr2));
        when(loadMaturityLevelsPort.loadAllByKitVersionId(kitVersionId)).thenReturn(maturityLevels);
        when(loadActiveKitVersionIdPort.loadKitVersionId(kitId)).thenReturn(kitVersionId);
        when(loadAnswerRangePort.load(question.getAnswerRangeId(), kitVersionId)).thenReturn(answerRange);
        when(loadMeasurePort.load(question.getMeasureId(), kitVersionId)).thenReturn(Optional.of(measure));

        var result = service.getKitQuestionDetail(param);

        ArgumentCaptor<List<Long>> idListCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Long> kitVersionIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(loadAttributesPort).loadAllByIdsAndKitVersionId(idListCaptor.capture(), kitVersionIdCaptor.capture());

        assertThat(idListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(attr1.getId(), attr2.getId()));
        assertEquals(kitVersionId, kitVersionIdCaptor.getValue());
        assertNull(result.options());
        assertEquals(2, result.attributeImpacts().size());
        result.attributeImpacts().forEach(im -> {
            if (attr1.getId() == im.id()) {
                assertEquals(attr1.getId(), im.id());
                assertEquals(attr1.getTitle(), im.title());
                assertEquals(2, im.affectedLevels().size());

                var attr1AffectedLevel1 = im.affectedLevels().getFirst();
                assertEquals(impact1.getAttributeId(), im.id());
                assertEquals(impact1.getMaturityLevelId(), attr1AffectedLevel1.maturityLevel().id());

                var attr1AffectedLevel2 = im.affectedLevels().get(1);
                assertEquals(impact2.getAttributeId(), im.id());
                assertEquals(impact2.getMaturityLevelId(), attr1AffectedLevel2.maturityLevel().id());
            } else if (attr2.getId() == im.id()) {
                var attr2AffectedLevel1 = im.affectedLevels().getFirst();
                assertEquals(impact3.getAttributeId(), im.id());
                assertEquals(impact3.getMaturityLevelId(), attr2AffectedLevel1.maturityLevel().id());
            } else fail();
        });
        assertEquals(answerRange.getId(), result.answerRange().id());
        assertEquals(answerRange.getTitle(), result.answerRange().title());
        assertEquals(measure.getId(), result.measure().id());
        assertEquals(measure.getTitle(), result.measure().title());
        assertEquals(question.getTranslations(), result.translations());
    }

    @Test
    void testGetKitQuestionDetail_whenQuestionExistsAndAnswerRangeIsNotReusable_thenReturnQuestionDetails() {
        long kitId = 123L;
        long kitVersionId = 456L;
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var attr1 = createAttributeMini();
        var attr2 = createAttributeMini();
        var maturityLevels = MaturityLevelMother.allLevels();
        var question = QuestionMother.createQuestion();

        var answerOption1 = createAnswerOption(question.getAnswerRangeId(), "1st option", 0);
        var answerOption2 = createAnswerOption(question.getAnswerRangeId(), "2nd option", 1);
        var answerOption3 = createAnswerOption(question.getAnswerRangeId(), "3rd option", 2);
        var answerOptions = List.of(answerOption1, answerOption2, answerOption3);

        var impact1 = createQuestionImpact(attr1.getId(), maturityLevels.get(3).getId(), 1, question.getId());
        var impact2 = createQuestionImpact(attr1.getId(), maturityLevels.get(4).getId(), 1, question.getId());
        var impact3 = createQuestionImpact(attr2.getId(), maturityLevels.get(3).getId(), 3, question.getId());
        var impacts = List.of(impact1, impact2, impact3);

        var answerRange = AnswerRangeMother.createNonReusableAnswerRangeWithTwoOptions();
        var measure = MeasureMother.measureWithTitle("title");

        var param = new Param(kitId, question.getId(), UUID.randomUUID());

        question.setOptions(answerOptions);
        question.setImpacts(impacts);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadQuestionPort.load(question.getId(), kitVersionId)).thenReturn(question);
        when(loadAttributesPort.loadAllByIdsAndKitVersionId(anyList(), anyLong())).thenReturn(List.of(attr1, attr2));
        when(loadMaturityLevelsPort.loadAllByKitVersionId(kitVersionId)).thenReturn(maturityLevels);
        when(loadActiveKitVersionIdPort.loadKitVersionId(kitId)).thenReturn(kitVersionId);
        when(loadAnswerRangePort.load(question.getAnswerRangeId(), kitVersionId)).thenReturn(answerRange);
        when(loadMeasurePort.load(question.getMeasureId(), kitVersionId)).thenReturn(Optional.of(measure));

        var result = service.getKitQuestionDetail(param);

        ArgumentCaptor<List<Long>> idListCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Long> kitVersionIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(loadAttributesPort).loadAllByIdsAndKitVersionId(idListCaptor.capture(), kitVersionIdCaptor.capture());

        assertThat(idListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(attr1.getId(), attr2.getId()));
        assertEquals(kitVersionId, kitVersionIdCaptor.getValue());
        assertThat(answerOptions)
            .zipSatisfy(result.options(), (expected, actual) -> {
                assertEquals(expected.getIndex(), actual.index());
                assertEquals(expected.getTitle(), actual.title());
                assertEquals(expected.getValue(), actual.value());
                assertEquals(expected.getTranslations(), actual.translations());
            });
        assertEquals(2, result.attributeImpacts().size());
        result.attributeImpacts().forEach(im -> {
            if (attr1.getId() == im.id()) {
                assertEquals(attr1.getId(), im.id());
                assertEquals(attr1.getTitle(), im.title());
                assertEquals(2, im.affectedLevels().size());

                var attr1AffectedLevel1 = im.affectedLevels().getFirst();
                assertEquals(impact1.getAttributeId(), im.id());
                assertEquals(impact1.getMaturityLevelId(), attr1AffectedLevel1.maturityLevel().id());

                var attr1AffectedLevel2 = im.affectedLevels().get(1);
                assertEquals(impact2.getAttributeId(), im.id());
                assertEquals(impact2.getMaturityLevelId(), attr1AffectedLevel2.maturityLevel().id());
            } else if (attr2.getId() == im.id()) {
                var attr2AffectedLevel1 = im.affectedLevels().getFirst();
                assertEquals(impact3.getAttributeId(), im.id());
                assertEquals(impact3.getMaturityLevelId(), attr2AffectedLevel1.maturityLevel().id());
            } else fail();
        });
        assertNull(result.answerRange());
        assertEquals(measure.getId(), result.measure().id());
        assertEquals(measure.getTitle(), result.measure().title());
        assertEquals(question.getTranslations(), result.translations());
    }

    @Test
    void testGetKitQuestionDetail_whenKitDoesNotExist_thenThrowResourceNotFoundException() {
        var param = new Param(2000L, 2L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getKitQuestionDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(
            loadActiveKitVersionIdPort,
            checkExpertGroupAccessPort,
            loadQuestionPort,
            loadAttributesPort,
            loadAnswerRangePort,
            loadMeasurePort
        );
    }

    @Test
    void testGetKitQuestionDetail_whenQuestionDoesNotExist_thenThrowResourceNotFoundException() {
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
            loadAttributesPort,
            loadAnswerRangePort,
            loadMeasurePort
        );
    }

    @Test
    void testGetKitQuestionDetail_whenUserIsNotMember_thenThrowAccessDeniedException() {
        var param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.getKitQuestionDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(
            loadAttributesPort,
            loadQuestionPort,
            loadAnswerRangePort,
            loadMeasurePort
        );
    }
}

