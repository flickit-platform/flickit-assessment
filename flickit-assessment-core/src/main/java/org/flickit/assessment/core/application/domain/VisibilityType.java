package org.flickit.assessment.core.application.domain;

public enum VisibilityType {
    RESTRICTED, PUBLIC;

    public static VisibilityType getDefault() {
        return RESTRICTED;
    }

    public int getId() {
        return ordinal();
    }
}
