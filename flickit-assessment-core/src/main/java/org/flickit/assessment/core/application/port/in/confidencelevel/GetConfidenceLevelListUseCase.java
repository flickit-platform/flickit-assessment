package org.flickit.assessment.core.application.port.in.confidencelevel;

import java.util.List;

public interface GetConfidenceLevelListUseCase {

    List<ConfidenceLevelItem> getConfidenceLevels();

    record ConfidenceLevelItem(int id, String title) {
    }

}
