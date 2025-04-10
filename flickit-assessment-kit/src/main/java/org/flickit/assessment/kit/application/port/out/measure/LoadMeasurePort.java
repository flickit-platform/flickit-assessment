package org.flickit.assessment.kit.application.port.out.measure;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.Measure;

import java.util.List;

public interface LoadMeasurePort {

    Measure loadByCode(String code, Long kitVersionId);

    List<Measure> loadAll(Long kitVersionId);

    PaginatedResponse<Result> loadAll(long kitVersionId, int page, int size);

    record Result(Measure measure, int questionsCount) {
    }
}
