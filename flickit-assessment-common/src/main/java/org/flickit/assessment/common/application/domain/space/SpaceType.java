package org.flickit.assessment.common.application.domain.space;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;

@Getter
@RequiredArgsConstructor
public enum SpaceType {

    BASIC(),
    PREMIUM();

    public String getTitle() {
        return MessageBundle.message(getClass().getSimpleName() + "_" + name());
    }

    public int getId() {
        return this.ordinal();
    }

    public String getCode() {
        return name();
    }

    public static SpaceType valueOfById(int id) {
        if (!isValid(id))
            return null;
        return SpaceType.values()[id];
    }

    public static SpaceType getDefault() {
        return BASIC;
    }

    private static boolean isValid(int id) {
        return id >= 0 && id <= SpaceType.values().length;
    }
}
