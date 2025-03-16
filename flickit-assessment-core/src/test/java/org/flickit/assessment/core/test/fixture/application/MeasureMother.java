package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Measure;

public class MeasureMother {

    private static long id = 134L;

    public static Measure createMeasure() {
        return new Measure(id++, "title" + id);
    }
}
