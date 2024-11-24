package org.flickit.assessment.kit.application.port.out.kitversion;

public interface CountKitVersionStatsPort {

    Result countKitVersionStats(long kitVersionId);

    record Result(int subjectCount, int questionnairesCount, int questionsCount, int maturityLevelsCount) {
    }
}
