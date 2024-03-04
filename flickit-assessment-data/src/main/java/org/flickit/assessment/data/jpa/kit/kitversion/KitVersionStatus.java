package org.flickit.assessment.data.jpa.kit.kitversion;

public enum KitVersionStatus {
    ACTIVE(1), UPDATING(2), ARCHIVE(3);

    final int code;

    KitVersionStatus(int code) {
        this.code = code;
    }
}
