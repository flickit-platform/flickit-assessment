package org.flickit.assessment.users.application.domain;

public enum ExpertGroupAccessStatus {

    PENDING,
    ACTIVE;

    public static ExpertGroupAccessStatus valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < ExpertGroupAccessStatus.values().length;
    }
}
