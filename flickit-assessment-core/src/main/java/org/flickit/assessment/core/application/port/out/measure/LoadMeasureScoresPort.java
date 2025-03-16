package org.flickit.assessment.core.application.port.out.measure;

import java.util.List;

public interface LoadMeasureScoresPort {

    List<Result> loadAll(List<Long> measureIds, long kitVersionId);

    record Result(Long id, String title, Double score) {
    }
}
