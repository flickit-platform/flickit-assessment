package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.InvalidStateException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportVisibilityUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_ASSESSMENT_REPORT_VISIBILITY;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.REPORT_UNPUBLISHED;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_PUBLISHED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentReportVisibilityService implements UpdateAssessmentReportVisibilityUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final UpdateAssessmentReportPort updateAssessmentReportPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentReportPort loadAssessmentReportPort;

    @Override
    public Result updateReportVisibility(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var assessmentReport = loadAssessmentReportPort.load(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_FOUND));

        if (!assessmentReport.isPublished())
            throw new InvalidStateException(REPORT_UNPUBLISHED, UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_PUBLISHED);

        UUID linkHash = null;
        var visibility = VisibilityType.valueOf(param.getVisibility());
        if (visibility.equals(VisibilityType.PUBLIC))
            linkHash = assessmentReport.getLinkHash();

        updateAssessmentReportPort.updateVisibilityStatus(toParam(assessmentResult.getId(), visibility, param.getCurrentUserId()));
        return new Result(linkHash);
    }

    private UpdateAssessmentReportPort.UpdateVisibilityParam toParam(UUID assessmentResultId, VisibilityType visibility, UUID userId) {
        return new UpdateAssessmentReportPort.UpdateVisibilityParam(assessmentResultId,
            visibility,
            LocalDateTime.now(),
            userId);
    }
}
