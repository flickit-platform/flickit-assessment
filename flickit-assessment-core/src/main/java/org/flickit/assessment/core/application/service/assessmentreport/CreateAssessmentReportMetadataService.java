package org.flickit.assessment.core.application.service.assessmentreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Optional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_REPORT_METADATA;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_REPORT_METADATA_METADATA_NOT_NULL;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentReportMetadataService implements CreateAssessmentReportMetadataUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final ObjectMapper objectMapper;
    private final CreateAssessmentReportPort createAssessmentReportPort;
    private final UpdateAssessmentReportPort updateAssessmentReportPort;


    @SneakyThrows
    @Override
    public void createReportMetadata(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        if (!containsNonNullParam(param)) {
            throw new InvalidParameterException(CREATE_ASSESSMENT_REPORT_METADATA_METADATA_NOT_NULL);
        }
        Optional<AssessmentReport> assessmentReport = loadAssessmentReportPort.load(param.getAssessmentId());
        if (assessmentReport.isEmpty()) {
            AssessmentReportMetadata domainModel = toDomainModel(param.getMetadata());
//            objectMapper.writeValueAsString(domainModel);
            createAssessmentReportPort.persist(param.getAssessmentId(), toDomainModel(param.getMetadata()));
        } else {
            MetadataParam paramMetadata = param.getMetadata();
            AssessmentReportMetadata metadata = new AssessmentReportMetadata(
                paramMetadata.getIntro(),
                paramMetadata.getProsAndCons(),
                paramMetadata.getSteps(),
                paramMetadata.getParticipants()
            );
            updateAssessmentReportPort.update(assessmentReport.get().getId(), metadata);
        }
    }

    private AssessmentReportMetadata toDomainModel(MetadataParam metadata) {
        return new AssessmentReportMetadata(metadata.getIntro(),
            metadata.getProsAndCons(),
            metadata.getSteps(),
            metadata.getParticipants());
    }

    private boolean containsNonNullParam(Param param) {
        var metadata = param.getMetadata();
        return Objects.nonNull(metadata.getIntro()) ||
            Objects.nonNull(metadata.getProsAndCons()) ||
            Objects.nonNull(metadata.getSteps()) ||
            Objects.nonNull(metadata.getParticipants());
    }
}
