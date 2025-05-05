package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.VisibilityType;

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
                              VisibilityType visibilityType,
                              LocalDateTime lastModificationTime,
                              UUID lastModifiedBy) {
    }

    void updateVisibility(UpdateVisibilityParam param);

    record UpdateVisibilityParam(UUID assessmentResultId,
                                 VisibilityType visibility,
                                 LocalDateTime lastModificationTime,
                                 UUID lastModifiedBy) {
    }
}
