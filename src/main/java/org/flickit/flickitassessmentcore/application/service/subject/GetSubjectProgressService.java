package org.flickit.flickitassessmentcore.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.Question;
import org.flickit.flickitassessmentcore.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CountAnswersByQuestionIdsPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectProgressService implements GetSubjectProgressUseCase {

    private final LoadQuestionsBySubjectPort loadQuestionsBySubjectPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CountAnswersByQuestionIdsPort countAnswersByQuestionIdsPort;

    @Override
    public Result getSubjectProgress(Param param) {
        var impactfulQuestions = loadQuestionsBySubjectPort.loadImpactfulQuestionsBySubjectId(param.getSubjectId());
        var impactfulQuestionsIds = impactfulQuestions.stream()
            .map(Question::getId)
            .toList();
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND));
        int answerCount = countAnswersByQuestionIdsPort
            .countByQuestionIds(assessmentResult.getId(), impactfulQuestionsIds);
        return new Result(param.getSubjectId(), answerCount);
    }
}
