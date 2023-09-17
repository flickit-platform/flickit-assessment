package org.flickit.flickitassessmentcore.application.service.subject;

import org.flickit.flickitassessmentcore.adapter.out.rest.question.ImpactfulQuestionDto;
import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentResultMother;
import org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.application.domain.mother.QualityAttributeValueMother;
import org.flickit.flickitassessmentcore.application.domain.mother.SubjectValueMother;
import org.flickit.flickitassessmentcore.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CountAnswersByQuestionIdsPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsBySubjectPort;
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
    void GetSubjectProgress_ValidResult() {
        var impactfulQuestionsList = List.of(
            new ImpactfulQuestionDto(1L),
            new ImpactfulQuestionDto(2L),
            new ImpactfulQuestionDto(3L)
        );
        var questionIds = impactfulQuestionsList.stream()
            .map(ImpactfulQuestionDto::id)
            .toList();
        var qav = QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1);
        var subjectValue = SubjectValueMother.withQAValues(List.of(qav));
        var result = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(
            List.of(subjectValue), MaturityLevelMother.levelTwo());

        when(loadQuestionsBySubjectPort.loadImpactfulQuestionsBySubjectId(subjectValue.getSubject().getId())).
            thenReturn(impactfulQuestionsList);
        when(loadAssessmentResultPort.loadByAssessmentId(result.getAssessment().getId())).thenReturn(Optional.of(result));
        when(countAnswersByQuestionIdsPort.countByQuestionIds(
            result.getId(), questionIds)).thenReturn(2);

        var subjectProgress = service.getSubjectProgress(new GetSubjectProgressUseCase.Param(
            result.getAssessment().getId(), subjectValue.getSubject().getId()));

        assertEquals(2, subjectProgress.answerCount());
        assertEquals(3, subjectProgress.questionCount());
    }
}
