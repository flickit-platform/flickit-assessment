package org.flickit.assessment.core.application.port.in.confidencelevel;

import java.util.List;

public interface GetConfidenceLevelsUseCase {

    Result getConfidenceLevels();

    record Result(
        ConfidenceLevelItem defaultConfidenceLevel,
        List<ConfidenceLevelItem> confidenceLevels) {}

    record ConfidenceLevelItem(int id, String title) {
    }

}
