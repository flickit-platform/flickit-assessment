package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreDetailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        var impactFullQuestions = loadAttributeScoreDetailPort.loadScoreDetail(
            param.getAssessmentId(),
            param.getAttributeId(),
            param.getMaturityLevelId());
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
