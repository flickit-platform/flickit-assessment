package org.flickit.assessment.kit.application.port.out.measure;

import org.flickit.assessment.kit.application.domain.Measure;

import java.util.List;

public interface LoadMeasurePort {

    Measure loadByCode(String code, Long kitVersionId);

    List<Measure> loadAll(Long kitVersionId);
}
