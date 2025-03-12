package org.flickit.assessment.kit.application.port.out.measure;

import org.flickit.assessment.kit.application.domain.Measure;

import java.util.List;

public interface LoadMeasurePort {

    Measure loadByCode(Long kitVersionId, String code);

    List<Measure> loadAll(Long kitVersionId);
}
