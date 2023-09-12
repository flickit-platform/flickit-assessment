package org.flickit.flickitassessmentcore.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionDto;
import org.flickit.flickitassessmentcore.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CountAnswersByQuestionAndAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadImpactfulQuestionsBySubjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectProgressService implements GetSubjectProgressUseCase {

    private final LoadImpactfulQuestionsBySubjectPort loadImpactfulQuestionsBySubjectPort;
    private final LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessmentPort;
    private final CountAnswersByQuestionAndAssessmentResultPort countAnswersByQuestionAndAssessmentResultPort;

    @Override
    public Result getSubjectProgress(Param param) {
        var impactfulQuestions = loadImpactfulQuestionsBySubjectPort.loadImpactfulQuestionsBySubjectId(param.getSubjectId());
        var impactfulQuestionsIds = impactfulQuestions.stream()
            .map(QuestionDto::id)
            .toList();
        var result = loadAssessmentResultByAssessmentPort.loadByAssessmentId(param.getAssessmentId());
        int answerCount = countAnswersByQuestionAndAssessmentResultPort
            .countAnswersByQuestionIdAndAssessmentResult(impactfulQuestionsIds, result.getId());
        return new Result(UUID.randomUUID(), answerCount, impactfulQuestions.size());
    }
}
