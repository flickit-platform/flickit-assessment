package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportVisibilityUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentReportVisibilityService implements UpdateAssessmentReportVisibilityUseCase {

    private final UpdateAssessmentReportPort updateAssessmentReportPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;

    @Override
    public void updateReportVisibility(Param param) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        updateAssessmentReportPort.updateVisibility(toParam(assessmentResult.getId(), param));
    }

    UpdateAssessmentReportPort.UpdateVisibilityParam toParam(UUID assessmentResultId, Param param) {
        return new UpdateAssessmentReportPort.UpdateVisibilityParam(assessmentResultId,
            VisibilityType.valueOf(param.getVisibility()),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
