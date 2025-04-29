package org.flickit.assessment.common.application.domain.kit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ImageSize {

    SMALL, LARGE;

    public static ImageSize valueOfById(int id) {
        if (!isValidId(id))
            return getDefault();
        return values()[id];
    }

    private static ImageSize getDefault() {
        return LARGE;
    }

    private static boolean isValidId(int id) {
        return id >= 0 && id < ImageSize.values().length;
    }
}
