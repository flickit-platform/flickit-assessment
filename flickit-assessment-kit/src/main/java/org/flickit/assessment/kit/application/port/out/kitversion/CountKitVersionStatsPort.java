package org.flickit.assessment.kit.application.port.out.kitversion;

import java.util.Map;

public interface CountKitVersionStatsPort {

    Result countKitVersionStats(long kitVersionId);

    record Result(int subjectCount, int questionnaireCount, int questionCount, int maturityLevelCount, Map<String, Long> attributeIndexToMeasuresCountMap) {
    }
}
