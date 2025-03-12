package org.flickit.assessment.kit.application.port.out.measure;

import org.flickit.assessment.kit.application.domain.Measure;

import java.util.UUID;

public interface CreateMeasurePort {

    Long persist(Measure measure, long kitVersionId, UUID createdBy);
}
