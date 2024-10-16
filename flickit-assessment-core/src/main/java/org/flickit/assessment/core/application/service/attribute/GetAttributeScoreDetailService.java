package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreDetailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_SCORE_DETAIL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeScoreDetailService implements GetAttributeScoreDetailUseCase {

    private final LoadAttributeScoreDetailPort loadAttributeScoreDetailPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result getAttributeScoreDetail(Param param) {
        checkUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        List<Questionnaire> questionnaires = loadAttributeScoreDetailPort.loadScoreDetail(
            param.getAssessmentId(),
            param.getAttributeId(),
            param.getMaturityLevelId());

        double maxPossibleScore = 0.0;
        double gainedScore = 0.0;

        List<QuestionScore> questionScores = questionnaires.stream()
            .flatMap(a -> a.questionScores().stream())
            .toList();

        for (QuestionScore qs : questionScores) {
            if (Boolean.TRUE.equals(qs.answerIsNotApplicable()))
                continue;
            maxPossibleScore += qs.questionWeight();
            if (qs.answerScore() != null)
                gainedScore += qs.weightedScore();
        }

        double gainedScorePercentage = maxPossibleScore > 0 ? gainedScore / maxPossibleScore : 0.0;
        return new Result(maxPossibleScore, gainedScore, gainedScorePercentage, questionScores.size(), questionnaires);
    }

    private void checkUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ATTRIBUTE_SCORE_DETAIL))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
