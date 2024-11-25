package org.flickit.assessment.kit.application.service.kitversion;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.kitversion.CountKitVersionStatsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother.createReusableAnswerRangeWithTwoOptions;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.attributeWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KitVersionValidatorTest {

    @InjectMocks
    private KitVersionValidator validator;

    @Mock
    private LoadQuestionsPort loadQuestionsPort;

    @Mock
    private LoadAnswerRangesPort loadAnswerRangesPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Mock
    private CountKitVersionStatsPort countKitVersionStatsPort;

    @Mock
    private LoadQuestionnairesPort loadQuestionnairesPort;

    @Test
    void testValidate() {
        var kitKitVersionId = 123L;

        var loadQuestionsPortResult = List.of(new LoadQuestionsPort.Result(1, 100L, "Q100Title"),
            new LoadQuestionsPort.Result(2, 200L, "Q100Title"));
        var listOfAnswerRanges = List.of(createReusableAnswerRangeWithTwoOptions());
        var listOfSubjects = List.of(subjectWithTitle("Title1"), subjectWithTitle("Title2"));
        var listOfAttributes = List.of(attributeWithTitle("Title1"), attributeWithTitle("Title2"));
        var listOfQuestionnaire = List.of(questionnaireWithTitle("Title1"), questionnaireWithTitle("Title2"));

        List<String> expectedErrors = List.of(
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_IMPACT_NOT_NULL, loadQuestionsPortResult.getFirst().questionIndex(), loadQuestionsPortResult.getFirst().questionnaireTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_IMPACT_NOT_NULL, loadQuestionsPortResult.getLast().questionIndex(), loadQuestionsPortResult.getLast().questionnaireTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_ANSWER_RANGE_NOT_NULL, loadQuestionsPortResult.getFirst().questionIndex(), loadQuestionsPortResult.getFirst().questionnaireTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_ANSWER_RANGE_NOT_NULL, loadQuestionsPortResult.getLast().questionIndex(), loadQuestionsPortResult.getLast().questionnaireTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_ANSWER_RANGE_LOW_OPTIONS, listOfAnswerRanges.getFirst().getTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_SUBJECT_ATTRIBUTE_NOT_NULL, listOfSubjects.getFirst().getTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_SUBJECT_ATTRIBUTE_NOT_NULL, listOfSubjects.getLast().getTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTIONNAIRE_QUESTION_NOT_NULL, listOfAttributes.getLast().getTitle()),
            MessageBundle.message(VALIDATE_KIT_VERSION_SUBJECT_NOT_NULL),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_NOT_NULL),
            MessageBundle.message(VALIDATE_KIT_VERSION_QUESTIONNAIRE_NOT_NULL),
            MessageBundle.message(VALIDATE_KIT_VERSION_MATURITY_LEVEL_NOT_NULL)
        );

        when(loadQuestionsPort.loadQuestionsWithoutImpact(kitKitVersionId)).thenReturn(loadQuestionsPortResult);
        when(loadQuestionsPort.loadQuestionsWithoutAnswerRange(kitKitVersionId)).thenReturn(loadQuestionsPortResult);
        when(loadAnswerRangesPort.loadAnswerRangesWithNotEnoughOptions(kitKitVersionId)).thenReturn(listOfAnswerRanges);
        when(loadSubjectsPort.loadSubjectsWithoutAttribute(kitKitVersionId)).thenReturn(listOfSubjects);
        when(loadAttributesPort.loadUnimpactedAttributes(kitKitVersionId)).thenReturn(listOfAttributes);
        when(countKitVersionStatsPort.countKitVersionStats(kitKitVersionId)).thenReturn(new CountKitVersionStatsPort.Result(0, 0, 0, 0));
        when(loadQuestionnairesPort.loadQuestionnairesWithoutQuestion(kitKitVersionId)).thenReturn(listOfQuestionnaire);

        var result = validator.validate(kitKitVersionId);
        assertEquals(15, result.size());
        assertThat(result).containsAll(expectedErrors);
    }
}
