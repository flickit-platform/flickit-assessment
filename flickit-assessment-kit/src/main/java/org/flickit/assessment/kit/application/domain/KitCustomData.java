package org.flickit.assessment.kit.application.domain;

import java.util.List;

public record KitCustomData(List<Subject> subjects, List<Attribute> attributes) {

    public record Subject(long id, int weight) {}

    public record Attribute(long id, int weight) {}
}
