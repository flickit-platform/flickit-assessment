package org.flickit.assessment.common.application.domain.space;

public enum SpaceStatus {

    ACTIVE,
    INACTIVE;

    public int getId() {
        return this.ordinal();
    }

    public String getCode() {
        return name();
    }

    public static SpaceStatus valueOfById(int id) {
        if (!isValid(id))
            return null;
        return SpaceStatus.values()[id];
    }

    private static boolean isValid(int id) {
        return id >= 0 && id < values().length;
    }
}
