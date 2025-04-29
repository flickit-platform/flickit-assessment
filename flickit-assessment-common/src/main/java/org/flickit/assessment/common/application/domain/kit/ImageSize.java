package org.flickit.assessment.common.application.domain.kit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ImageSize {

    SMALL, LARGE;

    public int getId() {
        return this.ordinal();
    }

    public static ImageSize valueOfById(int id) {
        if (!isValidId(id))
            return getDefault();
        return values()[id];
    }

    public static ImageSize getDefault() {
        return LARGE;
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < ImageSize.values().length;
    }
}
