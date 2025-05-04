package org.flickit.assessment.core.application.domain;

import java.util.Objects;
import java.util.stream.Stream;

public enum VisibilityType {
    RESTRICTED, PUBLIC;

    public static VisibilityType valueOfById(Integer id) {
        return Stream.of(VisibilityType.values())
            .filter(x -> Objects.equals(x.getId(), id))
            .findAny()
            .orElse(getDefault());
    }

    public static VisibilityType getDefault() {
        return RESTRICTED;
    }

    public int getId() {
        return ordinal();
    }
}
