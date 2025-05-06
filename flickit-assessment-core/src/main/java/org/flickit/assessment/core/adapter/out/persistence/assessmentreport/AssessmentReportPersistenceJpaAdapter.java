package org.flickit.assessment.core.adapter.out.persistence.assessmentreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.data.jpa.core.assessmentreport.AssessmentReportJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.adapter.out.persistence.assessmentreport.AssessmentReportMapper.mapToJpaEntity;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_ASSESSMENT_REPORT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentReportPersistenceJpaAdapter implements
    LoadAssessmentReportPort,
    CreateAssessmentReportPort,
    UpdateAssessmentReportPort {

    private final AssessmentReportJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public Optional<AssessmentReport> load(UUID assessmentId) {
        var assessmentResultId = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND)).getId();

        var reportEntity = repository.findByAssessmentResultId(assessmentResultId);

        if (reportEntity.isPresent()) {
            AssessmentReportMetadata metadata = objectMapper.readValue(reportEntity.get().getMetadata(), AssessmentReportMetadata.class);
            return Optional.of(AssessmentReportMapper.mapToDomainModel(reportEntity.get(), metadata));
        }
        return Optional.empty();
    }

    @Override
    @SneakyThrows
    public void persist(CreateAssessmentReportPort.Param assessmentReport) {
        var metadata = objectMapper.writeValueAsString(assessmentReport.metadata());
        repository.save(mapToJpaEntity(assessmentReport, metadata));
    }

    @Override
    @SneakyThrows
    public void updateMetadata(UpdateAssessmentReportPort.UpdateMetadataParam param) {
        repository.updateMetadata(param.id(),
            objectMapper.writeValueAsString(param.reportMetadata()),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public void updatePublishStatus(UpdateAssessmentReportPort.UpdatePublishParam param) {
        if (!repository.existsByAssessmentResultId(param.assessmentResultId()))
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_ASSESSMENT_REPORT_NOT_FOUND);

        repository.updatePublished(param.assessmentResultId(),
            param.published(),
            param.visibilityType().getId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public void updateVisibilityStatus(UpdateVisibilityParam param) {
        if (!repository.existsByAssessmentResultId(param.assessmentResultId()))
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_FOUND);

        repository.updateVisibility(param.assessmentResultId(),
            param.visibility().getId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }
}
