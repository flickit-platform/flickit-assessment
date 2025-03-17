package org.flickit.assessment.core.application.port.out.measure;

import org.flickit.assessment.core.application.domain.Measure;

import java.util.List;

public interface LoadMeasuresPort {

    List<Measure> loadAll(List<Long> measureIds, long kitVersionId);
}
