package org.flickit.flickitassessmentcore.application.service.subject;

import org.flickit.flickitassessmentcore.application.domain.Question;
import org.flickit.flickitassessmentcore.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CountAnswersByQuestionIdsPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.flickitassessmentcore.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSubjectProgressServiceTest {

    @InjectMocks
    private GetSubjectProgressService service;

    @Mock
    private LoadQuestionsBySubjectPort loadQuestionsBySubjectPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CountAnswersByQuestionIdsPort countAnswersByQuestionIdsPort;

    @Test
    void testGetSubjectProgress_ValidResult() {
        var questions = List.of(
            QuestionMother.withNoImpact(),
            QuestionMother.withNoImpact(),
            QuestionMother.withNoImpact()
        );
        var questionIds = questions.stream()
            .map(Question::getId)
            .toList();
        var qav = QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1);
        var subjectValue = SubjectValueMother.withQAValues(List.of(qav));
        var result = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(
            List.of(subjectValue), MaturityLevelMother.levelTwo());

        when(loadQuestionsBySubjectPort.loadQuestionsBySubject(subjectValue.getSubject().getId())).
            thenReturn(questions);
        when(loadAssessmentResultPort.loadByAssessmentId(result.getAssessment().getId())).thenReturn(Optional.of(result));
        when(countAnswersByQuestionIdsPort.countByQuestionIds(
            result.getId(), questionIds)).thenReturn(2);

        var subjectProgress = service.getSubjectProgress(new GetSubjectProgressUseCase.Param(
            result.getAssessment().getId(), subjectValue.getSubject().getId()));

        assertEquals(2, subjectProgress.answerCount());
    }
}
