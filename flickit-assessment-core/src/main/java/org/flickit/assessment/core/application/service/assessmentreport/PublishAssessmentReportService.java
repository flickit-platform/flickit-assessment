package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.port.in.assessmentreport.PublishAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.PublishAssessmentReportPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.PUBLISH_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.PUBLISH_ASSESSMENT_REPORT_ASSESSMENT_REPORT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class PublishAssessmentReportService implements PublishAssessmentReportUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final PublishAssessmentReportPort publishAssessmentReportPort;

    @Override
    public void publishAssessmentReport(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentReport = loadAssessmentReportPort.load(param.getAssessmentId());
        if (assessmentReport.isEmpty())
            throw new ResourceNotFoundException(PUBLISH_ASSESSMENT_REPORT_ASSESSMENT_REPORT_NOT_FOUND);

        publishAssessmentReportPort.publish(toPublishPortParam(assessmentReport.get(), param));
    }

    private PublishAssessmentReportPort.Param toPublishPortParam(AssessmentReport assessmentReport, Param param) {
        return new PublishAssessmentReportPort.Param(assessmentReport.getId(),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
