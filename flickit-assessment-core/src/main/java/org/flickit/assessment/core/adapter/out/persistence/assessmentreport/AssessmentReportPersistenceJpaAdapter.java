package org.flickit.assessment.core.adapter.out.persistence.assessmentreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportMetaDataPort;
import org.flickit.assessment.data.jpa.core.assessmentreport.AssessmentReportJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentReportPersistenceJpaAdapter implements LoadAssessmentReportMetaDataPort {

    private final AssessmentReportJpaRepository repository;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public AssessmentReportMetadata loadMetadata(UUID assessmentId) {
        var assessmentReportMetaData = repository.findMetaDataByAssessmentId(assessmentId);
        return objectMapper.readValue(assessmentReportMetaData, AssessmentReportMetadata.class);
    }
}
