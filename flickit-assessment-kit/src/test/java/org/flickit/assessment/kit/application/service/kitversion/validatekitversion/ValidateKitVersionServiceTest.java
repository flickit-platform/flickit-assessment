package org.flickit.assessment.kit.application.service.kitversion.validatekitversion;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.kitversion.ValidateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.application.service.kitversion.ValidateKitVersionService;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
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
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateKitVersionServiceTest {

    @InjectMocks
    private ValidateKitVersionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadQuestionsPort loadQuestionsPort;

    @Mock
    private LoadAnswerRangesPort loadAnswerRangesPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private LoadAttributePort loadAttributePort;

    @Test
    void testValidateKitVersionService_WhenInvalidKitVersion_ShouldThrowValidationException() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createActiveKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);

        var throwable = assertThrows(ValidationException.class, () -> service.validate(param));
        assertEquals(VALIDATE_KIT_VERSION_STATUS_INVALID, throwable.getMessageKey());
    }

    @Test
    void testValidateKitVersionService_WhenThereIsNotAnyProblem_ShouldReturnIsValid() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadQuestionsPort.loadQuestionsWithoutImpact(param.getKitVersionId())).thenReturn(List.of());
        when(loadQuestionsPort.loadQuestionsWithoutAnswerRange(param.getKitVersionId())).thenReturn(List.of());
        when(loadAnswerRangesPort.loadByKitVersionIdAndWithoutAnswerOptions(param.getKitVersionId())).thenReturn(List.of());
        when(loadSubjectsPort.loadByKitVersionIdAndWithoutAttribute(param.getKitVersionId())).thenReturn(List.of());
        when(loadAttributePort.loadByKitVersionIdAndQuestionsWithoutImpact(param.getKitVersionId())).thenReturn(List.of());

        var result = service.validate(param);
        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void testValidateKitVersionService_WhenThereAreProblems_ShouldReturnIsValid() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadQuestionsPort.loadQuestionsWithoutImpact(param.getKitVersionId())).thenReturn(List.of(mock(Question.class)));
        when(loadQuestionsPort.loadQuestionsWithoutAnswerRange(param.getKitVersionId())).thenReturn(List.of(mock(Question.class)));
        when(loadAnswerRangesPort.loadByKitVersionIdAndWithoutAnswerOptions(param.getKitVersionId())).thenReturn(List.of(mock(AnswerRange.class)));
        when(loadSubjectsPort.loadByKitVersionIdAndWithoutAttribute(param.getKitVersionId())).thenReturn(List.of(mock(Subject.class)));
        when(loadAttributePort.loadByKitVersionIdAndQuestionsWithoutImpact(param.getKitVersionId())).thenReturn(List.of(mock(Attribute.class)));

        var result = service.validate(param);
        assertFalse(result.isValid());
        assertEquals(5, result.errors().size());
        assertTrue(result.errors().contains(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_QUESTION_IMPACT_UNSUPPORTED)));
        assertTrue(result.errors().contains(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_QUESTION_ANSWER_RANGE_UNSUPPORTED)));
        assertTrue(result.errors().contains(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_ANSWER_RANGE_OPTION_UNSUPPORTED)));
        assertTrue(result.errors().contains(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_ATTRIBUTE_SUBJECT_UNSUPPORTED)));
        assertTrue(result.errors().contains(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_ATTRIBUTE_QUESTION_IMPACT_UNSUPPORTED)));
    }

    @Test
    void testValidateKitVersionService_WhenCurrentUserIsNotExpertGroupOwner_ShouldThrowAccessDeniedException() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.validate(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private ValidateKitVersionUseCase.Param createParam(Consumer<ValidateKitVersionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private ValidateKitVersionUseCase.Param.ParamBuilder paramBuilder() {
        return ValidateKitVersionUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }

}
