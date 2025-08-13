package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.Attribute;

public class AttributeMother {

    private static long id = 1034;

    public static Attribute createWithWeight(int weight) {
        id++;
        return new Attribute(id, "title" + id, weight);
    }
}
