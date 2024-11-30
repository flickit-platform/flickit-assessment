package org.flickit.assessment.advice.application.domain.adviceitem;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ImpactLevel {

    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String title;

    public int getId() {
        return this.ordinal();
    }

    public static ImpactLevel valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < ImpactLevel.values().length;
    }
}
