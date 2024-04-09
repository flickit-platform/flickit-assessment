package org.flickit.assessment.kit.application.port.out.assessmentkit;

public interface CountKitStatsPort {

    Result countKitStats(long kitId);

    record Result(int questionnairesCount, int attributesCount, int questionsCount, int maturityLevelsCount, int likes,
                  int assessmentCounts) {
    }
}
