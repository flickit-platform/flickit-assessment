package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_REPORT_METADATA;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentReportMetadataService implements CreateAssessmentReportMetadataUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final CreateAssessmentReportPort createAssessmentReportPort;
    private final UpdateAssessmentReportPort updateAssessmentReportPort;

    @Override
    public void createReportMetadata(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        Optional.ofNullable(loadAssessmentReportPort.load(param.getAssessmentId()))
            .flatMap(Function.identity())
            .ifPresentOrElse(
                report -> {
                    var existedMetadata = report.getMetadata();
                    var newMetadata = buildNewMetadata(existedMetadata, param.getMetadata());
                    updateAssessmentReportPort.updateMetadata(
                        toUpdateParam(report.getId(), newMetadata, param.getCurrentUserId())
                    );
                },
                () -> {
                    var metadata = toDomainModel(param.getMetadata());
                    createAssessmentReportPort.persist(
                        toAssessmentReportParam(assessmentResult.getId(), metadata, param.getCurrentUserId())
                    );
                }
            );
    }

    private AssessmentReportMetadata toDomainModel(MetadataParam metadata) {
        return new AssessmentReportMetadata(metadata.getIntro(),
            metadata.getProsAndCons(),
            metadata.getSteps(),
            metadata.getParticipants());
    }

    private CreateAssessmentReportPort.Param toAssessmentReportParam(UUID assessmentResultId, AssessmentReportMetadata metadata, UUID currentUserId) {
        return new CreateAssessmentReportPort.Param(
            assessmentResultId,
            metadata,
            Boolean.FALSE,
            VisibilityType.RESTRICTED,
            LocalDateTime.now(),
            currentUserId);
    }

    private AssessmentReportMetadata buildNewMetadata(AssessmentReportMetadata existedMetadata, MetadataParam metadataParam) {
        return new AssessmentReportMetadata(
            resolveField(existedMetadata.intro(), metadataParam.getIntro()),
            resolveField(existedMetadata.prosAndCons(), metadataParam.getProsAndCons()),
            resolveField(existedMetadata.steps(), metadataParam.getSteps()),
            resolveField(existedMetadata.participants(), metadataParam.getParticipants())
        );
    }

    private <T> T resolveField(T existingValue, T newValue) {
        return newValue != null ? newValue : existingValue;
    }

    private UpdateAssessmentReportPort.UpdateMetadataParam toUpdateParam(UUID assessmentReportId, AssessmentReportMetadata newMetadata, UUID currentUserId) {
        return new UpdateAssessmentReportPort.UpdateMetadataParam(assessmentReportId,
            newMetadata,
            LocalDateTime.now(),
            currentUserId);
    }
}
