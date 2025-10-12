package org.flickit.assessment.kit.application.port.out.measure;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.Measure;

import java.util.List;
import java.util.Optional;

public interface LoadMeasurePort {

    Optional<Measure> load(long id,  long kitVersionId);

    List<Measure> loadAll(long kitVersionId);

    PaginatedResponse<Result> loadAll(long kitVersionId, int page, int size);

    record Result(Measure measure, int questionsCount) {
    }
}
