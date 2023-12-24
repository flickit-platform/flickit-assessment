package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.question.LoadAttributeScoreDetailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeScoreDetailService implements GetAttributeScoreDetailUseCase {

    private final LoadAttributeScoreDetailPort loadAttributeScoreDetailPort;

    @Override
    public Result getAttributeScoreDetail(Param param) {
        List<QuestionScore> impactFullQuestions = loadAttributeScoreDetailPort.load(param.getAttributeId(), param.getMaturityLevelId(), param.getAssessmentId());
        double totalScore = 0.0;
        double gainedScore = 0.0;

        for (QuestionScore qs : impactFullQuestions) {
            totalScore += qs.questionWeight();
            if (qs.answerScore() != null)
                gainedScore += qs.weightedScore();
        }

        return new Result(totalScore, gainedScore, impactFullQuestions);
    }
}
