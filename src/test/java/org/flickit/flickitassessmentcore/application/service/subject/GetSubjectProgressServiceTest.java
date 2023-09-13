package org.flickit.flickitassessmentcore.application.service.subject;

import org.flickit.flickitassessmentcore.adapter.out.rest.question.ImpactfulQuestionDto;
import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentResultMother;
import org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.application.domain.mother.QualityAttributeValueMother;
import org.flickit.flickitassessmentcore.application.domain.mother.SubjectValueMother;
import org.flickit.flickitassessmentcore.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CountAnswersByQuestionAndAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadImpactfulQuestionsBySubjectPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSubjectProgressServiceTest {

    @InjectMocks
    private GetSubjectProgressService service;

    @Mock
    private LoadImpactfulQuestionsBySubjectPort loadImpactfulQuestionsBySubjectPort;

    @Mock
    private LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessmentPort;

    @Mock
    private CountAnswersByQuestionAndAssessmentResultPort countAnswersByQuestionAndAssessmentResultPort;

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

        when(loadImpactfulQuestionsBySubjectPort.loadImpactfulQuestionsBySubjectId(subjectValue.getSubject().getId())).
            thenReturn(impactfulQuestionsList);
        when(loadAssessmentResultByAssessmentPort.loadByAssessmentId(result.getAssessment().getId())).thenReturn(result);
        when(countAnswersByQuestionAndAssessmentResultPort.countAnswersByQuestionIdAndAssessmentResult(
            questionIds, result.getId())).thenReturn(2);

        var subjectProgress = service.getSubjectProgress(new GetSubjectProgressUseCase.Param(
            result.getAssessment().getId(), subjectValue.getSubject().getId()));

        assertEquals(2, subjectProgress.answerCount());
        assertEquals(3, subjectProgress.questionCount());
    }
}
