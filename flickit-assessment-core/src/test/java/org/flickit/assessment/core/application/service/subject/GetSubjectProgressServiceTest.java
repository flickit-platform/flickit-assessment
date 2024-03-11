package org.flickit.assessment.core.application.service.subject;

import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.assessment.core.application.port.out.answer.CountAnswersByQuestionIdsPort;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSubjectProgressServiceTest {

    @InjectMocks
    private GetSubjectProgressService service;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Mock
    private LoadQuestionsBySubjectPort loadQuestionsBySubjectPort;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CountAnswersByQuestionIdsPort countAnswersByQuestionIdsPort;

    @Test
    void testGetSubjectProgress_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
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

        when(checkUserAssessmentAccessPort.hasAccess(result.getAssessment().getId(), currentUserId)).thenReturn(true);
        when(loadQuestionsBySubjectPort.loadQuestionsBySubject(subjectValue.getSubject().getId())).
            thenReturn(questions);
        when(loadSubjectPort.loadByIdAndKitVersionId(subjectValue.getSubject().getId(), result.getKitVersionId())).
            thenReturn(Optional.of(subjectValue.getSubject()));
        when(loadAssessmentResultPort.loadByAssessmentId(result.getAssessment().getId())).thenReturn(Optional.of(result));
        when(countAnswersByQuestionIdsPort.countByQuestionIds(
            result.getId(), questionIds)).thenReturn(2);

        var subjectProgress = service.getSubjectProgress(new GetSubjectProgressUseCase.Param(
            result.getAssessment().getId(), subjectValue.getSubject().getId(), currentUserId));

        assertFalse(subjectProgress.title().isBlank());
        assertEquals(2, subjectProgress.answerCount());
        assertEquals(3, subjectProgress.questionCount());
    }
}
