package org.flickit.assessment.core.application.port.out.measure;

import org.flickit.assessment.core.application.domain.Measure;

import java.util.List;
import java.util.UUID;

public interface LoadMeasuresPort {

    List<Measure> loadAll(List<Long> measureIds, UUID assessmentId);
}
