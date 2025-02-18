package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAssessmentReportPort {

    void updateMetadata(UpdateMetadataParam param);

    record UpdateMetadataParam(UUID id,
                               AssessmentReportMetadata reportMetadata,
                               LocalDateTime lastModificationTime,
                               UUID lastModifiedBy) {
    }

    void updatePublishStatus(UpdatePublishParam param);

    record UpdatePublishParam(UUID assessmentResultId,
                              boolean published,
                              LocalDateTime lastModificationTime,
                              UUID lastModifiedBy) {
    }
}
