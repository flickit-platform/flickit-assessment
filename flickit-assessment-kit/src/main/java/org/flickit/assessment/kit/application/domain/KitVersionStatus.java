package org.flickit.assessment.kit.application.domain;

import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
public enum KitVersionStatus {

    ACTIVE, UPDATING, ARCHIVE;

    public static KitVersionStatus valueOfByOrdinal(Integer ordinal) {
        return Stream.of(KitVersionStatus.values())
            .filter(x -> Objects.equals(x.ordinal(), ordinal))
            .findFirst()
            .orElse(null);
    }
}
