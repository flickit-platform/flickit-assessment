package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentMode {

    QUICK,
    ADVANCED;

    public String getCode() {
        return name();
    }

    public int getId() {
        return this.ordinal();
    }

    public static AssessmentMode valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < AssessmentMode.values().length;
    }
}
