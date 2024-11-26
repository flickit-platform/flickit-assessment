package org.flickit.assessment.common.application.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ID <T> {
    private T value;

    public ID(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public static ID<String> toDomain(UUID id) {
        return new ID<>(id.toString());
    }


/*    public static ID<String> toDomain(String id) {
        return new ID<>(id);
    }*/

    public static UUID fromDomain(ID<String> value) {
        return UUID.fromString(value.toString());
    }
}
