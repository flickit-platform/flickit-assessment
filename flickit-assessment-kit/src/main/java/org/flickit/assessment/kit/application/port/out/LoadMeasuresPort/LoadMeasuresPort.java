package org.flickit.assessment.kit.application.port.out.LoadMeasuresPort;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.Measure;

public interface LoadMeasuresPort {

    PaginatedResponse<LoadMeasuresPort.Result> loadAll(long kitVersionId, int page, int size);

    record Result(Measure measure, int questionsCount) {
    }
}
