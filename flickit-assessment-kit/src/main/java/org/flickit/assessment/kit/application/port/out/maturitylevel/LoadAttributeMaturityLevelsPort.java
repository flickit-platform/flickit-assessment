package org.flickit.assessment.kit.application.port.out.maturitylevel;

import java.util.List;

public interface LoadAttributeMaturityLevelsPort {

    List<Result> loadAttributeLevels(long attributeId, long kitVersionId);

    record Result(Long id, String title, int index, int questionCount) {
    }
}
