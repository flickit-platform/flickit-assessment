package org.flickit.assessment.kit.application.domain;

import lombok.Getter;

@Getter
public enum KitVersionStatus {

    ACTIVE, UPDATING, ARCHIVE;

    public int getId() {
        return this.ordinal();
    }

    public static KitVersionStatus valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < KitVersionStatus.values().length;
    }
}
