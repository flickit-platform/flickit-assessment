package org.flickit.assessment.kit.application.port.out.assessmentkit;

import java.util.List;

public interface CountKitListStatsPort {

    List<Result> countKitsStats(List<Long> kitIds);

    record Result(long id, int likes, int assessmentsCount) {
    }
}
