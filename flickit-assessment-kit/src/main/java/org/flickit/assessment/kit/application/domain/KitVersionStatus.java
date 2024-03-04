package org.flickit.assessment.kit.application.domain;

public enum KitVersionStatus {
    ACTIVE(1), UPDATING(2), ARCHIVE(3);

    final int code;

    KitVersionStatus(int code) {
        this.code = code;
    }
}
