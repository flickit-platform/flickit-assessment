package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
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

        var assessmentReport = loadAssessmentReportPort.load(param.getAssessmentId()).orElse(null);
        if (assessmentReport == null) {
            assessmentReport = buildAssessmentReport(assessmentResult.getId(), param.getCurrentUserId());
            createAssessmentReportPort.persist(assessmentReport);
        }

        if (Boolean.TRUE.equals(param.getPublished()))
            updateAssessmentReportPort.updatePublishStatus(buildPublishParam(assessmentResult.getId(), param.getCurrentUserId()));
        else
            updateAssessmentReportPort.updatePublishStatus(buildUnpublishParam(assessmentResult.getId(), assessmentReport.getVisibility(), param.getCurrentUserId()));
    }

    private AssessmentReport buildAssessmentReport(UUID assessmentResultId, UUID currentUserId) {
        return new AssessmentReport(null,
            assessmentResultId,
            null,
            false,
            VisibilityType.RESTRICTED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            currentUserId,
            currentUserId);
    }

    private UpdateAssessmentReportPort.UpdatePublishParam buildPublishParam(UUID assessmentResultId, UUID currentUserId) {
        return new UpdateAssessmentReportPort.UpdatePublishParam(assessmentResultId,
            Boolean.TRUE,
            VisibilityType.RESTRICTED,
            LocalDateTime.now(),
            currentUserId);
    }

    private UpdateAssessmentReportPort.UpdatePublishParam buildUnpublishParam(UUID assessmentResultId, VisibilityType visibility, UUID currentUserId) {
        return new UpdateAssessmentReportPort.UpdatePublishParam(assessmentResultId,
            Boolean.FALSE,
            visibility,
            LocalDateTime.now(),
            currentUserId);
    }
}
