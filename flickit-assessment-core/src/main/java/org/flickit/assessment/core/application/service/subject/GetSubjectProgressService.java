package org.flickit.assessment.core.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.assessment.core.application.port.out.answer.CountAnswersByQuestionIdsPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectProgressService implements GetSubjectProgressUseCase {

    private final LoadQuestionsBySubjectPort loadQuestionsBySubjectPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CountAnswersByQuestionIdsPort countAnswersByQuestionIdsPort;

    @Override
    public Result getSubjectProgress(Param param) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND));

        var questionIds = loadQuestionsBySubjectPort.loadQuestionsBySubject(param.getSubjectId())
            .stream()
            .map(Question::getId)
            .toList();

        int answerCount = countAnswersByQuestionIdsPort.countByQuestionIds(assessmentResult.getId(), questionIds);
        return new Result(param.getSubjectId(), answerCount);
    }
}
