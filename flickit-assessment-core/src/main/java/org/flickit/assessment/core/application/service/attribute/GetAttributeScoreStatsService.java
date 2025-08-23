package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreStatsUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoresPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_SCORE_DETAIL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class GetAttributeScoreStatsService implements GetAttributeScoreStatsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAttributeScoresPort loadAttributeScoresPort;

    @Override
    public Result getAttributeScoreStats(Param param) {
        checkUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        var stats = loadAttributeScoresPort.loadScores(param.getAssessmentId(), param.getAttributeId(), param.getMaturityLevelId());

        double maxPossibleScore = 0.0;
        double gainedScore = 0.0;
        for (LoadAttributeScoresPort.Result result : stats) {
            if (Boolean.TRUE.equals(result.answerIsNotApplicable()))
                continue;
            maxPossibleScore += result.questionWeight();
            if (result.answerScore() != null)
                gainedScore += result.questionWeight() * result.answerScore();
        }
        double gainedScorePercentage = maxPossibleScore > 0 ? (gainedScore / maxPossibleScore) * 100 : 0.0;

        return new Result(maxPossibleScore, gainedScore, gainedScorePercentage, stats.size());
    }

    private void checkUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ATTRIBUTE_SCORE_DETAIL))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
