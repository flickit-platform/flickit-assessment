package org.flickit.assessment.kit.application.port.out.measure;

import org.flickit.assessment.kit.application.domain.Measure;

public interface LoadMeasurePort {

    Measure loadByCode(Long kitVersionId, String code);
}
