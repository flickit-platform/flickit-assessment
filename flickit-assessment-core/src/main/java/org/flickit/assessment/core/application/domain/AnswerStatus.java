package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AnswerStatus {

    APPROVED("Approved"),
    UNAPPROVED("Unapproved");

    private final String title;

    public String getCode() {
        return name();
    }

    public static AnswerStatus valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < AnswerStatus.values().length;
    }
}
