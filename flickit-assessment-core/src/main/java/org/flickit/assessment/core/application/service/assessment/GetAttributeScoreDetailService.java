package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.question.LoadAttributeScoreDetailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeScoreDetailService implements GetAttributeScoreDetailUseCase {

    private final LoadAttributeScoreDetailPort loadAttributeScoreDetailPort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Override
    public Result getAttributeScoreDetail(Param param) {
        checkUserAccess(param.getAssessmentId(), param.getCurrentUserId());

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

    private void checkUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }


}
