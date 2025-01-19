package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportMetaDataPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetaDataUseCase;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentReportMetaDataService implements GetAssessmentReportMetaDataUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentReportMetaDataPort loadAssessmentReportMetaDataPort;

    @Override
    public Result getAssessmentReportMetaData(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentReportMetaData = loadAssessmentReportMetaDataPort.loadMetadata(param.getAssessmentId());
        return toResult(assessmentReportMetaData);
    }

    private Result toResult(AssessmentReportMetadata metadata) {
        return new Result(metadata.getIntro(), metadata.getProsAndCons(), metadata.getSteps(), metadata.getParticipants());
    }
}
