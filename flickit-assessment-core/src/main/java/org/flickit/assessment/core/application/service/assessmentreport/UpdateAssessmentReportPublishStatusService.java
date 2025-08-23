package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportPublishStatusUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.PUBLISH_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentReportPublishStatusService implements UpdateAssessmentReportPublishStatusUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final CreateAssessmentReportPort createAssessmentReportPort;
    private final UpdateAssessmentReportPort updateAssessmentReportPort;

    @Override
    public void updateReportPublishStatus(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        loadAssessmentReportPort.load(param.getAssessmentId())
            .ifPresentOrElse(
                report ->
                    updatePublishStatus(assessmentResult.getId(), param, report.getVisibility()),
                () -> {
                    var createReportParam = buildAssessmentReportParam(assessmentResult.getId(), param);
                    createAssessmentReportPort.persist(createReportParam);
                }
            );
    }

    private CreateAssessmentReportPort.Param buildAssessmentReportParam(UUID assessmentResultId, Param param) {
        return new CreateAssessmentReportPort.Param(
            assessmentResultId,
            null,
            param.getPublished(),
            VisibilityType.RESTRICTED,
            LocalDateTime.now(),
            param.getCurrentUserId());
    }

    private void updatePublishStatus(UUID assessmentResultId, Param param, VisibilityType visibility) {
        var updateParam = Boolean.TRUE.equals(param.getPublished())
            ? buildUpdatePublishParam(assessmentResultId, param, VisibilityType.RESTRICTED)
            : buildUpdatePublishParam(assessmentResultId, param, visibility);

        updateAssessmentReportPort.updatePublishStatus(updateParam);
    }

    private UpdateAssessmentReportPort.UpdatePublishParam buildUpdatePublishParam(UUID assessmentResultId, Param param, VisibilityType visibility) {
        return new UpdateAssessmentReportPort.UpdatePublishParam(assessmentResultId,
            param.getPublished(),
            visibility,
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
