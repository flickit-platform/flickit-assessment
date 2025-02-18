package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetadataUseCase.Result.Metadata;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentReportMetadataService implements GetAssessmentReportMetadataUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentReportPort loadAssessmentReportPort;

    @Override
    public Result getAssessmentReportMetadata(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentReport = loadAssessmentReportPort.load(param.getAssessmentId());
        return assessmentReport.map(this::toResult)
            .orElseGet(() -> new Result(new Metadata(null, null, null, null), false));
    }

    private Result toResult(AssessmentReport assessmentReport) {
        return new Result(toMetadata(assessmentReport.getMetadata()), assessmentReport.isPublished());
    }

    private Metadata toMetadata(AssessmentReportMetadata metadata) {
        return new Metadata(metadata.intro(),
            metadata.prosAndCons(),
            metadata.steps(),
            metadata.participants());
    }
}
