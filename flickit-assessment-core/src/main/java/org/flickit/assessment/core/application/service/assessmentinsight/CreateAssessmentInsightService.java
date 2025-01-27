package org.flickit.assessment.core.application.service.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentInsightService implements CreateAssessmentInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final CreateAssessmentInsightPort createAssessmentInsightPort;
    private final UpdateAssessmentInsightPort updateAssessmentInsightPort;

    @Override
    public void createAssessmentInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResultId = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .map(AssessmentResult::getId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResultId);

        if (assessmentInsight.isPresent())
            updateAssessmentInsightPort.updateInsight(toAssessmentInsight(assessmentInsight.get().getId(), assessmentResultId, param));
        else
            createAssessmentInsightPort.persist(toAssessmentInsight(null, assessmentResultId, param));
    }

    AssessmentInsight toAssessmentInsight(UUID insightId, UUID assessmentResultId, Param param) {
        return new AssessmentInsight(insightId,
            assessmentResultId,
            param.getInsight(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            true);
    }
}
