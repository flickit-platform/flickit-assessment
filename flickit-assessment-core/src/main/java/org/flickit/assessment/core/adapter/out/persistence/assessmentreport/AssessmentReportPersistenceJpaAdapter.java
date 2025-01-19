package org.flickit.assessment.core.adapter.out.persistence.assessmentreport;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportMetaDataPort;
import org.flickit.assessment.data.jpa.core.assessmentreport.AssessmentReportJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentReportPersistenceJpaAdapter implements LoadAssessmentReportMetaDataPort {

    private final AssessmentReportJpaRepository repository;

    @SneakyThrows
    @Override
    public String loadMetadata(UUID assessmentId) {
        return repository.findMetaDataByAssessmentId(assessmentId);
    }
}
