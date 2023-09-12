package org.flickit.flickitassessmentcore.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.out.subject.GetSubjectAnsweredQuestionsCountPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectProgressService implements GetSubjectProgressUseCase {

    private final GetSubjectAnsweredQuestionsCountPort getSubjectAnsweredQuestionsCountPort;

    @Override
    public Result getSubjectProgress(Param param) {
        var result = getSubjectAnsweredQuestionsCountPort.getSubjectAnsweredQuestionsCount(param.getAssessmentId(), param.getSubjectId());
        return new Result(result.id(), result.answerCount(), result.questionCount());
    }
}
