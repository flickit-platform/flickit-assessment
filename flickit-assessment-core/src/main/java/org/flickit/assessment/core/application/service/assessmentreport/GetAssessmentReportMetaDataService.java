package org.flickit.assessment.core.application.service.assessmentreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Result getAssessmentReportMetaData(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadAssessmentReportMetaDataPort.loadMetadata(param.getAssessmentId());
        if (portResult != null)
            return toResult(objectMapper.readValue(portResult, AssessmentReportMetadata.class));

        return new Result(null, null, null, null);
    }

    private Result toResult(AssessmentReportMetadata metadata) {
        return new Result(metadata.intro(), metadata.prosAndCons(), metadata.steps(), metadata.participants());
    }
}
