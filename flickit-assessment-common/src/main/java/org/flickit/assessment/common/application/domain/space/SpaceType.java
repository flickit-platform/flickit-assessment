package org.flickit.assessment.common.application.domain.space;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;

import static org.flickit.assessment.common.error.MessageKey.SPACE_TYPE_BASIC;
import static org.flickit.assessment.common.error.MessageKey.SPACE_TYPE_PREMIUM;

@Getter
@RequiredArgsConstructor
public enum SpaceType {

    BASIC(SPACE_TYPE_BASIC),
    PREMIUM(SPACE_TYPE_PREMIUM);

    private final String title;

    public String getTitle() {
        return MessageBundle.message(title);
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

    private static boolean isValid(int id) {
        return id >= 0 && id <= SpaceType.values().length;
    }
}
