package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.SubjectReportItem;

public class SubjectReportItemMother {

    private static long id = 134L;

    public static SubjectReportItem createWithMaturityLevel(MaturityLevel maturityLevel) {
        return new SubjectReportItem(id++,
            "reportTitle",
            "desc",
            maturityLevel,
            1d,
            true,
            true);
    }
}
