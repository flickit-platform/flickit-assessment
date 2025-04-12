package org.flickit.assessment.kit.application.port.out.measure;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.Measure;

public interface LoadMeasurePort {

    PaginatedResponse<Result> loadAll(long kitVersionId, int page, int size);

    record Result(Measure measure, int questionsCount) {
    }
}
