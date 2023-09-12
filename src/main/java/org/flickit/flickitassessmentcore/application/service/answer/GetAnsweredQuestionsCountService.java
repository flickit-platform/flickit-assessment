package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnsweredQuestionsCountUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.GetAnsweredQuestionsCountPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAnsweredQuestionsCountService implements GetAnsweredQuestionsCountUseCase {

    private final GetAnsweredQuestionsCountPort getAnsweredQuestionsCountPort;

    @Override
    public Result getAnsweredQuestionsCount(Param param) {
        var result = getAnsweredQuestionsCountPort.getAnsweredQuestionsCountById(param.getAssessmentId());
        return new Result(result.id(), result.allAnswersCount());
    }
}
