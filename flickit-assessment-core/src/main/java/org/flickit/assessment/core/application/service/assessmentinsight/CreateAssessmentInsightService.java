package org.flickit.assessment.core.application.service.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
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
    public Result createAssessmentInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId());
        if (assessmentResult.isEmpty())
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND);

        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.get().getId());

        if (assessmentInsight.isEmpty())
            return new Result(createAssessmentInsightPort.persist
                (toAssessmentInsight(null, assessmentResult.get().getId(), param.getInsight(), LocalDateTime.now(), param.getCurrentUserId())));

        updateAssessmentInsightPort.updateInsight
            (toAssessmentInsight(assessmentInsight.get().getId(), assessmentResult.get().getId(), param.getInsight(), LocalDateTime.now(), param.getCurrentUserId()));
        return new Result(assessmentInsight.get().getId());
    }

    AssessmentInsight toAssessmentInsight(UUID insightId, UUID assessmentResultId, String insight, LocalDateTime insightTime, UUID insightBy) {
        return new AssessmentInsight(insightId, assessmentResultId, insight, insightTime, insightBy);
    }
}
