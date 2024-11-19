package org.flickit.assessment.kit.application.service.kitversion.validatekitversion;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.port.in.kitversion.ValidateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.application.service.kitversion.ValidateKitVersionService;
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
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
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
    private LoadAttributesPort loadAttributesPort;

    @Test
    void testValidateKitVersion_whenKitVersionIsInvalid_shouldThrowValidationException() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createActiveKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());

        var throwable = assertThrows(ValidationException.class, () -> service.validate(param));
        assertEquals(VALIDATE_KIT_VERSION_STATUS_INVALID, throwable.getMessageKey());
    }

    @Test
    void testValidateKitVersion_WhenCurrentUserIsNotExpertGroupOwner_ShouldThrowAccessDeniedException() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.validate(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testValidateKitVersion_whenKitVersionIsValid_ShouldReturnIsValid() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadQuestionsPort.loadQuestionsWithoutImpact(param.getKitVersionId())).thenReturn(List.of());
        when(loadQuestionsPort.loadQuestionsWithoutAnswerRange(param.getKitVersionId())).thenReturn(List.of());
        when(loadAnswerRangesPort.loadAnswerRangesWithoutAnswerOptions(param.getKitVersionId())).thenReturn(List.of());
        when(loadSubjectsPort.loadByKitVersionIdWithoutAttribute(param.getKitVersionId())).thenReturn(List.of());
        when(loadAttributesPort.loadUnImpactedAttributes(param.getKitVersionId())).thenReturn(List.of());

        var result = service.validate(param);
        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void testValidateKitVersion_WhenProblemsExist_ShouldReturnIsInvalid() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createKitVersion(assessmentKit);
        var loadQuestionsPortResult = List.of(new LoadQuestionsPort.Result(1, 100L, "Q100Title"),
            new LoadQuestionsPort.Result(2, 200L, "Q100Title"));
        var listOfAnswerRanges = List.of(AnswerRangeMother.createReusableAnswerRangeWithTwoOptions());
        var listOfSubjects = List.of(SubjectMother.subjectWithTitle("Title1"), SubjectMother.subjectWithTitle("Title2"));
        var listOfAttributes = List.of(AttributeMother.attributeWithTitle("Title1"), AttributeMother.attributeWithTitle("Title2"));
        List<String> expectedErrors = List.of(
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_IMPACT_NOT_NULL, loadQuestionsPortResult.getFirst().questionIndex(), loadQuestionsPortResult.getFirst().questionnaireTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_IMPACT_NOT_NULL, loadQuestionsPortResult.getLast().questionIndex(), loadQuestionsPortResult.getLast().questionnaireTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_ANSWER_RANGE_NOT_NULL, loadQuestionsPortResult.getFirst().questionIndex(), loadQuestionsPortResult.getFirst().questionnaireTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_ANSWER_RANGE_NOT_NULL, loadQuestionsPortResult.getLast().questionIndex(), loadQuestionsPortResult.getLast().questionnaireTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_ANSWER_RANGE_ANSWER_OPTION_NOT_NULL, listOfAnswerRanges.getFirst().getTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_SUBJECT_ATTRIBUTE_NOT_NULL, listOfSubjects.getFirst().getTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_SUBJECT_ATTRIBUTE_NOT_NULL, listOfSubjects.getLast().getTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_ATTRIBUTE_QUESTION_IMPACT_NOT_NULL, listOfAttributes.getFirst().getTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_ATTRIBUTE_QUESTION_IMPACT_NOT_NULL, listOfAttributes.getLast().getTitle())
        );

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadQuestionsPort.loadQuestionsWithoutImpact(param.getKitVersionId())).thenReturn(loadQuestionsPortResult);
        when(loadQuestionsPort.loadQuestionsWithoutAnswerRange(param.getKitVersionId())).thenReturn(loadQuestionsPortResult);
        when(loadAnswerRangesPort.loadAnswerRangesWithoutAnswerOptions(param.getKitVersionId())).thenReturn(listOfAnswerRanges);
        when(loadSubjectsPort.loadByKitVersionIdWithoutAttribute(param.getKitVersionId())).thenReturn(listOfSubjects);
        when(loadAttributesPort.loadUnImpactedAttributes(param.getKitVersionId())).thenReturn(listOfAttributes);

        var result = service.validate(param);
        assertFalse(result.isValid());
        assertEquals(9, result.errors().size());
        assertTrue(result.errors().containsAll(expectedErrors));
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
