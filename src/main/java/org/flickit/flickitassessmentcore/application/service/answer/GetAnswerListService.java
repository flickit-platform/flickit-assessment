package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadAnswersByAssessmentAndQuestionIdsPort;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAnswerListService implements GetAnswerListUseCase {

    private final LoadAnswersByAssessmentAndQuestionIdsPort loadAnswersPort;

    @Override
    public Result getAnswerList(Param param) {
        return mapToResult(loadAnswersPort.loadAnswersByAssessmentAndQuestionIdsPort(mapToPortParam(param)));
    }

    private LoadAnswersByAssessmentAndQuestionIdsPort.Param mapToPortParam(Param param) {
        return new LoadAnswersByAssessmentAndQuestionIdsPort.Param(param.getAssessmentId(), param.getQuestionIds());
    }

    private Result mapToResult(List<Answer> answers) {
        return new Result(answers);
    }
}
